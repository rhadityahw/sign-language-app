package com.pk.signlanguageapp.ui.dictionary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pk.signlanguageapp.data.repository.DictionaryRepository
import com.pk.signlanguageapp.data.response.DetailDictionaryResponseItem
import com.pk.signlanguageapp.data.response.DictionaryResponseItem
import com.pk.signlanguageapp.data.result.Result
import kotlinx.coroutines.launch

class DictionaryViewModel(private val dictionaryRepository: DictionaryRepository) : ViewModel() {

    private val _lettersList = MutableLiveData<Result<List<DictionaryResponseItem>>>()
    val lettersList: LiveData<Result<List<DictionaryResponseItem>>> get() = _lettersList

    private val _wordsList = MutableLiveData<Result<List<DictionaryResponseItem>>>()
    val wordsList: LiveData<Result<List<DictionaryResponseItem>>> get() = _wordsList

    private val _letter = MutableLiveData<Result<List<DetailDictionaryResponseItem>>>()
    val letter: LiveData<Result<List<DetailDictionaryResponseItem>>> get() = _letter

    private val _word = MutableLiveData<Result<List<DetailDictionaryResponseItem>>>()
    val word: LiveData<Result<List<DetailDictionaryResponseItem>>> get() = _word

    fun getAllLetters(): LiveData<Result<List<DictionaryResponseItem>>> {
        viewModelScope.launch {
            dictionaryRepository.getAllLetters().collect { result ->
                _lettersList.value = result
            }
        }
        return _lettersList
    }

    fun getAllWords(): LiveData<Result<List<DictionaryResponseItem>>> {
        viewModelScope.launch {
            dictionaryRepository.getAllWords().collect { result ->
                _wordsList.value = result
            }
        }
        return _wordsList
    }

    fun getLetterByName(nama: String): LiveData<Result<List<DetailDictionaryResponseItem>>> {
        viewModelScope.launch {
            dictionaryRepository.getLetterByName(nama).collect { result ->
                _letter.value = result
            }
        }
        return _letter
    }

    fun getWordByName(nama: String): LiveData<Result<List<DetailDictionaryResponseItem>>> {
        viewModelScope.launch {
            dictionaryRepository.getWordByName(nama).collect { result ->
                _word.value = result
            }
        }
        return _word
    }

}