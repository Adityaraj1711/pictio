package com.pictionary.pictio.view

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pictionary.pictio.R


class RegisterActivity : AppCompatActivity() {
    lateinit var username : EditText
    lateinit var fullname : EditText
    lateinit var email: EditText
    lateinit var password: EditText

    lateinit var registerButton: Button
    lateinit var text_login : TextView

    lateinit var auth: FirebaseAuth
    lateinit var database_reference: DatabaseReference
    lateinit var pd: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        bindViews()
        setClickListener()
    }

    private fun bindViews() {
        username = findViewById(R.id.username)
        fullname = findViewById(R.id.fullname)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        registerButton = findViewById(R.id.register)
        text_login = findViewById(R.id.text_login)

        auth = FirebaseAuth.getInstance()
    }

    private fun setClickListener() {
        text_login.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        })

        registerButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                pd = ProgressDialog(this@RegisterActivity)
                pd.setMessage("Please wait...")
                pd.show()

                val str_username = username.text.toString()
                val str_fullname = fullname.text.toString()
                val str_email = email.text.toString()
                val str_password = password.text.toString()

                if(TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                    Toast.makeText(this@RegisterActivity, "Please fill all the fields!", Toast.LENGTH_SHORT).show()
                } else {
                    if(str_password.length < 6){
                        Toast.makeText(this@RegisterActivity, "Password should be greater than 6 characters", Toast.LENGTH_SHORT).show()
                    } else {
                        register(str_username, str_fullname,str_email, str_password)
                    }
                }
            }
        })
    }


    private fun register(username: String, fullname: String, email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@RegisterActivity, object: OnCompleteListener<AuthResult>{
                override fun onComplete(task: Task<AuthResult>) {
                    Log.d("RegisterActivity", "onCompleteCalled")
                    if(task.isSuccessful()){
                        Log.d("RegisterActivity", "taskSuccessful")
                        val firebaseUser: FirebaseUser = auth.currentUser!!
                        var userId = firebaseUser?.uid
                        database_reference = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                        var hashMap : HashMap<String, Any> = HashMap<String, Any>()
                        hashMap.put("id", userId)
                        hashMap.put("username", username.toLowerCase())
                        hashMap.put("fullname", fullname)
                        hashMap.put("bio", "")
                        hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/pictio-4fdaf.appspot.com/o/placeholder.png?alt=media&token=48b94881-b81a-4c76-bf39-bd8f6c2bc80f")
                        database_reference.setValue(hashMap).addOnCompleteListener(object: OnCompleteListener<Void>{
                            override fun onComplete(task: Task<Void>) {
                                Log.d("RegisterActivity", "onComplete")
                                if(task.isSuccessful()){
                                    Log.d("RegisterActivity", "taskSuccessfulTaskSuccessful")
                                    pd.dismiss()
                                    val intent: Intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }
                            }
                        })
                    } else {
                        Log.d("RegisterActivity", task.exception.toString())
                        pd.dismiss()
                        Toast.makeText(this@RegisterActivity, "Registration error. Check the input fields!!", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }
}