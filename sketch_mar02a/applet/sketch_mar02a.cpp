#define USB_RATE 38400

#define GAIN_CO2  7       // GAIN of the CO2 sensor stage
#define GAIN  1          // GAIN of the CO sensor stage
#define RESISTOR 100    // LOAD RESISTOR of the sensor stage

// INTRODUCE IN THE NEXT ARRAY THE CONCENTRATION coCalibrationOutput
#include "WProgram.h"
void setup();
void loop();
float calculateResistance(uint16_t sensor, float value, uint8_t gain, float load);
float calculateConcentration(float coCalibrationConcentration[],int coCalibrationOutput[],float input);
int coCalibrationConcentration[3] = {
  50,100,300};

// INTRODUCE IN THE NEXT ARRAY THE CALIBRATION OUTPUT OF THE SENSORS
float coCalibrationOutput[3] = {
  114.3312 , 40.2036 , 11.7751};
void setup()
{
  // Turn on the USB and print a start message
  USB.begin();
  USB.println("start monitoring");
  delay(100);
  RTC.ON();


  // Configure the CO2 sensor socket
  SensorGas.configureSensor(SENS_CO2, GAIN_CO2);

  // Configure the CO sensor on socket 4
  SensorGas.configureSensor(SENS_SOCKET4B,GAIN,RESISTOR);
}

void loop()
{
  float co2Val,coVal;
  float co2inc=0,coInc=0;
  // Turn on the sensor board
  //SensorGas.setBoardMode(SENS_ON);
  //USB.println("start");  


  // Turn on the CO2 sensor and wait for stabilization and
  // sensor response time
  SensorGas.setSensorMode(SENS_ON, SENS_CO2);
  delay(10000);

  //USB.println("start 2");
  // Read the sensor 
  co2Val = SensorGas.readValue(SENS_CO2);
  delay(10000);
  SensorGas.setSensorMode (SENS_OFF,SENS_CO2);

  coVal=SensorGas.readValue(SENS_SOCKET4B);

  //USB.println("start 3");

  // Print the result through the USB
  //  USB.print("CO2: ");
  //  USB.print(co2Val);
  //  USB.println("V");
  //  USB.print("CO: ");
  //  USB.print(coVal);
  //  USB.println("V");  

  // Conversion from voltage into ppm 
  co2Val=pow(10, ((co2Val + 158.631) / 62.877));
  if(co2inc==0){
  	co2inc=co2Val;
  }else{
  	co2inc=co2inc+100;
  }

  USB.print("CO2: ");
  USB.print(co2inc);
  USB.println(" ");

  coVal = calculateResistance(SENS_SOCKET4B, coVal, GAIN, RESISTOR);
  if(coInc==0){
  	coInc=coVal;
  }else{
  	coInc=coInc+2;
  }
  //  USB.print("CO Res: ");
  //  USB.print(coVal);
  //  USB.println("ohm");

  coVal = calculateConcentration(coCalibrationOutput,coCalibrationConcentration,coVal);
  USB.print("Co:");
  USB.print(coVal); 
  USB.println(" ");

  if(coVal<5.0 && co2Val<350.0){
    Utils.setLED(LED0,LED_OFF);
    Utils.setLED(LED1,LED_ON);
  }
  else{
    Utils.setLED(LED1,LED_OFF);
    Utils.setLED(LED0,LED_ON);
  }
}

float calculateResistance(uint16_t sensor, float value, uint8_t gain, float load)
{
  float resistor;
  float realGain;
  float realLoad;
  float aux;
  int aux2;

  if( (load < 100) || (load > 0) )
  {
    aux = 128*load;
    aux = aux/100;
    aux2 = (uint8_t) 128-aux;
    aux2 = 128 - aux2;

    // Calculate the number of steps of the digipot
    // Multiplicate the number of steps by the approximate step resistance and
    // add the approximate wipper resistance
    realLoad = aux2 * 0.781 + 0.12;
  } 
  else
  {
    // Return error if load resistor is out of range
    return -1;
  }

  if( (gain < 101) || (gain > 1) )
  {
    // Calculate the number of steps of the digipot
    aux2 = int((gain - 1) * 128 / 100);

    // Add the gain according to the number of steps of the resistor and the
    // wipper approximate resistance
    realGain = (1 + 0.12 + 0.781 * aux2);           
  } 
  else
  {
    // Return error if gain is out of range
    return -2;
  }

  aux = value / realGain;

  switch ( sensor )
  {
  case    SENS_SOCKET4B :       
    resistor = realLoad*(5 - aux)/aux;
    break;
  case    SENS_SOCKET2B   :       
    resistor = realLoad*(2.5 - aux)/aux;
    break;
  case    SENS_SOCKET3B   :       
    resistor = realLoad*(1.8 - aux)/aux;
    break;
  default: 
    return -3;
  }

  return resistor;
}

float calculateConcentration(float coCalibrationConcentration[],int coCalibrationOutput[],float input){
  bool inRange = false; 
  int i = 0;
  int numPoints=3;
  // This loop is to find the range where the input is located
  while ((!inRange) && (i < (numPoints-1))) {

    if ((input > coCalibrationOutput[i]) && (input <= coCalibrationOutput[i + 1]))
      inRange = true;
    else if ((input <= coCalibrationOutput[i]) && (input > coCalibrationOutput[i+1]))
      inRange = true;
    else
      i++;
  }

  float temp_slope = 0.0;
  float temp_intersection = 0.0;
  float concentration = 0.0;

  // If the voltage input is in a range, we calculate in the slope 
  // and the intersection of the logaritmic function
  if (inRange) 
  {
    // Slope of the logarithmic function 
    temp_slope = (coCalibrationOutput[i] - coCalibrationOutput[i+1]) / (log10(coCalibrationConcentration[i]) - log10(coCalibrationConcentration[i+1]));
    // Intersection of the logarithmic function
    temp_intersection = coCalibrationOutput[i] - temp_slope * log10(coCalibrationConcentration[i]);	
  }
  // Else, we calculate the logarithmic function with the nearest point
  else
  {
    if (fabs(input - coCalibrationOutput[0]) < fabs(input - coCalibrationOutput[numPoints-1])) {
      // Slope of the logarithmic function
      temp_slope = (coCalibrationOutput[1] - coCalibrationOutput[0]) / (log10(coCalibrationConcentration[1]) - log10(coCalibrationConcentration[0]));
      // Intersection of the logarithmic function
      temp_intersection = coCalibrationOutput[0] - temp_slope * log10(coCalibrationConcentration[0]);
    } 
    else {
      // Slope of the logarithmic function
      temp_slope = (coCalibrationOutput[numPoints-1] - coCalibrationOutput[numPoints-2]) / (log10(coCalibrationConcentration[numPoints-1]) - log10(coCalibrationConcentration[numPoints-2]));
      // Intersection of the logarithmic function
      temp_intersection = coCalibrationOutput[numPoints-1] - temp_slope * log10(coCalibrationConcentration[numPoints-1]);
    }
  }

  // Return the value of the concetration
  concentration = pow(10, ((input - temp_intersection) / temp_slope));

  if (concentration < 99999.9)
  {
    return concentration;
  }
  else 
  {
#if DEBUG_GASES > 1
    PRINTLN_GASES("Concentration out of range");
#endif

    return -1.0;
  } 
}




int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

