# 物联网设备管理平台

基于 **Spring Boot 3 + MyBatis + MySQL/H2 + MQTT (Eclipse Paho)** 的物联网设备管理后台，支持设备注册、状态监控、传感器数据存储、阈值告警与操作日志，适合面试现场演示。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.2、Spring Web、Validation |
| 持久层 | MyBatis、PageHelper |
| 数据库 | H2（默认，零配置演示）/ MySQL 8 |
| 消息协议 | Eclipse Paho MQTT Client |
| 其他 | Lombok、定时任务 |

## 项目结构

```
iot-device-platform/
├── src/main/java/com/iot/platform/
│   ├── controller/     # REST API
│   ├── service/        # 业务逻辑
│   ├── mapper/         # MyBatis 接口
│   ├── entity/         # 实体类
│   ├── mqtt/           # MQTT 订阅
│   ├── scheduler/      # 设备离线检测
│   └── common/         # 统一响应、异常处理
├── src/main/resources/
│   ├── static/index.html   # 可视化演示页面
│   ├── mapper/*.xml        # SQL 映射
│   └── application.yml
├── sql/schema.sql          # MySQL 建表脚本
├── docker-compose.yml      # MySQL + Mosquitto
└── scripts/mqtt-simulator.py
```

## 快速启动（面试推荐：H2 内存库）

**环境要求：** JDK 17+、Maven 3.8+

```bash
cd iot-device-platform
mvn spring-boot:run
```

启动后访问：

- **演示大屏：** http://localhost:8080/index.html
- **H2 控制台：** http://localhost:8080/h2-console（JDBC URL: `jdbc:h2:mem:iot_platform`）

无需安装 MySQL 和 MQTT，即可完整演示业务流程。

## 核心 API

### 设备管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/devices` | 注册设备 |
| GET | `/api/devices` | 设备列表（支持 keyword、status 筛选） |
| GET | `/api/devices/{id}` | 设备详情 |
| GET | `/api/devices/{id}/status` | 在线状态查询 |
| PUT | `/api/devices/{id}` | 更新设备 |
| DELETE | `/api/devices/{id}` | 删除设备 |

### 传感器数据

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/sensor-data` | 分页查询（deviceId、时间段） |
| GET | `/api/sensor-data/export` | 导出 CSV |
| POST | `/api/sensor-data/simulate` | **模拟 MQTT 上报（演示专用）** |

### 告警 & 日志

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/alerts/records` | 告警记录分页 |
| PUT | `/api/alerts/records/{id}/resolve` | 处理告警 |
| GET | `/api/logs` | 操作日志分页 |
| GET | `/api/dashboard/overview` | 仪表盘概览 |

## 面试演示流程（约 5 分钟）

### 1. 介绍架构（30 秒）

> 设备通过 MQTT 上报 JSON 数据 → Paho 客户端订阅解析 → 写入 sensor_data 表 → 更新设备在线状态 → 匹配告警规则 → 记录操作日志。后台提供 RESTful API 供管理端查询和导出。

### 2. 打开演示页面（1 分钟）

访问 http://localhost:8080/index.html，展示：

- 设备总数 / 在线 / 离线 / 待处理告警
- 预置的 3 台设备和历史传感器数据

### 3. 模拟设备上报（1 分钟）

点击页面上的 **「模拟 DEV-002 上报」**，讲解：

- 等效于 MQTT 消息 `iot/device/DEV-002/data`
- 数据入库后设备状态变为 ONLINE
- 若空气质量 > 150，自动触发告警

### 4. 展示 API（1 分钟）

```bash
# 查询设备列表
curl http://localhost:8080/api/devices

# 分页查询历史数据
curl "http://localhost:8080/api/sensor-data?pageNum=1&pageSize=5"

# 导出 CSV
curl -O "http://localhost:8080/api/sensor-data/export"
```

### 5. 讲解亮点（1 分钟）

- **MQTT 解耦：** 硬件只负责 publish，平台 subscribe 入库
- **离线检测：** 定时任务扫描 `last_online_time`，超 5 分钟标记 OFFLINE
- **告警引擎：** 支持全局/单设备规则，GT/LT 阈值比较
- **操作审计：** 注册、上报、告警、离线检测均记录日志

## 完整环境（MySQL + MQTT）

```bash
# 启动 MySQL 和 Mosquitto
docker-compose up -d

# 使用 MySQL 配置启动
mvn spring-boot:run -Dspring-boot.run.profiles=mysql

# 模拟设备 MQTT 上报
pip install paho-mqtt
python scripts/mqtt-simulator.py
```

MQTT 消息格式：

```json
{
  "deviceCode": "DEV-001",
  "temperature": 26.5,
  "humidity": 55.0,
  "airQuality": 68
}
```

主题：`iot/device/{deviceCode}/data`

## 数据库表

- `device` — 设备信息及在线状态
- `sensor_data` — 温湿度、空气质量
- `alert_rule` — 告警规则
- `alert_record` — 告警记录
- `operation_log` — 操作日志

## 面试常见问题

**Q: 为什么用 MQTT 而不是 HTTP 轮询？**  
A: MQTT 轻量、支持 pub/sub，设备端只需上报一次，服务端实时接收，适合弱网络和大量设备场景。

**Q: 怎么判断设备离线？**  
A: 每次收到数据更新 `last_online_time`，定时任务将超过 5 分钟未上报的设备标记为 OFFLINE。

**Q: 告警怎么设计的？**  
A: `alert_rule` 表配置指标类型、比较符和阈值，数据入库后同步匹配规则，命中则写入 `alert_record`。

**Q: 如果 MQTT Broker 挂了怎么办？**  
A: Paho 客户端开启 `automaticReconnect`；本项目还提供 `/simulate` 接口和演示页面，保证无 Broker 时也能演示核心流程。

## 许可证

个人学习 / 面试展示项目
