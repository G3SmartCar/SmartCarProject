#include <Smartcar.h>
Car car;


void setup() {
  Serial3.begin(9600);
  car.begin(); //initialize the car using the encoders and the gyro
}

void loop() {
  handleInput();
}

void handleInput() { //handle serial input if there is any
  if (Serial3.available()) {
    String input = Serial3.readStringUntil('\n'); //read everything that has been received so far and log down the last entry
    if (input.startsWith("m")) {
      int throttle = input.substring(1).toInt();
      car.setSpeed(throttle);
    } else if (input.startsWith("t")) {
      int deg = input.substring(1).toInt();
      car.setAngle(deg);
    } else {
      car.setSpeed(0);
      car.setAngle(0);
    }
  }
}
