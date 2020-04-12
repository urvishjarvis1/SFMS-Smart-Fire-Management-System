/*
 *  ------Waspmote XBee ZigBee Sending & Receiving Example------
 *
 *  Explanation: This example shows how to send and receive packets
 *  using Waspmote XBee ZigBee API
 *
 *  This code sends a packet to another node and waits for an answer from
 *  it. When the answer is received it is shown.
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
 *  Version:                0.3
 *  Design:                 David Gascón
 *  Implementation:    Alberto Bielsa, Yuri Carmona
 */
 #define USB_RATE 38400
 packetXBee* paq_sent;
 int8_t state=0;
 long previous=0;
 char*  data="Test message!";
 
void setup()
{
  // Inits the XBee ZigBee library
  xbeeZB.init(ZIGBEE,FREQ2_4G,NORMAL);
  
  // Powers XBee
  xbeeZB.ON();
  
  xbeeZB.setAPSencryption(XBEE_ON);
  
  delay(2000);
}

void loop()
{
  // Set params to send
  paq_sent=(packetXBee*) calloc(1,sizeof(packetXBee)); 
  paq_sent->mode=UNICAST;
  paq_sent->MY_known=0;
  paq_sent->packetID=0x52;
  paq_sent->opt=0; 
  xbeeZB.hops=0;
  xbeeZB.setOriginParams(paq_sent, "5678", MY_TYPE);
  xbeeZB.setDestinationParams(paq_sent, "0013A2004077A0A9", data, MAC_TYPE,DATA_ABSOLUTE);
  xbeeZB.sendXBee(paq_sent);
  if( !xbeeZB.error_TX )
  {
    XBee.println("ok");
  }
  free(paq_sent);
  paq_sent=NULL;
  
  // Waiting the answer
  previous=millis();
  while( (millis()-previous) < 20000 )
  {
    if( XBee.available() )
    {
      xbeeZB.treatData();
      if( !xbeeZB.error_RX )
      {
        // Writing the parameters of the packet received
        while(xbeeZB.pos>0)
        {
          XBee.print("Network Address Source: ");
          XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->naS[0],HEX);
          XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->naS[1],HEX);
          XBee.println("");
          XBee.print("MAC Address Source: ");          
          for(int b=0;b<4;b++)
          {
            XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->macSH[b],HEX);
          }
          for(int c=0;c<4;c++)
          {
            XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->macSL[c],HEX);
          }
          XBee.println("");
          XBee.print("Network Address Origin: ");          
          XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->naO[0],HEX);
          XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->naO[1],HEX);
          XBee.println("");
          XBee.print("MAC Address Origin: ");          
          for(int d=0;d<4;d++)
          {
            XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->macOH[d],HEX);
          }
          for(int e=0;e<4;e++)
          {
            XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->macOL[e],HEX);
          }
          XBee.println("");
          XBee.print("RSSI: ");                    
          XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->RSSI,HEX);
          XBee.println("");         
          XBee.print("16B(0) or 64B(1): ");                    
          XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->mode,HEX);
          XBee.println("");
          XBee.print("Data: ");                    
          for(int f=0;f<xbeeZB.packet_finished[xbeeZB.pos-1]->data_length;f++)
          {
            XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->data[f],BYTE);
          }
          XBee.println("");
          XBee.print("PacketID: ");                    
          XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->packetID,HEX);
          XBee.println("");      
          XBee.print("Type Source ID: ");                              
          XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->typeSourceID,HEX);
          XBee.println("");     
          XBee.print("Network Identifier Origin: ");          
          for(int g=0;g<4;g++)
          {
            XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->niO[g],BYTE);
          }
          XBee.println("");  
          XBee.print("Source Destination: ");
          XBee.println(xbeeZB.packet_finished[xbeeZB.pos-1]->SD,HEX);
          XBee.print("Destination Endpoint: ");          
          XBee.println(xbeeZB.packet_finished[xbeeZB.pos-1]->DE,HEX);
          XBee.print("Cluster ID: ");          
          XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->CID[0],HEX);
          XBee.println(xbeeZB.packet_finished[xbeeZB.pos-1]->CID[1],HEX);
          XBee.print("Profile ID: ");          
          XBee.print(xbeeZB.packet_finished[xbeeZB.pos-1]->PID[0],HEX);
          XBee.println(xbeeZB.packet_finished[xbeeZB.pos-1]->PID[1],HEX);
          free(xbeeZB.packet_finished[xbeeZB.pos-1]);
          xbeeZB.packet_finished[xbeeZB.pos-1]=NULL;
          xbeeZB.pos--;
        }
        previous=millis();
      }
    }
  }
  
  delay(5000);
}


