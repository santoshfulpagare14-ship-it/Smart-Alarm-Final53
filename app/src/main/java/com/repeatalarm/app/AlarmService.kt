
package com.repeatalarm.app
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AlarmService : Service() {
    companion object {
        var mediaPlayer: MediaPlayer? = null
        fun stopSound(){ mediaPlayer?.stop(); mediaPlayer?.release(); mediaPlayer = null }
    }
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getIntExtra("alarmId", -1) ?: -1
        val alarm = if(alarmId != -1) AlarmStorage.load(this, alarmId) else null

        createChannel()
        val notif = NotificationCompat.Builder(this, "alarm_channel")
            .setContentTitle(alarm?.label ?: "Alarm")
            .setContentText("Baj raha hai... Volume Down se mute")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .build()
        startForeground(1, notif)

        playSound(alarm?.audioUri)
        return START_NOT_STICKY
    }

    private fun playSound(uriString: String?){
        stopSound()
        try {
            mediaPlayer = if(uriString != null){
                MediaPlayer().apply {
                    setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build())
                    setDataSource(this@AlarmService, Uri.parse(uriString))
                    isLooping = true
                    prepare()
                    start()
                }
            } else {
                MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI)?.apply {
                    isLooping = true
                    start()
                }
            }
        } catch (e: Exception){ e.printStackTrace() }
    }

    private fun createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val ch = NotificationChannel("alarm_channel","Alarms", NotificationManager.IMPORTANCE_HIGH)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(ch)
        }
    }
    override fun onDestroy() { stopSound(); super.onDestroy() }
}
