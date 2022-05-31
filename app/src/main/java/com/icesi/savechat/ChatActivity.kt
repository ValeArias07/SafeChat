package com.icesi.savechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.icesi.savechat.databinding.ActivityChatBinding
import com.icesi.savechat.model.Message
import com.icesi.savechat.model.Nick
import com.icesi.savechat.model.Session
import com.icesi.savechat.model.User
import com.icesi.umarket.model.MessageAdapter
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: MessageAdapter
    private lateinit var sesionInformation: Session
    private lateinit var currentUser: User
    private lateinit var partnerNick: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MessageAdapter()


        binding.recyclerMsgView.setHasFixedSize(true)
        binding.recyclerMsgView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )

        binding.recyclerMsgView.adapter = adapter

        sesionInformation = Gson().fromJson(
            intent.extras?.getString("sessionInformation", ""),
            Session::class.java
        )

        currentUser = Gson().fromJson(
            intent.extras?.getString("currentUser", ""),
            User::class.java
        )
        searchPartnerNick(sesionInformation.idPartner)
        adapter.user = currentUser

        sesionInformation.let {
            binding.recyclerMsgView.scrollToPosition(adapter.size() - 1);
            Firebase.firestore
                .collection("chats")
                .document(sesionInformation.idChat)
                .collection("messages")
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { task ->
                    for (doc in task.documents) {
                        var message = doc.toObject(Message::class.java)!!
                        adapter.addMessage(message)
                    }
                }
        }

        binding.sendMsgButton.setOnClickListener {
            binding.recyclerMsgView.scrollToPosition(adapter.size() - 1);
            var message: Message = Message(
                currentUser.email,
                binding.messageTextBox.text.toString(),
                Timestamp.now()
            )
            Firebase.firestore
                .collection("chats")
                .document(sesionInformation.idChat)
                .collection("messages")
                .document(UUID.randomUUID().toString())
                .set(message)
                .addOnSuccessListener {
                    binding.messageTextBox.setText("")
                }

            Firebase.firestore
                .collection("chats")
                .document(sesionInformation.idChat)
                .collection("messages")
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener { messages, error ->
                    if(error == null){
                        messages.let{
                            var list = it?.toMutableList()
                            adapter.setData(list)
                        }
                    }
                }
                }
            binding.messageTextBox.setText("")
        }

    fun searchPartnerNick(partnerEmail: String){
        Firebase.firestore
            .collection("users")
            .document(partnerEmail)
            .get().addOnSuccessListener {
                partnerNick = it.toObject(Nick::class.java)!!.nick
                binding.partnerName.text = partnerNick
            }
    }
}