package com.pk.signlanguageapp.data.remote

import com.pk.signlanguageapp.data.response.DetailDictionaryResponseItem
import com.pk.signlanguageapp.data.response.DictionaryResponse
import com.pk.signlanguageapp.data.response.DictionaryResponseItem
import com.pk.signlanguageapp.data.response.HateSpeechResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/videos-huruf")
    fun getAllHuruf() : Call<List<DictionaryResponseItem>>

    @GET("/videos-kata")
    fun getAllKata() : Call<List<DictionaryResponseItem>>

    @GET("/videos-huruf/{nama}")
    fun getLetterByName(
        @Path("nama") nama: String
    ) : Call<List<DetailDictionaryResponseItem>>

    @GET("/videos-kata/{nama}")
    fun getWordByName(
        @Path("nama") nama: String
    ) : Call<List<DetailDictionaryResponseItem>>

    @GET("hate-speech/{text}")
    fun getHateSpeech(
        @Path("text") text: String
    ) : Call<HateSpeechResponse>
}