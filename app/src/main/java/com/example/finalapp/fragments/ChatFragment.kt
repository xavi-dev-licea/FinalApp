package com.example.finalapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalapp.R
import com.example.finalapp.adapters.ChatAdapter
import com.example.finalapp.models.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatFragment : Fragment() {

    private lateinit var _view: View
    //Variables para la instancia de la base de datos de firebase
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBReference:  CollectionReference
    //Variables del usuario logueado
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    //Adaptador del chat para el recycler
    private lateinit var adapter: ChatAdapter
    private val messageList: ArrayList<Messages> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        _view = inflater.inflate(R.layout.fragment_chat, container, false)

        setUpChatDB()
        setUpCurrenuser()
        setUpRecyclerView()
        setUpChatBtn()
        return _view
    }

    //Configurando la base de datos
    private fun setUpChatDB() {
        chatDBReference = db.collection("chat")
    }
    //Obteniendo el usuario logueado
    private fun setUpCurrenuser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpRecyclerView() {
        var layoutManager = LinearLayoutManager(context)
        adapter = ChatAdapter(messageList, currentUser.uid)
        _view.recyclerChat.setHasFixedSize(true)
        _view.recyclerChat.layoutManager = layoutManager
        _view.recyclerChat.itemAnimator = DefaultItemAnimator()
        _view.recyclerChat.adapter = adapter
    }

    private fun setUpChatBtn() {
        _view.fabSendChat.setOnClickListener{
            Toast.makeText(context, "Mandar mensaje", Toast.LENGTH_SHORT).show()
            val messageText = editText.text.toString()
            if(messageText.isNotEmpty()){
                val message = Messages(currentUser.uid, messageText, currentUser.photoUrl.toString(), Date())
                saveMessage(message)
                _view.editText.setText("")
            }
        }
    }

    private fun saveMessage(message: Messages){
        val newMessage = HashMap<String, Any>()
        newMessage["authorId"] = message.authorId
        newMessage["message"] = message.message
        newMessage["profileImageURL"] = message.profileImageURL
        newMessage["sentAt"] = message.sentAt

        chatDBReference.add(newMessage)
            .addOnCompleteListener {
                Toast.makeText(context, "Message added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(context, "Fail when try to save the message", Toast.LENGTH_SHORT).show()
            }
    }





}
