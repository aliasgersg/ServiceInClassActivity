package edu.temple.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.Button

private var timerService: TimerService.TimerBinder? = null
private var isConnected = false

class MainActivity : AppCompatActivity() {

    private val timerHandler = Handler()

    private val timerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val timerBinder = service as TimerService.TimerBinder
            timerBinder.setHandler(timerHandler)
            timerService = timerBinder
            isConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // Handle disconnection, if needed
            isConnected = false
            timerService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            timerService?.start(100)
        }

        findViewById<Button>(R.id.pauseButton).setOnClickListener {
            timerService?.pause()
        }

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            timerService?.stop()
        }

        val serviceIntent = Intent(this, TimerService::class.java)
        bindService(serviceIntent, timerServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isConnected) {
            unbindService(timerServiceConnection)
            isConnected = false
        }
    }
}
