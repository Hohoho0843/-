package com.iot.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AlertRuleRequest {

    private Long deviceId;

    @NotBlank(message = "指标类型不能为空")
    private String metricType;

    @NotBlank(message = "比较符不能为空")
    private String operator;

    @NotNull(message = "阈值不能为空")
    private BigDecimal threshold;

    private Integer enabled = 1;

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public BigDecimal getThreshold() { return threshold; }
    public void setThreshold(BigDecimal threshold) { this.threshold = threshold; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
