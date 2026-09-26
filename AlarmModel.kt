
package com.repeatalarm.app
import android.net.Uri
data class AlarmModel(
    val id: Int,
    val label: String,
    val firstTriggerMillis: Long,
    val repeatIntervalMillis: Long, // 1000 = 1 sec to 86400000 = 1 day
    val totalExecutions: Int,
    var executedCount: Int = 0,
    val audioUri: String?, // content uri string
    val volumeMuteEnabled: Boolean
)
