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

/**
 *LoginActivity
 *kullanıcının e-posta ve şifre ile giriş yaptığı ekrandır
 *bu ekranda
 *giriş kontrolü yapılır
 *başarılı girişte MainActivity'e yönlendirilir
 *hatalı girişte kullanıcı bilgilendirilir
 *kayıt ol ve şifremi unuttum ekranlarına geçiş sağlanır
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


         //eğer kullanıcı zaten giriş yapmışsabtekrar login ekranı göstermeden direkt ana ekrana yönlendir

        if (DataStore.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        //login ekranı layout'u
        setContentView(R.layout.activity_login)

        //UI bileşenleri
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        val tvForgot = findViewById<TextView>(R.id.tvForgot)

         //giriş Yap butonu

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString()

            //basit form doğrulama
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "E-posta ve şifre boş olamaz.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /**
             *DataStore üzerinden login kontrolü yapılır
             *başarılıysa güncelkullanıcı atanır
             */
            val ok = DataStore.login(email, pass)

            if (ok) {
                Toast.makeText(this, "Giriş başarılı!", Toast.LENGTH_SHORT).show()

                //ana ekrana geç
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            } else {
                Toast.makeText(
                    this,
                    "Hatalı giriş: E-posta veya şifre yanlış.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


         //Kayıt Ol ekranına geçiş

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }


         //Şifremi Unuttum ekranına geçiş

        tvForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}
