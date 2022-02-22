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

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.huawei.hms.audioeditor.sdk.ChangeSoundCallback
import com.huawei.hms.audioeditor.sdk.ChangeVoiceOption
import com.huawei.hms.audioeditor.sdk.HAEChangeVoiceFile
import java.io.File
import com.huawei.hms.audioeditor.sdk.HuaweiAudioEditor

class EffectScreen : Fragment() {

    lateinit var dialog: BottomSheetDialog
    lateinit var btnShowBottomSheet: LinearLayout
    lateinit var effectImage: ImageView
    lateinit var playEffect: Button
    lateinit var audioFileName: TextView
    private var imgId: Int? = null

    private var outputPath = File(
        Environment.getExternalStorageDirectory(), ""
    ).absolutePath
    private var path: String? = File(
        Environment.getExternalStorageDirectory(),
        "record_tmp.mp3"
    ).absolutePath

    private lateinit var callBack: ChangeSoundCallback

    private var chosedEffect: Effect? = null

    enum class Effect {
        Male, Female, Monster, Cute
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_effect_screen, container, false)
        btnShowBottomSheet = view.findViewById(R.id.btn_add_audio)
        dialog = activity?.let { it1 ->
            BottomSheetDialog(
                it1,
                R.style.ThemeOverlay_App_BottomSheetDialog
            )
        }!!
        effectImage = view.findViewById(R.id.img_effect)
        playEffect = view.findViewById(R.id.btn_effect_play)
        audioFileName = view.findViewById(R.id.txt_audio_name)
        path = arguments?.getString("path")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callBack = object : ChangeSoundCallback {
            override fun onSuccess(outAudioPath: String) {
                // Callback when the processing succeeds.
            }

            override fun onProgress(progress: Int) {
                // Callback when the processing progress is received.
            }

            override fun onFail(errorCode: Int) {
                // Callback when the processing fails.
            }

            override fun onCancel() {
                // Callback when the processing is canceled.
            }
        }

        audioFileName.text = path?.split("/")?.last()
        btnShowBottomSheet.setOnClickListener {
            val v = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            val male: LinearLayout = v.findViewById(R.id.voice_changer_male)
            val female: LinearLayout = v.findViewById(R.id.voice_changer_female)
            val monster: LinearLayout = v.findViewById(R.id.voice_changer_monster)
            val cute: LinearLayout = v.findViewById(R.id.voice_changer_cute)
            male.setOnClickListener {
                chosedEffect = Effect.Male
                dialog.dismiss()
                effectImage.setImageResource(R.drawable.male)
                imgId = R.drawable.male
                effectImage.visibility = View.VISIBLE
                playEffect.visibility = View.VISIBLE
                showDialog()
            }
            female.setOnClickListener {
                chosedEffect = Effect.Female
                dialog.dismiss()
                effectImage.setImageResource(R.drawable.female)
                imgId = R.drawable.female
                effectImage.visibility = View.VISIBLE
                playEffect.visibility = View.VISIBLE
                showDialog()
            }
            monster.setOnClickListener {
                chosedEffect = Effect.Monster
                dialog.dismiss()
                effectImage.setImageResource(R.drawable.monster)
                imgId = R.drawable.monster
                effectImage.visibility = View.VISIBLE
                playEffect.visibility = View.VISIBLE
                showDialog()
            }
            cute.setOnClickListener {
                chosedEffect = Effect.Cute
                dialog.dismiss()
                effectImage.setImageResource(R.drawable.bear)
                imgId = R.drawable.bear
                effectImage.visibility = View.VISIBLE
                playEffect.visibility = View.VISIBLE
                showDialog()
            }
            dialog.setContentView(v)
            dialog.show()
        }
        playEffect.setOnClickListener {
            val mEditor = HuaweiAudioEditor.create(requireContext())
            mEditor.initEnvironment()
            val mTimeLine = mEditor.timeLine
            val audioLane = mTimeLine.appendAudioLane()
            audioLane.appendAudioAsset(path, mTimeLine.currentTime)
            mEditor.playTimeLine(mTimeLine.startTime, mTimeLine.endTime)
        }
    }

    private fun convertAudio(outFileName: String) {
        val haeChangeVoiceFile = HAEChangeVoiceFile()
        val changeVoiceOption = ChangeVoiceOption()
        when (chosedEffect) {
            Effect.Male -> {
                changeVoiceOption.voiceType = ChangeVoiceOption.VoiceType.MALE
            }
            Effect.Female -> {
                changeVoiceOption.voiceType = ChangeVoiceOption.VoiceType.FEMALE
            }
            Effect.Monster -> {
                changeVoiceOption.voiceType = ChangeVoiceOption.VoiceType.MONSTER
            }
            Effect.Cute -> {
                changeVoiceOption.voiceType = ChangeVoiceOption.VoiceType.CUTE
            }
            else -> {
                changeVoiceOption.voiceType = ChangeVoiceOption.VoiceType.MALE
            }
        }
        haeChangeVoiceFile.changeVoiceOption(changeVoiceOption)
        haeChangeVoiceFile.applyAudioFile(path, outputPath, outFileName, callBack)
    }

    private fun showDialog() {
        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.custom_layout)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val fileName = dialog?.findViewById(R.id.filename) as EditText
        val convert = dialog.findViewById(R.id.btn_convert) as Button
        convert.setOnClickListener {
            if (fileName.text != null && !fileName.text.equals("")) {
                dialog.dismiss()
                convertAudio(fileName.text.toString())
                imgId?.let { it1 -> CustomAdapter.ItemsViewModel(it1, fileName.text.toString()) }
                    ?.let { it2 ->
                        App.savedList.add(
                            it2
                        )
                    }
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.frame_layout, Library())?.commit()
            } else {
                Toast.makeText(requireContext(), "Please enter file name", Toast.LENGTH_LONG).show()
            }
        }
        dialog.show()
    }
}