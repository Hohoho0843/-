package com.iot.platform.controller;

import com.iot.platform.common.PageResult;
import com.iot.platform.common.Result;
import com.iot.platform.entity.OperationLog;
import com.iot.platform.service.OperationLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public Result<PageResult<OperationLog>> page(
            @RequestParam(required = false) String module,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(operationLogService.page(module, pageNum, pageSize));
    }
}
