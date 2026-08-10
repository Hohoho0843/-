package com.iot.platform.mapper;

import com.iot.platform.entity.Device;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DeviceMapper {

    int insert(Device device);

    int update(Device device);

    int deleteById(Long id);

    Device findById(Long id);

    Device findByCode(String deviceCode);

    List<Device> findList(@Param("keyword") String keyword, @Param("status") String status);

    int updateStatus(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("lastOnlineTime") LocalDateTime lastOnlineTime);

    int markOfflineBefore(@Param("deadline") LocalDateTime deadline);
}
