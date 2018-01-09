/*
 * Copyright 2018 Nazmul Idris. All rights reserved.
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

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater

class VerticalListActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vertical_list)
        find<RecyclerView>(R.id.rv_vertical_list_container).let {
            setup(it)
        }
    }

    private fun setup(recyclerView: RecyclerView) {
        // Set layout manager
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Set adapter
        recyclerView.adapter = DataAdapter()
    }

    private class DataAdapter : RecyclerView.Adapter<DataAdapter.RowViewHolder>() {

        // Data
        val data = listOf("One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight")

        // RecyclerView.Adapter implementation
        override fun getItemCount(): Int {
            return data.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
            parent.context.layoutInflater.inflate(
                    R.layout.item_vertical_list_row,
                    parent,
                    false).let {
                return RowViewHolder(it)
            }
        }

        override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
            holder.bindToDataItem(data[position])
        }

        // ViewHolder (row renderer) implementation
        class RowViewHolder : RecyclerView.ViewHolder {
            val rowText: TextView

            constructor(itemView: View) : super(itemView) {
                rowText = itemView.find(R.id.text_vertical_list_row)
            }

            fun bindToDataItem(data: String) {
                rowText.text = data
            }

        }

    }

}
