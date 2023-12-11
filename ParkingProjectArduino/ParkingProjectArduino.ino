#include <ArduinoBearSSL.h>
#include <ArduinoECCX08.h>
#include <ArduinoMqttClient.h>
#include <WiFiNINA.h> // MKR1000를 위해 #include <WiFi101.h>로 변경 가능

#include "arduino_secrets.h"

#define LED_1_PIN 5

#include <ArduinoJson.h>
#include "Led.h"
#include <Servo.h>
#include <Wire.h>

/////// 민감한 데이터는 arduino_secrets.h에 입력합니다.
const char ssid[] = SECRET_SSID;           // WiFi SSID
const char pass[] = SECRET_PASS;           // WiFi 비밀번호
const char broker[] = SECRET_BROKER;       // MQTT 브로커 주소
const char *certificate = SECRET_CERTIFICATE; // 안전한 MQTT를 위한 SSL 인증서

WiFiClient wifiClient;             // TCP 소켓 연결용
BearSSLClient sslClient(wifiClient); // ECC508과 통합되는 SSL/TLS 연결용
MqttClient mqttClient(sslClient);

unsigned long lastMillis = 0; // 주기적인 MQTT 메시지를 위한 시간 추적 변수

Servo servo; // 서보 모터 인스턴스
int time = 0; // 시간 추적 변수
int fee = 3000; // 요금 변수
int light; // 빛의 세기 저장 변수

void setup()
{
  Serial.begin(115200);
  while (!Serial);

  if (!ECCX08.begin())
  {
    Serial.println("No ECCX08 present!");
    while (1);
  }

  ArduinoBearSSL.onGetTime(getTime);
  sslClient.setEccSlot(0, certificate);

  mqttClient.onMessage(onMessageReceived);

  servo.attach(8); // 서보 모터가 핀 8에 연결되어 있다고 가정
}

void loop()
{
  light = analogRead(A1);

  // WiFi가 연결되어 있지 않으면 연결을 시도합니다.
  if (WiFi.status() != WL_CONNECTED)
  {
    connectWiFi();
  }

  // MQTT 클라이언트가 연결되어 있지 않으면 연결을 시도합니다.
  if (!mqttClient.connected())
  {
    connectMQTT();
  }

  // 새로운 MQTT 메시지를 폴링하고 keep-alive를 전송합니다.
  mqttClient.poll();

  // 대략 5초마다 메시지를 발행합니다.
  if (millis() - lastMillis > 5000)
  {
    lastMillis = millis();
    char payload[512];
    getDeviceStatus(payload);
    sendMessage(payload);
  }

  // 빛의 강도에 따른 서보 제어
  if (light < 900)
  {
    servo.write(-30);
    time = 0;
    fee = 3000;
  }
  else
  {
    time += 1;
    if (time > 20)
    {
      fee += 1;
    }
    servo.write(30);
  }

  delay(1000);
}

// 현재 시간을 WiFi 모듈에서 가져오는 함수
unsigned long getTime()
{
  return WiFi.getTime();
}

// WiFi에 연결하는 함수
void connectWiFi()
{
  Serial.print("Attempting to connect to SSID: ");
  Serial.print(ssid);
  Serial.print(" ");

  while (WiFi.begin(ssid, pass) != WL_CONNECTED)
  {
    // 실패하면 재시도
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the network");
  Serial.println();
}

// MQTT 브로커에 연결하는 함수
void connectMQTT()
{
  Serial.print("Attempting to MQTT broker: ");
  Serial.print(broker);
  Serial.println(" ");

  while (!mqttClient.connect(broker, 8883))
  {
    // 실패하면 재시도
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the MQTT broker");
  Serial.println();

  // 특정 주제를 구독합니다.
  mqttClient.subscribe("$aws/things/MyMKRWiFi1010/shadow/update/delta");
}

// 디바이스 상태 페이로드를 작성하는 함수
void getDeviceStatus(char *payload)
{
  // 디바이스 업데이트 주제를 위한 페이로드 생성
  sprintf(payload, "{\"state\":{\"reported\":{\"light\":\"%d\",\"time\":\"%d\",\"fee\":\"%d\",\"servo\":\"%d\"}}}", light, time, fee, servo.read());
}

// MQTT 메시지를 보내는 함수
void sendMessage(char *payload)
{
  char TOPIC_NAME[] = "$aws/things/MyMKRWiFi1010/shadow/update";

  Serial.print("Publishing send message:");
  Serial.println(payload);
  mqttClient.beginMessage(TOPIC_NAME);
  mqttClient.print(payload);
  mqttClient.endMessage();
}

// MQTT 메시지를 받았을 때의 콜백 함수
void onMessageReceived(int messageSize)
{
  Serial.print("Received a message with topic '");
  Serial.print(mqttClient.messageTopic());
  Serial.print("', length ");
  Serial.print(messageSize);
  Serial.println(" bytes:");

  // 받은 메시지를 버퍼에 저장
  char buffer[512];
  int count = 0;
  while (mqttClient.available())
  {
    buffer[count++] = (char)mqttClient.read();
  }
  buffer[count] = '\0'; // 버퍼의 마지막 문자는 널 문자

  Serial.println(buffer);
  Serial.println();

  // 받은 JSON 메시지를 파싱
  DynamicJsonDocument doc(1024);
  deserializeJson(doc, buffer);
  JsonObject root = doc.as<JsonObject>();
  JsonObject state = root["state"];

  int receivedLight = state["light"];

  char payload[512];

  // 받은 빛 값에 따라 동작
  if (receivedLight == 0)
  {
    // 서보 모터를 움직임
    servo.write(-30);
    delay(10000);
  }
}
