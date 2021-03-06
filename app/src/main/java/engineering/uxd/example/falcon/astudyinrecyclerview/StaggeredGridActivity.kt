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

package engineering.uxd.example.falcon.astudyinrecyclerview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.sdk25.coroutines.onClick

class StaggeredGridActivity : AppCompatActivity() {

    val mData = loremIpsumData.toList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staggered_grid)
        find<RecyclerView>(R.id.rv_staggered_grid_container).let {
            setup(it)
        }
    }

    private fun setup(recyclerView: RecyclerView) {
        val SPAN_COUNT = resources.getInteger(R.integer.staggered_list_span_count)
        // Set layout manager
        StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL)
                .apply {
                    gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
                }
                .apply {
                    recyclerView.layoutManager = this
                }
        recyclerView.adapter = DataAdapter(object : ItemClickListener<String> {
            override fun onClick(item: String) {
                snackbar(find<View>(android.R.id.content), item)
            }
        })
    }

    private inner class DataAdapter(val clickListener: ItemClickListener<String>) :
            RecyclerView.Adapter<RowViewHolder>() {

        // RecyclerView.Adapter implementation
        override fun getItemCount(): Int {
            return mData.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
            parent.context.layoutInflater.inflate(
                    R.layout.item_staggered_grid_cell,
                    parent,
                    false).let {
                return RowViewHolder(it)
            }
        }

        override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
            holder.bindToDataItem(mData[position], clickListener)
        }

    }

    // ViewHolder (row renderer) implementation
    private class RowViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        val cellText: TextView

        init {
            cellText = itemView.find(R.id.text_staggered_grid_cell)
        }

        fun bindToDataItem(data: String, clickListener: ItemClickListener<String>) {
            cellText.text = data
            cellText.onClick { clickListener.onClick(item = data) }
        }
    }

    // Click handler (more info: https://goo.gl/on7MDd)
    interface ItemClickListener<T> {
        fun onClick(item: T)
    }

}