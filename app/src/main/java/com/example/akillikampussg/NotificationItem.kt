package com.example.akillikampussg

/**
 *NotificationItem
 *uygulamadaki tek bir bildirimi temsil eden veri sınıfıdır
 *feed, harita, detay ve admin panel ekranlarında kullanılan ortak yapıdır
 *bu sınıf sayesinde
 *bildirimler listelenebilir
 *harita üzerinde konumlandırılabilir
 *admin tarafından yönetilebilir
 *kullanıcı tarafından takip edilebilir
 */
data class NotificationItem(

    //bildirimin benzersiz kimliği
    val id: String,

    //bildirim türü Genel, Güvenlik, Temizlik, Arıza, Kayıp-Buluntu
    val type: String,

    //bildirim başlığı
    val title: String,

    //bildirimin detay açıklaması
    val desc: String,

    //oluşturulma zamanı
    val createdAt: Long,

    //bildirimin durumu
    //admin tarafından değiştirilebilir olduğu için var
    var status: String,

    //bildirimi oluşturan kullanıcının eposta bilgisi
    val creatorEmail: String,

    //bildirimi oluşturan kullanıcının birimi
    val unit: String,

    /**
     *konum bilgisi
     *görülen değerler kampüs sınırları içinde örnek koordinatlardır
     */
    val lat: Double = 39.90,
    val lng: Double = 41.27,

    /**
     *bildirimi takip eden kullanıcıların e-posta listesi
     *aynı kullanıcı birden fazla kez eklenmesin diye
     *takip takipten çık işlemleri kolay olsun diye aşağıdaki parantezi kullandık
     */
    var followers: MutableSet<String> = mutableSetOf()
)
