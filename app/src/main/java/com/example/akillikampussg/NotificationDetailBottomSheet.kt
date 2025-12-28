package com.example.akillikampussg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 *NotificationDetailBottomSheet
 *bildirim detay ekranı BottomSheet olarak açılır
 *burada
 *başlık, tür, açıklama, durum, zaman gösterilir
 *kullanıcı için takip et takipten çık butonu vardır
 *admin için durum güncelleme butonları görünür
 */
class NotificationDetailBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //bottomSheet layoutu şişir
        return inflater.inflate(R.layout.bottomsheet_notification_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


         //parametre ile gelen id üzerinden ilgili bildirimi bul

        val id = requireArguments().getString("id") ?: return
        val item = DataStore.items.find { it.id == id } ?: return

        //UI bileşenleri
        val tvTitle = view.findViewById<TextView>(R.id.tvDTitle)
        val tvType = view.findViewById<TextView>(R.id.tvDType)
        val tvDesc = view.findViewById<TextView>(R.id.tvDDesc)
        val tvStatus = view.findViewById<TextView>(R.id.tvDStatus)
        val tvTime = view.findViewById<TextView>(R.id.tvDTime)

        val btnFollow = view.findViewById<Button>(R.id.btnFollow)
        val btnClose = view.findViewById<Button>(R.id.btnClose)

        //admin kontrol paneli
        val layoutAdmin = view.findViewById<LinearLayout>(R.id.layoutAdmin)
        val btnOpen = view.findViewById<Button>(R.id.btnOpen)
        val btnInProgress = view.findViewById<Button>(R.id.btnInProgress)
        val btnResolved = view.findViewById<Button>(R.id.btnResolved)


        //rol kontrolü admin ise durum değiştirme paneli görünür kullanıcı ise gizlenir

        val isAdmin = DataStore.currentUser?.role == "ADMIN"
        layoutAdmin.visibility = if (isAdmin) View.VISIBLE else View.GONE

        //durumu ekranda güncelleyen fonksiyon
        fun refreshStatus() {
            tvStatus.text = "Durum: ${item.status}"
        }
        refreshStatus()

        /**
         *admin durum güncellendiğinde çalışır
         *ekrandaki durum yazısı güncellenir
         *eğer güncel kullanıcı bu bildirimi takip ediyorsa anlık bildirim gösterilir
         */
        fun updateStatus(newStatus: String) {
            item.status = newStatus
            refreshStatus()

            //bu bildirimi takip ediyorsan durum değişiminde bildirim göster
            val meEmail = DataStore.currentUser?.email ?: ""
            val isFollowing = meEmail.isNotEmpty() && item.followers.contains(meEmail)

            if (isFollowing) {
                NotificationHelper.notifyStatusChanged(
                    requireContext(),
                    "Takip ettiğin bildirim güncellendi",
                    "${item.title} → Yeni durum: ${item.status}"
                )
            }
        }


         //admin durum değiştir butonları

        if (isAdmin) {
            btnOpen.setOnClickListener { updateStatus("Açık") }
            btnInProgress.setOnClickListener { updateStatus("İnceleniyor") }
            btnResolved.setOnClickListener { updateStatus("Çözüldü") }
        }


         //detay bilgilerini ekrana bas

        tvTitle.text = item.title
        tvType.text = "Tür: ${item.type}"
        tvDesc.text = item.desc
        tvStatus.text = "Durum: ${item.status}"
        tvTime.text = "Zaman: ${
            android.text.format.DateFormat.format("dd.MM.yyyy HH:mm", item.createdAt)
        }"

        /**
         *takip et takipten çık butonu
         *kullanıcı bildirimi takip listesine ekler veya çıkarır
         */
        val meEmail = DataStore.currentUser?.email ?: ""

        fun refreshFollowText() {
            val followed = meEmail.isNotEmpty() && item.followers.contains(meEmail)
            btnFollow.text = if (followed) "Takipten Çık" else "Takip Et"
        }
        refreshFollowText()

        btnFollow.setOnClickListener {
            if (meEmail.isNotEmpty()) {
                val followed = item.followers.contains(meEmail)
                if (followed) item.followers.remove(meEmail) else item.followers.add(meEmail)
                refreshFollowText()
            }
        }

        //bottomSheeti kapat
        btnClose.setOnClickListener { dismiss() }
    }

    companion object {

        /**
         *dışarıdan çağırmak için helper
         *NotificationDetailBottomSheet.show(fm, item.id)
         */
        fun show(fm: androidx.fragment.app.FragmentManager, id: String) {
            NotificationDetailBottomSheet().apply {
                arguments = Bundle().apply { putString("id", id) }
            }.show(fm, "detail")
        }
    }
}
