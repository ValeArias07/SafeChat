package com.icesi.savechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.icesi.savechat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.loginButton.setOnClickListener {
            var email = binding.emailUser.text.toString()
            var password = binding.passwordUser.text.toString()
            Firebase.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                startActivity(Intent(this, SessionActivity::class.java))
            }.addOnFailureListener {
                Toast.makeText(this.baseContext, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.hyperText.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}