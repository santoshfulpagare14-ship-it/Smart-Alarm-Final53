# Repeat Alarm - Native Android App

Native Android Alarm App jo 1 sec se 1 din tak repeat kar sakta hai.

## Features
- ⏰ **Custom Repeat Interval:** 1 Second se 1 Day (86400000 ms) tak
- 🔁 **Auto Stop after Executions:** 1 se 100 baar tak, uske baad automatic delete
- 🔇 **Volume Down to Mute:** Alarm bajte time Volume Down dabao -> instant mute. Volume Up -> unmute
- 🎵 **Custom Audio from Storage:** Phone storage se koi bhi MP3/WAV/M4A choose karo (SAF API)
- 📳 Foreground Service, Lock Screen par bajta hai, Exact Alarm (Android 12+ supported)
- 💾 Offline, No Ads, 100% Native Kotlin

## Tech Stack
- Kotlin, AlarmManager + setExactAndAllowWhileIdle
- BroadcastReceiver -> ForegroundService (MediaPlayer looping)
- Gson for local storage (SharedPreferences)

## Project Structure
```
app/src/main/java/com/repeatalarm/app/
- MainActivity.kt (UI + Set Alarm)
- AlarmReceiver.kt (Repeat logic + auto stop)
- AlarmScheduler.kt (Exact scheduling)
- AlarmService.kt (MediaPlayer + Notification)
- AlarmRingActivity.kt (Volume Down mute handling)
- AlarmStorage.kt
```

## How to Build APK
1. Android Studio me open karo
2. Sync Gradle
3. Build > Build APK(s)

APK location: `app/build/outputs/apk/debug/app-debug.apk`

## Permissions Needed
- SCHEDULE_EXACT_ALARM
- POST_NOTIFICATIONS
- FOREGROUND_SERVICE
- READ_MEDIA_AUDIO

## GitHub Push Commands
```bash
git init
git add .
git commit -m "Initial commit: Repeat Alarm App"
git branch -M main
git remote add origin YOUR_REPO_URL
git push -u origin main
```

Made for Indore Dev. 
