package com.skymonkey.core.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.skymonkey.core.presentation.ui.R
import com.skymonkey.core.presentation.ui.formatted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import org.koin.android.ext.android.inject
import kotlin.time.Duration

class ActiveRunService : Service() {
    private val notificationManager by lazy {
        getSystemService<NotificationManager>()
    }

    private val elapsedTime by inject<StateFlow<Duration>>() // elapsed time provided by RunningTracker via DI

    private val baseNotification by lazy {
        NotificationCompat
            .Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(com.skymonkey.core.presentation.designsystem.R.drawable.logo)
            .setContentTitle(getString(R.string.active_run))
            .setOnlyAlertOnce(true)
    }

    private var serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        when (intent?.action) {
            ACTION_START -> {
                val activityClass =
                    intent.getStringExtra(EXTRA_ACTIVITY_CLASS)
                        ?: throw IllegalArgumentException("No Activity Class provided")
                start(Class.forName(activityClass)) // expecting our MainActivity
            }
            ACTION_STOP -> {
                stop()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    /**
     * starts our foreground service and attaches a notification to it
     * @param activityClass - the activity that should be started when we tap on our notification.
     * the reason the activityClass is passed as a Class and not our MainActivity, is because we don't
     * have access to the MainActivity located in our app module.
     */
    private fun start(activityClass: Class<*>) {
        if (!isServiceActive.value) {
            _isServiceActive.value = true
            createNotificationChannel()

            val activityIntent =
                Intent(applicationContext, activityClass).apply {
                    data = DEEPLINK_URI.toUri()
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) // if active run is already on top of back stack, reopen existing instance.
                }

            val pendingIntent =
                TaskStackBuilder.create(applicationContext).run {
                    addNextIntentWithParentStack(activityIntent)
                    getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
                }

            val notification =
                baseNotification
                    .setContentText("00:00:00")
                    .setContentIntent(pendingIntent)
                    .build()

            startForeground(NOTIFICATION_ID, notification)
            updateNotification()
        }
    }

    private fun stop() {
        stopSelf()
        _isServiceActive.value = false
        serviceScope.cancel() // cancel current jobs

        // after we've cancelled our scope, we need to create a new one as it can't be restarted.
        // Reason is because stopping the service doesn't mean it's destroyed.
        // So if we choose to start the service again we still need a functioning service scope
        serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

    @OptIn(FlowPreview::class) // for sample
    private fun updateNotification() {
        elapsedTime
            .sample(1000) // throttle but don't wait on our values
            .onEach { elapsedTime ->
                val notification =
                    baseNotification
                        .setContentText(elapsedTime.formatted())
                        .build()

                notificationManager?.notify(NOTIFICATION_ID, notification)
            }.launchIn(serviceScope)
    }

    private fun createNotificationChannel() {
        // foreground notifications only needed with 26 and newer
        if (Build.VERSION.SDK_INT >= 26) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.active_run),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        private var _isServiceActive = MutableStateFlow(false)
        val isServiceActive = _isServiceActive.asStateFlow()
        const val DEEPLINK_URI = "runbuddy://active_run"
        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"
        private const val CHANNEL_ID = "active_run"
        private const val NOTIFICATION_ID = 1
        private const val EXTRA_ACTIVITY_CLASS = "EXTRA_ACTIVITY_CLASS"

        fun createStartIntent(
            context: Context,
            activityClass: Class<*>
        ): Intent =
            Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ACTIVITY_CLASS, activityClass.name)
            }

        fun createStopIntent(context: Context): Intent =
            Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_STOP
            }
    }
}
