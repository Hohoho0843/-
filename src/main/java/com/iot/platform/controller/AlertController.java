package com.iot.platform.controller;

import com.iot.platform.common.PageResult;
import com.iot.platform.common.Result;
import com.iot.platform.dto.AlertRuleRequest;
import com.iot.platform.entity.AlertRecord;
import com.iot.platform.entity.AlertRule;
import com.iot.platform.service.AlertService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping("/rules")
    public Result<AlertRule> createRule(@Valid @RequestBody AlertRuleRequest request) {
        return Result.success(alertService.createRule(request));
    }

    @GetMapping("/rules")
    public Result<List<AlertRule>> listRules() {
        return Result.success(alertService.listRules());
    }

    @GetMapping("/records")
    public Result<PageResult<AlertRecord>> pageRecords(
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(alertService.pageRecords(deviceId, status, pageNum, pageSize));
    }

    @PutMapping("/records/{id}/resolve")
    public Result<Void> resolve(@PathVariable Long id) {
        alertService.resolve(id);
        return Result.success();
    }
}
