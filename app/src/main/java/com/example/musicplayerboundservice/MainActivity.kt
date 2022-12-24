package com.example.musicplayerboundservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var mMusicPlayerService : MusicPlayerService
    private var mBound = false

    lateinit var play:TextView
    lateinit var pause:TextView

    private val mServiceConnection = object :ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {

           val myServiceBinder: MusicPlayerService.MyServiceBinder =
               binder as MusicPlayerService.MyServiceBinder

            mMusicPlayerService= myServiceBinder.getService()

            mBound = true
            Log.d("service","onServiceConnected method called ")

        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        play = findViewById(R.id.play)
        pause = findViewById(R.id.pause)

        play.setOnClickListener {

            onBtnMusicClicked(play)
        }
        pause.setOnClickListener {
            onBtnMusicClicked(pause)
        }
    }

    fun  onBtnMusicClicked(view:View){

        if (mBound){

            if (mMusicPlayerService.isPlaying()){
                mMusicPlayerService.pause()

            }
            else{
//                to play song on bckground we also run service as started service
                val i = Intent(this , MusicPlayerService::class.java)
                startService(i)
                mMusicPlayerService.play()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        Log.d("service", "OnStart methos class is called")
        val i = Intent(this , MusicPlayerService::class.java)
        bindService(i ,mServiceConnection , Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (mBound){
            unbindService(mServiceConnection)
            mBound = false
        }
    }
}