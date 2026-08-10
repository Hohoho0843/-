package com.iot.platform.controller;

import com.iot.platform.common.Result;
import com.iot.platform.entity.Device;
import com.iot.platform.mapper.AlertRecordMapper;
import com.iot.platform.mapper.DeviceMapper;
import com.iot.platform.mapper.SensorDataMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DeviceMapper deviceMapper;
    private final SensorDataMapper sensorDataMapper;
    private final AlertRecordMapper alertRecordMapper;

    public DashboardController(DeviceMapper deviceMapper,
                               SensorDataMapper sensorDataMapper,
                               AlertRecordMapper alertRecordMapper) {
        this.deviceMapper = deviceMapper;
        this.sensorDataMapper = sensorDataMapper;
        this.alertRecordMapper = alertRecordMapper;
    }

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        List<Device> devices = deviceMapper.findList(null, null);
        long online = devices.stream().filter(d -> "ONLINE".equals(d.getStatus())).count();
        long offline = devices.size() - online;
        long pendingAlerts = alertRecordMapper.findPage(null, "PENDING").size();
        long dataCount = sensorDataMapper.findPage(null, null, null).size();

        Map<String, Object> overview = new HashMap<>();
        overview.put("totalDevices", devices.size());
        overview.put("onlineDevices", online);
        overview.put("offlineDevices", offline);
        overview.put("pendingAlerts", pendingAlerts);
        overview.put("sensorDataCount", dataCount);
        overview.put("devices", devices);
        return Result.success(overview);
    }
}
