package com.pk.signlanguageapp.ui.account

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pk.signlanguageapp.R
import com.pk.signlanguageapp.databinding.FragmentAccountBinding
import com.squareup.picasso.Picasso

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var documentReference: DocumentReference
    private lateinit var userId: String

    private lateinit var storageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference()

        userId = firebaseAuth.currentUser!!.uid

        val profileRef = storageReference.child( "users/"+ firebaseAuth.currentUser!!.uid +"profile.jpg")
        profileRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(binding?.ivAvatarAccount)
        }

        documentReference = firebaseFirestore.collection("users").document(userId)
        documentReference.addSnapshotListener { documentSnapshot, _ ->
            binding?.apply {
                tvUsernameAccount.text = documentSnapshot?.getString("username")
                tvEmailAccount.text = documentSnapshot?.getString("email")
            }
        }

        logout()
        setBadgeClick()
        setProfileClick()
    }

    private fun setBadgeClick() {
        binding?.cvBadgeAccount?.setOnClickListener {
            val intent = Intent(activity, BadgeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setProfileClick() {
        binding?.cvProfile?.setOnClickListener {
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logout() {
        binding?.btnLogout?.setOnClickListener {
            AlertDialog.Builder(requireActivity()).apply {
                setTitle("Peringatan!")
                setMessage("Apakah anda yakin ingin keluar?")
                setPositiveButton("Ya") { _, _ ->
                    firebaseAuth.signOut()
                    activity?.finish()
                }
                setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
                create()
                show()
            }
        }
    }
}