package com.pk.signlanguageapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.pk.signlanguageapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        login()
        moveToRegister()
    }

    private fun login() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = resources.getString(R.string.message_validation, "email")
                }
                password.isEmpty() -> {
                    binding.edLoginEmail.error = resources.getString(R.string.message_validation, "password")
                }
                else -> {
                    if (email.isNotEmpty() && password.isNotEmpty()){
                        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
//                            val userId = firebaseAuth.currentUser?.uid
//                            Toast.makeText(this, userId.toString(), Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this@LoginActivity, "Please Try Again", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }else{
                        Toast.makeText(this, resources.getString(R.string.login_error), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }
    }

    private fun moveToRegister() {
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}