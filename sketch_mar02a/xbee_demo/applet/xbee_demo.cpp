/*
 *  ------Waspmote Demo Test 1--------
 *
 *  Explanation:This Demo shows the accelerometer working and sending
 *  messages using XBee modules
 *
 *  Copyright (C) 2009 Libelium Comunicaciones Distribuidas S.L.
 *  http://www.libelium.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Version:		0.1
 *  Design:		        David Gasc\u00f3n
 *  Implementation:	Alberto Bielsa
 */
 
 
#define USB_RATE 38400
#include "WProgram.h"
void setup();
void loop();
void sendData(uint8_t option);
int x_acc, y_acc, z_acc =0;
long previous=0;
char X[10];
char Y[10];
char Z[10];
packetXBee* paq_sent;
char data[40];
uint8_t state=2;
uint8_t counter=0;


#define NORMAL_OPTION  0
#define FREE_OPTION  1

void setup()
{  
  ACC.begin();    // opens I2C bus
  ACC.setMode(ACC_ON); // starts accelerometer
  ACC.setFF();
  
  xbee802.init(XBEE_802_15_4,FREQ2_4G,NORMAL); // init 'xbee802' object  
  xbee802.ON(); // opens UART and powers XBee
  
  previous=millis();
}

void loop()
{
  if( intFlag & ACC_INT )
  {
    intFlag &= ~(ACC_INT);    
    sendData(FREE_OPTION);
    ACC.setFF();
  }
  else if( (millis()-previous) > 100 )
  {
    sendData(NORMAL_OPTION);
    previous=millis();
  }
}

// sends a message changing it depending on the input option
void sendData(uint8_t option)
{
   paq_sent=(packetXBee*) calloc(1,sizeof(packetXBee)); 
   paq_sent->mode=UNICAST;
   paq_sent->MY_known=0;
   paq_sent->packetID=0x52;
   paq_sent->opt=0; 
   xbee802.hops=0;
   xbee802.setOriginParams(paq_sent, "ACC", NI_TYPE);
   
  switch(option)
  {
    case  NORMAL_OPTION :      x_acc=ACC.getX();
                               y_acc=ACC.getY();
                               z_acc=ACC.getZ();   
                                               
                               Utils.long2array(x_acc,X);
                               Utils.long2array(y_acc,Y);
                               Utils.long2array(z_acc,Z);   
                                               
                               sprintf(data,"***%u,%u,%s,%s,%s,$", PWR.getBatteryLevel(), 0, X, Y, Z);
                               break;
    case  FREE_OPTION :        x_acc=ACC.getX();
                               y_acc=ACC.getY();
                               z_acc=ACC.getZ();   
                                               
                               Utils.long2array(x_acc,X);
                               Utils.long2array(y_acc,Y);
                               Utils.long2array(z_acc,Z);
                                              
                               int auxReg = ACC.readRegister(FF_WU_SRC);
                               int acc=0;
                               if (auxReg & XHIE) acc+=1;
                               if (auxReg & YHIE) acc+=2;
                               if (auxReg & ZHIE) acc+=4;                                          
                                              
                               sprintf(data,"***%u,%u,%s,%s,%s,$", PWR.getBatteryLevel(), acc, X, Y, Z);
                               break;                                          
  }

   xbee802.setDestinationParams(paq_sent, "0013A200403D6C2F", data, MAC_TYPE, DATA_ABSOLUTE);
   while( counter<3 ) {
     state=xbee802.sendXBee(paq_sent);
     counter++;
   }
   counter=0;
   if(!state)
   {
     XBee.println("OK");
   }
   free(paq_sent);
   paq_sent=NULL;
}



int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

