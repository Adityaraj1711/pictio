package com.pictionary.pictio.view

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pictionary.pictio.R


class LoginActivity : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var loginButton: Button
    lateinit var text_signup: TextView

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bindViews()
        setClickListener()
    }

    private fun setClickListener() {
        text_signup.setOnClickListener {
            val intent: Intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                var pd: ProgressDialog = ProgressDialog(this@LoginActivity)
                pd.setMessage("Please Wait...")
                pd.show()

                var string_email = email.text.toString()
                var string_password = password.text.toString()
                if(TextUtils.isEmpty(string_email) || TextUtils.isEmpty(string_password)){
                    Toast.makeText(this@LoginActivity, "All fields are required!!", Toast.LENGTH_SHORT)
                } else {
                    auth.signInWithEmailAndPassword(string_email, string_password)
                        .addOnCompleteListener(this@LoginActivity, object : OnCompleteListener<AuthResult>{
                            override fun onComplete(task: Task<AuthResult>) {
                                if(task.isSuccessful){
                                    var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(
                                        auth.currentUser!!.uid
                                    )

                                    reference.addValueEventListener(object : ValueEventListener{
                                        override fun onCancelled(p0: DatabaseError) {
                                            pd.dismiss()
                                            val intent: Intent = Intent(this@LoginActivity, MainActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            startActivity(intent)
                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            pd.dismiss()
                                        }
                                    })
                                } else {
                                    pd.dismiss()
                                    Toast.makeText(this@LoginActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                                }

                            }
                        })
                }
            }
        })
    }

    private fun bindViews() {
        email = findViewById(R.id.email)
        loginButton = findViewById(R.id.login)
        text_signup = findViewById(R.id.text_signup)

        auth = FirebaseAuth.getInstance()

    }
}