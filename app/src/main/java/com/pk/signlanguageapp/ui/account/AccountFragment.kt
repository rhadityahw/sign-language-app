package com.pk.signlanguageapp.ui.account

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.pk.signlanguageapp.R
import com.pk.signlanguageapp.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding

    private lateinit var firebaseAuth: FirebaseAuth

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