package com.icesi.savechat.model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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

class Cipher {

    @RequiresApi(Build.VERSION_CODES.O)
    fun cipherPilot() {
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
    fun encrypt(
        algorithm: String?, input: String, key: SecretKey?,
        iv: IvParameterSpec?
    ): String {
        val cipher: Cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val cipherText: ByteArray = cipher.doFinal(input.toByteArray())
        return Base64.getEncoder()
            .encodeToString(cipherText)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(
        algorithm: String?, cipherText: String?, key: SecretKey?,
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