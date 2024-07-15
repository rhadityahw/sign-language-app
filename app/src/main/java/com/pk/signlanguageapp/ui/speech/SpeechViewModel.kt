package com.pk.signlanguageapp.ui.speech

import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.pk.signlanguageapp.data.repository.HateSpeechRepository
import com.pk.signlanguageapp.data.response.HateSpeechResponse
import com.pk.signlanguageapp.data.result.Result
import kotlinx.coroutines.launch

class SpeechViewModel(private val hateSpeechRepository: HateSpeechRepository) : ViewModel() {

//    private val _isHate = MutableLiveData<Result<HateSpeechResponse>>()
//    val isHate: LiveData<Result<HateSpeechResponse>> get() = _isHate

    @OptIn(UnstableApi::class)
    fun getHateSpeech(text: String): LiveData<Result<HateSpeechResponse>> {
//        viewModelScope.launch {
//            hateSpeechRepository.getHateSpeech(text).collect { result ->
//                _isHate.value = result
//                Log.d("isHateVM", _isHate.value.toString())
//            }
//        }
//        return _isHate
        return hateSpeechRepository.getHateSpeech(text).asLiveData()
    }

}