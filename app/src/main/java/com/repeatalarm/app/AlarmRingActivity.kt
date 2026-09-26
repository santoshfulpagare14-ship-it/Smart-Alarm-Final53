
package com.repeatalarm.app
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AlarmRingActivity : AppCompatActivity() {
    private var alarmId: Int = -1
    private var volumeMuteEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_ring)
        alarmId = intent.getIntExtra("alarmId", -1)
        val alarm = if(alarmId != -1) AlarmStorage.load(this, alarmId) else null
        volumeMuteEnabled = alarm?.volumeMuteEnabled ?: true

        findViewById<TextView>(R.id.tvRingLabel).text = alarm?.label ?: "ALARM!"
        findViewById<TextView>(R.id.tvRingInfo).text = "Execution ${alarm?.executedCount}/${alarm?.totalExecutions} | Vol Down = Mute"

        findViewById<Button>(R.id.btnStop).setOnClickListener {
            stopAll()
        }
        findViewById<Button>(R.id.btnSnooze).setOnClickListener {
            // Manual repeat - close and wait for next scheduled
            stopAll()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(volumeMuteEnabled && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            // MUTE feature you asked
            AlarmService.stopSound()
            findViewById<TextView>(R.id.tvRingInfo).text = "Muted! Volume Down dabaya"
            return true
        }
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            // Volume up se unmute - restart service
            val serviceIntent = Intent(this, AlarmService::class.java).apply { putExtra("alarmId", alarmId) }
            startService(serviceIntent)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun stopAll(){
        stopService(Intent(this, AlarmService::class.java))
        AlarmService.stopSound()
        finish()
    }
    override fun onBackPressed() { /* block back */ }
}
