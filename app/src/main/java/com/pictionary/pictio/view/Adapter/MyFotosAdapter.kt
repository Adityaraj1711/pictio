package com.pictionary.pictio.view.Adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pictionary.pictio.R
import com.pictionary.pictio.view.Fragment.PostDetailFragment
import com.pictionary.pictio.view.Model.Post


class MyFotosAdapter(private val mContext: Context, private val mPosts: List<Post>) : RecyclerView.Adapter<MyFotosAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.fotos_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val post = mPosts[position]
        Glide.with(mContext).load(post.postimage).into(holder.post_image)
//        holder.post_image.setOnClickListener(object : View.OnClickListener() {
//            override fun onClick(view: View?) {
//                val editor: SharedPreferences.Editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit()
//                editor.putString("postid", post.postid)
//                editor.apply()
//                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, PostDetailFragment()).commit()
//            }
//        })

        holder.post_image.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val editor: SharedPreferences.Editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit()
                editor.putString("postid", post.postid)
                editor.apply()
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PostDetailFragment()).commit()
            }
        })

    }

    override fun getItemCount(): Int {
        return mPosts.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var post_image: ImageView
        init {
            post_image = itemView.findViewById(R.id.post_image)
        }
    }

}