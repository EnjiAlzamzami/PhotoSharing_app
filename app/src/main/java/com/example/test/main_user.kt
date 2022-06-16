package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.ArrayList

class main_user : AppCompatActivity() {
    private lateinit var  auth:FirebaseAuth
    private lateinit var  database:FirebaseFirestore
    var post_list = ArrayList<Post>()
    private lateinit var recyclerViewAdapter: new_adapter
    private lateinit var myrecyclerview :RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_user)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        myrecyclerview=findViewById(R.id.myrecyclerview)
        get_information()
        var layoutManager = LinearLayoutManager(this)
        myrecyclerview.layoutManager = layoutManager
        recyclerViewAdapter= new_adapter(post_list)
        myrecyclerview.adapter = recyclerViewAdapter

    }
    fun get_information(){
        database.collection("Post").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener{
            snaphot , exception ->
                if(exception != null){
                    Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
                }else{
                    if(snaphot != null){
                        if(!snaphot.isEmpty) {
                            val documents = snaphot.documents
                            post_list.clear()
                            for(document in documents){
                                val email = document.get("email") as String
                                val comment = document.get("comment") as String
                                val image = document.get("imageUri") as String
                                val download_post = Post(email,comment,image)
                                post_list.add(download_post)
                            }
                        }
                        recyclerViewAdapter.notifyDataSetChanged()
                    }

                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_file,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.share_photo){
            val intent = Intent(this, PhotoSharingActivity::class.java)
            startActivity(intent)
        }else if (item.itemId == R.id.log_out){
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}