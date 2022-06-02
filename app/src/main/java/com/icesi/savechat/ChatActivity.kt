package com.icesi.savechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.icesi.savechat.databinding.ActivityChatBinding
import com.icesi.savechat.chatModel.MessageAdapter
import com.icesi.savechat.model.*
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class ChatActivity : AppCompatActivity(), MessageAdapter.Listener {
    private lateinit var adapter: MessageAdapter
    private lateinit var sessionInformation: Session
    private lateinit var currentUser: User
    private lateinit var partnerNick: String
    private lateinit var userPin : String

    private var key: SecretKey? = null
    private val iv get() = IvParameterSpec(sessionInformation.iv.toByteArray(java.nio.charset.StandardCharsets.UTF_8))

    private val binding: ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //DEFAULT WHILE LOAD
        binding.sendMsgButton.isEnabled = false
        binding.messageTextBox.isEnabled = false

        loadExtras()
        recyclerConfig()
        loadMsgs()

        binding.sendMsgButton.setOnClickListener {
            sendMsg()
        }
    }

    private fun loadExtras(){
        sessionInformation = Gson().fromJson(intent.extras?.getString("sessionInformation", ""),Session::class.java)
        currentUser = Gson().fromJson(intent.extras?.getString("currentUser", ""), User::class.java)
        partnerNick = (intent.extras?.getString("partnerNick", "").toString())
        userPin = (intent.extras?.getString("pin", "0").toString())

        binding.partnerName.text = partnerNick
    }

    private fun recyclerConfig(){
        adapter = MessageAdapter()
        adapter.user = currentUser
        adapter.listener = this
        binding.recyclerMsgView.setHasFixedSize(true)
        binding.recyclerMsgView
        binding.recyclerMsgView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerMsgView.adapter = adapter
    }

    private fun loadMsgs(){
        Firebase.firestore
            .collection("chats")
            .document(sessionInformation.idChat)
            .collection("messages")
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { task ->
                if (task.size() in 0..2){
                    for (doc in task.documents) {
                        var message = doc.toObject(Message::class.java)!!
                        adapter.addMessage(message)
                    }
                    verifyStartMessage()
                }else{
                    initKey()
                    for (doc in task.documents) {
                        var message = doc.toObject(Message::class.java)!!
                        adapter.addMessage(message)
                    }
                    binding.sendMsgButton.isEnabled = true
                    binding.messageTextBox.isEnabled = true
                    loadNewMsg()
                }
                binding.recyclerMsgView.scrollToPosition(adapter.size() - 1)
            }
    }

    private fun verifyStartMessage(){
        if (sessionInformation.ty == 0){
            loadNewMsg()
        }else if(adapter.size()==1){
            Firebase.firestore.collection("users")
                .document(sessionInformation.idPartner)
                .collection("sessions")
                .document(currentUser.email)
                .update("ty", DiffieHellman().calculateTx(Integer.parseInt(userPin),sessionInformation.g, sessionInformation.p))
                .addOnSuccessListener {
                    Firebase.firestore
                        .collection("chats")
                        .document(sessionInformation.idChat)
                        .collection("messages").add(
                            Message(
                                currentUser.email,
                                "Intercambio terminado! :)",
                                Timestamp.now()
                            )
                        ).addOnSuccessListener {
                            binding.sendMsgButton.isEnabled = true
                            binding.messageTextBox.isEnabled = true
                            initKey()
                            loadNewMsg()
                        }
                }
        }else{
            binding.sendMsgButton.isEnabled = true
            binding.messageTextBox.isEnabled = true
            initKey()
            loadNewMsg()
        }
    }

    private fun sendMsg(){
        var message = Message(currentUser.email, binding.messageTextBox.text.toString(), Timestamp.now())
        val encryptedMessage = Cipher().encrypt(message.msg,key,iv)
        message.msg = encryptedMessage

        Firebase.firestore
            .collection("chats")
            .document(sessionInformation.idChat)
            .collection("messages")
            .document(UUID.randomUUID().toString())
            .set(message)
            .addOnSuccessListener {
                binding.messageTextBox.setText("")
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
                    for(change in messages!!.documentChanges){
                        when(change.type){
                            DocumentChange.Type.ADDED->{
                                if(adapter.size()==1){
                                    updateCurrentTy()
                                }
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

    private fun initKey(){
        val tyrx = DiffieHellman().generateSecurityNumber(sessionInformation.ty,Integer.parseInt(userPin),sessionInformation.p)
        key = Cipher().getKeyFromPassword(getString(tyrx), "77")
    }

    private fun updateCurrentTy(){
        Firebase.firestore.collection("users")
            .document(currentUser.email)
            .collection("sessions")
            .document(sessionInformation.idPartner)
            .get()
            .addOnSuccessListener {
                if (it.data != null) {
                    val newTy = it.toObject(Session::class.java)!!.ty
                    sessionInformation.ty = newTy
                    if(newTy!=0){
                        initKey()
                        binding.sendMsgButton.isEnabled = true
                        binding.messageTextBox.isEnabled = true
                    }
                }else {
                    Toast.makeText(this, "El partner no existe", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDecrypt(msgEncrypt:String):String{
        return Cipher().decrypt(msgEncrypt, key, iv)
    }
}