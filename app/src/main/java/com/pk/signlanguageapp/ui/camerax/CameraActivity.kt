package com.pk.signlanguageapp.ui.camerax

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.text.textclassifier.TextClassifier
import com.google.mediapipe.tasks.text.textclassifier.TextClassifierResult
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.pk.signlanguageapp.databinding.ActivityCameraBinding
import com.pk.signlanguageapp.mediapipe.GestureRecognizerHelper
import com.pk.signlanguageapp.mediapipe.TextClassifierHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraActivity : AppCompatActivity(), GestureRecognizerHelper.GestureRecognizerListener {

    private lateinit var binding: ActivityCameraBinding

    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper
//    private lateinit var classifierHelper: TextClassifierHelper

    private val cameraViewModel: CameraViewModel by viewModels()

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT

    private lateinit var backgroundExecutor: ExecutorService

    private var lastDetectedGesture = ""
    private var lastDetectedTime = 0L
    private var detectedString = ""

    // NLP
    private val currentModel = "model.tflite"
    private val baseOptionsBuilder = BaseOptions.builder()
        .setModelAssetPath(currentModel)
    private val baseOptions = baseOptionsBuilder.build()
    private val optionsBuilder = TextClassifier.TextClassifierOptions.builder()
        .setBaseOptions(baseOptions)
    private val options = optionsBuilder.build()

    private var textClassifier: TextClassifier? = null

    private val listener = object :
        TextClassifierHelper.TextResultsListener {
        override fun onResult(
            results: TextClassifierResult,
            inferenceTime: Long
        ) {
            backgroundExecutor.execute {
                Log.d("HASIL NLP" , results.classificationResult()
                    .classifications().first()
                    .categories().sortedByDescending {
                        it.score()
                    }.toString()
                )
            }
        }

        override fun onError(error: String) {
            Toast.makeText(this@CameraActivity, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        backgroundExecutor.execute {
            if (gestureRecognizerHelper.isClosed()) {
                gestureRecognizerHelper.setupGestureRecognizer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::gestureRecognizerHelper.isInitialized) {
            cameraViewModel.setMinHandDetectionConfidence(gestureRecognizerHelper.minHandDetectionConfidence)
            cameraViewModel.setMinHandTrackingConfidence(gestureRecognizerHelper.minHandTrackingConfidence)
            cameraViewModel.setMinHandPresenceConfidence(gestureRecognizerHelper.minHandPresenceConfidence)
            cameraViewModel.setDelegate(gestureRecognizerHelper.currentDelegate)

            backgroundExecutor.execute { gestureRecognizerHelper.clearGestureRecognizer() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textClassifier = TextClassifier.createFromOptions(this, options)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        backgroundExecutor = Executors.newSingleThreadExecutor()

        switchCamera()

        binding.viewFinder.post {
            setupCamera()
        }


        backgroundExecutor.execute {
            gestureRecognizerHelper = GestureRecognizerHelper(
                context = this,
                runningMode = RunningMode.LIVE_STREAM,
                minHandDetectionConfidence = cameraViewModel.currentMinHandDetectionConfidence,
                minHandTrackingConfidence = cameraViewModel.currentMinHandTrackingConfidence,
                minHandPresenceConfidence = cameraViewModel.currentMinHandPresenceConfidence,
                currentDelegate = cameraViewModel.currentDelegate,
                gestureRecognizerListener = this
            )

//            classifierHelper = TextClassifierHelper(
//                context = this@CameraActivity,
//                listener = listener
//            )
        }


    }

    private fun setupCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()

                bindCameraUseCases()
            }, ContextCompat.getMainExecutor(this)
        )
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {

        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraFacing).build()

        preview = Preview.Builder()
            .build()

        imageAnalyzer =
            ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(backgroundExecutor) { image ->
                        recognizeHand(image)
                    }
                }

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun recognizeHand(imageProxy: ImageProxy) {
        gestureRecognizerHelper.recognizeLiveStream(
            imageProxy = imageProxy,
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onResults(
        resultBundle: GestureRecognizerHelper.ResultBundle
    ) {
        runOnUiThread {
            val gestureCategories = resultBundle.results.first().gestures()
            if (gestureCategories.isNotEmpty()) {

                val gestureText = gestureCategories.first().toString()

                val startIndex = gestureText.indexOf("\"") + 1
                val endIndex = gestureText.indexOf("\"", startIndex)
                val displayName = gestureText.substring(startIndex, endIndex)

                Log.d("HASIL", displayName)

                binding.tvGesture.text = "Gesture: $displayName"

                val currentTime = System.currentTimeMillis() / 1000 // Convert to seconds

                if (displayName == lastDetectedGesture) {
                    if (currentTime - lastDetectedTime >= 2) {
                        when (displayName) {
                            "space" -> detectedString += " "
                            "del" -> if (detectedString.isNotEmpty()) detectedString = detectedString.dropLast(1)
                            else -> detectedString += displayName
                        }
                        lastDetectedGesture = ""
                    }
                } else {
                    lastDetectedGesture = displayName
                    lastDetectedTime = currentTime
                }

                detectedString = "dasar babi"

                Log.d("HASIL", detectedString)

//                classifierHelper.classify(detectedString)

                val result = textClassifier?.classify(detectedString)
                Log.d("TAG", result.toString())
                val data = result?.classificationResult()?.classifications()
                if (data?.isNotEmpty() == true) {
                    Log.d("TAG", data.toString())
                    val category = data[0].categories()
                    if (category.isNotEmpty()) {
                        val name = "${category[0].categoryName()} ${category[0].displayName()}"
                        Log.d("TAG", name)
                    }
                }

                binding.tvTranslate.text = detectedString
            }

            binding.overlay.setResults(
                resultBundle.results.first(),
                resultBundle.inputImageHeight,
                resultBundle.inputImageWidth,
                RunningMode.LIVE_STREAM
            )

            binding.overlay.invalidate()
        }
    }

    override fun onError(error: String, errorCode: Int) {
        runOnUiThread {
            Toast.makeText(this@CameraActivity, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun switchCamera() {
        binding.switchCamera.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA

            cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_FRONT) CameraSelector.LENS_FACING_BACK
            else CameraSelector.LENS_FACING_FRONT

            setupCamera()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val TAG = "CameraActivity"
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}