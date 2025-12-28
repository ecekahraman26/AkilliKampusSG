package com.example.akillikampussg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 *MainActivity
 *uygulamanın ana ekranıdır
 *bottomNavigation ile sayfalar arası geçişi yönetir
 *kullanıcı rolüne göre admin sekmesini gösterir veya gizler
 *kulanıcı giriş yaptığında bekleyen takip bildirimlerini gösterir
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //önce ana layoutu yükle
        setContentView(R.layout.activity_main)


         //bildirim izni alabilmek için şart

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        //bottomNavigationview ile sekmelerin yönetimi
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)


         //rol yönetimi admin değilse admin sekmesi gizlenir

        val isAdmin = DataStore.currentUser?.role == "ADMIN"
        bottomNav.menu.findItem(R.id.nav_admin)?.isVisible = isAdmin

        /**
         *takip bildirimi simülasyonu
         *user tekrar giriş yaptığında bu bildirimler burada gösterilir ve sonra kuyruktan temizlenir
         */
        val me = DataStore.currentUser
        if (me != null && me.role == "USER") {

            //bu kullanıcıya ait bildirimler
            val myPending = DataStore.pendingNotifs.filter { it.toEmail == me.email }

            if (myPending.isNotEmpty()) {
                //bildirimleri göster
                myPending.forEach { p ->
                    NotificationHelper.notifyStatusChanged(this, p.title, p.message)
                }

                //gösterilenleri kuyruktan sil
                DataStore.pendingNotifs.removeAll { it.toEmail == me.email }
            }
        }

        /**
         *ilk açılışta akış ekranını göster
         * savedInstanceState null ise ilk kez açılıyor demekmiş
         */
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FeedFragment())
                .commit()
        }


         //bottomNav tıklamalarında ilgili Fragmente geç

        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.nav_feed -> FeedFragment()
                R.id.nav_map -> MapFragment()
                R.id.nav_create -> CreateReportFragment()
                R.id.nav_profile -> ProfileFragment()
                R.id.nav_admin -> AdminPanelFragment()
                else -> FeedFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit()

            true
        }
    }
}
