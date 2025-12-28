package com.example.akillikampussg

import com.example.akillikampussg.ui.User

/**
 * DataStore:
 * veritabanı yerine proje için simülasyon amaçlı yapıldı
 */
object DataStore {

    //kampüs içinde oluşturulan tüm bildirimler
    val items = mutableListOf<NotificationItem>()

    //sisteme kayıtlı kullanıcılar
    val users = mutableListOf<User>()

    //o an giriş yapan kullanıcı
    var currentUser: User? = null

    //harita ekranında seçilen konum
    var selectedLat: Double? = null
    var selectedLng: Double? = null


     //takip edilen bildirimlerde durum değiştiğinde kullanıcıya gösterilmek için tutulan bekleyen bildirimler
    data class PendingNotif(
        val toEmail: String,        // bildirimin gönderileceği kullanıcı
        val title: String,          // bildirim başlığı
        val message: String,        // bildirim içeriği
        val createdAt: Long = System.currentTimeMillis()
    )

    //bekleyen takip bildirimleri
    val pendingNotifs: MutableList<PendingNotif> = mutableListOf()


     //kullanıcı giriş yapmış mı kontrolü

    fun isLoggedIn(): Boolean = currentUser != null


     //kullanıcı çıkış yapar
     //oturum ve geçici konum bilgileri temizlenir

    fun logout() {
        currentUser = null
        selectedLat = null
        selectedLng = null
    }


     //yeni kullanıcı kaydı
     //aynı eposta ile kayıt varsa false döner

    fun register(user: User): Boolean {
        val exists = users.any { it.email.equals(user.email, ignoreCase = true) }
        if (exists) return false
        users.add(user)
        return true
    }


     //kullanıcı giriş işlemi
     //eposta ve şifre eşleşirse kullanıcı oturuma alınır

    fun login(email: String, password: String): Boolean {
        val found = users.find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
        currentUser = found
        return found != null
    }
}
