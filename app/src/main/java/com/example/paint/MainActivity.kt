package com.example.paint

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paint.controller.DrawingController
import com.example.paint.model.DrawingModel
import com.example.paint.ui.theme.PaintTheme
import java.lang.Math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaintTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CanvasScreen( DrawingController(model = DrawingModel()))
                    
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(controller: DrawingController) {
    var startDragPoint by remember { mutableStateOf<Offset?>(null) }
    var currentDragPoint by remember { mutableStateOf<Offset?>(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },

                actions = {
                    Row {
                        // Color selection buttons
                        Button(onClick = { controller.currentColor.value = Color.Red }, modifier = Modifier.padding(4.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {}
                        Button(onClick = { controller.currentColor.value = Color.Green }, modifier = Modifier.padding(4.dp), colors = ButtonDefaults.buttonColors(containerColor  = Color.Green)) {}
                        Button(onClick = { controller.currentColor.value = Color.Blue }, modifier = Modifier.padding(4.dp), colors = ButtonDefaults.buttonColors(containerColor  = Color.Blue)) {}
                        Button(onClick = { controller.currentColor.value = Color.Yellow }, modifier = Modifier.padding(4.dp), colors = ButtonDefaults.buttonColors(containerColor  = Color.Yellow)) {}
                    }
                    IconButton(onClick = { controller.clearCanvas() }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
                    Column {


                        Button(
                            onClick = { controller.undo() },
                            modifier = Modifier
                                .width(90.dp)
                                .height(50.dp)
                        ) {
                            Text(text = "Undo", fontSize = 10.sp)
                        }

                        Button(
                            onClick = { controller.redo() },
                            modifier = Modifier
                                .width(90.dp)
                                .height(50.dp)
                        ) {
                            Text(text = "Redo", fontSize = 10.sp)
                        }
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(controller) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    controller.startShape(offset)
                                },
                                onDragEnd = {
                                    controller.completeShape()
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    controller.updateShape(change.position)
                                }
                            )
                        }
                ) {
                    // Draw existing shapes from the model
                    controller.model.lines.forEach { line ->
                        drawLine(
                            color = line.color,
                            start = line.start,
                            end = line.end,
                            strokeWidth = line.strokeWidth.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    controller.model.circles.forEach { circle ->
                        drawCircle(
                            color = circle.color,
                            center = circle.center,
                            radius = circle.radius,
                            style = Stroke(width = circle.strokeWidth.toPx())
                        )
                    }
                    controller.model.rectangles.forEach { rectangle ->
                        drawRect(
                            color = rectangle.color,
                            topLeft = rectangle.topLeft,
                            size = rectangle.size,
                            style = Stroke(width = rectangle.strokeWidth.toPx())
                        )
                    }

                    // Draw preview of the current shape
                    when (controller.currentDrawMode.value) {
                        "circle" -> if (controller.currentStartPoint.value != Offset.Unspecified) {
                            val radius = (controller.currentStartPoint.value - controller.currentEndPoint.value).getDistance()
                            drawCircle(
                                color = controller.currentColor.value,
                                center = controller.currentStartPoint.value,
                                radius = radius,
                                style = Stroke(width = controller.currentStrokeWidth.value.toPx())
                            )
                        }
                        "rectangle" -> if (controller.currentStartPoint.value != Offset.Unspecified) {
                            val size = Size(
                                width = abs(controller.currentStartPoint.value.x - controller.currentEndPoint.value.x),
                                height = abs(controller.currentStartPoint.value.y - controller.currentEndPoint.value.y)
                            )
                            drawRect(
                                color = controller.currentColor.value,
                                topLeft = controller.currentStartPoint.value,
                                size = size,
                                style = Stroke(width = controller.currentStrokeWidth.value.toPx())
                            )
                        }
                        "straightLine" -> if (controller.currentStartPoint.value != Offset.Unspecified) {
                            val adjustedEndPoint = if (abs(controller.currentStartPoint.value.x - controller.currentEndPoint.value.x) > abs(controller.currentStartPoint.value.y - controller.currentEndPoint.value.y)) {
                                Offset(controller.currentEndPoint.value.x, controller.currentStartPoint.value.y)
                            } else {
                                Offset(controller.currentStartPoint.value.x, controller.currentEndPoint.value.y)
                            }
                            drawLine(
                                color = controller.currentColor.value,
                                start = controller.currentStartPoint.value,
                                end = adjustedEndPoint,
                                strokeWidth = controller.currentStrokeWidth.value.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                content = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { controller.currentDrawMode.value = "line" },
                            modifier = Modifier
                                .width(100.dp)
                                .height(50.dp)
                        ) {
                            Text(text = "freehand", fontSize = 11.sp)
                        }
                        Button(
                            onClick = { controller.currentDrawMode.value = "circle" },
                            modifier = Modifier
                                .width(100.dp)
                                .height(50.dp)
                        ) {
                            Text(text = "Circle")
                        }
                        Button(
                            onClick = { controller.currentDrawMode.value = "rectangle" },
                            modifier = Modifier
                                .width(100.dp)
                                .height(50.dp)
                        ) {
                            Text(text = "Rectangle", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { controller.currentDrawMode.value = "straightLine" },
                            modifier = Modifier
                                .width(90.dp)
                                .height(50.dp)
                        ) {
                            Text(text = "Line", fontSize = 10.sp)
                        }



                    }
                }
            )
        }
    )
}

















