package com.example.akillikampussg

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * AdminPanelFragment
 *
 * Sadece ADMIN rolündeki kullanıcıların erişebildiği yönetim ekranıdır.
 * Admin;
 *  - Tüm bildirimleri görür
 *  - Bildirim durumunu günceller
 *  - Yanlış/uygunsuz bildirimi sonlandırır
 *  - Acil duyuru yayınlayabilir
 */
class AdminPanelFragment : Fragment(R.layout.fragment_admin_panel) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView ve Acil Duyuru butonu
        val rv = view.findViewById<RecyclerView>(R.id.rvAdmin)
        val btnEmergency = view.findViewById<Button>(R.id.btnEmergencyAdmin)

        /**
         * Güvenlik kontrolü:
         * Admin olmayan kullanıcı bu ekrana giremez
         */
        val isAdmin = DataStore.currentUser?.role == "ADMIN"
        if (!isAdmin) {
            Toast.makeText(
                requireContext(),
                "Bu ekrana sadece Admin erişebilir.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Bildirim listesi dikey olarak gösterilir
        rv.layoutManager = LinearLayoutManager(requireContext())

        /**
         * AdminNotificationAdapter:
         * Bildirimlerin listelenmesi ve yönetilmesi
         */
        rv.adapter = AdminNotificationAdapter(
            items = DataStore.items,

            // Bildirim durumu değiştiğinde çalışır
            onStatusChanged = { item, oldStatus, newStatus ->

                /**
                 * 1️⃣ Admin kendi cihazında anında bildirim görsün
                 * (test + görsel geri bildirim için)
                 */
                NotificationHelper.notifyStatusChanged(
                    requireContext(),
                    "Durum güncellendi",
                    "\"${item.title}\" : $oldStatus → $newStatus"
                )

                /**
                 * 2️⃣ Bildirimi takip eden tüm kullanıcılar için
                 * "pending notification" oluşturulur
                 *
                 * Bu kullanıcılar daha sonra giriş yaptığında
                 * bu bildirimleri görecektir (simülasyon mantığı)
                 */
                item.followers.forEach { followerEmail ->
                    DataStore.pendingNotifs.add(
                        DataStore.PendingNotif(
                            toEmail = followerEmail,
                            title = "Takip ettiğin bildirimin durumu değişti",
                            message = "\"${item.title}\" → $newStatus"
                        )
                    )
                }

                // Debug amaçlı kullanılabilir
                // Toast.makeText(requireContext(),
                // "Takipçilere pending eklendi: ${item.followers.size}",
                // Toast.LENGTH_SHORT).show()
            }
        )

        /**
         * Acil duyuru yayınlama
         * (yüksek öncelikli sistem bildirimi)
         */
        btnEmergency.setOnClickListener {
            NotificationHelper.notifyEmergency(
                requireContext(),
                "Acil Duyuru",
                "Admin panelden acil duyuru (simülasyon)."
            )
        }
    }
}
