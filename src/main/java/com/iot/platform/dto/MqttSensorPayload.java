package com.iot.platform.dto;

import java.math.BigDecimal;

public class MqttSensorPayload {

    private String deviceCode;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private Integer airQuality;

    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }
    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    public BigDecimal getHumidity() { return humidity; }
    public void setHumidity(BigDecimal humidity) { this.humidity = humidity; }
    public Integer getAirQuality() { return airQuality; }
    public void setAirQuality(Integer airQuality) { this.airQuality = airQuality; }
}
