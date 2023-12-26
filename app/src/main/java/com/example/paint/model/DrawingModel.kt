package com.example.paint.model

// DrawingModel.kt

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

class DrawingModel {
    val lines = mutableStateListOf<Line>()
    val circles = mutableStateListOf<Circle>()
    val rectangles = mutableStateListOf<Rectangle>()

    // List to keep track of undone actions
    private val undoneActions = mutableListOf<UndoableAction>()

    fun addLine(line: Line) {
        lines.add(line)
        undoneActions.clear()
    }

    // Method to add a circle
    fun addCircle(circle: Circle) {
        circles.add(circle)
        undoneActions.clear()
    }

    // Method to add a rectangle
    fun addRectangle(rectangle: Rectangle) {
        rectangles.add(rectangle)
        undoneActions.clear()
    }

    // Method to clear all drawings
    fun clear() {
        lines.clear()
        circles.clear()
        rectangles.clear()
        undoneActions.clear()
    }

    // Method to remove the last drawing and add it to undone actions
    fun undo() {
        when {
            lines.isNotEmpty() -> {
                // remmove the 5 last
                for (i in 1..5) {
                    val lastLine = lines.removeLast()
                    undoneActions.add(UndoableAction.LineAction(lastLine))
                }

            }
            circles.isNotEmpty() -> {
                for (i in 1..5) {
                    val lastLine = lines.removeLast()
                    undoneActions.add(UndoableAction.LineAction(lastLine))
                }
            }
            rectangles.isNotEmpty() -> {  for (i in 1..5) {
                val lastLine = lines.removeLast()
                undoneActions.add(UndoableAction.LineAction(lastLine))
            }
            }
        }
    }

    // Method to redo the last undone drawing
    fun redo() {
        if (undoneActions.isNotEmpty()) {
            val lastAction = undoneActions.removeLast()
            when (lastAction) {
                is UndoableAction.LineAction -> lines.add(lastAction.line)
                is UndoableAction.CircleAction -> circles.add(lastAction.circle)
                is UndoableAction.RectangleAction -> rectangles.add(lastAction.rectangle)
            }
        }
    }


}


sealed class UndoableAction {
    data class LineAction(val line: Line) : UndoableAction()
    data class CircleAction(val circle: Circle) : UndoableAction()
    data class RectangleAction(val rectangle: Rectangle) : UndoableAction()
}

data class Line(val start: Offset, val end: Offset, val color: Color, val strokeWidth: Dp)
data class Circle(val center: Offset, val radius: Float, val color: Color, val strokeWidth: Dp)
data class Rectangle(val topLeft: Offset, val size: Size, val color: Color, val strokeWidth: Dp)
