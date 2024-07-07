package com.pk.signlanguageapp.ui.dictionary

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.pk.signlanguageapp.R
import com.pk.signlanguageapp.ViewModelFactory
import com.pk.signlanguageapp.databinding.ActivityDetailDictionaryBinding
import com.pk.signlanguageapp.databinding.ActivityListDictionaryBinding
import com.pk.signlanguageapp.data.result.Result
import com.pk.signlanguageapp.ui.dictionary.DetailDictionaryActivity.Companion.EXTRA_DICTIONARY

class ListDictionaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListDictionaryBinding

    private val viewModel: DictionaryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val dictionaryAdapter = DictionaryAdapter()

    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListDictionaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        category = intent.getStringExtra(EXTRA_LIST_DICTIONARY)

        listDictionary()
    }

    private fun listDictionary() {
        binding.rvListDictionary.layoutManager = LinearLayoutManager(this)

        when (category) {
            "Letter" -> {
                viewModel.getAllLetters()
                viewModel.lettersList.observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                val letters = result.data
                                dictionaryAdapter.submitList(letters)
                                binding.rvListDictionary.adapter = dictionaryAdapter
                            }

                            is Result.Error -> {
                                Toast.makeText(
                                    this@ListDictionaryActivity,
                                    "Terjadi kesalahan: ${result.error}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("errorLetters", result.error)
                            }
                        }
                    }
                }
            }
            "Word" -> {
                viewModel.getAllWords()
                viewModel.wordsList.observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                val words = result.data
                                Log.d("resultWords", words.toString())
                                dictionaryAdapter.submitList(words)
                                binding.rvListDictionary.adapter = dictionaryAdapter
                            }

                            is Result.Error -> {
                                Toast.makeText(
                                    this@ListDictionaryActivity,
                                    "Terjadi kesalahan: ${result.error}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("errorWords", result.error)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_LIST_DICTIONARY = "extra_list_dictionary"
    }
}