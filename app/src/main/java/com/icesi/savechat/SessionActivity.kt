package com.icesi.savechat

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.icesi.savechat.databinding.ActivitySessionBinding
import com.icesi.savechat.model.*
import java.util.*


class SessionActivity : AppCompatActivity() {

    private lateinit var currentUser: User
    private lateinit var partnerNick: String
    private var pin : String = ""

    private val binding: ActivitySessionBinding by lazy { ActivitySessionBinding.inflate(layoutInflater) }

    @RequiresApi(Build.VERSION_CODES.S)
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
            pin = binding.passwordSesion.text.toString()
            if(pin == "")Toast.makeText(this, R.string.empty_field, Toast.LENGTH_SHORT).show()
            else{
                var partnerEmail = binding.emailPartner.text.toString()
                if(checkEmail(partnerEmail)) checkPartnerExist(partnerEmail.lowercase())
                else Toast.makeText(this,R.string.not_valid_email,Toast.LENGTH_SHORT).show()
            }
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

    @RequiresApi(Build.VERSION_CODES.S)
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

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startSession(partnerEmail: String, partnerNick: String){
        Firebase.firestore.collection("users")
            .document(currentUser.email)
            .collection("sessions")
            .whereEqualTo("idPartner", partnerEmail)
            .get()
            .addOnSuccessListener {
                if (it.size() != 0) {
                    var sessionInformation = it.documents[0].toObject(Session::class.java)
                    goToChat(sessionInformation!!, partnerNick)
                } else {
                    createNewChat(partnerEmail)
                }
            }
    }

    /**
     * Email is the partner email
     */


    @RequiresApi(Build.VERSION_CODES.S)
    private fun createNewChat(partnerEmail: String) {

        var g = DiffieHellman().calculateG()
        var p = DiffieHellman().calculateP()
        var iv = Cipher().generateIv().iv
        var ivToString = iv.contentToString()
        Firebase.firestore
            .collection("chats")
            .add(hashMapOf("p" to p, "g" to g, "iv" to ivToString)).addOnSuccessListener {
                val idChat = it.id
                Firebase.firestore
                    .collection("chats")
                    .document(idChat)
                    .collection("messages").add(
                        Message(
                            currentUser.email,
                            "Hola! :), estamos esperando a que tu compañero termine el intercambio",
                            Timestamp.now()
                        )
                    )
                createNewSession(idChat,partnerEmail, currentUser.email, p,g,0, ivToString)
                createNewSession(idChat,currentUser.email, partnerEmail, p,g,DiffieHellman().calculateTx(Integer.parseInt(pin),g,p), ivToString)
                goToChat(Session(partnerEmail,idChat,0,p,g, ivToString), partnerNick)
            }
    }

    private fun createNewSession(idChat:String, partnerEmail: String, currentEmail: String, p:Long,g:Int, ty:Long, iv:String){
        val s = Session(partnerEmail,idChat, ty, p, g, iv)
        Firebase.firestore.collection("users")
            .document(currentEmail)
            .collection("sessions")
            .document(partnerEmail)
            .set(s).addOnSuccessListener {
                Log.e(">>>", "Se ha creado la session")
            }
    }


    private fun goToChat(s:Session, partnerNick: String) {
        startActivity(Intent(this, ChatActivity::class.java).apply {
            putExtra("sessionInformation", Gson().toJson(s))
            putExtra("currentUser", Gson().toJson(currentUser))
            putExtra("partnerNick", partnerNick)
            putExtra("pin", pin)
        })
    }

    private fun checkEmail(email: String ): Boolean{
        return email.contains("@")
    }
}
