package com.example.chatapplication.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.chatapplication.R
import com.example.chatapplication.model.UserData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfilePage : AppCompatActivity() {

    private lateinit var profilePicture: ShapeableImageView
    private lateinit var username : TextView
    private lateinit var myemail : TextView
    private lateinit var auth: FirebaseAuth
    var imagePath : String = ""


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        auth = FirebaseAuth.getInstance()
        profilePicture = findViewById(R.id.image_id)
        username = findViewById(R.id.username_id)
        myemail = findViewById(R.id.myEmailId)

        var actionBar = getSupportActionBar()


        // showing the back button in action bar
        actionBar!!.setDisplayHomeAsUpEnabled(true)



        val myData = FirebaseDatabase.getInstance().getReference("user").child(auth.currentUser!!.uid)

        myData.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var user = snapshot.getValue(UserData::class.java)
                username.text = user!!.uName
                myemail.text = user!!.email
                if(user.profileImage!!.isNotEmpty()){
                    Glide.with(this@ProfilePage)
                        .load(user.profileImage)
                        .into(profilePicture)
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


        profilePicture.setOnClickListener {
            val intent = Intent(this, ImagePreviewActivity::class.java)
            resultLauncher.launch(intent)

        }


    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {

                imagePath = result.data?.getStringExtra("ImageURI").toString()
                println("Get Image from file : $imagePath")


                if(imagePath!=null){
                    Glide.with(this@ProfilePage)
                        .load(imagePath)
                        .into(profilePicture)
                }

            }

        }

    fun updateProfilePic(profileImage: String) {
        auth = FirebaseAuth.getInstance()
        val myData = FirebaseDatabase.getInstance().getReference("user").child(auth.currentUser!!.uid)

        myData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var data = snapshot.getValue(UserData::class.java)
                val user = UserData(data!!.email!!, data!!.uName!!, data!!.userID, profileImage)
                data?.profileImage = profileImage
                myData.setValue(user)

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_profile -> {
                if(imagePath.isNotEmpty()) {
                    updateProfilePic(imagePath)
                    this.finish();
                    return true
                }
                else
                {
                    MaterialAlertDialogBuilder(this).setTitle("Image Alert")
                        .setMessage("Please select or capture a image")
                        .setPositiveButton("OK") { _, _ ->
                        }
                }
                onSupportNavigateUp()

            }
            android.R.id.home -> {
                this.finish()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
        return true

    }


}