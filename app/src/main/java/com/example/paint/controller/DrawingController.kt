package com.example.paint.controller

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import com.example.paint.model.Circle
import com.example.paint.model.DrawingModel
import com.example.paint.model.Line
import com.example.paint.model.Rectangle
import kotlin.math.abs

class DrawingController(model: DrawingModel) : AbstractController(model) {    var currentColor = mutableStateOf(Color.Black)
    var currentDrawMode = mutableStateOf("none")
    var currentStrokeWidth = mutableStateOf(1.dp)
    var currentStartPoint = mutableStateOf(Offset.Unspecified)
    var currentEndPoint = mutableStateOf(Offset.Unspecified)

    override fun startShape(startPoint: Offset) {
        currentStartPoint.value = startPoint
    }

    override fun updateShape(endPoint: Offset) {
        currentEndPoint.value = endPoint
        if (currentDrawMode.value == "line") {
            val line = Line(start = currentStartPoint.value, end = endPoint, color = currentColor.value, strokeWidth = currentStrokeWidth.value)
            model.addLine(line)
            currentStartPoint.value = endPoint // Continue the line
        }
    }


  override  fun completeShape() {
        when (currentDrawMode.value) {
            "circle" -> {
                val radius = (currentStartPoint.value - currentEndPoint.value).getDistance()
                val circle = Circle(center = currentStartPoint.value, radius = radius, color = currentColor.value, strokeWidth = currentStrokeWidth.value)
                model.addCircle(circle)
            }
            "rectangle" -> {
                val size = Size(
                    width = abs(currentStartPoint.value.x - currentEndPoint.value.x),
                    height = abs(currentStartPoint.value.y - currentEndPoint.value.y)
                )
                val rectangle = Rectangle(topLeft = currentStartPoint.value, size = size, color = currentColor.value, strokeWidth = currentStrokeWidth.value)
                model.addRectangle(rectangle)
            }
            "straightLine" -> {
                val adjustedEndPoint = if (abs(currentStartPoint.value.x - currentEndPoint.value.x) > abs(currentStartPoint.value.y - currentEndPoint.value.y)) {
                    Offset(currentEndPoint.value.x, currentStartPoint.value.y)
                } else {
                    Offset(currentStartPoint.value.x, currentEndPoint.value.y)
                }
                val straightLine = Line(start = currentStartPoint.value, end = adjustedEndPoint, color = currentColor.value, strokeWidth = currentStrokeWidth.value)
                model.addLine(straightLine)
            }
        }
        currentStartPoint.value = Offset.Unspecified
        currentEndPoint.value = Offset.Unspecified
    }

 override   fun clearCanvas() {
        model.clear()
    }

  override  fun undo() {
        model.undo()
    }

  override  fun redo() {
      model.redo()
  }

}





