package com.pictionary.pictio.view.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pictionary.pictio.R
import com.pictionary.pictio.view.Adapter.UserAdapter
import com.pictionary.pictio.view.Model.User


class SearchFragment : Fragment() {
    companion object {
        val TAG = "SearchFragment"
    }
    lateinit var recycler_view: RecyclerView
    lateinit var userAdapter: UserAdapter
    lateinit var mUsers: ArrayList<User>
    lateinit var search_bar: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        bindViews(view)
        initializeViews()
        readUsers()
        searchOnTextChangeListener()

        return view
    }

    private fun bindViews(view : View) {
        recycler_view = view.findViewById(R.id.recycler_view)
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(context)
        search_bar = view.findViewById(R.id.search_bar)
    }

    private fun initializeViews() {
        mUsers = ArrayList()
        userAdapter = UserAdapter(context!!, mUsers, true)
        recycler_view.setAdapter(userAdapter)
    }

    private fun readUsers(){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (search_bar.text.toString() == "") {
                    mUsers.clear()
                    for (snapshot: DataSnapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)!!
                        mUsers.add(user)
                    }
                    userAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchOnTextChangeListener() {
        search_bar.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchUsers(charSequence.toString().toLowerCase())
            }
        })
    }

    private fun searchUsers(s: String){
        val query: Query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").startAt(s).endAt(s + "\uf8ff")
        Log.d(TAG, "searchOnTextChange")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)!!
                    mUsers.add(user)
                }
                userAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}