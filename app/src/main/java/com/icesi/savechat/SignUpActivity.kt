package com.icesi.savechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.icesi.savechat.databinding.ActivitySignUpBinding
import com.icesi.savechat.model.User

class SignUpActivity : AppCompatActivity() {

    private val binding: ActivitySignUpBinding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.signUpButton.setOnClickListener { validateUser() }
    }

    private fun validateUser() {

        var email: String = binding.emailUserSignUp.text.toString()
        var password: String = binding.passwordUserSignUp.text.toString()
        var nick: String = binding.nicknameUser.text.toString()

        if(checkEmail(email)){
            if(checkPassword(password)) createUser(nick, email,password) else Toast.makeText(this, R.string.not_valid_password, Toast.LENGTH_SHORT).show()
        }else Toast.makeText(this, R.string.not_valid_email, Toast.LENGTH_SHORT).show()
    }

    private fun createUser(nick: String, email: String, password: String){
        var user = User(nick, email, password)

        Firebase.auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener{
            Firebase.firestore.collection("users").document(email).set(user)
            Firebase.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                startActivity(Intent(this, SessionActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("user", Gson().toJson(user))
                })
                finish()
            }
        }.addOnFailureListener{
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkEmail(email: String ): Boolean{
        return email.contains("@")
    }

    private fun checkPassword(password: String ): Boolean{
        return true
    }
}