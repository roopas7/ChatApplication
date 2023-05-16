package com.roopa.messagingapplication.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.roopa.messagingapplication.activity.UserChatPage
import com.roopa.messagingapplication.model.UserData
import com.google.firebase.database.*
import com.roopa.messagingapplication.R



class UserAdapter(val context: Context, var arrayList:HashMap<String, UserData>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var keys: MutableSet<String>? = arrayList.keys
    private var mFirebaseDBInstance: FirebaseDatabase? = null
    private var mFirebaseDB: DatabaseReference? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewlayout =
            LayoutInflater.from(parent.context).inflate(R.layout.list_row, parent, false)
        return ViewHolder(viewlayout)


    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val viewItem = arrayList.get(keys?.elementAt(position))

        holder.nameView.text = viewItem?.uName
        holder.emailView.text = viewItem?.email
        holder.deleteUser.visibility = GONE

        holder.cardView.setOnClickListener {
            val intent = Intent(context, UserChatPage::class.java)
            intent.putExtra("name",viewItem?.uName)
            intent.putExtra("uid", viewItem?.userID)
            context.startActivity(intent)
        }
        holder.cardView.setOnLongClickListener {
            onLongClick(holder.deleteUser)
        }

        holder.deleteUser.setOnClickListener {
            deleteUser(viewItem?.userID!!)
        }
    }

    fun setData(map: HashMap<String, UserData>) {

        arrayList = map
        notifyDataSetChanged()

    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val nameView: TextView = itemView.findViewById(R.id.view_name_id)
        val emailView: TextView = itemView.findViewById(R.id.view_email_id)
        val cardView : CardView = itemView.findViewById(R.id.card_view_id)
        var deleteUser : ImageButton = itemView.findViewById(R.id.delete_user)


    }

    fun onLongClick(view: View?): Boolean {
        // Handle long click
        // Return true to indicate the click was handled
        view?.visibility = View.VISIBLE
        return true
    }

    fun deleteUser(userId: String){
        mFirebaseDBInstance= FirebaseDatabase.getInstance()
        mFirebaseDB = mFirebaseDBInstance?.getReference()?.child("user")?.child(userId)


        mFirebaseDB?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mFirebaseDB?.removeValue()?.addOnSuccessListener {
                    arrayList.remove(userId)
                    notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



    }

}
