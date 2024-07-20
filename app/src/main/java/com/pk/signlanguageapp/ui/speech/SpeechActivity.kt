package com.pk.signlanguageapp.ui.speech

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.components.containers.Category
import com.google.mediapipe.tasks.text.textclassifier.TextClassifierResult
import com.pk.signlanguageapp.ViewModelFactory
import com.pk.signlanguageapp.databinding.ActivitySpeechBinding
import com.pk.signlanguageapp.ui.camerax.CameraViewModel
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import com.pk.signlanguageapp.data.result.Result

class SpeechActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpeechBinding
    private val speechRecognizer: SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(this)
    }

    private val viewModel: SpeechViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var speechResult = ""
    private var hateResult = false

    private val allowPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            it.let {
                if (it) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpeechBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnListen.setOnTouchListener { _, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    speechRecognizer.stopListening()
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_DOWN -> {
                    getPermissionOver(this) {
                        startListen()
                    }
                    return@setOnTouchListener true
                }
                else -> {
                    return@setOnTouchListener true
                }
            }
        }
    }

    private fun startListen() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ID")
        speechRecognizer.setRecognitionListener(object: RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {

            }

            override fun onBeginningOfSpeech() {
                binding.tvTranslate.text = "Listening..."
            }

            override fun onRmsChanged(rmsdB: Float) {
            }

            override fun onBufferReceived(buffer: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(error: Int) {
            }

            override fun onResults(bundle: Bundle?) {
                bundle?.let {
                    val result = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    speechResult = result?.get(0).toString()
                    viewModel.getHateSpeech(speechResult).observe(this@SpeechActivity) { category ->
                        if (category != null) {
                            when (category) {
                                is Result.Loading -> {

                                }
                                is Result.Success -> {
                                    hateResult = category.data.result
                                    Log.d("speechHateSpeech", hateResult.toString())
                                    if (hateResult) {
                                        AlertDialog.Builder(this@SpeechActivity).apply {
                                            setTitle("Peringatan!")
                                            setMessage("Anda terdeteksi melakukan hate speech")
                                            setNegativeButton("OK") { dialog, _ ->
                                                dialog.dismiss()
                                            }
                                            create()
                                            show()
                                        }
                                    }
                                }
                                is Result.Error -> {

                                }
                            }
                        }
                    }

                    binding.tvTranslate.text = speechResult
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
            }
        })

        speechRecognizer.startListening(intent)
    }

    private fun getPermissionOver(context: Context, call: () -> Unit) {
        if (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED) {
            call.invoke()
        } else {
            allowPermission.launch(android.Manifest.permission.RECORD_AUDIO)
        }
    }
}