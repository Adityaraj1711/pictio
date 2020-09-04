package com.pictionary.pictio.view.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.pictionary.pictio.R
import com.pictionary.pictio.view.MainActivity
import com.pictionary.pictio.view.Model.Comment
import com.pictionary.pictio.view.Model.User

public class CommentAdapter(private val mContext: Context, private val mComment: List<Comment>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>(){
    companion object{
        val TAG = "CommentAdapter"
    }

    lateinit var firebaseUser: FirebaseUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mComment.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val comment: Comment = mComment[position]

        holder.comment.setText(comment.comment)
        getUserInfo(holder.image_profile, holder.username, comment.publisher!!)
        holder.comment.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val intent: Intent = Intent(mContext, MainActivity::class.java)
                intent.putExtra("publisherid", comment.publisher)
                mContext.startActivity(intent)
            }
        })
        holder.image_profile.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val intent: Intent = Intent(mContext, MainActivity::class.java)
                intent.putExtra("publisherid", comment.publisher)
                mContext.startActivity(intent)
            }
        })
    }


    private fun getUserInfo(imageView: ImageView, username: TextView,  publisherId: String){
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId)
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User = dataSnapshot.getValue(User::class.java)!!
                Glide.with(mContext).load(user.imageurl).into(imageView)
                username.setText(user.username)
            }

        })
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var image_profile : ImageView

        lateinit var username: TextView
        lateinit var comment: TextView

        init {
            image_profile = itemView.findViewById(R.id.image_profile)
            comment = itemView.findViewById(R.id.comment)
            username = itemView.findViewById(R.id.username)
        }
    }

}