package com.pk.signlanguageapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moneo.moneo.di.Injection
import com.pk.signlanguageapp.data.repository.DictionaryRepository
import com.pk.signlanguageapp.data.repository.HateSpeechRepository
import com.pk.signlanguageapp.ui.camerax.CameraViewModel
import com.pk.signlanguageapp.ui.camerax.WordLevelCameraViewModel
import com.pk.signlanguageapp.ui.dictionary.DictionaryViewModel
import com.pk.signlanguageapp.ui.speech.SpeechViewModel

class ViewModelFactory(
    private var dictionaryRepository: DictionaryRepository,
    private var hateSpeechRepository: HateSpeechRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DictionaryViewModel::class.java)) {
            return DictionaryViewModel(dictionaryRepository) as T
        }
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            return CameraViewModel(hateSpeechRepository) as T
        }
        if (modelClass.isAssignableFrom(WordLevelCameraViewModel::class.java)) {
            return WordLevelCameraViewModel() as T
        }
        if (modelClass.isAssignableFrom(SpeechViewModel::class.java)) {
            return SpeechViewModel(hateSpeechRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideDictionaryRepository(context),
                    Injection.provideHateSpeechRepository(context)
                )
            }.also { instance = it }
    }
}