package com.example.chatapplication.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.chatapplication.R
import com.example.chatapplication.adapter.ChatAdapter
import com.example.chatapplication.model.Message


class UserChatPage : AppCompatActivity() {
    private lateinit var chatRecycler: RecyclerView
    private lateinit var msgBox : EditText
    private lateinit var sendButton : ImageView
    private lateinit var messageList : ArrayList<Message>
    private lateinit var chatAdapter: ChatAdapter
    private var mFirebaseDB: DatabaseReference? = null
    private var mFirebaseDBInstance: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_chat_page)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mFirebaseDBInstance = FirebaseDatabase.getInstance()
        mFirebaseDB = mFirebaseDBInstance?.getReference("chats")


        var actionBar = getSupportActionBar()
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = name

        chatRecycler = findViewById(R.id.chatRecycler)
        msgBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendBtn)
        messageList = ArrayList()

        chatAdapter = ChatAdapter(this, messageList)
        chatRecycler.layoutManager = LinearLayoutManager(this)
        chatRecycler.adapter = chatAdapter
        mFirebaseDB?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Message::class.java)

                    if (chat!!.senderId.equals(senderUid) && chat!!.receiverId.equals(receiverUid) ||
                        chat!!.senderId.equals(receiverUid) && chat!!.receiverId.equals(senderUid)
                    ) {
                        messageList.add(chat)
                    }
                }
                chatAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        sendButton.setOnClickListener {

            val msg = msgBox.text.toString()
            val messageObj = Message(msg, senderUid, receiverUid)

            //adding msg to db

            mFirebaseDB?.push()?.setValue(messageObj)
            //adding data to recycler view

            /*mFirebaseDB?.child("chats")?.child(senderRoom!!)?.child("messages")?.push()?.setValue(messageObj)
                ?.addOnSuccessListener {
                    mFirebaseDB?.child("chats")?.child(receiverRoom!!)?.child("message")?.push()
                        ?.setValue(messageObj)
                }
            msgBox.setText("")*/

        }


    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}