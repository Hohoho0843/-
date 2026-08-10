INSERT INTO device (device_code, device_name, device_type, location, status, last_online_time) VALUES
('DEV-001', '一号温湿度传感器', '温湿度传感器', 'A栋-101教室', 'ONLINE', CURRENT_TIMESTAMP),
('DEV-002', '二号空气质量监测仪', '空气质量监测仪', 'B栋-实验室', 'ONLINE', CURRENT_TIMESTAMP),
('DEV-003', '三号综合环境监测站', '综合监测站', 'C栋-机房', 'OFFLINE', DATEADD('DAY', -1, CURRENT_TIMESTAMP));

INSERT INTO alert_rule (device_id, metric_type, operator, threshold, enabled) VALUES
(NULL, 'temperature', 'GT', 35.00, 1),
(NULL, 'humidity', 'GT', 80.00, 1),
(NULL, 'air_quality', 'GT', 150.00, 1);

INSERT INTO sensor_data (device_id, temperature, humidity, air_quality, report_time) VALUES
(1, 26.50, 55.00, 68, DATEADD('MINUTE', -30, CURRENT_TIMESTAMP)),
(1, 27.10, 58.00, 72, DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
(1, 28.30, 60.00, 75, DATEADD('MINUTE', -10, CURRENT_TIMESTAMP)),
(2, 24.00, 48.00, 120, DATEADD('MINUTE', -25, CURRENT_TIMESTAMP)),
(2, 25.50, 50.00, 135, DATEADD('MINUTE', -15, CURRENT_TIMESTAMP)),
(2, 26.00, 52.00, 142, DATEADD('MINUTE', -5, CURRENT_TIMESTAMP));

INSERT INTO operation_log (module, action, detail, operator) VALUES
('设备管理', '注册设备', '注册设备 DEV-001 一号温湿度传感器', 'admin'),
('设备管理', '注册设备', '注册设备 DEV-002 二号空气质量监测仪', 'admin'),
('数据监控', '数据上报', '设备 DEV-001 上报传感器数据', 'system');
