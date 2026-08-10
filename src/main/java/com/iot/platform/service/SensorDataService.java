package com.iot.platform.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iot.platform.common.BusinessException;
import com.iot.platform.common.PageResult;
import com.iot.platform.dto.MqttSensorPayload;
import com.iot.platform.entity.Device;
import com.iot.platform.entity.SensorData;
import com.iot.platform.mapper.SensorDataMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorDataService {

    private final SensorDataMapper sensorDataMapper;
    private final DeviceService deviceService;
    private final AlertService alertService;
    private final OperationLogService operationLogService;

    public SensorDataService(SensorDataMapper sensorDataMapper,
                             DeviceService deviceService,
                             AlertService alertService,
                             OperationLogService operationLogService) {
        this.sensorDataMapper = sensorDataMapper;
        this.deviceService = deviceService;
        this.alertService = alertService;
        this.operationLogService = operationLogService;
    }

    @Transactional
    public void saveFromMqtt(MqttSensorPayload payload) {
        Device device = deviceService.findByCode(payload.getDeviceCode());
        if (device == null) {
            throw new BusinessException("设备未注册: " + payload.getDeviceCode());
        }

        LocalDateTime now = LocalDateTime.now();
        SensorData data = new SensorData();
        data.setDeviceId(device.getId());
        data.setTemperature(payload.getTemperature());
        data.setHumidity(payload.getHumidity());
        data.setAirQuality(payload.getAirQuality());
        data.setReportTime(now);
        sensorDataMapper.insert(data);

        deviceService.markOnline(device.getId(), now);
        alertService.checkAndTrigger(device, data);
        operationLogService.log("数据监控", "数据上报",
                "设备 " + device.getDeviceCode() + " 上报传感器数据", "system");
    }

    public PageResult<SensorData> page(Long deviceId, LocalDateTime startTime,
                                       LocalDateTime endTime, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SensorData> list = sensorDataMapper.findPage(deviceId, startTime, endTime);
        PageInfo<SensorData> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, list);
    }

    public List<SensorData> export(Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        return sensorDataMapper.findForExport(deviceId, startTime, endTime);
    }
}
