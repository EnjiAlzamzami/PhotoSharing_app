package com.example.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class new_adapter (val post_list : ArrayList<Post>): RecyclerView.Adapter<new_adapter.PostHolder>(){
    class PostHolder(itemView : View):RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
       val inflater = LayoutInflater.from(parent.context)
        return PostHolder(inflater.inflate(R.layout.recyclerview_new,parent,false))
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.rec_mail).text = post_list[position].mail
        holder.itemView.findViewById<TextView>(R.id.rec_comment).text = post_list[position].comment
        Picasso.get().load(post_list[position].image).into(holder.itemView.findViewById<ImageView>(R.id.rec_img))

    }
    override fun getItemCount(): Int {
        return post_list.size
    }
}


