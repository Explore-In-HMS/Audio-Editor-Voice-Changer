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

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hms.referenceapp.voicechanger.utils.FilePathUtil

class MainActivity : AppCompatActivity() {

    var bnv : BottomNavigationView? = null
    val record = Record()
    val library = Library()
    var uri:Uri? = null
    var path:String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUI()
        setListeners()
        requestPermission()

        setFragment(record)

    }
    private fun setUI(){
        bnv = findViewById(R.id.bnv)
    }

    private fun setListeners(){
        bnv?.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.add_icon ->{
                    selectAudio()
                }
                R.id.record_icon ->{
                    setFragment(record)
                }
                R.id.saved_icon ->{
                    setFragment(library)
                }
            }
            true
        }
    }
    private  fun setFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragment).commit()
    }
    private fun selectAudio(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "audio/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                uri = data?.data
                path = FilePathUtil().getRealPath(this,uri)
                val frag = EffectScreen()
                val bundle = Bundle()
                bundle.putString("path",path)
                frag.arguments = bundle
                setFragment(frag)
            }
        }
    }
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO),1001)
        }
    }
}