package com.icesi.savechat.model

import com.google.firebase.Timestamp

data class Message(
    var from: String ="",
    var msg: String="",
    var date: Timestamp? = null
        )