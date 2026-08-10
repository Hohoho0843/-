package com.iot.platform.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iot.platform.common.BusinessException;
import com.iot.platform.common.PageResult;
import com.iot.platform.dto.AlertRuleRequest;
import com.iot.platform.entity.AlertRecord;
import com.iot.platform.entity.AlertRule;
import com.iot.platform.entity.Device;
import com.iot.platform.entity.SensorData;
import com.iot.platform.mapper.AlertRecordMapper;
import com.iot.platform.mapper.AlertRuleMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AlertService {

    private final AlertRuleMapper alertRuleMapper;
    private final AlertRecordMapper alertRecordMapper;
    private final OperationLogService operationLogService;

    public AlertService(AlertRuleMapper alertRuleMapper,
                        AlertRecordMapper alertRecordMapper,
                        OperationLogService operationLogService) {
        this.alertRuleMapper = alertRuleMapper;
        this.alertRecordMapper = alertRecordMapper;
        this.operationLogService = operationLogService;
    }

    public AlertRule createRule(AlertRuleRequest request) {
        AlertRule rule = new AlertRule();
        rule.setDeviceId(request.getDeviceId());
        rule.setMetricType(request.getMetricType());
        rule.setOperator(request.getOperator());
        rule.setThreshold(request.getThreshold());
        rule.setEnabled(request.getEnabled());
        alertRuleMapper.insert(rule);
        operationLogService.log("告警管理", "创建规则",
                "指标=" + request.getMetricType() + ", 阈值=" + request.getThreshold(), "admin");
        return rule;
    }

    public List<AlertRule> listRules() {
        return alertRuleMapper.findEnabledRules();
    }

    public PageResult<AlertRecord> pageRecords(Long deviceId, String status, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<AlertRecord> list = alertRecordMapper.findPage(deviceId, status);
        PageInfo<AlertRecord> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, list);
    }

    public void resolve(Long id) {
        if (alertRecordMapper.resolve(id) == 0) {
            throw new BusinessException("告警记录不存在");
        }
        operationLogService.log("告警管理", "处理告警", "告警ID=" + id, "admin");
    }

    public void checkAndTrigger(Device device, SensorData data) {
        List<AlertRule> rules = alertRuleMapper.findEnabledRules();
        for (AlertRule rule : rules) {
            if (rule.getDeviceId() != null && !rule.getDeviceId().equals(device.getId())) {
                continue;
            }
            BigDecimal actual = getMetricValue(data, rule.getMetricType());
            if (actual == null) {
                continue;
            }
            if (isTriggered(actual, rule)) {
                AlertRecord record = new AlertRecord();
                record.setDeviceId(device.getId());
                record.setMetricType(rule.getMetricType());
                record.setActualValue(actual);
                record.setThreshold(rule.getThreshold());
                record.setMessage(buildMessage(device, rule, actual));
                record.setStatus("PENDING");
                alertRecordMapper.insert(record);
                operationLogService.log("告警管理", "触发告警", record.getMessage(), "system");
            }
        }
    }

    private BigDecimal getMetricValue(SensorData data, String metricType) {
        return switch (metricType) {
            case "temperature" -> data.getTemperature();
            case "humidity" -> data.getHumidity();
            case "air_quality" -> data.getAirQuality() == null ? null : BigDecimal.valueOf(data.getAirQuality());
            default -> null;
        };
    }

    private boolean isTriggered(BigDecimal actual, AlertRule rule) {
        int cmp = actual.compareTo(rule.getThreshold());
        return ("GT".equals(rule.getOperator()) && cmp > 0)
                || ("LT".equals(rule.getOperator()) && cmp < 0);
    }

    private String buildMessage(Device device, AlertRule rule, BigDecimal actual) {
        return "设备[" + device.getDeviceName() + "] " + rule.getMetricType()
                + " 当前值 " + actual + " 超过阈值 " + rule.getThreshold();
    }
}
