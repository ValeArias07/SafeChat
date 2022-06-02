package com.icesi.savechat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.icesi.savechat.databinding.ActivitySessionBinding
import com.icesi.savechat.model.Message
import com.icesi.savechat.model.Nick
import com.icesi.savechat.model.Session
import com.icesi.savechat.model.User
import java.util.*


class SessionActivity : AppCompatActivity() {

    private lateinit var currentUser: User
    private lateinit var partnerNick: String

    private val binding: ActivitySessionBinding by lazy { ActivitySessionBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkCurrentUser()

        binding.logOutButton.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.startChatButton.setOnClickListener {
            var partnerEmail = binding.emailPartner.text.toString()
            if(checkEmail(partnerEmail)) checkPartnerExist(partnerEmail.lowercase())
            else Toast.makeText(this,R.string.not_valid_email,Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCurrentUser(){
        if (Firebase.auth.currentUser  == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            var email: String = Firebase.auth.currentUser?.email.toString()
            Firebase.firestore.collection("users").document(email).get().addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if(user!=null) currentUser = user!!
                else Log.e(">>>","CURRENT USER FROM FIRESTORE IS NULL FOR $email")

                binding.userNick.text = user?.nick.toString()
            }
        }
    }

    private fun checkPartnerExist(partnerEmail: String) {
        Firebase.firestore
            .collection("users")
            .document(partnerEmail)
            .get().addOnSuccessListener {
                if (it.data != null) {
                    partnerNick = it.toObject(Nick::class.java)!!.nick
                    startSession(partnerEmail, partnerNick)
                }else {
                    Toast.makeText(this, "El partner no existe", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun startSession(partnerEmail: String, partnerNick: String){
        Firebase.firestore.collection("users")
            .document(currentUser.email)
            .collection("sessions")
            .whereEqualTo("idPartner", partnerEmail)
            .get()
            .addOnSuccessListener {
                if (it.size() != 0) {
                    var sessionInformation = it.documents[0].toObject(Session::class.java)
                    goToChat(sessionInformation!!.idPartner, sessionInformation!!.idChat, partnerNick)
                } else {
                    createNewSession(partnerEmail, currentUser.email, true)
                }
            }
    }

    /**
     * Email is the partner email
     */
    private fun createNewSession(partnerEmail: String, currentEmail: String, isCurrentUser: Boolean){
        val idChat = UUID.randomUUID().toString()
        Firebase.firestore.collection("users")
            .document(currentEmail)
            .collection("sessions")
            .document(partnerEmail)
            .set(Session(partnerEmail, idChat)).addOnSuccessListener {
                if(isCurrentUser){
                    createNewChat(idChat, partnerEmail)
                    createNewSession(idChat, currentEmail, partnerEmail, false)
                    Toast.makeText(this, "Sesion creada", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createNewSession(idChat:String, partnerEmail: String, currentEmail: String, isCurrentUser: Boolean){
        Firebase.firestore.collection("users")
            .document(currentEmail)
            .collection("sessions")
            .document(partnerEmail)
            .set(Session(partnerEmail, idChat)).addOnSuccessListener {
                if(isCurrentUser){
                    createNewChat(idChat, partnerEmail)
                    createNewSession(idChat, currentEmail, partnerEmail, false)
                    Toast.makeText(this, "Sesion creada", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createNewChat(idChat: String, partnerEmail: String) {
        Firebase.firestore
            .collection("chats")
            .document(idChat)
            .collection("messages")
            .document(UUID.randomUUID().toString())
            .set(
                Message(
                    currentUser.email,
                    "Hola! Este es nuestro primer mensaje :)",
                    Timestamp.now()
                )
            )
        goToChat(partnerEmail, idChat, partnerNick)
    }

    private fun goToChat(partnerEmail: String, idChat: String, partnerNick: String) {
        startActivity(Intent(this, ChatActivity::class.java).apply {
            putExtra(
                "sessionInformation", Gson().toJson(
                    Session(partnerEmail, idChat)
                )
            )
            putExtra("currentUser", Gson().toJson(currentUser))
            putExtra("partnerNick", partnerNick)
        })
    }

    private fun checkEmail(email: String ): Boolean{
        return email.contains("@")
    }
}
