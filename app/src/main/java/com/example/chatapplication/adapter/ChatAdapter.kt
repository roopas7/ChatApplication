package com.example.chatapplication.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.model.Message
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(val context: Context, val messageList: ArrayList<Message> ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == 1){
            val view = LayoutInflater.from(context).inflate(R.layout.msgreceived, parent, false)
            return MsgReceiveViewHolder(view)

        }else{

            val view = LayoutInflater.from(context).inflate(R.layout.msgsent, parent, false)
            return MsgSentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if(holder.javaClass == MsgSentViewHolder::class.java){
            val viewHolder = holder as MsgSentViewHolder
            holder.sentMsg.text = currentMessage.message
        }else{
            val viewHolder = holder as MsgReceiveViewHolder
            holder.receiveMsg.text = currentMessage.message

        }

    }

    override fun getItemViewType(position: Int): Int {
        //to return view

        val currentMsg = messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMsg.senderId)){
            return ITEM_SENT
        }
        else{
            return ITEM_RECEIVE
        }
    }

    class MsgSentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val sentMsg = itemView.findViewById<TextView>(R.id.message_sent)
    }

    class MsgReceiveViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val receiveMsg = itemView.findViewById<TextView>(R.id.message_received)

    }

}