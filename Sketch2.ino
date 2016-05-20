#include <Smartcar.h>
Car car;
SR04 ultrasonicSensor;
const int TRIGGER_PIN = 6; //D6
const int ECHO_PIN = 5; //D5


void setup() {
  Serial3.begin(9600);
  Serial.begin(9600);
  car.begin(); //initialize the car using the encoders and the gyro
  ultrasonicSensor.attach(TRIGGER_PIN, ECHO_PIN);

}

void loop() {
    String input;
   if (Serial3.available()) {
    input = Serial3.readStringUntil('\n'); //read everything that has been received so far and log down the last entry
   }
   else
    if (Serial.available()) {
    input = Serial3.readStringUntil('\n'); //read everything that has been received so far and log down the last entry
   }
  handleInput(input);
}

void handleInput(String input) { //handle serial input if there is any
    if (input.startsWith("m")) {
      int throttle = input.substring(1).toInt();
      if (ultrasonicSensor.getDistance()<15 && throttle>0) {
        throttle=0;
      } 
      car.setSpeed(throttle);
    } else if (input.startsWith("t")) {
      int deg = input.substring(1).toInt();
      car.setAngle(deg);
    } else {
      car.setSpeed(0);
      car.setAngle(0);
    }
  }
