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

import android.animation.AnimatorInflater
import android.graphics.Canvas
import android.support.animation.FloatPropertyCompat
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.*
import android.view.View
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info




/**
 * Callback for [ItemTouchHelper] which is attached to your [RecyclerView].
 *
 * When the user performs drag & drop, swipe-to-dismiss touch operations
 * on your [RecyclerView], the [ItemTouchHelper] uses this callback to handle
 * how those moves & deletions should be handled by your [RecyclerView.Adapter].
 *
 * Notes:
 * - Your [RecyclerView.Adapter] needs to create the [ItemTouchHelper] and
 * attach this callback to it.
 * - You also need to attach a [RecyclerView] to
 * the [ItemTouchHelper.attachToRecyclerView] function.
 *
 * More info:
 * - [Medium articles on drag & drop, swipe to dismiss](https://goo.gl/trCJy5)
 */
class TouchHelperCallback(val mAdapter: AdapterTouchListener) :
        ItemTouchHelper.Callback(), AnkoLogger {
    override fun isLongPressDragEnabled() = false

    override fun isItemViewSwipeEnabled() = true

    override fun getMovementFlags(recyclerView: RecyclerView,
                                  viewHolder: RecyclerView.ViewHolder): Int {
        when (recyclerView.layoutManager) {
            is GridLayoutManager -> return makeMovementFlags(
                    UP or DOWN or LEFT or RIGHT,
                    0)
            is LinearLayoutManager -> return makeMovementFlags(
                    UP or DOWN,
                    START or END)
            else -> return makeMovementFlags(
                    0,
                    0)
        }
    }

    override fun onMove(recyclerView: RecyclerView,
                        source: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        if (source.itemViewType != target.itemViewType) return false
        mAdapter.onItemMove(source.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                          direction: Int) {
        mAdapter.onItemDismiss(viewHolder.adapterPosition)
    }

    // All the following function overrides are optional:
    // [onChildDraw], [onSelectedChanged], and [clearView].
    // They aren't required for the drag and drop, and swipe to
    // dismiss to work. They are purely for adding more control
    // over the UI effects of swipe, select, deselect, and dismiss.

    override fun onChildDraw(c: Canvas,
                             recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float,
                             dY: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean) {
        when (actionState) {
            ACTION_STATE_SWIPE -> {
                // Fade out the view as it is swiped out of the parent's bounds
                val alpha = 1.0f - Math.abs(dX) / viewHolder.itemView.width.toFloat()
                viewHolder.itemView.alpha = alpha
                viewHolder.itemView.translationX = dX
            }
            else -> super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?,
                                   actionState: Int) {
        info { "onSelectedChanged -> ${actionStateString(actionState)}" }
        when (actionState) {
            ACTION_STATE_DRAG -> viewHolder?.itemView?.animateScaleUp()
            ACTION_STATE_SWIPE -> viewHolder?.itemView?.animatePulse()
            ACTION_STATE_IDLE -> viewHolder?.itemView?.apply {}
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView,
                           viewHolder: RecyclerView.ViewHolder?) {
        super.clearView(recyclerView, viewHolder)

        // for onChildDraw()
        viewHolder?.itemView?.alpha = 1.0f

        // for onSelectedChanged()
        viewHolder?.itemView?.animateScaleNormal()
    }

    fun View.animatePulse() {

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
            dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
            stiffness = SpringForce.STIFFNESS_MEDIUM
        }
        with(SpringAnimation(this, scaleProperty)) {
            spring = force
            setStartVelocity(5f * forceConstant)
            start()
        }

    }

    fun View.animatePulseOld() {
        AnimatorInflater.loadAnimator(context, R.animator.pulse).let {
            it.setTarget(this)
            it.start()
        }
    }

    fun View.animateScaleUp() {
        AnimatorInflater.loadAnimator(context, R.animator.scale_up).let {
            it.setTarget(this)
            it.start()
        }
    }

    fun View.animateScaleNormal() {
        AnimatorInflater.loadAnimator(context, R.animator.scale_normal).let {
            it.setTarget(this)
            it.start()
        }
    }

    fun actionStateString(id: Int): String {
        return when (id) {
            ACTION_STATE_DRAG -> "DRAG"
            ACTION_STATE_IDLE -> "IDLE"
            ACTION_STATE_SWIPE -> "SWIPE"
            else -> "UNKNOWN"
        }
    }

}

/**
 * Interface for your [RecyclerView.Adapter] handle move or dismissal event
 * from the [TouchHelperCallback].
 */
interface AdapterTouchListener {
    /**
     * Called when an item has been dragged far enough to trigger a move.
     * This is called every time an item is shifted, and **not** at the end
     * of a "drop".
     *
     * Implementations should call [RecyclerView.Adapter.notifyItemMoved] after
     * adjusting the underlying data to reflect this move.
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then resolved position of the moved item.
     * @return True if the item was moved to the new adapter position.
     * @see RecyclerView.getAdapterPositionFor
     * @see RecyclerView.ViewHolder.getAdapterPosition
     */
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    /**
     * Called when an item has been dismissed by a swipe.
     *
     * Implementations should call [RecyclerView.Adapter.notifyItemRemoved] after
     * adjusting the underlying data to reflect this removal.
     *
     * @param position The position of the item dismissed.
     * @see RecyclerView.getAdapterPositionFor
     * @see RecyclerView.ViewHolder.getAdapterPosition
     */
    fun onItemDismiss(position: Int)
}