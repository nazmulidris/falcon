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

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.singleTop
import recyclerview.nazmul.com.astudyinrecyclerview.LauncherActivitiesAdapter.ExperimentName
import recyclerview.nazmul.com.astudyinrecyclerview.LauncherActivitiesAdapter.RowViewHolder

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        setup(this, find<RecyclerView>(R.id.rv_launcher_container))
    }

}

// RecyclerView setup
fun setup(ctx: AppCompatActivity, recyclerView: RecyclerView) {

    // Layout
    recyclerView.layoutManager = LinearLayoutManager(ctx)

    // ItemClickListener
    val clickListener = object : ItemClickListener<ExperimentName> {
        override fun onClick(item: ExperimentName) {
            when (item.name) {
                ExperimentName.VerticalList.name -> {
                    with(ctx) {
                        startActivity(intentFor<VerticalListActivity>().singleTop())
                    }
                }
                else -> {
                    snackbar(ctx.find<View>(android.R.id.content),
                            "${item.name} was clicked")
                }

            }
        }
    }

    // Adapter (with ItemClickListener)
    recyclerView.adapter = LauncherActivitiesAdapter(clickListener)
}

// List Adapter
class LauncherActivitiesAdapter(val clickListener: ItemClickListener<ExperimentName>) :
        RecyclerView.Adapter<RowViewHolder>() {

    // Enum of experiments (which is the underlying data for the list)
    enum class ExperimentName(val label: String) {
        VerticalList("Vertical List"),
        HorizontalList("Horizontal List"),
        Grid("Uniform Grid"),
        IrregularGrid("Irregular Grid")
    }

    override fun getItemCount(): Int {
        return ExperimentName.values().size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        with(parent.context.layoutInflater.inflate(
                R.layout.item_launcher_row,
                parent,
                false)) {
            return RowViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bindToDataItem(ExperimentName.values()[position], clickListener)
    }

    // Row Renderer ViewHolder
    class RowViewHolder : RecyclerView.ViewHolder {
        val rowText: TextView

        constructor(itemView: View) : super(itemView) {
            rowText = itemView.find<TextView>(R.id.text_row)
        }

        fun bindToDataItem(data: ExperimentName,
                           clickListener: ItemClickListener<ExperimentName>) {
            rowText.text = data.label
            rowText.setOnClickListener { view -> clickListener.onClick(data) }
        }
    }

}

// Click handler (more info: https://goo.gl/on7MDd)
interface ItemClickListener<T> {
    fun onClick(item: T)
}