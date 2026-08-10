package com.iot.platform.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.config.MqttProperties;
import com.iot.platform.dto.MqttSensorPayload;
import com.iot.platform.service.SensorDataService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MqttSubscriber implements MqttCallback {

    private static final Logger log = LoggerFactory.getLogger(MqttSubscriber.class);

    private final MqttProperties mqttProperties;
    private final SensorDataService sensorDataService;
    private final ObjectMapper objectMapper;

    private MqttClient client;

    public MqttSubscriber(MqttProperties mqttProperties,
                          SensorDataService sensorDataService,
                          ObjectMapper objectMapper) {
        this.mqttProperties = mqttProperties;
        this.sensorDataService = sensorDataService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void start() {
        if (!mqttProperties.isEnabled()) {
            log.info("MQTT 已禁用，跳过连接");
            return;
        }
        try {
            client = new MqttClient(mqttProperties.getBrokerUrl(), mqttProperties.getClientId());
            client.setCallback(this);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            if (mqttProperties.getUsername() != null && !mqttProperties.getUsername().isBlank()) {
                options.setUserName(mqttProperties.getUsername());
                options.setPassword(mqttProperties.getPassword() == null
                        ? new char[0] : mqttProperties.getPassword().toCharArray());
            }
            client.connect(options);
            client.subscribe(mqttProperties.getTopic(), mqttProperties.getQos());
            log.info("MQTT 已连接并订阅主题: {}", mqttProperties.getTopic());
        } catch (Exception e) {
            log.warn("MQTT 连接失败，可使用 /api/sensor-data/simulate 接口上报数据: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void stop() {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
                client.close();
            } catch (MqttException e) {
                log.warn("MQTT 断开失败: {}", e.getMessage());
            }
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.warn("MQTT 连接断开: {}", cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            log.info("收到 MQTT 消息 topic={}, payload={}", topic, payload);
            MqttSensorPayload data = objectMapper.readValue(payload, MqttSensorPayload.class);
            sensorDataService.saveFromMqtt(data);
        } catch (Exception e) {
            log.error("解析 MQTT 消息失败", e);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}
