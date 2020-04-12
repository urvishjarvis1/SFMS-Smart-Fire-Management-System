package com.example.sfms

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity() , MqttHelper.DataAvailable {
    private val RC_SIGN_IN: Int = 0;
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var coDataClass = DataClass("co","0.0ppm")
    private var co2DataClass= DataClass("co2","0.0ppm")
    lateinit var mqttHelper: MqttHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mqttHelper=MqttHelper(this@MainActivity)
        mqttHelper.connect(this)
        mqttHelper.setDataAvailable(this)
        auth = FirebaseAuth.getInstance()

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        if (user == null) {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )
        }

        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(coDataClass,co2DataClass)
        (viewAdapter as MyAdapter).setOnItemClickListener(onItemClickListener)
        recyclerView = findViewById<RecyclerView>(R.id.waspmote_list).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }



    }
    private val onItemClickListener = object : View.OnClickListener {
        override fun onClick(view: View) {
            val intent=Intent(this@MainActivity,DetailActvity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {

                user = FirebaseAuth.getInstance().currentUser
                Log.d("TAG0", "signInWithEmail:success$user")

            } else {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
            }
    }

    override fun onDataAvailable(topic: String, data: String) {
        Log.d("TAG0","Data arrived : $topic:$data")
        if(topic.equals("co",true)){
            coDataClass.data=data.plus("ppm")
        }else{
            co2DataClass.data=data.plus("ppm")
        }
        viewAdapter.notifyDataSetChanged()

    }

    override fun onStop() {
        super.onStop()
       mqttHelper.disconnect()
        mqttHelper.setDataAvailable(null)
    }
}
