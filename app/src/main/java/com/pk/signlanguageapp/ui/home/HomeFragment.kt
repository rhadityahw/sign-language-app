package com.pk.signlanguageapp.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.pk.signlanguageapp.ui.speech.SpeechActivity
import com.pk.signlanguageapp.databinding.FragmentHomeBinding
import com.pk.signlanguageapp.ui.camerax.CameraActivity
import com.pk.signlanguageapp.ui.camerax.WordLevelCameraActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth= FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        userId = firebaseAuth.currentUser!!.uid

        firebaseFirestore.collection("users").document(userId).addSnapshotListener { documentSnapshot, _ ->
            binding?.apply {
                tvHomeName.text = documentSnapshot?.getString("username")
            }
        }

        startSpeechRecognition()
        startWordLevelCamera()
        startCameraX()
    }

    private fun startCameraX() {
        binding?.cvVideoToText?.setOnClickListener {
            val intent = Intent(activity, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startWordLevelCamera() {
        binding?.cvVideoToTextWord?.setOnClickListener {
            val intent = Intent(activity, WordLevelCameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startSpeechRecognition() {
        binding?.cvSpeechToText?.setOnClickListener {
            val intent = Intent(activity, SpeechActivity::class.java)
            startActivity(intent)
        }
    }
}