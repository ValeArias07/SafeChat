package com.icesi.savechat.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.math.BigInteger
import java.util.*
import kotlin.math.pow


class DiffieHellman {
    private val bytesLength = 7
    private val bitsLength get() =  bytesLength * 4

    //Position 0 is G, position 1 is P
    @RequiresApi(Build.VERSION_CODES.S)
    fun calculateGP(): IntArray {
        val random = Random()
        val biginteger = BigInteger.probablePrime(bitsLength, random)
        val ra = Random()
        return intArrayOf(ra.nextInt(100) + 10, biginteger.intValueExact())
    }

    //x hace la operacion para calcular Tx e intercambiarlo con y
    fun calculateTx(randomx: Int, G: Int, P: Int): Int {
        val grx = G.toDouble().pow(randomx)
        return grx.toInt() % P
    }

    //x Toma el ty recibido, y su propio randomx
    fun generateSecurityNumber(ty: Int, randomx: Int, P: Int): Int {
        val tyrx = ty.toDouble().pow(randomx)
        return tyrx.toInt() % P
    }
}