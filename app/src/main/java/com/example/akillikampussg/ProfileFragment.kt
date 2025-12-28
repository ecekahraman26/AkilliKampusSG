package com.example.akillikampussg

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.akillikampussg.ui.LoginActivity

/**
 *ProfileFragment
 *kullanıcının profil ve ayar ekranıdır
 *bu ekranda:bkullanıcı bilgileri, takip edilen bildirimler, admin için acil duyuru butonu, çıkış yap fonksiyonları yer alır
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //fragment layoutu
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //UI bileşenleri
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvRole = view.findViewById<TextView>(R.id.tvRole)
        val tvUnit = view.findViewById<TextView>(R.id.tvUnit)
        val layoutFollowed = view.findViewById<LinearLayout>(R.id.layoutFollowed)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val btnEmergency = view.findViewById<Button>(R.id.btnEmergency)

        //oturumdaki kullanıcı
        val user = DataStore.currentUser


         //kullanıcı bilgilerini ekrana bas

        if (user != null) {
            tvName.text = "Ad Soyad: ${user.nameSurname}"
            tvEmail.text = "E-posta: ${user.email}"
            tvRole.text = "Rol: ${user.role}"
            tvUnit.text = "Birim: ${user.unit}"

            /**
             *takip edilen bildirimler
             *kullanıcının e-postası followers listesinde geçen bildirimler filtrelenir
             */
            val followed = DataStore.items.filter {
                it.followers.contains(user.email)
            }

            if (followed.isEmpty()) {
                val tv = TextView(requireContext())
                tv.text = "Takip edilen bildirim yok."
                layoutFollowed.addView(tv)
            } else {
                followed.forEach { item ->
                    val tv = TextView(requireContext())
                    tv.text = "• ${item.title} (${item.status})"
                    tv.textSize = 15f
                    layoutFollowed.addView(tv)
                }
            }

        } else {
            // Güvenlik için boş durum
            tvName.text = "Ad Soyad: -"
            tvEmail.text = "E-posta: -"
            tvRole.text = "Rol: -"
            tvUnit.text = "Birim: -"
        }

        /**
         *admine özel acil duyuru butonu
         *kullanıcı girişinde görünmez
         */
        val isAdmin = user?.role == "ADMIN"
        btnEmergency.visibility = if (isAdmin) View.VISIBLE else View.GONE

        btnEmergency.setOnClickListener {
            NotificationHelper.notifyEmergency(
                requireContext(),
                "Acil Duyuru",
                "Kampüste acil durum duyurusu (simülasyon)."
            )
        }

        /**
         *çıkış yap
         *DataStore temizlenir
         *giriş ekranına dönülür
         */
        btnLogout.setOnClickListener {
            DataStore.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}
