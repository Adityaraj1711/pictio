package com.pictionary.pictio.view.Fragment

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pictionary.pictio.R
import com.pictionary.pictio.view.Adapter.MyFotosAdapter
import com.pictionary.pictio.view.Model.Post
import com.pictionary.pictio.view.Model.User
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {

    lateinit var image_profile: ImageView
    lateinit var options:ImageView
    lateinit var posts: TextView
    lateinit var followers:TextView
    lateinit var following:TextView
    lateinit var fullname:TextView
    lateinit var bio:TextView
    lateinit var username:TextView
    lateinit var edit_profile: Button

    lateinit var mySaves: ArrayList<String>

    lateinit var firebaseUser: FirebaseUser
    lateinit var profileid: String

    lateinit var recyclerView: RecyclerView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var my_fotos: ImageButton
    lateinit var saved_fotos:ImageButton

    lateinit var postList: ArrayList<Post>
    lateinit var recyclerView_saves: RecyclerView
    lateinit var postList_saves: ArrayList<Post>

    lateinit var myFotosAdapter: MyFotosAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        bindViews(view)

        userInfo()
        getFollowers()
        getNrPosts()
        myFotos()
        mySaves()
        editProfileClickListener()
        return view
    }

    private fun bindViews(view:View) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        sharedPreferences = context!!.getSharedPreferences("PREFS", MODE_PRIVATE)
        profileid = sharedPreferences.getString("profileid", "none")!!

        image_profile = view.findViewById(R.id.image_profile)
        posts = view.findViewById(R.id.posts)
        followers = view.findViewById(R.id.followers)
        following = view.findViewById(R.id.following)
        fullname = view.findViewById(R.id.fullname)
        bio = view.findViewById(R.id.bio)
        edit_profile = view.findViewById(R.id.edit_profile)
        username = view.findViewById(R.id.username)
        my_fotos = view.findViewById(R.id.my_fotos)
        saved_fotos = view.findViewById(R.id.saved_fotos)
        options = view.findViewById(R.id.options)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val mLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerView.layoutManager = mLayoutManager
        postList = ArrayList()
        myFotosAdapter = MyFotosAdapter(context!!, postList)
        recyclerView.adapter = myFotosAdapter

        recyclerView_saves = view.findViewById(R.id.recycler_view_save)
        recyclerView_saves.setHasFixedSize(true)
        val mLayoutManagers: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerView_saves.layoutManager = mLayoutManagers
        postList_saves = ArrayList()

        recyclerView.visibility = View.VISIBLE
        recyclerView_saves.visibility = View.GONE

    }

    private fun userInfo() {
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
            reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (context == null) {
                    return
                }
                val user = dataSnapshot.getValue(User::class.java)
                if(user != null) {
                    Glide.with(context!!).load(user.imageurl).into(image_profile)
                    username.text = user.username
                    fullname.text = user.fullname
                    bio.text = user.bio
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun checkFollow() {
        val reference = FirebaseDatabase.getInstance().reference
            .child("Follow").child(firebaseUser.uid).child("following")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(profileid).exists()) {
                    edit_profile.text = "following"
                } else {
                    edit_profile.text = "follow"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getFollowers() {
        val reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("followers")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                followers.text = "" + dataSnapshot.childrenCount
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        val reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("following")
        reference1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                following.text = "" + dataSnapshot.childrenCount
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun getNrPosts() {
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var i = 0
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)!!
                    if (post.publisher == profileid) {
                        i += 1
                    }
                }
                posts.text = i.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun editProfileClickListener() {
        if (profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");
        } else {
            checkFollow();
            saved_fotos.setVisibility(View.GONE);
        }

        edit_profile.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val btn = edit_profile.text.toString()
                when (btn) {
                    "Edit Profile" -> {
                        // goto profile
                    }
                    "follow" -> {
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(firebaseUser.uid)
                            .child("following").child(profileid).setValue(true)
                        FirebaseDatabase.getInstance().reference.child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.uid).setValue(true)
                    }
                    "following" -> {
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(firebaseUser.uid)
                            .child("following").child(profileid).removeValue()
                        FirebaseDatabase.getInstance().reference.child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.uid).removeValue()
                    }
                }
            }
        })

        my_fotos.setOnClickListener {
            recyclerView.visibility = View.VISIBLE
            recyclerView_saves.visibility = View.GONE
        }

        saved_fotos.setOnClickListener {
            recyclerView.visibility = View.GONE
            recyclerView_saves.visibility = View.VISIBLE
        }

    }

    private fun myFotos() {
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)!!
                    if (post.publisher == profileid) {
                        postList.add(post)
                    }
                }
                Collections.reverse(postList)
                myFotosAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun mySaves() {
        mySaves = ArrayList()
        val reference =
            FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    mySaves.add(snapshot.key!!)
                }
                readSaves()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun readSaves() {
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList_saves.clear()
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)!!
                    for (id in mySaves) {
                        if (post.postid == id) {
                            postList_saves.add(post)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}