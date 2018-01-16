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

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.*

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
 */
class TouchHelperCallback(val mAdapter: AdapterTouchListener) : ItemTouchHelper.Callback() {
    override fun isLongPressDragEnabled() = true

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

/**
 * Interface to notify an item ViewHolder of relevant callbacks from
 * [TouchHelperCallback].
 */
interface ViewHolderTouchListener {
    /**
     * Called when the [ItemTouchHelper] first registers an item as being moved or swiped.
     * Implementations should update the item view to indicate it's active state.
     */
    fun onItemSelected()

    /**
     * Called when the [ItemTouchHelper] has completed the move or swipe, and the active
     * item state should be cleared.
     */
    fun onItemClear()
}
