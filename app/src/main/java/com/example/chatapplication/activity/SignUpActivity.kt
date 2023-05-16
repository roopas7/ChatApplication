package com.example.chatapplication.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ActivitySignUpBinding
import com.example.chatapplication.model.UserData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    lateinit var bindingSignup: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private var mFirebaseDBInstance: FirebaseDatabase?=null
    private var mFirebaseDB: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingSignup = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(bindingSignup.root)

        FirebaseApp.initializeApp(this)

        mFirebaseDBInstance= FirebaseDatabase.getInstance()
        bindingSignup.saveData.setOnClickListener {
            saveData()
        }
    }

    fun saveData(){
        val name = bindingSignup.name.text.toString()
        val email = bindingSignup.addEmail.text.toString()
        val password1 = bindingSignup.enterPassword.text.toString()
        val password2 = bindingSignup.reenterPassword.text.toString()

        auth = FirebaseAuth.getInstance()


        if (name != ""||email != "" || password1 != "" || password2 != ""){
            if(password1 == password2){


                auth.createUserWithEmailAndPassword(email,password1)
                    .addOnCompleteListener{ task->
                        if(task.isSuccessful){
                            Toast.makeText(this, "Authentication Successful", Toast.LENGTH_LONG).show()
                            val currentUser = auth.currentUser
                            val userId = currentUser?.uid
                            addDataInDB(name,email,userId!!)

                        }
                        else{
                            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show()
                        }
                    }



            }
            else {
                MaterialAlertDialogBuilder(this).setTitle("Password Mismatch!!")
                    .setMessage("Passwords did not match")
                    .setPositiveButton("OK") { _, _ ->
                        Toast.makeText(this, "Ok", Toast.LENGTH_LONG).show()
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        Toast.makeText(this, "Cancel", Toast.LENGTH_LONG).show()
                    }.show()
            }


        }
        else{
            MaterialAlertDialogBuilder(this).setTitle("Empty fields")
                .setMessage("Name, email or passwords cannot be empty, please add your info ")
                .setPositiveButton("OK") { _, _ ->
                    Toast.makeText(this, "Ok", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(this, "Cancel", Toast.LENGTH_LONG).show()
                }.show()
        }
    }

    fun addDataInDB(name: String, email: String, userId: String){

        mFirebaseDB = FirebaseDatabase.getInstance().getReference("user")
        val user = UserData(email, name,userId, "")

        mFirebaseDB!!.child(userId).setValue(user).addOnSuccessListener {

            bindingSignup.name.setText("")
            bindingSignup.addEmail.setText("")
            bindingSignup.enterPassword.setText("")
            bindingSignup.reenterPassword.setText("")

            Toast.makeText(this, "Succussfully saved", Toast.LENGTH_LONG).show()
            val intent = Intent(this, UserListPage::class.java)
            startActivity(intent)
            finish()


        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
        }
    }


}