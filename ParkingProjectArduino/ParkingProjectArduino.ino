#include <ArduinoBearSSL.h>
#include <ArduinoECCX08.h>
#include <ArduinoMqttClient.h>
#include <WiFiNINA.h> // Change to #include <WiFi101.h> for MKR1000

#include "arduino_secrets.h"


#define LED_1_PIN 5

#include <ArduinoJson.h>
#include "Led.h"
#include <Servo.h>
#include <Wire.h>

/////// Enter your sensitive data in arduino_secrets.h
const char ssid[] = SECRET_SSID;
const char pass[] = SECRET_PASS;
const char broker[] = SECRET_BROKER;
const char *certificate = SECRET_CERTIFICATE;

WiFiClient wifiClient;            // Used for the TCP socket connection
BearSSLClient sslClient(wifiClient); // Used for SSL/TLS connection, integrates with ECC508
MqttClient mqttClient(sslClient);

unsigned long lastMillis = 0;

Servo servo;
int time = 0;
int fee = 3000;
int light;


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

  servo.attach(8); // Assuming the servo is connected to pin 8
}

void loop()
{
  light= analogRead(A1);
  if (WiFi.status() != WL_CONNECTED)
  {
    connectWiFi();
  }

  if (!mqttClient.connected())
  {
    // MQTT client is disconnected, connect
    connectMQTT();
  }

  // Poll for new MQTT messages and send keep-alives
  mqttClient.poll();

  // Publish a message roughly every 5 seconds.
  if (millis() - lastMillis > 5000)
  {
    lastMillis = millis();
    char payload[512];
    getDeviceStatus(payload);
    sendMessage(payload);
  }

  // Servo control
  if (light < 900 )
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

unsigned long getTime()
{
  // Get the current time from the WiFi module
  return WiFi.getTime();
}

void connectWiFi()
{
  Serial.print("Attempting to connect to SSID: ");
  Serial.print(ssid);
  Serial.print(" ");

  while (WiFi.begin(ssid, pass) != WL_CONNECTED)
  {
    // Failed, retry
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the network");
  Serial.println();
}

void connectMQTT()
{
  Serial.print("Attempting to MQTT broker: ");
  Serial.print(broker);
  Serial.println(" ");

  while (!mqttClient.connect(broker, 8883))
  {
    // Failed, retry
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the MQTT broker");
  Serial.println();

  // Subscribe to a topic
  mqttClient.subscribe("$aws/things/MyMKRWiFi1010/shadow/update/delta");
}

void getDeviceStatus(char *payload)
{
  // Read temperature as Celsius (the default)
  //float t = dht.readTemperature();

  // Make payload for the device update topic ($aws/things/MyMKRWiFi1010/shadow/update)
  sprintf(payload, "{\"state\":{\"reported\":{\"light\":\"%d\",\"time\":\"%d\",\"fee\":\"%d\",\"servo\":\"%d\"}}}", light, time, fee, servo.read());
}

void sendMessage(char *payload)
{
  char TOPIC_NAME[] = "$aws/things/MyMKRWiFi1010/shadow/update";

  Serial.print("Publishing send message:");
  Serial.println(payload);
  mqttClient.beginMessage(TOPIC_NAME);
  mqttClient.print(payload);
  mqttClient.endMessage();
}

void onMessageReceived(int messageSize)
{
  // We received a message, print out the topic and contents
  Serial.print("Received a message with topic '");
  Serial.print(mqttClient.messageTopic());
  Serial.print("', length ");
  Serial.print(messageSize);
  Serial.println(" bytes:");

  // Store the message received to the buffer
  char buffer[512];
  int count = 0;
  while (mqttClient.available())
  {
    buffer[count++] = (char)mqttClient.read();
  }
  buffer[count] = '\0'; // Buffer's last character is a null character
  Serial.println(buffer);
  Serial.println();

  DynamicJsonDocument doc(1024);
  deserializeJson(doc, buffer);
  JsonObject root = doc.as<JsonObject>();
  JsonObject state = root["state"];

  int receivedLight = state["light"];

  char payload[512];

  if (receivedLight == 0)
  {
    // Move the servo motor
    servo.write(-30);
    delay(10000);
  }
}


