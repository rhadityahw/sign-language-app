package com.pk.signlanguageapp.ui.dictionary

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.pk.signlanguageapp.ViewModelFactory
import com.pk.signlanguageapp.data.response.DictionaryResponseItem
import com.pk.signlanguageapp.data.result.Result
import com.pk.signlanguageapp.databinding.ActivityDetailDictionaryBinding
import kotlinx.coroutines.launch

class DetailDictionaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailDictionaryBinding

    private val viewModel: DictionaryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var category: String? = null
    private var dictionary: DictionaryResponseItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDictionaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dictionary = intent.getParcelableExtra(EXTRA_DICTIONARY)
        category = intent.getStringExtra(EXTRA_CATEGORY_DICTIONARY)

        when (category) {
            "Letter" -> {
                viewModel.getLetterByName(dictionary!!.nama)
                viewModel.letter.observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                lifecycleScope.launch {
                                    val letter = result.data
                                    val text = letter.joinToString("\n") { item ->
                                        item.nama
                                    }
                                    val video = letter.joinToString("\n") { item ->
                                        item.video
                                    }
                                    binding.tvDetailDictionary.text = text
                                    val videoUri = MediaItem.fromUri(video)

                                    val player = ExoPlayer.Builder(this@DetailDictionaryActivity).build().also { exoPlayer ->
                                        exoPlayer.setMediaItem(videoUri)
                                        exoPlayer.prepare()
                                        exoPlayer.playWhenReady = true
                                    }
                                    binding.videoDetailDictionary.player = player
                                }
                            }
                            is Result.Error -> {
                                Toast.makeText(
                                    this@DetailDictionaryActivity,
                                    "Terjadi kesalahan: ${result.error}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
            "Word" -> {
                viewModel.getWordByName(dictionary!!.nama)
                viewModel.word.observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                lifecycleScope.launch {
                                    val word = result.data
                                    val text = word.joinToString("\n") { item ->
                                        item.nama
                                    }
                                    val video = word.joinToString("\n") { item ->
                                        item.video
                                    }
                                    val videoUri = MediaItem.fromUri(video)

                                    val player = ExoPlayer.Builder(this@DetailDictionaryActivity).build().also { exoPlayer ->
                                        exoPlayer.setMediaItem(videoUri)
                                        exoPlayer.addListener(object : Player.Listener {
                                            override fun onPlaybackStateChanged(state: Int) {
                                                if (state == Player.STATE_READY) {
                                                    binding.videoDetailDictionary.visibility = View.VISIBLE
                                                    binding.tvDetailDictionary.visibility = View.VISIBLE
                                                    binding.progressBar.visibility = View.GONE
                                                }
                                            }
                                        })
                                        exoPlayer.prepare()
                                    }
                                    binding.videoDetailDictionary.player = player
                                    binding.tvDetailDictionary.text = text
                                }
                            }
                            is Result.Error -> {
                                Toast.makeText(
                                    this@DetailDictionaryActivity,
                                    "Terjadi kesalahan: ${result.error}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_DICTIONARY = "extra_dictionary"
        const val EXTRA_CATEGORY_DICTIONARY = "extra_category_dictionary"
    }
}