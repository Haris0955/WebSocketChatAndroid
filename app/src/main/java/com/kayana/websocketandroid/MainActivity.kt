package com.kayana.websocketandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class MainActivity : AppCompatActivity() {

    private lateinit var webSocketListener: WebSocketListener
    private lateinit var mainViewModel: MainViewModel
    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val btnDisconnect = findViewById<Button>(R.id.btnDisconnect)
        val tvMessage = findViewById<TextView>(R.id.tvMessage)
        val edtMessage = findViewById<EditText>(R.id.edtMessage)
        val btnSend = findViewById<Button>(R.id.button_send)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        webSocketListener = WebSocketListener(mainViewModel)

        mainViewModel.socketStatus.observe(this, Observer {
            tvMessage.text = if (it) "Connected" else "Disconnected"
        })

        var text=""

        mainViewModel.message.observe(this, Observer {
            text+="${if (it.first)"You: " else "Other: "} ${it.second}\n"
            tvMessage.text= text
        })

        btnConnect.setOnClickListener {
            webSocket = okHttpClient.newWebSocket(createRequest(),webSocketListener)
        }
        btnDisconnect.setOnClickListener {
            webSocket?.close(1000, "Cancelled Manually...")
        }
 btnSend.setOnClickListener {
     if (edtMessage.text.toString().isNotEmpty()){
         webSocket?.send(edtMessage.text.toString())
         mainViewModel.setMessage(Pair(true, edtMessage.text.toString()))
     }
 }
    }

    private fun createRequest(): Request {
val webSocketUrl ="wss://s11170.nyc1.piesocket.com/v3/1?api_key=AKgj35nqjtxSTygNKQb3g0atHZq5dcYBF6jzL8b4&notify_self=1"
        return  Request.Builder().url(webSocketUrl).build()
    }

}