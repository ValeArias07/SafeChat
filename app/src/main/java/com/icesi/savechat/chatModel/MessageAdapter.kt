package com.icesi.savechat.chatModel

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.icesi.savechat.R
import com.icesi.savechat.model.Message
import com.icesi.savechat.model.User


class MessageAdapter: RecyclerView.Adapter<MessageViewHolder>() {

    lateinit var user: User
    private var messages = ArrayList<Message>()

    var listener : Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.message_row, parent, false)

        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        if(position <= 1)holder.setSystemTextMode()
        else holder.setNormalTextMode()
        holder.bindMessage(message, user.email)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: Message){
        if(messages.size <=1) messages.add(message)
        else messages.add(message.apply { msg = listener!!.onDecrypt(message.msg) })

        notifyItemInserted(messages.size-1)
        Log.e(">>>","size adapater: ${messages.size}")
    }

    fun size(): Int{
        return messages.size
    }

    interface Listener {
        fun onDecrypt(m:String):String
    }
}