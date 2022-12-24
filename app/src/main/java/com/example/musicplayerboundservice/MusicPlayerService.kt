package com.example.musicplayerboundservice

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MusicPlayerService : Service() {

    private val mBinder = MyServiceBinder()

    lateinit var  mPlayer: MediaPlayer


    override fun onCreate() {
        super.onCreate()
        Log.d("service","onCreate method is called ")
        mPlayer = MediaPlayer.create(this , R.raw.file)

        mPlayer.setOnCompletionListener {

            stopSelf()
        }
    }

    //inner class Binder
    inner class MyServiceBinder : Binder(){
         //this class will return the reference of this service class
          fun getService(): MusicPlayerService{
//             return MusicPlayerService()
             return this@MusicPlayerService
         }
    }
    //


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //if your service crash during playing song
        //donot run service again
        Log.d("service" , "onStartCommand method ")
        return START_NOT_STICKY

    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("service","unbind  method is called ")
        return true
    }

    override fun unbindService(conn: ServiceConnection) {

        Log.d("service","unbindService  method is called ")

        super.unbindService(conn)
    }

    override fun onRebind(intent: Intent?) {

        Log.d("service","onRebind called")
        super.onRebind(intent)
    }
    override fun onDestroy() {

        Log.d("service","onDestroy method is called ")
        mPlayer.release()
        super.onDestroy()
    }

    //client methods
     fun isPlaying() :Boolean{
        return mPlayer.isPlaying
    }
    fun play(){
        mPlayer.start()
    }
    fun pause(){
        mPlayer.pause()
    }
}