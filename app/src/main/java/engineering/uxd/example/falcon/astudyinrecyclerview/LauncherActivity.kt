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
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.sdk25.coroutines.onClick

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        setupToolbar(this, find<Toolbar>(R.id.rv_launcher_toolbar))
        setupRecyclerView(find<RecyclerView>(R.id.rv_launcher_container))
    }

    //
    // RecyclerView setup
    //
    private fun setupRecyclerView(recyclerView: RecyclerView) {

        // Decoration
        recyclerView.addItemDecoration(
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // Layout
        recyclerView.layoutManager = LinearLayoutManager(ctx)

        // Adapter (with ItemClickListener)
        recyclerView.adapter = DataAdapter(this)
    }

    //
    // List Adapter
    //
    private class DataAdapter(val ctx: AppCompatActivity) :
            RecyclerView.Adapter<RowViewHolder>() {

        //
        // Implement RecyclerView.Adapter
        //
        override fun getItemCount(): Int {
            return ExperimentName.values().size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
            with(parent.context.layoutInflater.inflate(
                    R.layout.item_launcher_row,
                    parent,
                    false)
            ) { return RowViewHolder(ctx = ctx, itemView = this) }
        }

        override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
            holder.bindToDataItem(ExperimentName.values()[position])
        }

        // Click handler (more info: https://goo.gl/on7MDd)
        interface ItemClickListener<T> {
            fun onClick(item: T)
        }

    }

    //
    // Row Renderer ViewHolder
    //
    private class RowViewHolder(val ctx: AppCompatActivity, itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        val rowText: TextView

        init {
            rowText = itemView.find<TextView>(R.id.text_launcher_row)
        }

        val clickListener = object : DataAdapter.ItemClickListener<ExperimentName> {
            override fun onClick(item: ExperimentName) {
                when (item.name) {
                    ExperimentName.VerticalList.name -> {
                        ctx.startActivity(ctx.intentFor<VerticalListActivity>().singleTop())
                    }
                    ExperimentName.HorizontalList.name -> {
                        ctx.startActivity(ctx.intentFor<HorizontalListActivity>().singleTop())
                    }
                    ExperimentName.Grid.name -> {
                        ctx.startActivity(ctx.intentFor<VerticalGridActivity>().singleTop())
                    }
                    ExperimentName.GridHorizontal.name -> {
                        ctx.startActivity(ctx.intentFor<HorizontalGridActivity>().singleTop())
                    }
                    ExperimentName.AutoOrientationChangeList.name -> {
                        ctx.startActivity(ctx.intentFor<AutoOrientationChangeActivity>().singleTop())
                    }
                    ExperimentName.StaggeredGrid.name -> {
                        ctx.startActivity(ctx.intentFor<StaggeredGridActivity>().singleTop())
                    }
                    ExperimentName.SimpleTouchableVerticalListActivity.name -> {
                        ctx.startActivity(ctx.intentFor<TouchableVerticalListActivity>().singleTop())
                    }
                    else -> {
                        snackbar(ctx.find<View>(android.R.id.content),
                                "${item.name} was clicked")
                    }

                }
            }
        }

        fun bindToDataItem(data: ExperimentName) {
            rowText.text = data.label
            rowText.onClick { view -> clickListener.onClick(data) }
        }

    }

    //
    // Underlying data for the list - enum of Experiments
    //
    private enum class ExperimentName(val label: String) {
        VerticalList("Vertical List"),
        HorizontalList("Horizontal List"),
        Grid("Uniform Grid - Vertical"),
        GridHorizontal("Uniform Grid - Horizontal"),
        AutoOrientationChangeList("Dynamically change between list and grid"),
        StaggeredGrid("Staggered Grid w/ Cards"),
        SimpleTouchableVerticalListActivity("Simple touchable vertical list")
    }

}