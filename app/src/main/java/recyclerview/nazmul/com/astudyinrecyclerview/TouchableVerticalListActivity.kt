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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

class TouchableVerticalListActivity : AppCompatActivity() {

    private lateinit var mState: State

    private fun setupViewModel() {
        mState = ViewModelProviders.of(this).get(State::class.java)
    }

    private data class State(
            var position: Int = 0,
            var data: MutableList<String> = loremIpsumData.toMutableList()) :
            ViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touch_vertical_list)
        setupViewModel()
        find<RecyclerView>(R.id.rv_touch_vertical_list_container).let {
            setupRecyclerView(it)
        }
    }

    fun setupRecyclerView(recyclerView: RecyclerView) {
        // Create layout manager
        var layoutManager: LinearLayoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        .apply {
                            // Set layout manager
                            recyclerView.layoutManager = this
                        }

        // Create adapter
        DataAdapter(
                object : ItemClickListener<String> {
                    override fun onClick(item: String) {
                        snackbar(find<View>(android.R.id.content), item)
                    }
                })
                .apply {
                    // Set adapter
                    recyclerView.adapter = this
                    // Setup TouchHelper
                    this.mTouchHelper.attachToRecyclerView(recyclerView)
                }

        // Set decoration
        recyclerView.addItemDecoration(
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // Scroll position
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun saveListPosition() {
                mState.position = layoutManager.findFirstVisibleItemPosition()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun restoreListPosition() {
                layoutManager.scrollToPosition(mState.position)
            }
        })
    }

    private inner class DataAdapter(val clickListener: ItemClickListener<String>) :
            RecyclerView.Adapter<RowViewHolder>(),
            AdapterTouchListener {

        val mTouchHelper: ItemTouchHelper = ItemTouchHelper(TouchHelperCallback(this))

        // Handle touch - drag and drop
        override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
            Collections.swap(mState.data, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
            return true
        }

        // Handle touch - swipe to dismiss
        override fun onItemDismiss(position: Int) {
            mState.data.removeAt(position)
            notifyItemRemoved(position)
        }

        // RecyclerView.Adapter implementation
        override fun getItemCount(): Int {
            return mState.data.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
            parent.context.layoutInflater.inflate(
                    R.layout.item_touch_vertical_list_row,
                    parent,
                    false).let {
                return RowViewHolder(it)
            }
        }

        override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
            holder.bindToDataItem(mState.data[position], clickListener)
        }

    }

    // ViewHolder (row renderer) implementation
    private class RowViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        val rowText: TextView

        init {
            rowText = itemView.find(R.id.text_touch_vertical_list_row)
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