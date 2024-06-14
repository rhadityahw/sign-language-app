package com.pk.signlanguageapp.ui.account

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.pk.signlanguageapp.MainActivity
import com.pk.signlanguageapp.R
import com.pk.signlanguageapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var documentReference: DocumentReference
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        userId = firebaseAuth.currentUser!!.uid

        documentReference = firebaseFirestore.collection("users").document(userId)
        documentReference.addSnapshotListener(this@ProfileActivity, EventListener { documentSnapshot, _ ->
            binding.apply {
                if (documentSnapshot != null) {
                    edRegisterUsername.setText(documentSnapshot.getString("username"))
                    edRegisterEmail.setText(documentSnapshot.getString("email"))
                }
            }
        })

        setSubmitClick()
    }

    private fun setSubmitClick() {
        binding.btnSubmit.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}