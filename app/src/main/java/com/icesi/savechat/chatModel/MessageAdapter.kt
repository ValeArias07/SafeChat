package com.icesi.umarket.model

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.message_row,parent,false)
        val messageViewHolder = MessageViewHolder(view)

        return messageViewHolder
    }

    fun setData(list: MutableList<QueryDocumentSnapshot>?){
        var auxMessages = ArrayList<Message>()
        list?.onEach { task ->
            auxMessages.add(task.toObject(Message::class.java))
        }
        messages = auxMessages
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bindMessage(message, user.email)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: Message){
        messages.add(message)
        notifyItemInserted(messages.size-1)
    }

    fun size(): Int{
        return messages.size
    }
}