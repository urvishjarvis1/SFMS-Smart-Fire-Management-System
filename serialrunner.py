import serial               
from matplotlib import pyplot as plt
import csv
import math
import paho.mqtt.publish as publish



with open('sensordata.csv', mode='w') as csv_file:
    fieldnames = ['co2', 'co']
    writer = csv.DictWriter(csv_file, fieldnames=fieldnames)
    writer.writeheader()
    
ser=serial.Serial('COM6',38400,timeout=20,parity=serial.PARITY_NONE, rtscts=1)
se=ser.read(100)
co2Val='null'
coVal='null'
while True:
    bytesToRead = ser.inWaiting()
    if(bytesToRead>0):
        se=ser.readline()
        val=se.decode("utf-8")
        print("val",val)
        val.rstrip()
        if 'CO2' in val:
            co2Val=val[5:]
            co2Val=float(co2Val)
            print("co2:",co2Val)
        if 'Co' in val:
            coVal=val[3:]
            coVal=float(coVal)
            print("co:",coVal)
            
        if(co2Val!='null' and coVal!='null'):
            if(not math.isnan(float(co2Val))  and not math.isnan(float(coVal))):    
                mydict=[{'topic':'co2','payload': float(co2Val)} , {'topic':'co','payload': float(coVal)}]
                publish.multiple(mydict,hostname="m10.cloudmqtt.com", port=10014, auth={'username':"kakuxfpm", 'password':"24WNZuLxQnyM"})
            else:
                print("null")




