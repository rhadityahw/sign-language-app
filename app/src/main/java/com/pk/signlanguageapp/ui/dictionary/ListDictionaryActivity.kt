package com.pk.signlanguageapp.ui.dictionary

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.pk.signlanguageapp.ViewModelFactory
import com.pk.signlanguageapp.data.response.DictionaryResponseItem
import com.pk.signlanguageapp.data.result.Result
import com.pk.signlanguageapp.databinding.ActivityListDictionaryBinding
import java.util.Locale

class ListDictionaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListDictionaryBinding

    private val viewModel: DictionaryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var originalList = listOf<DictionaryResponseItem>()

    private val dictionaryAdapter = DictionaryAdapter()

    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListDictionaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        category = intent.getStringExtra(EXTRA_LIST_DICTIONARY)

        listDictionary()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })
    }

    private fun listDictionary() {
        binding.rvListDictionary.layoutManager = LinearLayoutManager(this)

        when (category) {
            "Letter" -> {
                viewModel.getAllLetters()
                viewModel.lettersList.observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                val letters = result.data
                                val sortedLetters = letters.sortedBy { it.nama }
                                originalList = sortedLetters
                                dictionaryAdapter.submitList(sortedLetters)
                                binding.rvListDictionary.adapter = dictionaryAdapter
                            }

                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
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
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                val words = result.data
                                Log.d("resultWords", words.toString())
                                val sortedWords = words.sortedBy { it.nama }
                                originalList = sortedWords
                                dictionaryAdapter.submitList(sortedWords)
                                binding.rvListDictionary.adapter = dictionaryAdapter
                            }

                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
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

    private fun filterList(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            originalList
        } else {
            originalList.filter { it.nama.contains(query, ignoreCase = true) }
        }

        dictionaryAdapter.submitList(filteredList)
    }

    companion object {
        const val EXTRA_LIST_DICTIONARY = "extra_list_dictionary"
    }
}