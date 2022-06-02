package com.icesi.savechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.icesi.savechat.databinding.ActivityChatBinding
import com.icesi.savechat.model.Message
import com.icesi.savechat.model.Session
import com.icesi.savechat.model.User
import com.icesi.savechat.chatModel.MessageAdapter
import com.icesi.savechat.chatModel.MessageViewHolder
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var adapter: MessageAdapter
    private lateinit var sessionInformation: Session
    private lateinit var currentUser: User
    private lateinit var partnerNick: String

    private val binding: ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadExtras()
        recyclerConfig()
        loadMsgs()

        binding.sendMsgButton.setOnClickListener {
            sendMsg()
        }
    }

    private fun recyclerConfig(){
        adapter = MessageAdapter()
        adapter.user = currentUser
        binding.recyclerMsgView.setHasFixedSize(true)
        binding.recyclerMsgView
        binding.recyclerMsgView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerMsgView.adapter = adapter
    }

    private fun sendMsg(){
        var message = Message(currentUser.email, binding.messageTextBox.text.toString(), Timestamp.now())
        //ENCRIPTAR
        Firebase.firestore
            .collection("chats")
            .document(sessionInformation.idChat)
            .collection("messages")
            .document(UUID.randomUUID().toString())
            .set(message)
            .addOnSuccessListener {
                binding.messageTextBox.setText("")
            }
        loadNewMsg()
    }

    private fun loadMsgs(){
        Firebase.firestore
            .collection("chats")
            .document(sessionInformation.idChat)
            .collection("messages")
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { task ->
                for (doc in task.documents) {
                    var message = doc.toObject(Message::class.java)!!
                    adapter.addMessage(message)
                }
                binding.recyclerMsgView.scrollToPosition(adapter.size() - 1)
            }
    }

    private fun loadNewMsg(){
        Firebase.firestore
            .collection("chats")
            .document(sessionInformation.idChat)
            .collection("messages")
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { messages, error ->
                if (error == null) {
                    // messages.let {
                    // var list = it?.toMutableList()
                    // adapter.setData(list)
                    // binding.recyclerMsgView.scrollToPosition(adapter.size() - 1);
                    // }
                    for(change in messages!!.documentChanges){
                        when(change.type){
                            DocumentChange.Type.ADDED->{
                                val message = change.document.toObject((Message::class.java))
                                adapter.addMessage(message)
                                //binding.recyclerMsgView.scrollToPosition(adapter.size() - 1)
                            }
                            else -> {}
                        }
                    }
                }
            }
    }

    private fun loadExtras(){
        sessionInformation = Gson().fromJson(
            intent.extras?.getString("sessionInformation", ""),
            Session::class.java
        )

        currentUser = Gson().fromJson(
            intent.extras?.getString("currentUser", ""),
            User::class.java
        )

        partnerNick = (intent.extras?.getString("partnerNick", "").toString())
        binding.partnerName.text = partnerNick
    }
}