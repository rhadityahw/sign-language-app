package com.pk.signlanguageapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.pk.signlanguageapp.data.remote.ApiService
import com.pk.signlanguageapp.data.response.DetailDictionaryResponseItem
import com.pk.signlanguageapp.data.response.DictionaryResponseItem
import com.pk.signlanguageapp.data.result.Result
import com.pk.signlanguageapp.utils.AppExecutors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.awaitResponse

class DictionaryRepository private constructor(
    private val apiService: ApiService,
    private val appExecutors: AppExecutors
){

    fun getAllLetters(): Flow<Result<List<DictionaryResponseItem>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllHuruf().awaitResponse()
            if (response.isSuccessful) {
                val letters = response.body()
                if (letters != null) {
                    emit(Result.Success(letters))
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

    fun getAllWords(): Flow<Result<List<DictionaryResponseItem>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllKata().awaitResponse()
            if (response.isSuccessful) {
                val words = response.body()
                if (words != null) {
                    emit(Result.Success(words))
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

    fun getLetterByName(nama: String) : Flow<Result<List<DetailDictionaryResponseItem>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getLetterByName(nama).awaitResponse()
            if (response.isSuccessful) {
                val letter = response.body()
                if (letter != null) {
                    emit(Result.Success(letter))
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

    suspend fun getWordByName(nama: String): Flow<Result<List<DetailDictionaryResponseItem>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getWordByName(nama).awaitResponse()
            if (response.isSuccessful) {
                val word = response.body()
                if (word != null) {
                    emit(Result.Success(word))
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
        private var INSTANCE: DictionaryRepository? = null
        fun getInstance(
            apiService: ApiService,
            appExecutors: AppExecutors
        ): DictionaryRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: DictionaryRepository(apiService, appExecutors)
            }.also { INSTANCE = it }
    }
}