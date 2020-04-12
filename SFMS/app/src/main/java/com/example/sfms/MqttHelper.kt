package com.example.sfms

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class  MqttHelper(application: Context) {
    private lateinit var mqttAndroidClient: MqttAndroidClient
    private lateinit var application:Context
    private var callback: DataAvailable?=null
    fun connect(application: Context){
        this.application=application
        mqttAndroidClient= MqttAndroidClient(application.applicationContext,"tcp://m10.cloudmqtt.com:10014","androidapp")
        try{
            var options=MqttConnectOptions()
            options.userName="kakuxfpm"
            options.password="24WNZuLxQnyM".toCharArray()
            val token = mqttAndroidClient.connect(options)
            token.actionCallback=object : IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("TAG0","Mqtt Success!")
                    Toast.makeText(application,"Mqtt Connection established!",Toast.LENGTH_LONG).show()
                    subscribeToTopic("co")
                    subscribeToTopic("co2")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("TAG0","Mqtt Failed!")
                    Toast.makeText(application,"Mqtt Connection failed!",Toast.LENGTH_LONG).show()
                }
            }

        }catch (e: MqttException) {

            e.printStackTrace()
        }

    }

    fun subscribeToTopic(topic: String){
        val qos=2
        try{
            mqttAndroidClient.subscribe(topic,qos,null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("TAG0","$topic Subscribed")
                    Toast.makeText(application,"$topic Subscribed",Toast.LENGTH_LONG).show()
                    receiveMessages()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("TAG0","Mqtt Failed!")
                    Toast.makeText(application,"Failed to $topic Subscribe",Toast.LENGTH_LONG).show()
                }
            })
        }catch (e:MqttException){
            e.printStackTrace()
        }
    }

    private fun receiveMessages() {
        mqttAndroidClient.setCallback(object : MqttCallback{
            override fun connectionLost(cause: Throwable?) {
                Log.d("TAG0","Connection Lost")
                connect(application)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                if (message != null) {
                    val data = String(message.payload, charset("UTF-8"))
                    Log.d("TAG0","topic: $topic message: $data")
                    callback?.onDataAvailable(topic.orEmpty(),data)

                }else{
                    Log.d("TAG0","Message null")
                }
            }
        })
    }

     fun disconnect(){
        try{
            val disconnectToken=mqttAndroidClient.disconnect()
            disconnectToken.actionCallback=object :IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("TAG0","Successfully disconnected")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("TAG0","Failed to disconnect")
                }
            }
        }catch (e:MqttException){
            e.printStackTrace()
        }
    }
    fun setDataAvailable(callback: MqttHelper.DataAvailable?) {
        this.callback = callback
    }
    interface DataAvailable{
        fun onDataAvailable(topic: String, data: String)
    }
}

