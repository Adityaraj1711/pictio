package com.pictionary.pictio.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pictionary.pictio.R

class StartActivity : AppCompatActivity() {

    lateinit var buttonLogin: Button
    lateinit var buttonRegister: Button
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        bindViews()
        setClickListeners()
    }

    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if(firebaseUser != null){
            val intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun bindViews() {
        buttonLogin = findViewById(R.id.login)
        buttonRegister = findViewById(R.id.register)
    }

    private fun setClickListeners() {
        buttonLogin.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val intent = Intent(this@StartActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        })

        buttonRegister.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val intent = Intent(this@StartActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        })
    }
}
