package com.iot.platform.scheduler;

import com.iot.platform.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeviceHeartbeatScheduler {

    private static final Logger log = LoggerFactory.getLogger(DeviceHeartbeatScheduler.class);

    private final DeviceService deviceService;

    public DeviceHeartbeatScheduler(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Scheduled(fixedRate = 60000)
    public void checkOfflineDevices() {
        try {
            int count = deviceService.markOfflineDevices();
            if (count > 0) {
                log.info("设备离线检测完成，标记 {} 台设备为 OFFLINE", count);
            }
        } catch (Exception e) {
            log.error("设备离线检测任务执行失败: {}", e.getMessage(), e);
        }
    }
}
