package com.repeatalarm.app
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private var selectedAudioUri: Uri? = null
    private var triggerMillis: Long = System.currentTimeMillis() + 60000
    private val calendar = Calendar.getInstance()

    private val audioPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()){
        it?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedAudioUri = it
            findViewById<TextView>(R.id.tvAudioName).text = it.lastPathSegment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkExactAlarmPermission()

        val etLabel = findViewById<TextInputEditText>(R.id.etLabel)
        val etInterval = findViewById<TextInputEditText>(R.id.etInterval)
        val toggle = findViewById<MaterialButtonToggleGroup>(R.id.toggleUnit)
        val tvPreview = findViewById<TextView>(R.id.tvIntervalPreview)
        val slider = findViewById<Slider>(R.id.sliderCount)
        val tvCount = findViewById<TextView>(R.id.tvCount)
        val btnTime = findViewById<Button>(R.id.btnTime)
        val btnDate = findViewById<Button>(R.id.btnDate)

        toggle.check(R.id.unitSec)
        fun updatePreview(){
            val v = etInterval.text.toString().toIntOrNull() ?: 0
            val unit = when(toggle.checkedButtonId){
                R.id.unitSec -> "sec" ; R.id.unitMin -> "min" ; R.id.unitHour -> "hour" ; R.id.unitDay -> "day" ; else -> "sec"
            }
            tvPreview.text = "Har $v $unit me bajega | ${getMillis(v,unit)/1000} sec"
        }
        etInterval.setOnFocusChangeListener{_,_-> updatePreview()}
        toggle.addOnButtonCheckedListener{_,_,_ -> updatePreview()}

        slider.addOnChangeListener{_,value,_ -> tvCount.text = "${value.toInt()} executions ke baad auto band" }

        btnTime.setOnClickListener {
            TimePickerDialog(this,{_,h,m -> calendar.set(Calendar.HOUR_OF_DAY,h); calendar.set(Calendar.MINUTE,m); btnTime.text = "%02d:%02d".format(h,m); updateTrigger() }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }
        btnDate.setOnClickListener {
            DatePickerDialog(this,{_,y,mo,d -> calendar.set(y,mo,d); btnDate.text = "$d/${mo+1}/$y"; updateTrigger() }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        findViewById<Button>(R.id.btnPickAudio).setOnClickListener {
            audioPicker.launch(arrayOf("audio/*"))
        }

        findViewById<Button>(R.id.btnSetAlarm).setOnClickListener {
            val intervalVal = etInterval.text.toString().toIntOrNull()
            if(intervalVal == null || intervalVal <=0){ Toast.makeText(this,"Interval sahi dalo",Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            val unit = when(toggle.checkedButtonId){
                R.id.unitSec -> "sec" ; R.id.unitMin -> "min" ; R.id.unitHour -> "hour" ; R.id.unitDay -> "day" ; else -> "sec"
            }
            val intervalMillis = getMillis(intervalVal, unit)
            if(intervalMillis < 1000 || intervalMillis > 86400000L){ Toast.makeText(this,"1 sec se 1 din ke beech rakho", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

            val id = (System.currentTimeMillis() % 100000).toInt()
            val alarm = com.repeatalarm.app.AlarmModel(
                id = id,
                label = etLabel.text.toString(),
                firstTriggerMillis = triggerMillis,
                repeatIntervalMillis = intervalMillis,
                totalExecutions = slider.value.toInt(),
                executedCount = 0,
                audioUri = selectedAudioUri?.toString(),
                volumeMuteEnabled = findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switchVolumeMute).isChecked
            )
            AlarmStorage.save(this, alarm)
            AlarmScheduler.scheduleExact(this, alarm, triggerMillis)
            findViewById<TextView>(R.id.tvStatus).text = "✅ Alarm set! ID:$id \nPehli baar: ${java.util.Date(triggerMillis)}\nRepeat: har ${intervalVal} $unit\nTotal: ${alarm.totalExecutions} baar"
            Toast.makeText(this,"Alarm Set!",Toast.LENGTH_LONG).show()
        }
        updateTrigger()
        updatePreview()
    }

    private fun getMillis(value: Int, unit: String): Long = when(unit){
        "sec" -> value * 1000L
        "min" -> value * 60_000L
        "hour" -> value * 3_600_000L
        "day" -> value * 86_400_000L
        else -> value * 1000L
    }
    private fun updateTrigger(){ triggerMillis = calendar.timeInMillis; if(triggerMillis < System.currentTimeMillis()) triggerMillis += 60000 }

    private fun checkExactAlarmPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            val am = getSystemService(ALARM_SERVICE) as AlarmManager
            if(!am.canScheduleExactAlarms()){
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply { data = Uri.parse("package:$packageName") })
            }
        }
        if(Build.VERSION.SDK_INT >= 33){
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }
    }
}
