package com.example.akillikampussg

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * CreateReportFragment
 * kullanıcının kampüs içi yeni bir bildirim oluşturduğu ekrandır
 * bildirimler
 *tür
 *başlık
 *açıklama
 *konum
 *bilgileriyle oluşturulur
 */
class CreateReportFragment : Fragment(R.layout.fragment_create_report) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI bileşenleri
        val spType = view.findViewById<Spinner>(R.id.spCreateType)
        val etTitle = view.findViewById<EditText>(R.id.etCreateTitle)
        val etDesc = view.findViewById<EditText>(R.id.etDesc)
        val btnPick = view.findViewById<Button>(R.id.btnPickLocation)
        val tvLoc = view.findViewById<TextView>(R.id.tvSelectedLocation)
        val btnSend = view.findViewById<Button>(R.id.btnSend)


         //bildirim türleri
        val types = listOf("Genel", "Güvenlik", "Temizlik", "Arıza", "Kayıp-Buluntu")

        spType.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, types)


         //seçilen konumu kullanıcıya gösteren kısım

        fun refreshSelectedLocationText() {
            val lat = DataStore.selectedLat
            val lng = DataStore.selectedLng
            tvLoc.text = if (lat != null && lng != null) {
                "Seçilen konum: %.5f, %.5f".format(lat, lng)
            } else {
                "Seçilen konum: (yok)"
            }
        }

        refreshSelectedLocationText()


         //harita ekranına giderek konum seçme işlemi

        btnPick.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Haritada bir yere tıklayıp konum seç!",
                Toast.LENGTH_SHORT
            ).show()

            //harita sekmesine geçiş
            activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
                ?.selectedItemId = R.id.nav_map
        }


         //bildirim oluşturma işlemi

        btnSend.setOnClickListener {

            val titleText = etTitle.text.toString().trim()
            val descText = etDesc.text.toString().trim()
            val typeText = spType.selectedItem?.toString() ?: "Genel"

            //form da eksik yerleri doldurma ve doğrulama
            if (titleText.isEmpty()) {
                Toast.makeText(requireContext(), "Başlık boş olamaz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (descText.isEmpty()) {
                Toast.makeText(requireContext(), "Açıklama boş olamaz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val lat = DataStore.selectedLat
            val lng = DataStore.selectedLng
            if (lat == null || lng == null) {
                Toast.makeText(
                    requireContext(),
                    "Lütfen önce haritadan konum seç",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            //bildirimi oluşturan kullanıcı bilgileri
            val currentUser = DataStore.currentUser
            val creatorEmail = currentUser?.email ?: "-"
            val creatorUnit = currentUser?.unit ?: ""

            /**
             *yeni gelen bildirimleri DataStorea ekle
             *feed harita ve admin panelde otomatik görünür
             */
            DataStore.items.add(
                0,
                NotificationItem(
                    id = java.util.UUID.randomUUID().toString(),
                    type = typeText,
                    title = titleText,
                    desc = descText,
                    createdAt = System.currentTimeMillis(),
                    status = "Açık",
                    creatorEmail = creatorEmail,
                    unit = creatorUnit,
                    lat = lat,
                    lng = lng
                )
            )

            //geçici konum bilgisini temizle
            DataStore.selectedLat = null
            DataStore.selectedLng = null
            refreshSelectedLocationText()

            //formu temizle işlemi
            etTitle.setText("")
            etDesc.setText("")

            Toast.makeText(requireContext(), "Bildirim oluşturuldu", Toast.LENGTH_SHORT).show()

            //akış ekranına geri dön
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FeedFragment())
                .commit()

            activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
                ?.selectedItemId = R.id.nav_feed
        }
    }


     //haritadan geri dönüldüğünde seçilen konum bilgisini güncelle

    override fun onResume() {
        super.onResume()

        view?.findViewById<TextView>(R.id.tvSelectedLocation)?.let { tv ->
            val lat = DataStore.selectedLat
            val lng = DataStore.selectedLng
            tv.text = if (lat != null && lng != null) {
                "Seçilen konum: %.5f, %.5f".format(lat, lng)
            } else {
                "Seçilen konum: (yok)"
            }
        }
    }
}
