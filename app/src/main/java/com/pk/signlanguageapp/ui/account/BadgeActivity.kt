package com.pk.signlanguageapp.ui.account

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pk.signlanguageapp.MainActivity
import com.pk.signlanguageapp.R
import com.pk.signlanguageapp.databinding.ActivityBadgeBinding

class BadgeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBadgeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBadgeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrowBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}