package com.kaamkram.app.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.kaamkram.app.MainActivity
import com.kaamkram.app.R
import com.kaamkram.app.data.local.dao.WarrantyDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@HiltWorker
class WarrantyReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val warrantyDao: WarrantyDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = Date()
        val calendar = Calendar.getInstance()
        calendar.time = today
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val oneWeekFromNowStr = dateFormat.format(calendar.time)
        val todayStr = dateFormat.format(today)

        val allWarranties = warrantyDao.getAllWarranties().firstOrNull() ?: emptyList()
        val expiringSoon = allWarranties.filter {
            it.expiryDate in todayStr..oneWeekFromNowStr
        }

        if (expiringSoon.isNotEmpty()) {
            sendExpiringNotification(expiringSoon.size, expiringSoon.first().serviceOrItemName)
        }

        return Result.success()
    }

    private fun sendExpiringNotification(count: Int, sampleItem: String) {
        val channelId = CHANNEL_ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Service Warranty Alerts"
            val descriptionText = "Notifications for expiring service and parts warranties"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (count == 1) {
            "Warranty Expiring Soon"
        } else {
            "$count Warranties Expiring Soon"
        }

        val message = if (count == 1) {
            "Warranty for $sampleItem is expiring within 7 days. Check with customer for AMC or renewal."
        } else {
            "Warranties including $sampleItem are expiring within 7 days. Tap to review."
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(appContext).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // Permission POST_NOTIFICATIONS may not have been granted yet
        }
    }

    companion object {
        const val CHANNEL_ID = "kaamkram_warranty_alerts"
        const val NOTIFICATION_ID = 2001
        private const val WORK_NAME = "KaamKramWarrantyReminder"

        fun scheduleDaily(context: Context) {
            val dailyWork = PeriodicWorkRequestBuilder<WarrantyReminderWorker>(1, TimeUnit.DAYS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWork
            )
        }
    }
}
