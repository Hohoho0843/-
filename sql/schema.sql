CREATE DATABASE IF NOT EXISTS iot_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE iot_platform;

CREATE TABLE IF NOT EXISTS device (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_code  VARCHAR(64)  NOT NULL UNIQUE COMMENT '设备编码',
    device_name  VARCHAR(128) NOT NULL COMMENT '设备名称',
    device_type  VARCHAR(64)  NOT NULL COMMENT '设备类型',
    location     VARCHAR(256) DEFAULT NULL COMMENT '安装位置',
    status       VARCHAR(16)  NOT NULL DEFAULT 'OFFLINE' COMMENT 'ONLINE/OFFLINE',
    last_online_time DATETIME DEFAULT NULL,
    create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '物联网设备表';

CREATE TABLE IF NOT EXISTS sensor_data (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_id    BIGINT NOT NULL,
    temperature  DECIMAL(5,2) DEFAULT NULL COMMENT '温度(℃)',
    humidity     DECIMAL(5,2) DEFAULT NULL COMMENT '湿度(%)',
    air_quality  INT DEFAULT NULL COMMENT '空气质量指数',
    report_time  DATETIME NOT NULL,
    create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_device_time (device_id, report_time)
) COMMENT '传感器数据表';

CREATE TABLE IF NOT EXISTS alert_rule (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_id    BIGINT DEFAULT NULL COMMENT 'NULL表示全局规则',
    metric_type  VARCHAR(32) NOT NULL COMMENT 'temperature/humidity/air_quality',
    operator     VARCHAR(8)  NOT NULL COMMENT 'GT/LT',
    threshold    DECIMAL(10,2) NOT NULL,
    enabled      TINYINT NOT NULL DEFAULT 1,
    create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '告警规则表';

CREATE TABLE IF NOT EXISTS alert_record (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_id    BIGINT NOT NULL,
    metric_type  VARCHAR(32) NOT NULL,
    actual_value DECIMAL(10,2) NOT NULL,
    threshold    DECIMAL(10,2) NOT NULL,
    message      VARCHAR(512) NOT NULL,
    status       VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/RESOLVED',
    create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolve_time DATETIME DEFAULT NULL,
    INDEX idx_device_status (device_id, status)
) COMMENT '告警记录表';

CREATE TABLE IF NOT EXISTS operation_log (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    module       VARCHAR(64) NOT NULL,
    action       VARCHAR(64) NOT NULL,
    detail       VARCHAR(512) DEFAULT NULL,
    operator     VARCHAR(64) DEFAULT 'system',
    create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_create_time (create_time)
) COMMENT '操作日志表';
