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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter constructor(private val mList: List<ItemsViewModel>,private val clickListener: (position:Int)->Unit) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    data class ItemsViewModel(val image: Int, val text: String) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cardview, parent, false)

        return ViewHolder(view,clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        holder.imageView.setImageResource(ItemsViewModel.image)
        holder.textView.text = ItemsViewModel.text

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View,private val clickListener: (position:Int) -> Unit) : RecyclerView.ViewHolder(ItemView),View.OnClickListener {
        val imageView: ImageView = itemView.findViewById(R.id.ic_effect)
        val textView: TextView = itemView.findViewById(R.id.txt_filename)
        init {
            ItemView.setOnClickListener(this)
        }
        override fun onClick(v: View) {
            val position = adapterPosition
            clickListener(position)
        }
    }
}