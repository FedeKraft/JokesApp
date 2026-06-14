package com.example.jokesapp.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppClosedNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.showJokeNotification(context)
    }
}
