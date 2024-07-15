package com.pk.signlanguageapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.pk.signlanguageapp.data.remote.ApiService
import com.pk.signlanguageapp.data.response.HateSpeechResponse
import com.pk.signlanguageapp.data.result.Result
import com.pk.signlanguageapp.utils.AppExecutors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class HateSpeechRepository private constructor(
    private val apiService: ApiService,
    private val appExecutors: AppExecutors
) {

    fun getHateSpeech(text: String): Flow<Result<HateSpeechResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getHateSpeech(text).awaitResponse()
            if (response.isSuccessful) {
                val isHate = response.body()
                Log.d("isHateRepo", isHate.toString())
                if (isHate != null) {
                    emit(Result.Success(isHate))
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

    fun getHateSpeechResult(text: String): LiveData<Result<HateSpeechResponse>> {
        val result = MediatorLiveData<Result<HateSpeechResponse>>()

        val client = apiService.getHateSpeech(text)
        client.enqueue(object : Callback<HateSpeechResponse> {
            override fun onResponse(
                call: Call<HateSpeechResponse>,
                response: Response<HateSpeechResponse>,
            ) {
                if (response.isSuccessful) {
                    val hateSpeechResult = response.body()
                    if (hateSpeechResult != null) {
                        appExecutors.diskIO.execute {
                            result.value = Result.Success(hateSpeechResult)
                        }
                    }
                } else {
                    result.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<HateSpeechResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }

        })
        return result
    }

    companion object {
        @Volatile
        private var INSTANCE: HateSpeechRepository? = null
        fun getInstance(
            apiService: ApiService,
            appExecutors: AppExecutors
        ): HateSpeechRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HateSpeechRepository(apiService, appExecutors)
            }.also { INSTANCE = it }
    }
}