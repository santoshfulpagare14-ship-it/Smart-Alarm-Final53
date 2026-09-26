
package com.repeatalarm.app
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("alarmId", -1)
        if(alarmId == -1) return
        val alarm = AlarmStorage.load(context, alarmId) ?: return

        // Increment executed count
        alarm.executedCount += 1
        AlarmStorage.save(context, alarm)

        // Start ringing activity + service
        val ringIntent = Intent(context, AlarmRingActivity::class.java).apply {
            putExtra("alarmId", alarmId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        context.startActivity(ringIntent)

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("alarmId", alarmId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(serviceIntent)
        else context.startService(serviceIntent)

        // Schedule next if needed
        if(alarm.executedCount < alarm.totalExecutions){
            val nextTrigger = System.currentTimeMillis() + alarm.repeatIntervalMillis
            AlarmScheduler.scheduleExact(context, alarm, nextTrigger)
        } else {
            // Auto stop - finished
            AlarmStorage.remove(context, alarmId)
        }
    }
}
