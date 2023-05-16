package com.roopa.messagingapplication.activity



import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roopa.messagingapplication.model.UserData
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.roopa.messagingapplication.RealPathUtil
import com.roopa.messagingapplication.adapter.UserAdapter
import com.roopa.messagingapplication.R



class UserListPage : AppCompatActivity() {
    lateinit var recyclerView : RecyclerView
    lateinit var hashList: HashMap<String, UserData>
    lateinit var myData : HashMap<String, UserData>
    private lateinit var auth: FirebaseAuth
    lateinit var myProfileKey : String
    lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var profileImage: ShapeableImageView

    private var mFirebaseDBInstance: FirebaseDatabase? = null
    private var mFirebaseDB: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //userListbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_user_list_page)

        hashList = hashMapOf()
        myData = hashMapOf()

        mFirebaseDBInstance= FirebaseDatabase.getInstance()
        mFirebaseDB = mFirebaseDBInstance?.getReference("user")

        //toolbar = findViewById(R.id.toolbar)

        //setSupportActionBar(toolbar)

        profileImage = findViewById(R.id.image_id)
        recyclerView = findViewById(R.id.recycler_id)

        auth = FirebaseAuth.getInstance()
        initializeView()

        profileImage.setOnClickListener {
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
        }

    }

    private fun initializeView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val adapter = UserAdapter(this, hashList)

        if(adapter != null){
            recyclerView?.adapter = adapter
        }


        mFirebaseDB?.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue<UserData>()!!

                if (auth.currentUser?.uid != user.userID) {
                    hashList?.put(user.userID!!, user)
                }else {

                    myProfileKey = user.uName!!
                    myData.put(myProfileKey, user)

                   if(user.profileImage!!.isNotEmpty()){
                        //Glide.with(this@UserListPage)
                          //  .load(user.profileImage)
                           // .into(profileImage)

                        var realPath = ""
                        if (Build.VERSION.SDK_INT < 11)
                            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(applicationContext, Uri.parse(user.profileImage));

                        // SDK >= 11 && SDK < 19
                        else if (Build.VERSION.SDK_INT < 19)
                            realPath = RealPathUtil.getRealPathFromURI_API11to18(applicationContext,Uri.parse(user.profileImage));

                        // SDK > 19 (Android 4.4)
                        else
                            realPath = RealPathUtil.getRealPathFromURI_API19(applicationContext, Uri.parse(user.profileImage));


                        //val file_uri = Uri.parse(user.profileImage)
                        //val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, file_uri)
                       //profileImage.setImageBitmap(bitmap)

                    }

                }

                adapter.setData(hashList)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                val user = snapshot.getValue<UserData>()!!
                val personKey = snapshot.key
                hashList?.put(personKey!!,user)
                adapter.notifyDataSetChanged()

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                hashList?.remove(snapshot.key)
                adapter.notifyDataSetChanged()

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}