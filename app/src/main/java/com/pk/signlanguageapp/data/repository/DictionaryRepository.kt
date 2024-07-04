package com.pk.signlanguageapp.data.repository

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import com.pk.signlanguageapp.data.remote.ApiService
import com.pk.signlanguageapp.data.response.DetailDictionaryResponseItem
import com.pk.signlanguageapp.data.response.DictionaryResponseItem
import com.pk.signlanguageapp.data.result.Result
import com.pk.signlanguageapp.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DictionaryRepository private constructor(
    private val apiService: ApiService,
    private val appExecutors: AppExecutors
){

    fun getAllLetters(): MediatorLiveData<Result<List<DictionaryResponseItem>>> {
        val result = MediatorLiveData<Result<List<DictionaryResponseItem>>>()

        result.value = Result.Loading
        val client = apiService.getAllHuruf()
        client.enqueue(object : Callback<List<DictionaryResponseItem>> {
            override fun onResponse(call: Call<List<DictionaryResponseItem>>, response: Response<List<DictionaryResponseItem>>) {
                if (response.isSuccessful) {
                    val letters = response.body()
                    Log.d("response", letters.toString())
                    if (letters != null) {
                        appExecutors.diskIO.execute {
                            result.postValue(Result.Success(letters))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<DictionaryResponseItem>>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })

        return result
    }

    fun getAllWords(): MediatorLiveData<Result<List<DictionaryResponseItem>>> {
        val result = MediatorLiveData<Result<List<DictionaryResponseItem>>>()

        result.value = Result.Loading
        val client = apiService.getAllKata()
        client.enqueue(object : Callback<List<DictionaryResponseItem>> {
            override fun onResponse(call: Call<List<DictionaryResponseItem>>, response: Response<List<DictionaryResponseItem>>) {
                if (response.isSuccessful) {
                    val words = response.body()
                    if (words != null) {
                        appExecutors.diskIO.execute {
                            result.postValue(Result.Success(words))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<DictionaryResponseItem>>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }

        })

        return result
    }

    fun getWordByName(nama: String) : MediatorLiveData<Result<List<DetailDictionaryResponseItem>>> {
        val result = MediatorLiveData<Result<List<DetailDictionaryResponseItem>>>()

        result.value = Result.Loading
        val client = apiService.getWordByName(nama)
        client.enqueue(object : Callback<List<DetailDictionaryResponseItem>> {
            override fun onResponse(
                call: Call<List<DetailDictionaryResponseItem>>,
                response: Response<List<DetailDictionaryResponseItem>>
            ) {
                if (response.isSuccessful) {
                    val word = response.body()
                    if (word != null) {
                        appExecutors.diskIO.execute {
                            result.postValue(Result.Success(word))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<DetailDictionaryResponseItem>>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }

        })

        return result
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