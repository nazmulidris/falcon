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
import android.support.animation.*
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*




class TouchableVerticalListActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var mState: State
    val SPAN_COUNT = 3
    var mUseList = false

    private fun setupViewModel() {
        mState = ViewModelProviders.of(this).get(State::class.java)
    }

    private data class State(
            var position: Int = 0,
            var data: MutableList<String> = loremIpsumData.toMutableList()) :
            ViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        mUseList =
                when (getString(R.string.list_orientation)) {
                    "list" -> true
                    "grid" -> false
                    else -> false
                }
        setContentView(R.layout.activity_touch_vertical_list)
        find<RecyclerView>(R.id.rv_touch_vertical_list_container).let {
            setupRecyclerView(it)
        }
    }

    private fun setupFlingAnimation(rv: RecyclerView) {

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!recyclerView.canScrollVertically(1)) {
                        toast("Last")
                        rv.animatePulse()
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        toast("First")
                        rv.animatePulse()
                    }
                }
            }
        })
    }

    fun RecyclerView.animatePulse() {

        val forceConstant = 500f
        val scaleProperty = object : FloatPropertyCompat<View>("scaleProperty") {
            override fun getValue(view: View): Float {
                // Return the value of any one property
                return view.scaleX
            }

            override fun setValue(view: View, value: Float) {
                // Apply the same value to two properties
                with(value / forceConstant + 1f) {
                    view.scaleX = this
                    view.scaleY = this
                }
            }
        }
        val force = (SpringForce()).apply {
            finalPosition = 1f
            dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
            stiffness = SpringForce.STIFFNESS_MEDIUM
        }
        with(SpringAnimation(this, scaleProperty)) {
            spring = force
            setStartVelocity(2f * forceConstant)
            start()
        }

    }

    private fun setupFlingAnimationAlt(rv: RecyclerView) {
        rv.onFlingListener = object : RecyclerView.OnFlingListener() {
            override fun onFling(vX: Int, vY: Int): Boolean {

                info {
                    "onFling: vX = $vX, vY = $vY" +
                            ", maxVal: ${rv.measuredHeight}"
                }

                val fling = FlingAnimation(rv, DynamicAnimation.SCROLL_Y)
                fling.setMinValue(0f)
                fling.minimumVisibleChange = DynamicAnimation.MIN_VISIBLE_CHANGE_PIXELS
                fling.setMaxValue(rv.measuredHeight.toFloat())
                fling.setStartVelocity((-vY).toFloat())
                fling.friction = 1.1f
                fling.start()

                return true // consume event

            }
        }
    }

    private fun setupFlingAnimationAlt2(recyclerView: RecyclerView) {
        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, vX: Float, vY: Float): Boolean {
                info {
                    "${::onFling.name}: velocityX: $vX, velocityY: $vY" +
                            "\n\t event_down: $e1 \n\t event_move: $e2?"
                }

                FlingAnimation(recyclerView, DynamicAnimation.SCROLL_Y).apply {
                    setStartVelocity(-vY)
                    friction = 1.1f
                    start()
                }
                return true // consume event
            }

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, dX: Float, dY: Float): Boolean {
                info {
                    "${::onScroll.name}: distanceX: $dX, distanceY: $dY" +
                            "\n\t event_down: $e1 \n\t event_move: $e2"
                }
                return false // do not consume event
            }

        }
        val gestureDetector = GestureDetector(recyclerView.context, gestureListener)

        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                //info { "onTouch ${event}" }
                return gestureDetector.onTouchEvent(event)
            }
        })

    }

    fun setupRecyclerView(recyclerView: RecyclerView) {
        // Create layout manager
        var layoutManager: RecyclerView.LayoutManager =
                if (mUseList) {
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                } else {
                    GridLayoutManager(this, SPAN_COUNT)
                }
        // Set layout manager
        recyclerView.layoutManager = layoutManager

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
                mState.position =
                        when (layoutManager) {
                            is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                            is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                            else -> 0
                        }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun restoreListPosition() {
                layoutManager.scrollToPosition(mState.position)
            }
        })

        setupFlingAnimation(recyclerView)
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
            // Todo Use a different cell renderer for GridLayoutManager based on mUseList
            parent.context.layoutInflater.inflate(
                    R.layout.item_touch_vertical_list_row,
                    parent,
                    false).let {
                return RowViewHolder(it)
            }
        }

        override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
            holder.bindToDataItem(mState.data[position], clickListener, mTouchHelper, holder)
        }

    }

    // ViewHolder (row renderer) implementation
    private class RowViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        val rowText: TextView
        val rowHandle: ImageView
        val rowLayout: FrameLayout

        init {
            rowText = itemView.find(R.id.text_touch_vertical_list_row)
            rowHandle = itemView.find(R.id.image_touch_vertical_list_row_handle)
            rowLayout = itemView.find(R.id.layout_touch_vertical_list_row_container)
        }

        fun bindToDataItem(data: String,
                           clickListener: ItemClickListener<String>,
                           touchHelper: ItemTouchHelper,
                           holder: RowViewHolder) {
            // Text
            rowText.text = data
            rowLayout.onClick { clickListener.onClick(item = data) }

            // Image handle
            rowHandle.setOnTouchListener { view, motionEvent ->
                if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                    touchHelper.startDrag(holder)
                }
                false
            }
        }
    }

    // Click handler (more info: https://goo.gl/on7MDd)
    interface ItemClickListener<T> {
        fun onClick(item: T)
    }

}