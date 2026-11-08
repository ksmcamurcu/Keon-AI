package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

class KeonChatWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (ACTION_REFRESH_WIDGET == intent.action) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, KeonChatWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId)
            }
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.example.widget.ACTION_REFRESH_KEON_WIDGET"
        const val EXTRA_OPEN_TAB = "extra_open_tab"
        const val EXTRA_INITIAL_PROMPT = "extra_initial_prompt"
        const val EXTRA_TRIGGER_VOICE = "extra_trigger_voice"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.keon_chat_widget_layout)

            // Intent to open Main Activity directly into Keon Chat tab
            val openChatIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_OPEN_TAB, "chat")
            }
            val pendingChatIntent = PendingIntent.getActivity(
                context,
                101,
                openChatIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root_layout, pendingChatIntent)
            views.setOnClickPendingIntent(R.id.btn_open_chat, pendingChatIntent)
            views.setOnClickPendingIntent(R.id.btn_open_chat_top, pendingChatIntent)

            // Intent for Voice Chat
            val voiceChatIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_OPEN_TAB, "chat")
                putExtra(EXTRA_TRIGGER_VOICE, true)
            }
            val pendingVoiceIntent = PendingIntent.getActivity(
                context,
                102,
                voiceChatIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_voice_chat, pendingVoiceIntent)

            // Quick Prompt 1 Intent
            val quick1Intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_OPEN_TAB, "chat")
                putExtra(EXTRA_INITIAL_PROMPT, "Bugün bana ne tavsiye edersin? Günün motivasyon sözü nedir?")
            }
            val pendingQuick1 = PendingIntent.getActivity(
                context,
                103,
                quick1Intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_quick_1, pendingQuick1)

            // Quick Prompt 2 Intent
            val quick2Intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_OPEN_TAB, "chat")
                putExtra(EXTRA_INITIAL_PROMPT, "Keon AI, günün gündem özetini ve önemli ipuçlarını sun.")
            }
            val pendingQuick2 = PendingIntent.getActivity(
                context,
                104,
                quick2Intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_quick_2, pendingQuick2)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun pinWidgetToHomeScreen(context: Context): Boolean {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                val myProvider = ComponentName(context, KeonChatWidgetProvider::class.java)

                if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                    appWidgetManager.requestPinAppWidget(myProvider, null, null)
                    return true
                }
            }
            return false
        }
    }
}
