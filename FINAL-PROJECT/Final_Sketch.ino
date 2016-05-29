#include <Smartcar.h>
#include <NewPing.h>
Car car;
unsigned long time;

const int TRIGGER_PIN = 6; //D6
const int ECHO_PIN = 5; //D5
#define MAX_DISTANCE 100

NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);

void setup() {
  Serial3.begin(9600);
  Serial.begin(115200);
  car.begin(); //initialize the car using the encoders and the gyro

}

void loop() {
  String input;
  if (Serial.available()) {
    input = Serial.readStringUntil('\n'); //read everything that has been received so far and log down the last entry
    handleInput(input);
  }

  if (Serial3.available()) {
    input = Serial3.readStringUntil('\n'); //read everything that has been received so far and log down the last entry
    handleInput(input);
  }

  if (millis() - time > 500){
    car.setSpeed(0);
    car.setAngle(0);
  }
}

void handleInput(String input) { //handle serial input if there is any
  time = millis();

  unsigned int distance = sonar.ping() / 100;
   int setting = 20;
   
  if (input.startsWith("m")) {
    int throttle = input.substring(1).toInt();
    
    // Changes stopping distance based on speed of car
    if (throttle >= 90)
      setting = 40;
    else if (throttle >= 50 && throttle < 90)
      setting = 30;
    else if (throttle > 0 && throttle < 50)
      setting = 20;
      
          if (distance<16.0 && distance>0.0 && throttle>0) {
            throttle=0;
          }
    car.setSpeed(throttle);
  } else if (input.startsWith("t")) {
    int deg = input.substring(1).toInt();
    car.setAngle(deg);
  }

}
