package com.iot.platform.mapper;

import com.iot.platform.entity.AlertRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AlertRecordMapper {

    int insert(AlertRecord record);

    List<AlertRecord> findPage(@Param("deviceId") Long deviceId, @Param("status") String status);

    int resolve(@Param("id") Long id);
}
