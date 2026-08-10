package com.iot.platform.mapper;

import com.iot.platform.entity.AlertRule;

import java.util.List;

public interface AlertRuleMapper {

    int insert(AlertRule rule);

    List<AlertRule> findEnabledRules();
}
