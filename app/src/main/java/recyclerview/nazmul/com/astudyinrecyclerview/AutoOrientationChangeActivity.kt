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

import android.arch.lifecycle.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.find
import org.jetbrains.anko.info
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.sdk25.coroutines.onClick

class AutoOrientationChangeActivity : AppCompatActivity(), AnkoLogger {

    val SPAN_COUNT = 3
    var mUseList = false
    lateinit var mState: ScrollState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (getString(R.string.list_orientation)) {
            "list" -> mUseList = true
            "grid" -> mUseList = false
        }
        if (mUseList) {
            setContentView(R.layout.activity_vertical_list)
            find<RecyclerView>(R.id.rv_vertical_list_container).let {
                setup(it)
            }
        } else {
            setContentView(R.layout.activity_vertical_grid)
            find<RecyclerView>(R.id.rv_vertical_grid_container).let {
                setup(it)
            }
        }
        setupViewModel()
    }

    private fun setupViewModel() {
        mState = ViewModelProviders.of(this).get(ScrollState::class.java)
        info { "loading/creating ViewModel: $mState" }
    }

    private fun setup(recyclerView: RecyclerView) {
        var layoutManager: RecyclerView.LayoutManager

        // Set layout manager
        if (mUseList) {
            layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false)
            recyclerView.layoutManager = layoutManager
            // Set decoration
            recyclerView.addItemDecoration(
                    DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        } else {
            layoutManager = GridLayoutManager(
                    this,
                    SPAN_COUNT,
                    GridLayoutManager.VERTICAL,
                    false)
                    .apply {
                        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                if (position != 0 && position % 5 == 0) return SPAN_COUNT else return 1
                            }
                        }
                    }
            recyclerView.layoutManager = layoutManager
        }
        // Set adapter
        recyclerView.adapter = DataAdapter(
                object : ItemClickListener<String> {
                    override fun onClick(item: String) {
                        snackbar(find<View>(android.R.id.content), item)
                    }
                })
        // Scroll position - more info: https://goo.gl/i2PTb4
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun savePosition() {
                var index = 0
                when (layoutManager) {
                    is LinearLayoutManager -> {
                        index = layoutManager.findFirstVisibleItemPosition()
                    }
                    is GridLayoutManager -> {
                        index = layoutManager.findFirstVisibleItemPosition()
                    }
                }
                mState.position = index
                info { "saving the position to State ${mState}" }
            }
        })
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun restorePosition() {
                info { "restoring the position to State ${mState}" }
                when (layoutManager) {
                    is LinearLayoutManager -> {
                        layoutManager.scrollToPosition(mState.position)
                    }
                    is GridLayoutManager -> {
                        layoutManager.scrollToPosition(mState.position)
                    }
                }
            }
        })
    }

    data class ScrollState(var position: Int = 0, var offset: Int = 0) : ViewModel()

    private inner class DataAdapter(val clickListener: ItemClickListener<String>) :
            RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        // RecyclerView.Adapter implementation
        override fun getItemCount(): Int {
            return dynamicData.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (mUseList) {
                parent.context.layoutInflater.inflate(
                        R.layout.item_vertical_list_row,
                        parent,
                        false).let {
                    return RowViewHolder(it)
                }
            } else {
                parent.context.layoutInflater.inflate(
                        R.layout.item_vertical_grid_cell,
                        parent,
                        false).let {
                    return GridCellViewHolder(it)
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is GridCellViewHolder -> holder.bindToDataItem(dynamicData[position], clickListener)
                is RowViewHolder -> holder.bindToDataItem(dynamicData[position], clickListener)
            }
        }

    }

    // ViewHolder for Grid implementation
    private class GridCellViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        val cellText: TextView

        init {
            cellText = itemView.find(R.id.text_vertical_grid_cell)
        }

        fun bindToDataItem(data: String, clickListener: ItemClickListener<String>) {
            cellText.text = data
            cellText.onClick { clickListener.onClick(item = data) }
        }
    }

    // ViewHolder for List implementation
    private class RowViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        val rowText: TextView

        init {
            rowText = itemView.find(R.id.text_vertical_list_row)
        }

        fun bindToDataItem(data: String, clickListener: ItemClickListener<String>) {
            rowText.text = data
            rowText.onClick { clickListener.onClick(item = data) }
        }
    }


    // Click handler (more info: https://goo.gl/on7MDd)
    interface ItemClickListener<T> {
        fun onClick(item: T)
    }

}