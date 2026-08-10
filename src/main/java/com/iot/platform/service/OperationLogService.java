package com.iot.platform.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iot.platform.common.PageResult;
import com.iot.platform.entity.OperationLog;
import com.iot.platform.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public OperationLogService(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    public void log(String module, String action, String detail, String operator) {
        OperationLog log = new OperationLog();
        log.setModule(module);
        log.setAction(action);
        log.setDetail(detail);
        log.setOperator(operator);
        operationLogMapper.insert(log);
    }

    public PageResult<OperationLog> page(String module, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OperationLog> list = operationLogMapper.findPage(module);
        PageInfo<OperationLog> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, list);
    }
}
