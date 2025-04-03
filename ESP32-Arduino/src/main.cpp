#include <Arduino.h>

#include "DHT.h"
#include<HTTPClient.h>
#include<WiFi.h>
#include<string.h>
#include<time.h>

//wifi config
const char* ssid = "NamChan";
const char* password = "namDepZai123";

//dht config
#define DHTPin 12
#define DHTTYPE DHT22

DHT dht(DHTPin, DHTTYPE); // khoi chay bang dht.begin

//http url
String baseApiUrl = "http://192.168.63.201:8088/api/v1/";
String httpRequest(const char* url);

//data
struct Data
{
  float temp, humid;
  String deviceId = "ESP32";
  String timestamp;
};
Data data;

//time config
const char* ntpServer = "pool.ntp.org";
const long gmtOffset_sec = 25200;
const int daylightOffset_sec = 0;
String getISOTimestamp();

void setup() {
  Serial.begin(115200);

  dht.begin();

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  Serial.println("Connecting");

  while(WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nConnected to internet with IP address: ");
  Serial.println(WiFi.localIP());

  //time config
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);

  struct tm timeInfor;
  if(!getLocalTime(&timeInfor)){
    Serial.println("Failed set time");
  } else {
    Serial.println(&timeInfor, "Current time: %Y-%m-%d %H:%M:%S");
  }
}

void loop() {
  delay(60000); // gui 60s mot lan
  float temp = dht.readTemperature();
  float humid = dht.readHumidity();

  if(isnan(temp)){
    temp = NULL;

    // send request to user to control;

    Serial.println("DHT temperature sensor was failed");
  }

  if(isnan(humid)){
    humid = NULL;

    //send request to user to control

    Serial.println("DHT temperature sensor was failed");
  }

  data.temp = temp;
  data.humid = humid;
  data.timestamp = getISOTimestamp();

  // in ra man hinh
  Serial.print(F("Humidity: "));
  Serial.print(humid);
  Serial.println(" %");
  Serial.print(F("Temperature: "));
  Serial.print(temp);
  Serial.println(" oC");

  if(WiFi.status() == WL_CONNECTED){
    String tempUrl = baseApiUrl + "/temperature";
    String humidUrl = baseApiUrl + "/humidity";

    String tempResponse = httpRequest(tempUrl.c_str());
    String humidResponse = httpRequest(humidUrl.c_str());

    Serial.println(tempResponse);
    Serial.println(humidResponse);
  }

}

String getISOTimestamp(){
  struct tm timeInfor;
  if(!getLocalTime(&timeInfor)){
    Serial.println("failed to set time");
    return String(millis());
  }

  char timeString[30];
  //ISO 8601
  strftime(timeString, sizeof(timeString), "%Y-%m-%dT%H:%M:%SZ", &timeInfor);
  Serial.println(String(timeString));
  return String(timeString);
}

String httpRequest(const char* url){
  HTTPClient http;

  http.begin(url);
  http.addHeader("Content-Type", "application/json");

  String jsonBody = "{";
  jsonBody += "\"temp\":" + String(data.temp) + ",";
  jsonBody += "\"humid\":" + String(data.humid) + ",";
  jsonBody += "\"deviceId\":\"" + data.deviceId + "\",";
  jsonBody += "\"timestamp\":\"" + data.timestamp + "\"";
  jsonBody += "}";

  int responseCode = http.POST(jsonBody);
  String responseBody = "";
  if(responseCode > 0){
    Serial.print("HTTP response code: ");
    Serial.println(responseCode);
    responseBody = http.getString();
  }else{
    Serial.print("error on sending POST: ");
    Serial.println(responseCode);
  }

  http.end();

  return responseBody;
}