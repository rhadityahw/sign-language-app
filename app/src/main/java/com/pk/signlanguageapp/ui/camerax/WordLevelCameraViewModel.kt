package com.pk.signlanguageapp.ui.camerax

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.pk.signlanguageapp.data.repository.HateSpeechRepository
import com.pk.signlanguageapp.data.response.HateSpeechResponse
import com.pk.signlanguageapp.data.result.Result
import com.pk.signlanguageapp.mediapipe.GestureRecognizerHelper
import com.pk.signlanguageapp.mediapipe.HandLandmarkHelper

class WordLevelCameraViewModel(
    private val hateSpeechRepository: HateSpeechRepository
) : ViewModel() {

    private var _delegate: Int = GestureRecognizerHelper.DELEGATE_CPU
    private var _minHandDetectionConfidence: Float =
        GestureRecognizerHelper.DEFAULT_HAND_DETECTION_CONFIDENCE
    private var _minHandTrackingConfidence: Float = GestureRecognizerHelper
        .DEFAULT_HAND_TRACKING_CONFIDENCE
    private var _minHandPresenceConfidence: Float = GestureRecognizerHelper
        .DEFAULT_HAND_PRESENCE_CONFIDENCE

    val currentDelegate: Int get() = _delegate
    val currentMinHandDetectionConfidence: Float
        get() =
            _minHandDetectionConfidence
    val currentMinHandTrackingConfidence: Float
        get() =
            _minHandTrackingConfidence
    val currentMinHandPresenceConfidence: Float
        get() =
            _minHandPresenceConfidence

    fun setDelegate(delegate: Int) {
        _delegate = delegate
    }

    fun setMinHandDetectionConfidence(confidence: Float) {
        _minHandDetectionConfidence = confidence
    }
    fun setMinHandTrackingConfidence(confidence: Float) {
        _minHandTrackingConfidence = confidence
    }
    fun setMinHandPresenceConfidence(confidence: Float) {
        _minHandPresenceConfidence = confidence
    }

    fun getPrepKeypoints(resultBundle: HandLandmarkHelper.ResultBundle): List<Float> {
        val lh = MutableList(63) { 0.0f }
        val rh = MutableList(63) { 0.0f }

        resultBundle.results.firstOrNull()?.let { result ->
            result.landmarks().forEachIndexed { index, normalizedLandmarks ->
                val handLabel = result.handednesses()?.get(index)?.firstOrNull()?.categoryName()
                val keypoints = normalizedLandmarks.map { listOf(it.x(), it.y(), it.z()) }.flatten()
                when (handLabel) {
                    "Right" -> {
                        for (i in keypoints.indices) {
                            lh[i] = keypoints[i]
                        }
                    }
                    "Left" -> {
                        for (i in keypoints.indices) {
                            rh[i] = keypoints[i]
                        }
                    }
                }
            }
        }
        return lh + rh
    }

    fun getHateSpeech(text: String): LiveData<Result<HateSpeechResponse>> {
        return hateSpeechRepository.getHateSpeech(text).asLiveData()
    }
}