package com.icesi.savechat.chatModel

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.icesi.savechat.R
import com.icesi.savechat.model.Message


class MessageViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {

    //STATE
    var message: Message? = null

    //UI controllers
    var myMsgLayout: ConstraintLayout = itemView.findViewById(R.id.myMessageLayout)
    var partnerLayout: ConstraintLayout = itemView.findViewById(R.id.partnerMessageLayout)

    var myMsgText: TextView = itemView.findViewById(R.id.myMsgText)
    var partnerMsgText: TextView = itemView.findViewById(R.id.partnerMsgText)

    init {
    }

    fun bindMessage(message: Message, email: String){
        this.message = message
        if (email == message.from) {
            myMsgLayout.visibility = View.VISIBLE
            myMsgText.text = message.msg

            partnerLayout.visibility = View.GONE
        } else {
            myMsgLayout.visibility = View.GONE
            partnerMsgText.text = message.msg

            partnerLayout.visibility = View.VISIBLE
        }
    }
}