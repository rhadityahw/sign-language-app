package com.pk.signlanguageapp.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DictionaryResponse(

	@field:SerializedName("DictionaryResponse")
	val dictionaryResponse: List<DictionaryResponseItem>
) : Parcelable

@Parcelize
data class DictionaryResponseItem(

	@field:SerializedName("Nama")
	val nama: String,

	@field:SerializedName("Video")
	val video: String,

	@field:SerializedName("id")
	val id: String
) : Parcelable

@Parcelize
data class DetailDictionaryResponse(

	@field:SerializedName("DetailDictionaryResponse")
	val detailDictionaryResponse: List<DetailDictionaryResponseItem>
) : Parcelable

@Parcelize
data class DetailDictionaryResponseItem(

	@field:SerializedName("Nama")
	val nama: String,

	@field:SerializedName("Video")
	val video: String
) : Parcelable

