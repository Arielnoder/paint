package com.example.paint.controller

import androidx.compose.ui.geometry.Offset
import com.example.paint.model.DrawingModel

abstract class AbstractController(val model: DrawingModel) {
    abstract fun startShape(startPoint: Offset)
    abstract fun updateShape(endPoint: Offset)
    abstract fun completeShape()
    abstract fun clearCanvas()
    abstract fun undo()
    abstract fun redo()
}