package com.example.akillikampussg

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.math.abs

/**
 *MapFragment
 *harita ekranı bildirimleri harita üzerinde pin olarak gösterir
 *bu projede apı kullanılmadan manuel yapılmıştır
 *arka planda kampüs haritasını bir jpg olarak gösterdim
 *üstüne framelayout içine pinler ekledim
 *kullanıcı
 *uzaklaştırma yakınlaştırma yapabilir
 *harita üzerine tıklayarak konum seçebilir
 *pini tıklayınca mini bilgi kartı açılması lazım ve detay gör ile detay ekranına geçilir
 */
class MapFragment : Fragment(R.layout.fragment_map) {

    //basit zoom seviyesi
    private var zoom = 1.0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //harita pinlerinin çizildiği alan arka plan haritası xml den eklendi
        val mapCanvas = view.findViewById<FrameLayout>(R.id.mapCanvas)

        //mini bilgi kartı bileşenleri
        val infoCard = view.findViewById<LinearLayout>(R.id.mapInfoCard)
        val tvTitle = view.findViewById<TextView>(R.id.tvInfoTitle)
        val tvType = view.findViewById<TextView>(R.id.tvInfoType)
        val tvTime = view.findViewById<TextView>(R.id.tvInfoTime)
        val btnViewDetail = view.findViewById<Button>(R.id.btnViewDetail)
        val btnCloseCard = view.findViewById<Button>(R.id.btnCloseCard)

        //zoom butonları
        val btnZoomIn = view.findViewById<Button>(R.id.btnZoomIn)
        val btnZoomOut = view.findViewById<Button>(R.id.btnZoomOut)

        //bilgi kartını kapatma
        btnCloseCard.setOnClickListener { infoCard.visibility = View.GONE }

        /**
         *zoom işlemi
         *harita alanını scaleX scaleY ile büyütüp küçültüyoruz
         */
        fun applyZoom() {
            mapCanvas.scaleX = zoom
            mapCanvas.scaleY = zoom
        }

        btnZoomIn.setOnClickListener {
            zoom = (zoom + 0.1f).coerceAtMost(1.6f)
            applyZoom()
        }
        btnZoomOut.setOnClickListener {
            zoom = (zoom - 0.1f).coerceAtLeast(0.8f)
            applyZoom()
        }

        /**
         *harita alanına tıklanınca
         *bilgi kartı kapanır
         *koordinatlar manuel oluşturuldu
         */
        mapCanvas.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                infoCard.visibility = View.GONE

                //canvas boyutları
                val w = mapCanvas.width.toDouble().coerceAtLeast(1.0)
                val h = mapCanvas.height.toDouble().coerceAtLeast(1.0)

                //tıklanan noktayı 0.1 aralığına çekiyoruz
                val nx = (event.x / w).coerceIn(0.05, 0.95)
                val ny = (event.y / h).coerceIn(0.05, 0.95)

                //0.1 değerini sahte lat lng aralığına çevir
                val lat = 39.80 + (nx * 0.20)
                val lng = 41.15 + (ny * 0.20)

                //seçilen konumu DataStorea yaz
                DataStore.selectedLat = lat
                DataStore.selectedLng = lng

                android.widget.Toast.makeText(
                    requireContext(),
                    "Konum seçildi!",
                    android.widget.Toast.LENGTH_SHORT
                ).show()

                //konum seçince create ekranına geri dön
                activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_nav)
                    ?.selectedItemId = R.id.nav_create

                true
            } else {
                false
            }
        }

        /**
         *eski pinleri temizle
         *arka plan resim silinmesin diye removeAllViews yapmıyoruz
         *pinleri PIN tagi ile işaretleyip sadece onları kaldırıyoruz
         */
        val toRemove = mutableListOf<View>()
        for (i in 0 until mapCanvas.childCount) {
            val child = mapCanvas.getChildAt(i)
            if (child.tag == "PIN") toRemove.add(child)
        }
        toRemove.forEach { mapCanvas.removeView(it) }

        /**
         *offline koordinat dönüşümü
         *lat/lng değerlerini 0.1 aralığına map ediyoruz
         *böylece pinleri ekranda oransal olarak konumlandırabiliyoruz
         */
        fun toX(lat: Double): Double {
            return ((lat - 39.80) / 0.20).coerceIn(0.05, 0.95)
        }

        fun toY(lng: Double): Double {
            return ((lng - 41.15) / 0.20).coerceIn(0.05, 0.95)
        }

        /**
         *pinleri ekle
         *mapCanvas ölçüleri oluşunca pinleri hesaplayıp ekliyoruz
         */
        mapCanvas.post {
            val w = mapCanvas.width
            val h = mapCanvas.height

            //dataStoredaki tüm bildirimler haritada pin olarak gösterilir
            DataStore.items.forEach { item ->
                val pin = ImageView(requireContext())
                pin.tag = "PIN" //bu sayede pinleri daha sonra temizleyebiliyoruz


                 //türlere göre farklı ikon gösterimi

                val t: String = normType(item.type)

                val iconRes = when (t) {
                    "guvenlik" -> android.R.drawable.ic_dialog_alert
                    "temizlik" -> android.R.drawable.ic_menu_delete
                    "ariza" -> android.R.drawable.ic_menu_manage
                    "kayipbuluntu" -> android.R.drawable.ic_menu_search
                    else -> android.R.drawable.ic_menu_mylocation
                }
                pin.setImageResource(iconRes)

                //pin boyutu
                val size = (42 * resources.displayMetrics.density).toInt()
                val lp = FrameLayout.LayoutParams(size, size)

                //0.1 -> px konumlandırma
                val x = (toX(item.lat) * w).toInt() - size / 2
                val y = (toY(item.lng) * h).toInt() - size

                lp.leftMargin = x
                lp.topMargin = y

                pin.layoutParams = lp
                pin.isClickable = true

                /**
                 *pini tıklayınca mini kart açılır
                 *başlık
                 *tür
                 *kaç dakikabsaat önce oluşturuldu
                 *karttaki detayı gör butonu ile detay ekranına gidilir
                 */
                pin.setOnClickListener {
                    tvTitle.text = item.title
                    tvType.text = "Tür: ${item.type}"
                    tvTime.text = "Oluşturulma: ${timeAgo(item.createdAt)}"
                    infoCard.visibility = View.VISIBLE

                    btnViewDetail.setOnClickListener {
                        NotificationDetailBottomSheet.show(parentFragmentManager, item.id)
                    }
                }

                mapCanvas.addView(pin)
            }
        }
    }


     //tür normalizasyonu

    private fun normType(type: String): String {
        return type.trim()
            .lowercase()
            .replace("ı", "i")
            .replace("ğ", "g")
            .replace("ü", "u")
            .replace("ş", "s")
            .replace("ö", "o")
            .replace("ç", "c")
            .replace(" ", "")
            .replace("-", "")
    }


     //ne kadar önce bilgisini üretir 5 dakika önce gibi

    private fun timeAgo(createdAt: Long): String {
        val diffMs = abs(System.currentTimeMillis() - createdAt)
        val minutes = diffMs / 60000
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 1 -> "az önce"
            minutes < 60 -> "$minutes dk önce"
            hours < 24 -> "$hours saat önce"
            else -> "$days gün önce"
        }
    }
}
