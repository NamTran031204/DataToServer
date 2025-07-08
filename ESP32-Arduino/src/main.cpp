#include <Arduino.h>

#include "DHT.h"
#include<HTTPClient.h>
#include<WiFi.h>
#include<string.h>
#include<time.h>

// #include <Firebase.h>
#include <FirebaseESP32.h>
#include "firebase.h"

//wifi config
const char* ssid = "NamChan";
const char* password = "namDepZai";

//dht config
#define DHTPin 16
#define DHTTYPE DHT22

// Firebase config
FirebaseData fbdo;
FirebaseConfig config;
FirebaseAuth auth;
bool signupOK = false;

DHT dht(DHTPin, DHTTYPE); // khoi chay bang dht.begin

// //http url
// String baseApiUrl = "url";
// String httpRequest(const char* url);

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
  delay(5000);

  dht.begin();

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  Serial.println("Connecting");

  while(WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nConnected to internet with IP address: ");
  //Serial.println(WiFi.localIP());

  //time config
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);

  struct tm timeInfor;
  while (!getLocalTime(&timeInfor)) 
  {
    Serial.println("Waiting for time sync...");
    delay(1000);
  }
  Serial.println(&timeInfor, "Current time: %Y-%m-%d %H:%M:%S");


  config.api_key = FirebaseAuthToken;
  config.database_url = FirebaseHost;
  config.timeout.serverResponse = 10000;

  Serial.print("DEBUG: API Key from config: ");
  Serial.printf("%s\n",config.api_key.c_str());
  Serial.print("DEBUG: Database URL from config: ");
  Serial.printf("%s\n", config.database_url.c_str());

  if(Firebase.signUp(&config, &auth, "", "")){
    Serial.println("firebase authentication OK");
    signupOK = true;
  }else{
    Serial.printf("Firebase authentication failed: %s\n", config.signer.signupError.message.c_str());
  }

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void loop() {
  
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
  Serial.println(data.timestamp);

  // if(WiFi.status() == WL_CONNECTED){
  //   String tempUrl = baseApiUrl + "temperature";
  //   String humidUrl = baseApiUrl + "humidity";

  //   String tempResponse = httpRequest(tempUrl.c_str());
  //   String humidResponse = httpRequest(humidUrl.c_str());

  //   Serial.println(tempResponse);
  //   Serial.println(humidResponse);
  // }

  if(signupOK && Firebase.ready()){
    FirebaseJson json;
    json.set("temperature", data.temp);
    json.set("humidity", data.humid);
    json.set("deviceId", data.deviceId);
    json.set("timestamp", data.timestamp);

    String path = "/data-temp-humid";
    if(Firebase.push(fbdo, path, json)){
      Serial.println("Gửi dữ liệu thành công!");
      Serial.print("Push ID: ");
      Serial.println(fbdo.pushName());
    }else{
      Serial.print("Gửi dữ liệu thất bại, ");
      Serial.println(fbdo.errorReason());
    }
  }

  delay(60000); // gui 60s mot lan

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

// String httpRequest(const char* url){
//   HTTPClient http;

//   http.begin(url);
//   http.addHeader("Content-Type", "application/json");

//   String jsonBody = "{";
//   jsonBody += "\"temp\":" + String(data.temp) + ",";
//   jsonBody += "\"humid\":" + String(data.humid) + ",";
//   jsonBody += "\"deviceId\":\"" + data.deviceId + "\",";
//   jsonBody += "\"timestamp\":\"" + data.timestamp + "\"";
//   jsonBody += "}";

//   int responseCode = http.POST(jsonBody);
//   String responseBody = "";
//   if(responseCode > 0){
//     Serial.print("HTTP response code: ");
//     Serial.println(responseCode);
//     responseBody = http.getString();
//   }else{
//     Serial.print("error on sending POST: ");
//     Serial.println(responseCode);
//   }

//   http.end();

//   return responseBody;
// }