package com.example.akillikampussg

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.random.Random

/**
 *NotificationHelper
 *uygulamadaki tüm sistem bildirimlerini tek yerden yönetmek için yazılmış sınıf
 *2 farklı kanal kullanıyoruz
 *durum Güncellemeleri = takip edilen bildirimlerin durum değişimi
 *acil Duyurular = admin tarafından yayınlanan acil mesajlar
 */
object NotificationHelper {

    //kanal id’leri (bir kere tanımlanır her yerde aynı kullanılmalı)
    private const val CHANNEL_ID_STATUS = "status_updates"
    private const val CHANNEL_ID_EMERGENCY = "emergency_announcements"


     //bildirim kanallarını oluşturur

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //sistem notification manager
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //durum güncellemeleri kanalı
            val statusChannel = NotificationChannel(
                CHANNEL_ID_STATUS,
                "Durum Güncellemeleri",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            //acil duyuru kanalı
            val emergencyChannel = NotificationChannel(
                CHANNEL_ID_EMERGENCY,
                "Acil Duyurular",
                NotificationManager.IMPORTANCE_HIGH
            )

            //kanalları oluştur
            nm.createNotificationChannel(statusChannel)
            nm.createNotificationChannel(emergencyChannel)
        }
    }

    /**
     *genel bildirim
     *varsayılan olarak durum güncellemeleri kanalına gönderir
     */
    fun notify(context: Context, title: String, message: String) {
        ensureChannels(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_STATUS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)

        notifyInternal(context, builder)
    }


     //takip edilen bildirimde durum güncellendiğinde kullanılan bildirim

    fun notifyStatusChanged(context: Context, title: String, message: String) {
        ensureChannels(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_STATUS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)

        notifyInternal(context, builder)
    }


     //admin acil duyuru yayınladığında kullanılan bildirim
    fun notifyEmergency(context: Context, title: String, message: String) {
        ensureChannels(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_EMERGENCY)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notifyInternal(context, builder)
    }


     //takip edilen bildirimin durumu değiştiğinde kullanılacak mesaj formatı böylece her yerde aynı cümleyle bildirim atılır

    fun notifyFollowerStatusChange(context: Context, itemTitle: String, newStatus: String) {
        notifyStatusChanged(
            context,
            "Takip ettiğin bildirimin durumu değişti",
            "\"$itemTitle\" → $newStatus"
        )
    }


     //bildirimi gerçekten sisteme gönderen ortak fonksiyon

    private fun notifyInternal(context: Context, builder: NotificationCompat.Builder) {

        //android 13 izin kontrolü
        if (Build.VERSION.SDK_INT >= 33) {
            val granted = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) return
        }

        //her bildirim için rastgele id üst üste binmesin diye
        NotificationManagerCompat.from(context)
            .notify(Random.nextInt(), builder.build())
    }
}
