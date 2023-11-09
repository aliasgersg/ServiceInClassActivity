package edu.temple.myapplication

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log

const val UPDATE_COUNTER = 1



@Suppress("ControlFlowWithEmptyBody")
class TimerService : Service() {

    lateinit var timerHandler: Handler

    private var isRunning = false

    lateinit var t: TimerThread

    private var paused = false

    inner class TimerBinder : Binder() {


        // Check if Timer is already running
        private var isRunning: Boolean
            get() = this@TimerService.isRunning
            set(value) {this@TimerService.isRunning = value}

        // Start a new timer
        fun start(startValue: Int){

            if (!paused) {
                if (!isRunning) {
                    if (::t.isInitialized) t.interrupt()
                    this@TimerService.start(startValue)
                }
            } else {
                pause()
            }
        }

        // Stop a currently running timer
        fun stop() {
            if (::t.isInitialized || isRunning) {
                t.interrupt()
            }
        }

        // Pause a running timer
        fun pause() {
            this@TimerService.pause()
        }

        fun getService() : TimerService {
            return this@TimerService
        }

    }

    override fun onCreate() {
        super.onCreate()

        Log.d("TimerService status", "Created")
    }

    override fun onBind(intent: Intent): IBinder {
        return TimerBinder()
    }

    fun start(startValue: Int) {
        t = TimerThread(startValue)
        t.start()
    }

    fun pause () {
        if (::t.isInitialized) {
            paused = !paused
            isRunning = !paused
        }
    }

    fun setHandler(Timerhandler: Handler){
        timerHandler
    }

    inner class TimerThread(private val startValue: Int) : Thread() {

        override fun run() {
            isRunning = true
            try {
                for (i in startValue downTo 1)  {
                    Log.d("Countdown", i.toString())

                        while (paused);
                        sleep(1000)
                        if(::timerHandler.isInitialized) {
                            timerHandler.sendEmptyMessage(i)
                        }

                }
                isRunning = false
            } catch (e: InterruptedException) {
                Log.d("Timer interrupted", e.toString())
                isRunning = false
                paused = false
            }

        }

    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (::t.isInitialized) {
            t.interrupt()
        }

        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("TimerService status", "Destroyed")

    }


}

// implement the handler to have the serivce send the data back to the client and then put it in textview to show it a counter