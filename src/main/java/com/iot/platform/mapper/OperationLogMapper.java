package com.iot.platform.mapper;

import com.iot.platform.entity.OperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OperationLogMapper {

    int insert(OperationLog log);

    List<OperationLog> findPage(@Param("module") String module);
}
