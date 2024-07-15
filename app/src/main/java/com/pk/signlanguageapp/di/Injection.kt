package com.moneo.moneo.di

import android.content.Context
import com.pk.signlanguageapp.data.remote.ApiConfig
import com.pk.signlanguageapp.data.repository.DictionaryRepository
import com.pk.signlanguageapp.data.repository.HateSpeechRepository
import com.pk.signlanguageapp.utils.AppExecutors

object Injection {
    fun provideDictionaryRepository(context: Context): DictionaryRepository {
        val apiService = ApiConfig.getApiService()
        val appExecutors = AppExecutors()
        return DictionaryRepository.getInstance(apiService, appExecutors)
    }

    fun provideHateSpeechRepository(context: Context): HateSpeechRepository {
        val apiService = ApiConfig.getApiService()
        val appExecutors = AppExecutors()
        return HateSpeechRepository.getInstance(apiService, appExecutors)
    }
}