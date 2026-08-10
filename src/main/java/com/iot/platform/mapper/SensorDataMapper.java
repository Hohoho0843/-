package com.iot.platform.mapper;

import com.iot.platform.entity.SensorData;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorDataMapper {

    int insert(SensorData sensorData);

    List<SensorData> findPage(@Param("deviceId") Long deviceId,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime);

    List<SensorData> findForExport(@Param("deviceId") Long deviceId,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);
}
