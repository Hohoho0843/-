package com.iot.platform.controller;

import com.iot.platform.common.PageResult;
import com.iot.platform.common.Result;
import com.iot.platform.dto.MqttSensorPayload;
import com.iot.platform.entity.SensorData;
import com.iot.platform.service.SensorDataService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sensor-data")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    public SensorDataController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @GetMapping
    public Result<PageResult<SensorData>> page(
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(sensorDataService.page(deviceId, startTime, endTime, pageNum, pageSize));
    }

    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            HttpServletResponse response) throws IOException {
        List<SensorData> list = sensorDataService.export(deviceId, startTime, endTime);
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=sensor_data.csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        PrintWriter writer = response.getWriter();
        writer.write('\ufeff');
        writer.println("设备编码,设备名称,温度,湿度,空气质量,上报时间");
        for (SensorData data : list) {
            writer.printf("%s,%s,%s,%s,%s,%s%n",
                    data.getDeviceCode(),
                    data.getDeviceName(),
                    data.getTemperature(),
                    data.getHumidity(),
                    data.getAirQuality(),
                    data.getReportTime());
        }
        writer.flush();
    }

    /** 设备传感器数据上报接口 */
    @PostMapping("/simulate")
    public Result<Void> simulate(@RequestBody MqttSensorPayload payload) {
        sensorDataService.saveFromMqtt(payload);
        return Result.success();
    }
}
