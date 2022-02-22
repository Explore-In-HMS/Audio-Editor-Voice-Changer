// Copyright 2022. Explore in HMS. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.hms.referenceapp.voicechanger

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import java.io.File
import java.io.IOException

class Record : Fragment() {

    private var cMeter: Chronometer? = null
    private var anim: LottieAnimationView? = null
    private var state: State = State.Reset
    private var file: File = File(
        Environment.getExternalStorageDirectory(),
        "record_tmp.mp3"
    )

    private var play: Button? = null
    private var reset: Button? = null
    var done: Button? = null

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    enum class State {
        Recording, Playing, Reset, NotPlaying
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_record, container, false)
        anim = view.findViewById(R.id.record_animation)
        play = view.findViewById(R.id.play)
        reset = view.findViewById(R.id.reset)
        done = view.findViewById(R.id.send)
        cMeter = view.findViewById(R.id.cmeter)
        initRecorder()
        return view
    }

    private fun initPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setDataSource(file.absolutePath)
        mediaPlayer!!.prepare()
    }

    private fun initRecorder() {
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder?.setAudioEncodingBitRate(16);
        mediaRecorder?.setAudioSamplingRate(44100);
        mediaRecorder?.setOutputFile(file.absolutePath);

        try {
            mediaRecorder!!.prepare()
        } catch (e: java.lang.IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        done?.visibility = View.GONE
        cMeter?.apply {
            stop()
            base = SystemClock.elapsedRealtime()
            stop()
        }
        anim?.setOnClickListener {
            when (state) {
                State.Recording -> {
                    cMeter?.stop()
                    done?.visibility = View.VISIBLE
                    anim?.apply {
                        loop(false)
                        alpha = 0.85F
                    }
                    mediaRecorder?.stop()
                    state = State.NotPlaying
                    initPlayer()

                }
                State.Reset -> {
                    cMeter?.apply {
                        visibility = View.VISIBLE
                        start()
                    }
                    done?.visibility = View.GONE
                    anim?.apply {
                        playAnimation()
                        loop(true)
                        alpha = 1F
                    }
                    try {
                        mediaRecorder?.start()
                        state = State.Recording
                        Toast.makeText(requireContext(), "Recording started!", Toast.LENGTH_SHORT)
                            .show()
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else -> {
                }
            }
        }
        play?.setOnClickListener {
            if (state == State.NotPlaying || state == State.Playing) {
                when (state) {
                    State.NotPlaying -> {
                        state = State.Playing
                        play?.setBackgroundResource(R.drawable.pause)
                        mediaPlayer!!.start()
                    }
                    State.Playing -> {
                        state = State.NotPlaying
                        play?.setBackgroundResource(R.drawable.play)
                        mediaPlayer!!.pause()
                    }
                    else -> {
                    }
                }
            }
        }
        reset?.setOnClickListener {
            cMeter?.apply {
                stop()
                base = SystemClock.elapsedRealtime()
                stop()
            }
            done?.visibility = View.GONE
            state = State.Reset
            mediaRecorder?.reset()
            mediaRecorder?.release()
            initRecorder()
        }
        done?.setOnClickListener {
            state = State.Reset
            mediaRecorder?.reset()
            mediaRecorder?.release()

            val frag = EffectScreen()
            val bundle = Bundle()
            bundle.putString("path",file.absolutePath)
            frag.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frame_layout,frag)?.commit()
        }
    }
}