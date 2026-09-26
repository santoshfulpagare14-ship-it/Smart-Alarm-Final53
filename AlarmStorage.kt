
package com.repeatalarm.app
import android.content.Context
import com.google.gson.Gson
object AlarmStorage {
    private const val PREF = "alarms"
    fun save(context: Context, alarm: AlarmModel){
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
            .putString("alarm_${alarm.id}", Gson().toJson(alarm)).apply()
    }
    fun load(context: Context, id: Int): AlarmModel? {
        val json = context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString("alarm_${id}", null) ?: return null
        return Gson().fromJson(json, AlarmModel::class.java)
    }
    fun remove(context: Context, id: Int){
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().remove("alarm_${id}").apply()
    }
}
