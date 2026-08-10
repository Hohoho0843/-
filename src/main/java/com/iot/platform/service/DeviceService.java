package com.iot.platform.service;

import com.iot.platform.common.BusinessException;
import com.iot.platform.dto.DeviceRegisterRequest;
import com.iot.platform.dto.DeviceUpdateRequest;
import com.iot.platform.entity.Device;
import com.iot.platform.mapper.DeviceMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeviceService {

    private final DeviceMapper deviceMapper;
    private final OperationLogService operationLogService;

    @Value("${iot.device-offline-minutes:5}")
    private int offlineMinutes;

    public DeviceService(DeviceMapper deviceMapper, OperationLogService operationLogService) {
        this.deviceMapper = deviceMapper;
        this.operationLogService = operationLogService;
    }

    public Device register(DeviceRegisterRequest request) {
        if (deviceMapper.findByCode(request.getDeviceCode()) != null) {
            throw new BusinessException("设备编码已存在");
        }
        Device device = new Device();
        device.setDeviceCode(request.getDeviceCode());
        device.setDeviceName(request.getDeviceName());
        device.setDeviceType(request.getDeviceType());
        device.setLocation(request.getLocation());
        device.setStatus("OFFLINE");
        deviceMapper.insert(device);
        operationLogService.log("设备管理", "注册设备",
                "注册设备 " + device.getDeviceCode() + " " + device.getDeviceName(), "admin");
        return device;
    }

    public Device update(Long id, DeviceUpdateRequest request) {
        Device device = getById(id);
        if (request.getDeviceName() != null) {
            device.setDeviceName(request.getDeviceName());
        }
        if (request.getDeviceType() != null) {
            device.setDeviceType(request.getDeviceType());
        }
        if (request.getLocation() != null) {
            device.setLocation(request.getLocation());
        }
        deviceMapper.update(device);
        operationLogService.log("设备管理", "更新设备", "更新设备 " + device.getDeviceCode(), "admin");
        return deviceMapper.findById(id);
    }

    public void delete(Long id) {
        Device device = getById(id);
        deviceMapper.deleteById(id);
        operationLogService.log("设备管理", "删除设备", "删除设备 " + device.getDeviceCode(), "admin");
    }

    public Device getById(Long id) {
        Device device = deviceMapper.findById(id);
        if (device == null) {
            throw new BusinessException("设备不存在");
        }
        return device;
    }

    public Device findByCode(String deviceCode) {
        return deviceMapper.findByCode(deviceCode);
    }

    public List<Device> list(String keyword, String status) {
        return deviceMapper.findList(keyword, status);
    }

    public Map<String, Object> getStatus(Long id) {
        Device device = getById(id);
        Map<String, Object> status = new HashMap<>();
        status.put("deviceId", device.getId());
        status.put("deviceCode", device.getDeviceCode());
        status.put("deviceName", device.getDeviceName());
        status.put("status", device.getStatus());
        status.put("lastOnlineTime", device.getLastOnlineTime());
        status.put("offlineThresholdMinutes", offlineMinutes);
        return status;
    }

    public void markOnline(Long id, LocalDateTime time) {
        deviceMapper.updateStatus(id, "ONLINE", time);
    }

    public int markOfflineDevices() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(offlineMinutes);
        int count = deviceMapper.markOfflineBefore(deadline);
        if (count > 0) {
            operationLogService.log("设备管理", "离线检测",
                    "检测到 " + count + " 台设备超时离线", "system");
        }
        return count;
    }
}
