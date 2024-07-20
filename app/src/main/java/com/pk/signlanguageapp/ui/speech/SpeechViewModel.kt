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


    fun getHateSpeech(text: String): LiveData<Result<HateSpeechResponse>> {
        return hateSpeechRepository.getHateSpeech(text).asLiveData()
    }

}