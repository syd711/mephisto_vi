const int PUSH_BUTTON_DEBOUNCE = 160;

//status LED
const int STATUS_LED_APIN = A0;
// interval at which to blink (milliseconds)
const long blinkInterval = 300;  
int blinkLedState = LOW;      
unsigned long previousMillis = 0;     
boolean statusEnabled = false;
boolean statusReceived = false;
boolean blink = false;

//8-bit shift register
const int SER_Pin = 8;   //pin 14 on the 75HC595
const int RCLK_Pin = 9;  //pin 12 on the 75HC595
const int SRCLK_Pin = 10; //pin 11 on the 75HC595

#define numOfRegisterPins 8
boolean registers[numOfRegisterPins];
boolean registerState[6];

//rotary encoder stuff
const int encoderPin1 = 3;
const int encoderPin2 = 2;
const int ENCODER_BUTTON_PIN = 4; //push button switch
volatile int lastEncoded = 0;
int encoderValue = 0;
long lastencoderValue = 0;
int lastMSB = 0;
int lastLSB = 0;
int encoderButtonState = 0;

//push buttons
const int PUSH_BUTTON_APIN_1 = A1;
const int PUSH_BUTTON_APIN_2 = A2;
const int PUSH_BUTTON_APIN_3 = A3;
const int PUSH_BUTTON_APIN_4 = A4;
int lastButtonStates[4];

//switch buttons
const int SWITCH_BUTTON_PIN_1 = 11;
const int SWITCH_BUTTON_PIN_2 = 12;
const int SWITCH_BUTTON_PIN_3 = 13;
int switchStates[3];
boolean dirtySwitchState = true;

//switch LEDs
const int SWITCH_LED_PIN_1 = 7;
const int SWITCH_LED_PIN_2 = 6;
const int SWITCH_LED_PIN_3 = 5;

/*
 * 2  = ROTARY ENCODER 1
 * 3  = ROTARY ENCODER 2
 * 4  = ROTARY ENCODER PUSH BUTTON
 * 5  = Switch LED 1
 * 6  = Switch LED 2
 * 7  = Switch LED 3
 * 8  = Shift Register 1
 * 9  = Shift Register 2
 * 10 = Shift Register 3
 * 11 = Switch 1
 * 12 = Switch 2
 * 13 = Switch 3
 * A0 = Status LED
 * A1 = Push Button 1
 * A2 = Push Button 2
 * A3 = Push Button 3
 * A4 = Push Button 4 
 */

/*********** Command List *************/
const String CMD_STATUS_AVAILABLE = "status:true";
const String CMD_MONITORING = "monitoring";
const String CMD_BLINK = "blink";


void setup(){
  Serial.begin(9600);  
  pinMode(STATUS_LED_APIN, OUTPUT);

  //rotary encoder
  pinMode(ENCODER_BUTTON_PIN, INPUT);
  pinMode(encoderPin2, INPUT); 
  pinMode(encoderPin1, INPUT);
  digitalWrite(encoderPin1, HIGH); //turn pullup resistor on
  digitalWrite(encoderPin2, HIGH); //turn pullup resistor on
  
  //call updateEncoder() when any high/low changed seen
  attachInterrupt(digitalPinToInterrupt(encoderPin1), updateEncoder, CHANGE); 
  attachInterrupt(digitalPinToInterrupt(encoderPin2), updateEncoder, CHANGE);

  //8-bit shift register init
  pinMode(SER_Pin, OUTPUT);
  pinMode(RCLK_Pin, OUTPUT);
  pinMode(SRCLK_Pin, OUTPUT);

  //reset all register pins
  clearRegisters();
  writeRegisters();

  //push buttons
  pinMode(PUSH_BUTTON_APIN_1, INPUT);
  pinMode(PUSH_BUTTON_APIN_2, INPUT);
  pinMode(PUSH_BUTTON_APIN_3, INPUT);
  pinMode(PUSH_BUTTON_APIN_4, INPUT);
  
  //switch buttons
  pinMode(SWITCH_BUTTON_PIN_1, INPUT);
  pinMode(SWITCH_BUTTON_PIN_2, INPUT);
  pinMode(SWITCH_BUTTON_PIN_3, INPUT);
  
  //set the initial state to skip first event
  switchStates[0] = digitalRead(SWITCH_BUTTON_PIN_1);
  switchStates[1] = digitalRead(SWITCH_BUTTON_PIN_2);
  switchStates[2] = digitalRead(SWITCH_BUTTON_PIN_3);

  //Serial write of the initial switch button state
  writeSwitchState(digitalRead(SWITCH_BUTTON_PIN_1), "SWITCH_3", SWITCH_BUTTON_PIN_1, true);
  delay(100);
  writeSwitchState(digitalRead(SWITCH_BUTTON_PIN_2), "SWITCH_2", SWITCH_BUTTON_PIN_2, true);
  delay(100);
  writeSwitchState(digitalRead(SWITCH_BUTTON_PIN_3), "SWITCH_1", SWITCH_BUTTON_PIN_3, true);

  //switch LEDs
  pinMode(SWITCH_LED_PIN_1, OUTPUT);
  pinMode(SWITCH_LED_PIN_2, OUTPUT);
  pinMode(SWITCH_LED_PIN_3, OUTPUT);
}


/******************************************
 * Main Loop
 ******************************************/
void loop(){
  //check if data is available
  listenForUpdates();
  
  //checkStatus LED
  readStatus();

  //check blink state
  updateBlink();
 
  //read 4x push buttons
  readAnalogPushButton(0, PUSH_BUTTON_APIN_1, "F3_PUSH_BUTTON");
  readAnalogPushButton(1, PUSH_BUTTON_APIN_2, "F2_PUSH_BUTTON");
  readAnalogPushButton(2, PUSH_BUTTON_APIN_3, "F1_PUSH_BUTTON");
  readAnalogPushButton(3, PUSH_BUTTON_APIN_4, "PIPELINE_PUSH_BUTTON");
  
  //read rotary encoder button
  readRotaryPushButton(ENCODER_BUTTON_PIN, "ROTARY_ENCODER");

  //read 3x switches
  readSwitch(0, SWITCH_BUTTON_PIN_1, "SWITCH_3");
  readSwitch(1, SWITCH_BUTTON_PIN_2, "SWITCH_2");
  readSwitch(2, SWITCH_BUTTON_PIN_3, "SWITCH_1");

  //update 3x led of the switches
  updateSwitchLeds();

  //pipeline monitoring
  updatePipelineStatus();
}

/**
 * Checks if the LED should blink.
 */
void updateBlink() {
  if(blink) {
     unsigned long currentMillis = millis();
       if (currentMillis - previousMillis >= blinkInterval) {
      // save the last time you blinked the LED
      previousMillis = currentMillis;
  
      // if the LED is off turn it on and vice-versa:
      if (blinkLedState == LOW) {
        blinkLedState = HIGH;
      } else {
        blinkLedState = LOW;
      }
  
      // set the LED with the ledState of the variable:
      digitalWrite(STATUS_LED_APIN, blinkLedState);
    }
  }
}

/**
 * Checks if the connection with the main program was established.
 * True, if a serial command with the connection state was received.
 */
void readStatus() {
  if(!statusEnabled && statusReceived) {
    digitalWrite(STATUS_LED_APIN, HIGH);
    statusEnabled = true;
  }
}

/**
 * Checks if a switch is HIGH, so
 * update the corresponding LED state.
 */
void updateSwitchLeds() {
  if(dirtySwitchState) {
    digitalWrite(SWITCH_LED_PIN_1, switchStates[0]);
    digitalWrite(SWITCH_LED_PIN_2, switchStates[1]);
    digitalWrite(SWITCH_LED_PIN_3, switchStates[2]);
    dirtySwitchState = false;
  }
}

/**
 * Reads the given switch state
 * and checks if the state has changed.
 * If true, a serial command string is written
 * to notify the change.
 */
void readSwitch(int index, int pin, String source) {
  int state = LOW;
  if(digitalRead(pin)) {
    state = HIGH;
  }
  int lastState = switchStates[index];
  if(lastState != state) {
    dirtySwitchState = true;
    switchStates[index] = state;
    writeSwitchState(state, source, pin, false);
  }
}

/**
 * Writes the serial command string for the switch state.
 */
void writeSwitchState(int state, String source, int pin, boolean silent) {
  String stateString = "ON";
  if(state == 0) {
    stateString = "OFF";
  }
  String cmd = "{source:'" + source + "', event:'" + stateString + "', pin:" + String(pin) + ", silent: " + String(silent) + "}";
  Serial.println(cmd);
}

/**
 * Checks if a pipeline status has changed 
 * and updates the corresponding LED.
 */
void updatePipelineStatus() {
  for(int i=1; i<=6; i++) {
    setRegisterPin(i, registerState[i-1]);
  }
  writeRegisters();
}

/**
 * Debounces reading for a push button event.
 * If the button has pressed, a serial command
 * string is written to notify the change.
 */
void readRotaryPushButton(int pin, String source) {
  if (digitalRead(ENCODER_BUTTON_PIN)) {  
    encoderButtonState = 1;
  }
  else {
    if(encoderButtonState == 1) {
      encoderButtonState = 0;
      String cmd = "{source:'" + source + "', event:'PUSH', pin:" + String(pin) + "}";
      Serial.println(cmd); 
    }
  }
}

/**
 * Debounces reading for a push button event.
 * If the button has pressed, a serial command
 * string is written to notify the change.
 */
void readAnalogPushButton(int index, int pin, String source) {
  int value = analogRead(pin);
  if (value > 1000) { 
    lastButtonStates[index] = 1;    
  }  
  else {
    if(lastButtonStates[index] == 1) {
      lastButtonStates[index] = 0;
      String cmd = "{source:'" + source + "', event:'PUSH', pin:" + String(pin) + ", value: 'ON'}";
      Serial.println(cmd); 
    }
  }
}

/**
 * Checks if new commands have arrived 
 * on the serial command port.
 * If true, the commands will be parsed
 * and the data structes updated for the next loop.
 */
void listenForUpdates() {
  String command = "";
  if(Serial.available() > 0) {
    command = Serial.readStringUntil('|');    
  }

  if(command.length() > 0) {
    String commandToken = tokenize(command, ':', 0);
    
    if(command == CMD_STATUS_AVAILABLE) {
      statusReceived = true;
      Serial.println("{'message':'I have enabled the status LED'}");
    }
    else if(commandToken == CMD_MONITORING) {
      int index = tokenize(command, ':', 1).toInt();
      String statusToken = tokenize(command, ':', 2);
      boolean enabled = statusToken == "true";

      registerState[index-1] = enabled;
      Serial.println("{'message':'" + command + "'}");
    }
    else if(commandToken == CMD_BLINK) {
      blink = tokenize(command, ':', 1).toInt();
      if(blink == 0) {
        digitalWrite(STATUS_LED_APIN, HIGH);
        previousMillis = 0;
      }
      Serial.println("{'message':'" + command + "'}");
    }
  }
}

//-------------------- Helper -----------------------------------------------

String tokenize(String data, char separator, int index) {
  int found = 0;
  int strIndex[] = { 0, -1  };
  int maxIndex = data.length()-1;
  for(int i=0; i<=maxIndex && found<=index; i++){
    if(data.charAt(i)==separator || i==maxIndex){
      found++;
      strIndex[0] = strIndex[1]+1;
      strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
   }
 return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}

//set all register pins to LOW
void clearRegisters(){
  for(int i = numOfRegisterPins - 1; i >=  0; i--){
     registers[i] = LOW;
  }
} 

/**
 * Updates an LED of the shift register
 */
void setRegisterPin(int index, int value){
  registers[index] = value;
}



//Set and display registers
//Only call AFTER all values are set how you would like (slow otherwise)
void writeRegisters(){
  digitalWrite(RCLK_Pin, LOW);
  for(int i = numOfRegisterPins - 1; i >=  0; i--){
    digitalWrite(SRCLK_Pin, LOW);
    int val = registers[i];

    digitalWrite(SER_Pin, val);
    digitalWrite(SRCLK_Pin, HIGH);

  }
  digitalWrite(RCLK_Pin, HIGH);
}



/**
 * The rotary encoder implementation
 */
void updateEncoder(){
  int MSB = digitalRead(encoderPin1); //MSB = most significant bit
  int LSB = digitalRead(encoderPin2); //LSB = least significant bit

  int encoded = (MSB << 1) |LSB; //converting the 2 pin value to single number
  int sum  = (lastEncoded << 2) | encoded; //adding it to the previous encoded value

  //if(sum == 13 || sum == 4 || sum == 2 || sum == 11) encoderValue ++;
  if(sum == 2){
    encoderValue --;//skip updates

    String value = String(encoderValue);
    String cmd = "{source:'ROTARY_ENCODER', event:'ROTATE_LEFT'}";
    Serial.println(cmd);
  }
  //if(sum == 14 || sum == 7 || sum == 1 || sum == 8 ) encoderValue --;
  if(sum == 1) {
    encoderValue ++;//skip updates

    String value = String(encoderValue);
    String cmd = "{source:'ROTARY_ENCODER', event:'ROTATE_RIGHT'}";
    Serial.println(cmd);
  }

  lastEncoded = encoded; //store this value for next time  
}  
