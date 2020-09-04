package com.pictionary.pictio.view.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pictionary.pictio.R
import com.pictionary.pictio.view.Adapter.PostAdapter
import com.pictionary.pictio.view.Model.Post

class HomeFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var postAdapter: PostAdapter
    lateinit var postList: ArrayList<Post>
    lateinit var followingList: ArrayList<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        postList = ArrayList()
        postAdapter = PostAdapter(context!!, postList)
        recyclerView.adapter = postAdapter

        checkFollowing()

        return view
    }
    private fun checkFollowing(){
        followingList = ArrayList()
        val databaseReference = FirebaseDatabase.getInstance().getReference("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid).child("following")

        databaseReference.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                followingList.clear()
                for(snapshot in dataSnapshot.children){
                    followingList.add(snapshot.key!!)
                }
                readPosts()
            }
            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun readPosts(){
        val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        databaseReference.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                for(snapshot in dataSnapshot.children){
                    val post: Post? = snapshot.getValue(Post::class.java)
                    for(id in followingList){
                        if(post?.publisher == id){
                            postList.add(post)
                        }
                    }
                }
                postAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) { }
        })
    }


}