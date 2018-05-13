/**
 * Copyright 2017 Savvas Dalkitsis
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 'Game Frame' is a registered trademark of LEDSEQ
 */
package com.savvasdalkitsis.gameframe.feature.workspace.element.tools.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.savvasdalkitsis.gameframe.feature.workspace.R
import com.savvasdalkitsis.gameframe.feature.workspace.element.tools.model.Tools
import com.savvasdalkitsis.gameframe.kotlin.ViewAction

class ToolsViewHolder internal constructor(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_tool_entry, parent, false)) {

    private val title: TextView = itemView.findViewById(R.id.view_tool_entry_title)
    private val toolView: ToolView = itemView.findViewById(R.id.view_tool_entry_icon)

    fun bind(tool: Tools) {
        title.text = tool.label
        toolView.bind(tool)
    }

    internal fun clearListeners() {
        setOnClickListener(null)
    }

    internal fun setOnClickListener(onClickListener: ViewAction?) {
        itemView.setOnClickListener(onClickListener)
    }
}
