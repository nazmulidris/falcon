/*
 * Copyright 2018 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package recyclerview.nazmul.com.astudyinrecyclerview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup(this, find<RecyclerView>(R.id.recyclerview_main))
    }

}

// RecyclerView setup
fun setup(ctx: AppCompatActivity, recyclerView: RecyclerView) {
    // Layout
    recyclerView.layoutManager = LinearLayoutManager(ctx)
    // Adapter
    recyclerView.adapter = ListDataAdapter(object : ItemClickListener<String> {
        override fun onClick(item: String) {
            snackbar(
                    ctx.find<View>(android.R.id.content),
                    "$item was clicked")
        }
    })
}

// List Adapter
class ListDataAdapter(val clickListener: ItemClickListener<String>) :
        RecyclerView.Adapter<RowViewHolder>() {
    val data = listOf("1", "2", "3", "4", "5", "6")

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        with(parent.context.layoutInflater.inflate(
                R.layout.item_row,
                parent,
                false)) {
            return RowViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bind(data[position], clickListener)
    }
}

// Row Renderer ViewHolder
class RowViewHolder : RecyclerView.ViewHolder {
    val rowText: TextView

    constructor(itemView: View) : super(itemView) {
        rowText = itemView.find<TextView>(R.id.text_row)
    }

    fun bind(data: String, clickListener: ItemClickListener<String>) {
        rowText.text = data
        rowText.setOnClickListener { view -> clickListener.onClick(data) }
    }
}

// Click handler (more info: https://goo.gl/on7MDd)
interface ItemClickListener<T> {
    fun onClick(item: T)
}