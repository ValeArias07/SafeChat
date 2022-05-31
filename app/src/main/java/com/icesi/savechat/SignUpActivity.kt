package com.icesi.savechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.icesi.savechat.databinding.ActivitySignUpBinding
import com.icesi.savechat.model.User

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpButton.setOnClickListener {
            validateUser()
        }
    }

    private fun validateUser() {

        var email: String = binding.emailUserSignUp.text.toString()
        var password: String = binding.passwordUserSignUp.text.toString()

        if(checkEmail(email) && checkPassword(password)){

            var user: User = User(binding.nicknameUser.text.toString(), email, password)
            Firebase.auth.createUserWithEmailAndPassword(email,password).continueWith{
                Firebase.firestore.collection("users").document(email).set(user)
                Firebase.auth.signInWithEmailAndPassword(email, password)
                startActivity(Intent(this, SessionActivity::class.java).apply {
                    putExtra("user", Gson().toJson(user))
                })
            }
        }
    }

    private fun checkEmail(email: String ): Boolean{
        return email.contains("@")
    }

    private fun checkPassword(password: String ): Boolean{
        return true
    }
}