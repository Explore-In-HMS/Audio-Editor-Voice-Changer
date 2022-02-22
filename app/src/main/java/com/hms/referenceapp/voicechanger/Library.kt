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

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hms.audioeditor.sdk.HuaweiAudioEditor
import java.io.File

class Library : Fragment() {

    private lateinit var recyclerView : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_library, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val data = App.savedList
        val adapter = CustomAdapter(data){ position -> onListItemClick(position) }
        recyclerView.adapter = adapter
    }

    private fun onListItemClick(position: Int) {
        val path = File(Environment.getExternalStorageDirectory(),"").absolutePath+"/"+App.savedList[position].text+".wav"
        val mEditor = HuaweiAudioEditor.create(requireContext())
        mEditor.initEnvironment()
        val mTimeLine = mEditor.timeLine
        val audioLane = mTimeLine.appendAudioLane()
        audioLane.appendAudioAsset(path, mTimeLine.currentTime)
        mEditor.playTimeLine(mTimeLine.startTime, mTimeLine.endTime)
    }
}