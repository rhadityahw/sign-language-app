package com.pk.signlanguageapp.ui.dictionary

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pk.signlanguageapp.data.repository.DictionaryRepository
import com.pk.signlanguageapp.data.response.DetailDictionaryResponseItem
import com.pk.signlanguageapp.data.response.DictionaryResponseItem
import com.pk.signlanguageapp.data.result.Result
import kotlinx.coroutines.launch

class DictionaryViewModel(private val dictionaryRepository: DictionaryRepository) : ViewModel() {

    private var _alphabetsList: LiveData<Result<List<DictionaryResponseItem>>>? = null

    private var _wordsList: LiveData<Result<List<DictionaryResponseItem>>>? = null

    private var _word: LiveData<Result<List<DetailDictionaryResponseItem>>>? = null

    fun getAllLetters(): LiveData<Result<List<DictionaryResponseItem>>>? {
        viewModelScope.launch {
            _alphabetsList = dictionaryRepository.getAllLetters()
        }
        return _alphabetsList
    }

    fun getAllWords(): LiveData<Result<List<DictionaryResponseItem>>>? {
        viewModelScope.launch {
            _wordsList = dictionaryRepository.getAllWords()
        }
        return _wordsList
    }

    fun getWordByName(nama: String): LiveData<Result<List<DetailDictionaryResponseItem>>>? {
        viewModelScope.launch {
            _word = dictionaryRepository.getWordByName(nama)
        }
        return _word
    }

}