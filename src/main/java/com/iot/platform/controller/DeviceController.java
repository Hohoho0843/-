package com.iot.platform.controller;

import com.iot.platform.common.Result;
import com.iot.platform.dto.DeviceRegisterRequest;
import com.iot.platform.dto.DeviceUpdateRequest;
import com.iot.platform.entity.Device;
import com.iot.platform.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    public Result<Device> register(@Valid @RequestBody DeviceRegisterRequest request) {
        return Result.success(deviceService.register(request));
    }

    @GetMapping
    public Result<List<Device>> list(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) String status) {
        return Result.success(deviceService.list(keyword, status));
    }

    @GetMapping("/{id}")
    public Result<Device> detail(@PathVariable Long id) {
        return Result.success(deviceService.getById(id));
    }

    @PutMapping("/{id}")
    public Result<Device> update(@PathVariable Long id, @RequestBody DeviceUpdateRequest request) {
        return Result.success(deviceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deviceService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}/status")
    public Result<Map<String, Object>> status(@PathVariable Long id) {
        return Result.success(deviceService.getStatus(id));
    }
}
