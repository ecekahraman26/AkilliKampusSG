package com.example.akillikampussg.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.akillikampussg.R

/**
 *ForgotPasswordActivity
 *kullanıcının şifresini unuttuğu durumda kullanılan ekran
 *gerçek e-posta servisi kullanmadan kullanıcıya bilgilendirme mesajı göstererek süreci simüle ettim
 */
class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //activity layoutu yükle
        setContentView(R.layout.activity_forgot_password)

        //UI bileşenleri
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnSend = findViewById<Button>(R.id.btnSend)
        val tvInfo = findViewById<TextView>(R.id.tvInfo)

        /**
         *gönder butonuna basıldığında
         *e-posta boşsa kullanıcı uyarılır doluysa şifre sıfırlama maili gönderilmiş gibi bilgi mesajı gösterilir
         */
        btnSend.setOnClickListener {

            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                //basit doğrulama
                tvInfo.text = "Lütfen e-posta gir."
            } else {
                //simülasyon mesajı
                tvInfo.text =
                    "Şifre sıfırlama bağlantısı $email adresine gönderildi (simülasyon)."
            }
        }
    }
}
