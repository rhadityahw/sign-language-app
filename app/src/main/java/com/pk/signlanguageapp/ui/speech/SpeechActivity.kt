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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.components.containers.Category
import com.google.mediapipe.tasks.text.textclassifier.TextClassifierResult
import com.pk.signlanguageapp.databinding.ActivitySpeechBinding
import com.pk.signlanguageapp.mediapipe.TextClassifierHelper
import java.util.Locale

class SpeechActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpeechBinding
    private val speechRecognizer: SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(this)
    }

    private var resultTextClassifier: Category? = null

    private lateinit var classifierHelper: TextClassifierHelper

    private val allowPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            it.let {
                if (it) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private val listener = object :
        TextClassifierHelper.TextResultsListener {
        override fun onResult(
            results: TextClassifierResult,
            inferenceTime: Long
        ) {
            runOnUiThread {
                Log.d("HASIL NLP" , results.classificationResult()
                    .classifications().first()
                    .categories().maxByOrNull {
                        it.score()
                    }.toString()
                )
                resultTextClassifier = results.classificationResult()
                    .classifications().first()
                    .categories().maxByOrNull {
                        it.score()
                    }
            }
        }

        override fun onError(error: String) {
            Toast.makeText(this@SpeechActivity, error, Toast.LENGTH_SHORT).show()
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

        runOnUiThread {
            classifierHelper = TextClassifierHelper(
                context = this@SpeechActivity,
                listener = listener
            )
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
                    classifierHelper.classify(result?.get(0).toString())

                    resultTextClassifier?.let { category ->
                        Log.d("jajdja", category.toString())
                        val index = category.index()
                        val score = category.score()

                        if (index == 1 && score > 0.5) {
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

                    binding.tvTranslate.text = result?.get(0)
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