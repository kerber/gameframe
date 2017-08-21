package com.savvasdalkitsis.gameframe.feature.workspace.element.palette.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.savvasdalkitsis.gameframe.feature.history.model.Historical
import com.savvasdalkitsis.gameframe.feature.workspace.element.palette.model.Palette
import com.savvasdalkitsis.gameframe.feature.workspace.model.WorkspaceModel

class PalettesView : RecyclerView {

    private val palettes = PalettesAdapter()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onFinishInflate() {
        super.onFinishInflate()
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        setHasFixedSize(true)
        adapter = palettes
    }

    fun addNewPalette(palette: Palette) {
        palettes.addNewPalette(palette)
    }

    fun bind(modelHistory: Historical<WorkspaceModel>) {
        palettes.bind(modelHistory)
    }

}