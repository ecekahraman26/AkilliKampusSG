package com.example.akillikampussg.ui

/**
 *User
 *uygulamadaki bir kullanıcıyı temsil eden veri sınıfıdır
 *login, register, profil, admin kontrolü gibi tüm kullanıcı işlemlerinde kullanılır
 *bu sınıf sayesinde
 *kullanıcı bilgileri tek bir yapıda tutulur
 *rol bazlı yetkilendirme yapılabilir
 *bildirim ve admin panelleri kullanıcı rolüne göre yönetilir
 */
data class User(

    //kullanıcının benzersiz kimliği
    val id: String,

    //kullanıcının ad ve soyadı
    val nameSurname: String,

    //kullanıcının e-posta adresi giriş için
    val email: String,

    //kullanıcının şifresi
    val password: String,

    //kullanıcının bağlı olduğu birim admin filtreleme ve profil ekranı için
    val unit: String,

    /**
     *kullanıcı rolü
     *user= normal kullanıcı (bildirim oluşturur takip eder)
     *admin= yönetici (bildirimleri yönetir acil duyuru yayınlar)
     *rol ataması RegisterActivity içinde otomatik yapılır
     */
    val role: String // "USER" / "ADMIN"
)
