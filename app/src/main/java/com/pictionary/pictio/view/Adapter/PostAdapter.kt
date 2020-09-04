package com.pictionary.pictio.view.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.pictionary.pictio.R
import com.pictionary.pictio.view.Adapter.PostAdapter.ViewHolder
import com.pictionary.pictio.view.CommentsActivity
import com.pictionary.pictio.view.Model.Post
import com.pictionary.pictio.view.Model.User

// first write the inner viewholder class inside. Then extend the PostAdapter class with RecyclerView.ViewHolder
// then implement the members. then create the context and List. and create other variables required by the overridden functions
// Insert the constructor parameters for the post adapter
// Inflate the onCreateViewHolder to project the resource_layout in the recyclerView. Set the size in getItemCount

public class PostAdapter(private val mContext: Context, private val mPost: List<Post>) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        var TAG = "PostAdapter"
    }

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post: Post = mPost[position]
        Glide.with(mContext).load(post.postimage).into(holder.post_image)
        if(post.description == ""){
            holder.description.visibility = View.GONE
        } else {
            holder.description.visibility = View.VISIBLE
            holder.description.text = post.description
        }

        publisherInfo(holder.image_profile, holder.username, holder.publisher, post.publisher.toString())
        isLiked(post.postid!!, holder.like)
        numberOfLikes(holder.likes, post.postid!!)

        getComments(post.postid!!, holder.comments)

        holder.like.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if((holder.like.tag) == "like"){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.postid!!).child(firebaseUser!!.uid).setValue(true)
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.postid!!).child(firebaseUser!!.uid).removeValue()
                }
            }
        })

        // for comment image view
        holder.comment.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val intent: Intent = Intent(mContext, CommentsActivity::class.java)
                intent.putExtra("postid", post.postid)
                intent.putExtra("publisherid", post.publisher)
                mContext.startActivity(intent)
            }
        })

        // for comments text view
        holder.comments.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val intent: Intent = Intent(mContext, CommentsActivity::class.java)
                intent.putExtra("postid", post.postid)
                intent.putExtra("publisherid", post.publisher)
                mContext.startActivity(intent)
            }
        })


    }

    private fun publisherInfo(imageProfile: ImageView, username: TextView, publisher: TextView, userid: String){
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid)
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User = dataSnapshot.getValue(User::class.java)!!
                Glide.with(mContext).load(user!!.imageurl).into(imageProfile)
                username.setText(user.username)
                publisher.setText(user.username)
            }

        })
    }

    private fun getComments(postId: String, comments: TextView){
        var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                comments.setText("View all ${dataSnapshot.childrenCount} Comments")
            }

        })
    }

    private fun isLiked(postId: String, imageView: ImageView){
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Likes").child(postId)

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.child(firebaseUser!!.uid).exists()){
                    imageView.setImageResource(R.drawable.ic_liked)
                    imageView.setTag("liked")
                } else {
                    imageView.setImageResource(R.drawable.ic_like)
                    imageView.setTag("like")
                }
            }
        })
    }

    private fun numberOfLikes(likes: TextView, postId: String){
        var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Likes").child(postId)
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                likes.setText(dataSnapshot.childrenCount.toString() + " likes")
            }
        })
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var image_profile : ImageView
        lateinit var post_image: ImageView
        lateinit var like: ImageView
        lateinit var comment: ImageView
        lateinit var save: ImageView

        lateinit var username: TextView
        lateinit var comments: TextView
        lateinit var likes: TextView
        lateinit var publisher: TextView
        lateinit var description: TextView

        init {
            image_profile = itemView.findViewById(R.id.image_profile)
            post_image = itemView.findViewById(R.id.post_image)
            comment = itemView.findViewById(R.id.comment)
            like = itemView.findViewById(R.id.like)
            save = itemView.findViewById(R.id.save)

            comments = itemView.findViewById(R.id.comments)
            username = itemView.findViewById(R.id.username)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)

        }
    }


}