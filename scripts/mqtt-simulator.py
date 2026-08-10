#!/usr/bin/env python3
"""模拟物联网设备通过 MQTT 上报传感器数据"""

import json
import random
import time

try:
    import paho.mqtt.client as mqtt
except ImportError:
    print("请先安装: pip install paho-mqtt")
    raise

BROKER = "localhost"
PORT = 1883
DEVICES = ["DEV-001", "DEV-002"]


def publish_data(client, device_code):
    payload = {
        "deviceCode": device_code,
        "temperature": round(random.uniform(22, 38), 1),
        "humidity": round(random.uniform(40, 85), 1),
        "airQuality": random.randint(50, 180),
    }
    topic = f"iot/device/{device_code}/data"
    client.publish(topic, json.dumps(payload))
    print(f"[上报] {topic} -> {payload}")


def main():
    client = mqtt.Client()
    client.connect(BROKER, PORT, 60)
    print("MQTT 模拟器已启动，按 Ctrl+C 停止")
    try:
        while True:
            for code in DEVICES:
                publish_data(client, code)
            time.sleep(5)
    except KeyboardInterrupt:
        client.disconnect()
        print("\n已停止")


if __name__ == "__main__":
    main()
