/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pk.signlanguageapp.ui.camerax

import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.pk.signlanguageapp.data.repository.HateSpeechRepository
import com.pk.signlanguageapp.data.response.HateSpeechResponse
import com.pk.signlanguageapp.data.result.Result
import com.pk.signlanguageapp.mediapipe.GestureRecognizerHelper
import kotlinx.coroutines.launch

/**
 *  This ViewModel is used to store hand landmarker helper settings
 */
class CameraViewModel(private val hateSpeechRepository: HateSpeechRepository) : ViewModel() {

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

//    private val _gestureString = MutableLiveData<String>()
//    val gestureString: LiveData<String> get() = _gestureString
//
//    fun setGestureString(string: String){
//        _gestureString.value = string
//    }

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

//    private val _isHate = MutableLiveData<Result<HateSpeechResponse>>()
//    val isHate: LiveData<Result<HateSpeechResponse>> get() = _isHate

    @OptIn(UnstableApi::class)
    fun getHateSpeech(text: String): LiveData<Result<HateSpeechResponse>> {
//        viewModelScope.launch {
//            hateSpeechRepository.getHateSpeech(text).collect { result ->
//                _isHate.value = result
//                Log.d("isHateVM", _isHate.value.toString())
//            }
//        }
//        return _isHate
        return hateSpeechRepository.getHateSpeech(text).asLiveData()
    }
}
