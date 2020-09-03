package com.pictionary.pictio.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.pictionary.pictio.R
import com.theartofdev.edmodo.cropper.CropImage


class PostActivity : AppCompatActivity() {
    lateinit var imageUri: Uri
    var myUrl = ""
    private var uploadTask: StorageTask<*>? = null
    lateinit var storageRef: StorageReference


    lateinit var close: ImageView
    lateinit var image_added: ImageView
    lateinit var post: TextView
    lateinit var description: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        close = findViewById(R.id.close)
        image_added = findViewById(R.id.image_added)
        post = findViewById(R.id.post)
        description = findViewById(R.id.description)

        storageRef = FirebaseStorage.getInstance().getReference("posts")
        close.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val intent: Intent = Intent(this@PostActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        post.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                uploadImage()
            }

        })

        CropImage.activity().setAspectRatio(1, 1).start(this@PostActivity)
    }

    // ctrl + O
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_added.setImageURI(imageUri)
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            val intent: Intent = Intent(this@PostActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        var contentResolver : ContentResolver = getContentResolver()
        var mime : MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadImage(){
        val progressDialog: ProgressDialog = ProgressDialog(this)
        progressDialog.setMessage("Posting..")
        progressDialog.show()
        if(imageUri != null){
            val fileReference: StorageReference = storageRef.child(System.currentTimeMillis().toString() + "." + getFileExtension(imageUri) )
            uploadTask = fileReference.putFile(imageUri)
            (uploadTask as UploadTask).continueWithTask<Uri>(Continuation { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                fileReference.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    myUrl = downloadUri.toString()
                    val reference = FirebaseDatabase.getInstance().getReference("Posts")
                    val postid = reference.push().key
                    val hashMap: HashMap<String, Any?> = HashMap()
                    hashMap["postid"] = postid
                    hashMap["postimage"] = myUrl
                    hashMap["description"] = description.text.toString()
                    hashMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                    reference.child(postid!!).setValue(hashMap)
                    progressDialog.dismiss()
                    startActivity(Intent(this@PostActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@PostActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this@PostActivity, e.message, Toast.LENGTH_SHORT).show()
            }

        } else {
            progressDialog.dismiss()
            Toast.makeText(this@PostActivity, "No art is selected .. ", Toast.LENGTH_SHORT).show()
        }
    }
}