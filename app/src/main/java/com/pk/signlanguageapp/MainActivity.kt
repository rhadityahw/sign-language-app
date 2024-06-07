package com.pk.signlanguageapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.pk.signlanguageapp.ui.account.AccountFragment
import com.pk.signlanguageapp.databinding.ActivityMainBinding
import com.pk.signlanguageapp.ui.dictionary.DictionaryFragment
import com.pk.signlanguageapp.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var selectedFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.inflateMenu(R.menu.menu_bot_nav)
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    switchFragment(HomeFragment())
                    true
                }
                R.id.action_dictionary -> {
                    switchFragment(DictionaryFragment())
                    true
                }
                R.id.action_account -> {
                    switchFragment(AccountFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState != null) {
            selectedFragment = supportFragmentManager.getFragment(savedInstanceState, "selectedFragment") ?: HomeFragment()
        } else {
            selectedFragment = HomeFragment()
        }

        switchFragment(selectedFragment)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (selectedFragment.isAdded) {
            supportFragmentManager.putFragment(outState, "selectedFragment", selectedFragment)
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}