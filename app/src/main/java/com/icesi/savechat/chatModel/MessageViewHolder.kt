package com.icesi.savechat.chatModel

import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.icesi.savechat.R
import com.icesi.savechat.model.Message


class MessageViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {

    private val messageLengthMax = 20
    //STATE
    var message: Message? = null

    //UI controllers
    var myMsgLayout: ConstraintLayout = itemView.findViewById(R.id.myMessageLayout)
    var partnerLayout: ConstraintLayout = itemView.findViewById(R.id.partnerMessageLayout)

    private var myMsgTextShort: TextView = itemView.findViewById(R.id.myMsgTextShort)
    private var myMsgTextLarge: TextView = itemView.findViewById(R.id.myMsgTextLarge)
    private var partnerMsgTextShort: TextView = itemView.findViewById(R.id.partnerMsgTextShort)
    private var partnerMsgTextLarge: TextView = itemView.findViewById(R.id.partnerMsgTextLarge)

    init {
    }

    fun bindMessage(message: Message, email: String){
        this.message = message
        if (email == message.from) {
            myMsgLayout.visibility = View.VISIBLE
            partnerLayout.visibility = View.GONE
            if (message.msg.length >= messageLengthMax) {
                myMsgTextLarge.text = message.msg
                myMsgTextShort.visibility = View.GONE
                myMsgTextLarge.visibility = View.VISIBLE
            }else{
                myMsgTextShort.text = message.msg
                myMsgTextLarge.visibility = View.GONE
                myMsgTextShort.visibility = View.VISIBLE
            }
        } else {
            myMsgLayout.visibility = View.GONE
            partnerLayout.visibility = View.VISIBLE
            if (message.msg.length >= messageLengthMax) {
                partnerMsgTextLarge.text = message.msg
                partnerMsgTextShort.visibility = View.GONE
                partnerMsgTextLarge.visibility = View.VISIBLE
            } else {
                partnerMsgTextShort.text = message.msg
                partnerMsgTextLarge.visibility = View.GONE
                partnerMsgTextShort.visibility = View.VISIBLE
            }
        }
    }

    fun setSystemTextMode(){
        myMsgTextShort.setTypeface(null, Typeface.ITALIC)
        myMsgTextLarge.setTypeface(null, Typeface.ITALIC)
        partnerMsgTextShort.setTypeface(null, Typeface.ITALIC)
        partnerMsgTextLarge.setTypeface(null, Typeface.ITALIC)
    }

    fun setNormalTextMode(){
        myMsgTextShort.setTypeface(null, Typeface.NORMAL)
        myMsgTextLarge.setTypeface(null, Typeface.NORMAL)
        partnerMsgTextShort.setTypeface(null, Typeface.NORMAL)
        partnerMsgTextLarge.setTypeface(null, Typeface.NORMAL)
    }
}