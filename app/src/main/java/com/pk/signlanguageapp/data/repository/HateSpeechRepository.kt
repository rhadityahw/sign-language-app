package com.pk.signlanguageapp.data.repository

import com.pk.signlanguageapp.data.remote.ApiService
import com.pk.signlanguageapp.data.response.HateSpeechResponse
import com.pk.signlanguageapp.data.result.Result
import com.pk.signlanguageapp.utils.AppExecutors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.awaitResponse

class HateSpeechRepository private constructor(
    private val apiService: ApiService
) {

    suspend fun getHateSpeech(text: String): Flow<Result<HateSpeechResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getHateSpeech(text).awaitResponse()
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    emit(Result.Success(result))
                } else {
                    emit(Result.Error("No data found"))
                }
            } else {
                emit(Result.Error("Response not successful"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: HateSpeechRepository? = null
        fun getInstance(
            apiService: ApiService
        ): HateSpeechRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HateSpeechRepository(apiService)
            }.also { INSTANCE = it }
    }
}