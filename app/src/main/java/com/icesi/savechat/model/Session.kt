package com.icesi.savechat.model

data class Session(
    var idPartner: String ="",
    var idChat: String ="",
    var ty : Int = 0,
    var p : Int = 0,
    var g : Int = 0,
    var iv : String = ""
)