package com.pk.signlanguageapp.ui.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pk.signlanguageapp.MainActivity
import com.pk.signlanguageapp.R
import com.pk.signlanguageapp.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var documentReference: DocumentReference
    private lateinit var userId: String
    private lateinit var user: FirebaseUser

    private lateinit var storageReference: StorageReference

    private var currentImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference()

        userId = firebaseAuth.currentUser!!.uid
        user = firebaseAuth.currentUser!!

        val profileRef = storageReference.child( "users/"+ firebaseAuth.currentUser!!.uid +"profile.jpg")
        profileRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(binding.ivAvatar)
        }

        documentReference = firebaseFirestore.collection("users").document(userId)
        documentReference.addSnapshotListener(this@ProfileActivity, EventListener { documentSnapshot, _ ->
            binding.apply {
                if (documentSnapshot != null) {
                    edUsername.setText(documentSnapshot.getString("username"))
                    edEmail.setText(documentSnapshot.getString("email"))
                }
            }
        })

        binding.btnChangeAvatar.setOnClickListener {
            uploadPhotoFromGallery()
        }

        submitClick()
        backButton()
    }

    private fun backButton() {
        binding.arrowBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun submitClick() {
        binding.btnSubmit.setOnClickListener {
            binding.apply {
                if(edUsername.text.toString().isEmpty() || edEmail.text.toString().isEmpty()) {
                    Toast.makeText(this@ProfileActivity, "Please Try Again", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    currentImageUri?.let { uri -> uploadProfileToFirebase(uri) }
                    val email = edEmail.text.toString()
                    user.updateEmail(email).addOnSuccessListener {
                        val edited = mutableMapOf<String, Any>()
                        edited["username"] = edUsername.text.toString()
                        edited["email"] = email
                        documentReference.update(edited)
                        Log.d(TAG, "Profile updated")
                    }
                }
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun uploadPhotoFromGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivAvatar.setImageURI(it)
        }
    }

    private fun uploadProfileToFirebase(currentImageUri: Uri) {
        val fileRef = storageReference.child("users/"+ firebaseAuth.currentUser!!.uid +"profile.jpg")
        fileRef.putFile(currentImageUri).addOnSuccessListener {
            Toast.makeText(this, "Profile Uploaded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "ProfileActivity"
    }
}