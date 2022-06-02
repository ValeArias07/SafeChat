package com.icesi.savechat.model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import javax.crypto.KeyAgreement
import kotlin.math.pow


class DiffieHellman {
    private val bytesLength = 7
    private val bitsLength get() =  bytesLength * 4

    private val p : Long = 128903289023

    //Position 0 is G, position 1 is P
    @RequiresApi(Build.VERSION_CODES.S)
    /*
    fun calculateGP(): IntArray {
        val random = Random()
        val biginteger = BigInteger.probablePrime(bitsLength, random)
        val ra = Random()
        return intArrayOf(ra.nextInt(100) + 10, biginteger.intValueExact())
    }
     */

    fun calculateP(): Long {
        return p
    }

    fun calculateG():Int{
        val ra = Random()
        return ra.nextInt(3)+3
    }

    //x hace la operacion para calcular Tx e intercambiarlo con y
    fun calculateTx(randomx: Int, G: Int, P: Long): Long {
        val grx = G.toDouble().pow(randomx)
        val tx = grx.toInt() % P
        Log.e(">>>", "random recibido: $randomx y se generó el tx: $tx")
        return tx
    }

    //x Toma el ty recibido, y su propio randomx
    fun generateSecurityNumber(ty: Long, randomx: Int, P: Long): Long {
        val tyrx = ty.toDouble().pow(randomx)
        val securityNumber = tyrx.toInt() % P
        Log.e(">>>", "random recibido: $randomx y se generó el securityNumber: $securityNumber con el ty:$ty")
        return securityNumber
    }
}
