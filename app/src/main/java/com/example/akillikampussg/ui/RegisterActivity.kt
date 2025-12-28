package com.example.akillikampussg.ui

import com.example.akillikampussg.DataStore
import com.example.akillikampussg.MainActivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.akillikampussg.R
import java.util.UUID

/**
 *RegisterActivity
 *kullanıcının kayıt olduğu ekran
 *bu ekranda
 *ad soyad, e-posta, şifre, birim bilgileri alınır
 *kayıt kontrolü yapılır e posta için
 *rol otomatik atanır
 *kayıt sonrası kullanıcı oturumu başlatılıp MainActivity'e geçilir
 */
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //register ekranı layout'u
        setContentView(R.layout.activity_register)

        //UI bileşenleri
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etUnit = findViewById<EditText>(R.id.etUnit)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvBackLogin = findViewById<TextView>(R.id.tvBackLogin)


         //Kayıt Ol butonu

        btnRegister.setOnClickListener {

            //formdan değerleri al
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString()
            val unit = etUnit.text.toString().trim()

            //basit doğrulama boş alan kontrolü
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || unit.isEmpty()) {
                Toast.makeText(this, "Tüm alanları doldurmalısın.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /**
             *rol atama kuralı
             *e-posta @admin.com ile bitiyorsa admin değilse kullanıcı
             */
            val role = if (email.endsWith("@admin.com", ignoreCase = true)) "ADMIN" else "USER"

            //yeni kullanıcı modeli oluştur
            val user = User(
                id = UUID.randomUUID().toString(),
                nameSurname = name,
                email = email,
                password = pass,
                unit = unit,
                role = role
            )

            /**
             *DataStore.register ile kullanıcı eklenir
             *eğer aynı e-posta daha önce kayıtlıysa false döner
             */
            val ok = DataStore.register(user)

            if (!ok) {
                Toast.makeText(this, "Bu e-posta zaten kayıtlı.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //kayıt başarılı ise kullanıcıya bilgi ver
            Toast.makeText(this, "Kayıt başarılı! Rol: $role", Toast.LENGTH_SHORT).show()

            /**
             *kayıt sonrası otomatik giriş
             *MainActivity'e geçilir
             */
            DataStore.currentUser = user
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


         //girişe dön yazısına basılınca bu activity kapanır ve Login'e dönülür

        tvBackLogin.setOnClickListener {
            finish()
        }
    }
}
