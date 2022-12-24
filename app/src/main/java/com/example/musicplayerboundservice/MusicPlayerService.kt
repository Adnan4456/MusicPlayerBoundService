package com.example.musicplayerboundservice

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class MusicPlayerService : Service() {


    private val MUSIC_PLAY = "play"
    private val MUSIC_PAUSE = "pause"
    private val MUSIC_STOP = "stop"
    private val MUSIC_START = "start"

    private val mBinder = MyServiceBinder()

    lateinit var  mPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        Log.d("service","onCreate method is called ")
        mPlayer = MediaPlayer.create(this , R.raw.file)

        mPlayer.setOnCompletionListener {

            stopForeground(true)
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


        when(intent?.action){

            MUSIC_START ->{
                showNotification()
            }
            MUSIC_PLAY ->{
                play()

            }
            MUSIC_STOP ->{

                stopForeground(true)
                stopSelf()
            }
            MUSIC_PAUSE ->{
                pause()
            }

        }


//        showNotification()
        return START_NOT_STICKY

    }

    private fun showNotification() {

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            }
        else{
                ""
            }

        val playIntent = Intent(this , MusicPlayerService::class.java)
        playIntent.action = MUSIC_PLAY

        var pPlayIntent = PendingIntent.getService(this, 100,
            playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val pauseIntent = Intent(this , MusicPlayerService::class.java)
        pauseIntent.action = MUSIC_PAUSE

        var pPauseIntent = PendingIntent.getService(this, 100,
            pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val stopIntent = Intent(this , MusicPlayerService::class.java)
        stopIntent.action = MUSIC_STOP

        var pStopIntent = PendingIntent.getService(this, 100,
            stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
           this  , channelId
       )

        builder.setContentTitle("My music player")
            .setContentText("This is demo player")
            .setSmallIcon(R.mipmap.ic_launcher)
            .addAction(NotificationCompat.Action(R.drawable.ic_play,"Play" , pPlayIntent))
            .addAction(NotificationCompat.Action(R.drawable.ic_pause,"Pause" , pPauseIntent))
            .addAction(NotificationCompat.Action(R.drawable.ic_stop,"Stop" , pStopIntent))

        startForeground(123 , builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
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