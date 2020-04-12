package com.example.sfms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActvity : AppCompatActivity() , MqttHelper.DataAvailable{
    private lateinit var mqttHelper:MqttHelper
    private lateinit var builder:NotificationCompat.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        mqttHelper= MqttHelper(this)
        mqttHelper.connect(this)
        mqttHelper.setDataAvailable(this)
        val intent = Intent(this, DetailActvity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        builder= NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.drawable.ic_bell)
            .setContentTitle("Fire Detected")
            .setContentText("Fire alarm in cafeteria has picked up fire. ignore that location!!!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        createNotificationChannel()

    }

    override fun onDataAvailable(topic: String, data: String) {
        Log.d("TAG0","Data arrived in detail : $topic:$data")
        if(topic.equals("co",true)){
            var intdata=data.toFloatOrNull()

            Log.d("TAG0","here in if")
            coconcentration.text=data.plus("ppm")
            var ratio=intdata!!.div(10.0f)*100
            if(ratio>95){
                coconcentration.setTextColor(Color.RED)
                co_progress.progress=100
                co_progress.finishedStrokeColor= Color.RED
                co_progress.textColor=Color.RED
                with(NotificationManagerCompat.from(this)){
                    notify(1,builder.build())
                }
            }else {
                Log.d("TAG0", "ratio co $ratio")
                co_progress.progress = ratio.toInt()
            }

        }else{
            var intdata=data.toFloatOrNull()
            co2concentration.text=data.plus("ppm")
            var ratio=(intdata!!/600.0)*100
            if(ratio>95){
                co2concentration.setTextColor(Color.RED)
                with(NotificationManagerCompat.from(this)){
                    notify(1,builder.build())
                }
                co2_progress.progress=100
                co2_progress.finishedStrokeColor= Color.RED
                co2_progress.textColor=Color.RED
            }else{
                Log.d("TAG0","ratio co  $ratio")
                co2_progress.progress=ratio.toInt()
            }

        }
    }
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
