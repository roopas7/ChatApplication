package com.roopa.messagingapplication.activity


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.roopa.messagingapplication.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var mFirebaseDBInstance: FirebaseDatabase? = null
    private var mFirebaseDB: DatabaseReference? = null
    private lateinit var auth: FirebaseAuth
    //lateinit var progressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        mFirebaseDBInstance = FirebaseDatabase.getInstance()
        mFirebaseDB = mFirebaseDBInstance?.getReference("user")
        auth = FirebaseAuth.getInstance()

        binding.loginBtn.setOnClickListener {
            binding.loadingBar.visibility = View.VISIBLE
            val email = binding.emailId.text.toString()
            val password = binding.passwordId.text.toString()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {

                        val intent = Intent(this, UserListPage::class.java)
                        startActivity(intent)
                        binding.loadingBar.visibility = View.GONE
                        binding.emailId.setText("")
                        binding.passwordId.setText("")

                    }
                    else {
                        MaterialAlertDialogBuilder(this).setTitle("Authentication failed!")
                            .setMessage("Email id or Password did not match , please try again!!")
                            .setPositiveButton("OK") { _, _ ->

                            }
                            .show()
                        binding.loadingBar.visibility = View.GONE

                    }
                }

        }

        binding.signupBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

}