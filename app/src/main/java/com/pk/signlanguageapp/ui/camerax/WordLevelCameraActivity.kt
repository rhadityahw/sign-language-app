package com.pk.signlanguageapp.ui.camerax

import MotionClassifier
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
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.pk.signlanguageapp.ViewModelFactory
import com.pk.signlanguageapp.databinding.ActivityWordLevelCameraBinding
import com.pk.signlanguageapp.mediapipe.HandLandmarkHelper
import com.pk.signlanguageapp.utils.sliceLast
import okhttp3.internal.toImmutableList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class WordLevelCameraActivity : AppCompatActivity(), HandLandmarkHelper.LandmarkerListener {

    private lateinit var binding: ActivityWordLevelCameraBinding

    private lateinit var handLandmarkerHelper: HandLandmarkHelper

    private val wordLevelCameraViewModel: WordLevelCameraViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT

    private lateinit var backgroundExecutor: ExecutorService
    private var sequences: MutableList<List<Float>> = mutableListOf()
    private lateinit var motionClassifier: MotionClassifier

    override fun onResume() {
        super.onResume()
        backgroundExecutor.execute {
            if (handLandmarkerHelper.isClosed()) {
                handLandmarkerHelper.setupHandLandmarker()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::handLandmarkerHelper.isInitialized) {
            wordLevelCameraViewModel.setMinHandDetectionConfidence(handLandmarkerHelper.minHandDetectionConfidence)
            wordLevelCameraViewModel.setMinHandTrackingConfidence(handLandmarkerHelper.minHandTrackingConfidence)
            wordLevelCameraViewModel.setMinHandPresenceConfidence(handLandmarkerHelper.minHandPresenceConfidence)
            wordLevelCameraViewModel.setDelegate(handLandmarkerHelper.currentDelegate)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
        backgroundExecutor.shutdown()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        motionClassifier  = MotionClassifier(this, "gestura.tflite")
        binding = ActivityWordLevelCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        backgroundExecutor = Executors.newSingleThreadExecutor()

        switchCamera()

        backgroundExecutor.execute {
            handLandmarkerHelper = HandLandmarkHelper(
                context = this,
                runningMode = RunningMode.LIVE_STREAM,
                maxNumHands = 2,
                minHandDetectionConfidence = wordLevelCameraViewModel. currentMinHandDetectionConfidence,
                minHandTrackingConfidence = wordLevelCameraViewModel.currentMinHandTrackingConfidence,
                minHandPresenceConfidence = wordLevelCameraViewModel.currentMinHandPresenceConfidence,
                currentDelegate = wordLevelCameraViewModel.currentDelegate,
                handLandmarkerHelperListener = this
            )
        }
    }

//    private fun startCamera() {
//
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener({
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//                }
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    this,
//                    cameraSelector,
//                    preview
//                )
//            } catch (exc: Exception) {
//                Toast.makeText(
//                    this,
//                    "Gagal memunculkan kamera.",
//                    Toast.LENGTH_SHORT
//                ).show()
//                Log.e(TAG, "startCamera: ${exc.message}")
//            }
//        }, ContextCompat.getMainExecutor(this))
//    }

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
        handLandmarkerHelper.detectLiveStream(
            imageProxy = imageProxy
        )
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
        private const val TAG = "WordLevelCameraActivity"
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    override fun onError(error: String, errorCode: Int) {
        runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResults(resultBundle: HandLandmarkHelper.ResultBundle) {


        runOnUiThread {
            val keypoint = wordLevelCameraViewModel.getPrepKeypoints(resultBundle)
            sequences.add(keypoint)
            sequences = sequences.sliceLast(30).toMutableList()
            if(sequences.count() == 30){
                val classify = motionClassifier.classify(sequences)

                //Classify mereturn string berupa predictionnya
                Log.d("hasil: ", classify)
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
}