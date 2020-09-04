package com.pictionary.pictio.view

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.pictionary.pictio.R
import com.pictionary.pictio.view.Model.User

class CommentsActivity : AppCompatActivity() {

    lateinit var addcomment: EditText
    lateinit var image_profile: ImageView
    lateinit var post: TextView

    lateinit var postId: String
    lateinit var publisherId: String

    lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        bindViews()
        getIntentData()
        onClickListener()
        getImage()
    }

    private fun bindViews() {
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Comments")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View?) {
                finish()
            }
        })

        addcomment = findViewById(R.id.add_comment)
        image_profile = findViewById(R.id.image_profile)
        post = findViewById(R.id.post)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
    }


    private fun getIntentData() {
        var intent: Intent = getIntent()
        postId = intent.getStringExtra("postid")!!
        publisherId = intent.getStringExtra("publisherid")!!
    }

    private fun onClickListener() {
        post.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if(addcomment.text.toString() == ""){
                    Toast.makeText(this@CommentsActivity, "Comment is empty..", Toast.LENGTH_SHORT).toString()
                } else {
                    addComment()
                }
            }
        })
    }

    private fun addComment() {
        var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(postId)
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap.put("comment", addcomment.text.toString())
        hashMap.put("publisher", firebaseUser.uid)

        databaseReference.push().setValue(hashMap)
        addcomment.setText("")
    }

    private fun getImage(){
        var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue(User::class.java)
                Glide.with(applicationContext).load(user!!.imageurl).into(image_profile)
            }
        })
    }
}