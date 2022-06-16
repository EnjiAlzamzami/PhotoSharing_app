package com.example.test

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import java.util.jar.Manifest

class PhotoSharingActivity : AppCompatActivity() {

    var picked_image : Uri?=null
    var picked_bitmap : Bitmap?=null
    private lateinit var storage:FirebaseStorage
    private lateinit var database:FirebaseFirestore
    private lateinit var auth :FirebaseAuth
    //get values on variables
    private lateinit var imageview: ImageView
    private lateinit var comment_txt : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_sharing)
        storage=FirebaseStorage.getInstance()
        database=FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()
    }

    fun imgaction(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            val photointent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(photointent, 2)

        }
    }

    fun share_onclick(view: View) {
        val uuid = UUID.randomUUID()
        val name_image = "${uuid}.jpg"
        val reference = storage.reference
        val imageReference = reference.child("images").child(name_image)
        if (picked_image != null) {
            imageReference.putFile(picked_image!!).addOnSuccessListener { taskSnaphot ->
                val uploadImageReference =
                    FirebaseStorage.getInstance().reference.child("images").child(name_image)
                uploadImageReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val currentUser_email = auth.currentUser!!.email.toString()
                    comment_txt=findViewById(R.id.comment_text)
                    val comment = comment_txt.text.toString()
                    val date = com.google.firebase.Timestamp.now()
                    val postHashMap = hashMapOf<String, Any>()
                    postHashMap.put("imageUri", downloadUrl)
                    postHashMap.put("email", currentUser_email)
                    postHashMap.put("comment", comment)
                    postHashMap.put("date", date)
                    database.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"Your post is added: ${currentUser_email}",Toast.LENGTH_LONG).show()
                            val intent= Intent(this, main_user::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(
                            applicationContext,
                            exception.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.size > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                val photo_file_intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(photo_file_intent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==2 && resultCode == RESULT_OK && data != null){
            picked_image=data.data
            if(picked_image != null){
                //view Image
                if(Build.VERSION.SDK_INT > 28){
                    val source= ImageDecoder.createSource(this.contentResolver,picked_image!!)
                    picked_bitmap = ImageDecoder.decodeBitmap(source)
                    imageview=findViewById(R.id.imageView)
                    imageview.setImageBitmap(picked_bitmap)
                }else{
                    picked_bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,picked_image!!)
                    imageview.setImageBitmap(picked_bitmap)

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}
