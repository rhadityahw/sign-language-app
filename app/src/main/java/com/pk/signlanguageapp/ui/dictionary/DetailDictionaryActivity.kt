package com.pk.signlanguageapp.ui.dictionary

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.pk.signlanguageapp.ViewModelFactory
import com.pk.signlanguageapp.data.response.DictionaryResponseItem
import com.pk.signlanguageapp.data.result.Result
import com.pk.signlanguageapp.databinding.ActivityDetailDictionaryBinding

class DetailDictionaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailDictionaryBinding

    private val viewModel: DictionaryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var dictionary: DictionaryResponseItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDictionaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dictionary = intent.getParcelableExtra(EXTRA_DICTIONARY)


        viewModel.getWordByName(dictionary!!.nama)?.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        val word = result.data
                        val text = word.joinToString("\n") { item ->
                            item.nama
                        }
                        val video = word.joinToString("\n") { item ->
                            item.video
                        }
                        binding.tvDetailDictionary.text = text
                        val videoUri = MediaItem.fromUri(video)

                        val player = ExoPlayer.Builder(this).build().also { exoPlayer ->
                            exoPlayer.setMediaItem(videoUri)
                            exoPlayer.prepare()
                        }
                        binding.videoDetailDictionary.player = player
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            this,
                            "Terjadi kesalahan: ${result.error}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_DICTIONARY = "extra_dictionary"
    }
}