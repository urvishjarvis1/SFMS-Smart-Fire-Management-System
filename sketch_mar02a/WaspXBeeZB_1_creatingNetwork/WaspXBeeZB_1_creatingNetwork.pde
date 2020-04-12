/*
 *  ------Waspmote XBee ZigBee Creating a Network Example------
 *
 *  Explanation: This example shows how to create a Network using Waspmote
 *  XBee ZigBee API
 *
 *  Note: XBee modules must be configured at 38400bps and with API enabled.
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
 *  Version:                0.2
 *  Design:                 David Gascón
 *  Implementation:    Alberto Bielsa
 */
#define USB_RATE 38400
uint8_t  PANID[8]={0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08};
char*  NETKEY="WaspmoteKey";
char*  LINKKEY="WaspmoteKey";
 
void setup()
{
  // Inits the XBee ZigBee library
  xbeeZB.init(ZIGBEE,FREQ2_4G,NORMAL);
  
  // Powers XBee
  xbeeZB.ON();
}

void loop()
{
  // Getting Channel
  xbee802.getChannel();
  if( !xbee802.error_AT ){
    XBee.print("Channel is: ");
    XBee.println(xbeeZB.channel,HEX);
  }
  else XBee.println("Error getting channel");  
  
  // Chosing a PANID : PANID=0x0102030405060708
  xbeeZB.setPAN(PANID);
  if( !xbeeZB.error_AT ) XBee.println("PANID set OK");
  else XBee.println("Error while changing PANID");  
  
  // Enabling security
  xbeeZB.encryptionMode(1);
  if( !xbeeZB.error_AT ) XBee.println("Security enabled");
  else XBee.println("Error while enabling security");  
  
  // Configuring Trust Center
  !xbeeZB.setEncryptionOptions(0x02);
  if( !xbeeZB.error_AT ) XBee.println("Security options configured");
  else XBee.println("Error while configuring security");  
  
  // Setting Link Key
  xbeeZB.setLinkKey(LINKKEY);
  if( !xbeeZB.error_AT ) XBee.println("Link Key set OK");
  else XBee.println("Error while setting Key"); 
  
 
  
  xbeeZB.setAPSencryption(XBEE_ON);
  if( !xbeeZB.error_AT ) XBee.println("APS Encryption set OK");
  else XBee.println("Error while setting APS Encryption");
  
  // Keep values
  xbeeZB.writeValues();
  if( !xbeeZB.error_AT ) XBee.println("Changes stored OK");
  else XBee.println("Error while storing values");  
  
  delay(3000);
}


