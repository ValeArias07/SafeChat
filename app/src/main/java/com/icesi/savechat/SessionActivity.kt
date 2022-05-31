package com.icesi.savechat

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.icesi.savechat.databinding.ActivityLoginBinding
import com.icesi.savechat.databinding.ActivitySessionBinding
import com.icesi.savechat.model.User
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logOutButton.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        var email: String = Firebase.auth.currentUser?.email.toString()

        if ( Firebase.auth.currentUser.toString() == "null") {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            Firebase.firestore.collection("users").document(email).get().addOnSuccessListener {
                //currentUser = it.toObject(User::class.java)!!
                binding.userNick.text = it.toObject(User::class.java)?.nick.toString()

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cipherPilot(){
        val input = "valentina"
        val key: SecretKey = getKeyFromPassword("almohabana", "77")

        val ivParameterSpec: IvParameterSpec = generateIv()
        val algorithm = "AES/CBC/PKCS5Padding"
        val cipherText: String = encrypt(algorithm, input, key, ivParameterSpec)
        val plainText: String = decrypt(algorithm, cipherText, key, ivParameterSpec)

        Log.e("Plain: ", input)
        Log.e("Cipher: ", cipherText)
        Log.e("Plain: ", plainText)
    }


    fun generateKey(n: Int): SecretKey {
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(n)
        return keyGenerator.generateKey()
    }

    fun generateIv(): IvParameterSpec {
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        return IvParameterSpec(iv)
    }

    fun getKeyFromPassword(password: String, salt: String): SecretKey {
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), 65536, 256)
        return SecretKeySpec(
            factory.generateSecret(spec)
                .getEncoded(), "AES"
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(algorithm: String?, input: String, key: SecretKey?,
        iv: IvParameterSpec?
    ): String {
        val cipher: Cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val cipherText: ByteArray = cipher.doFinal(input.toByteArray())
        return Base64.getEncoder()
            .encodeToString(cipherText)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(algorithm: String?, cipherText: String?, key: SecretKey?,
        iv: IvParameterSpec?
    ): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val plainText = cipher.doFinal(
            Base64.getDecoder()
                .decode(cipherText)
        )
        return String(plainText)
    }
}