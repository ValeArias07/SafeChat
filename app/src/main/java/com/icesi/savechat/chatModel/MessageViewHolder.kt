package com.icesi.savechat.chatModel

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.icesi.savechat.R
import com.icesi.savechat.model.Message


class MessageViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {

    private val messageLengthMax = 26

    //STATE
    var message: Message? = null

    //UI controllers
    var myMsgLayout: ConstraintLayout = itemView.findViewById(R.id.myMessageLayout)
    var partnerLayout: ConstraintLayout = itemView.findViewById(R.id.partnerMessageLayout)

    var myMsgTextShort: TextView = itemView.findViewById(R.id.myMsgTextShort)
    var myMsgTextLarge: TextView = itemView.findViewById(R.id.myMsgTextLarge)
    var partnerMsgTextShort: TextView = itemView.findViewById(R.id.partnerMsgTextShort)
    var partnerMsgTextLarge: TextView = itemView.findViewById(R.id.partnerMsgTextLarge)

    init {
    }

    fun bindMessage(message: Message, email: String){
        this.message = message
        if (email == message.from) {
            myMsgLayout.visibility = View.VISIBLE
            partnerLayout.visibility = View.GONE
            if (message.msg.length >= messageLengthMax) {
                myMsgTextShort.visibility = View.GONE
                myMsgTextLarge.visibility = View.VISIBLE
                myMsgTextLarge.text = message.msg
            }else{
                myMsgTextLarge.visibility = View.GONE
                myMsgTextShort.visibility = View.VISIBLE
                myMsgTextShort.text = message.msg
            }
        } else {
            myMsgLayout.visibility = View.GONE
            partnerLayout.visibility = View.VISIBLE
            if (message.msg.length >= messageLengthMax) {
                partnerMsgTextShort.visibility = View.GONE
                partnerMsgTextLarge.visibility = View.VISIBLE
                partnerMsgTextLarge.text = message.msg
            } else {
                partnerMsgTextLarge.visibility = View.GONE
                partnerMsgTextShort.visibility = View.VISIBLE
                partnerMsgTextShort.text = message.msg
            }
        }
    }
}