package com.example.stepcounter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.unaryPlus
import androidx.ui.core.*
import androidx.ui.engine.geometry.Rect
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import androidx.ui.text.TextStyle
import androidx.ui.tooling.preview.Preview

class MainActivity : AppCompatActivity() {

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null

    private val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        override fun onSensorChanged(event: SensorEvent?) {
            event?.values?.get(0)?.let { Steps.current = it.toInt() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        setContent {
            MaterialTheme {
                Content(Steps.current.toFloat(), Steps.target.toFloat())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            sensorEventListener,
            stepSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(sensorEventListener);
    }
}

@Model
object Steps {
    var current = 0
    var target = 10000
}

@Composable
fun Content(currentSteps: Float, targetSteps: Float) {
    val image = +vectorResource(R.drawable.ic_running)
    val progress = ((currentSteps % targetSteps) / targetSteps) * 100.0f
    Column(
        modifier = ExpandedWidth,
        arrangement = Arrangement.Center
    ) {
        Container(expanded = true, height = 144.dp) {
            CircularProgress(size = 144.dp, progress = progress)
            DrawVector(vectorImage = image)
        }
        HeightSpacer(24.dp)
        Text(
            modifier = Gravity.Center,
            text = "Liczba kroków",
            style = TextStyle(color = Color(0xFFACACAC))
        )
        HeightSpacer(8.dp)
        Text(
            modifier = Gravity.Center,
            text = "${currentSteps.toInt()} / ${targetSteps.toInt()} kroków",
            style = TextStyle(color = Color(0xFF00105E))
        )
    }
}

@Composable
fun CircularProgress(size: Dp = 100.dp, progress: Float = 50.0f, padding: Float = 16.0f) {
    Container(height = size, width = size, expanded = true) {
        val paint = Paint()
        Draw { canvas, parentSize ->
            val rect = Rect(0.0f, 0.0f, parentSize.width.value, parentSize.height.value)

            paint.color = Color(0xFFACACAC)
            canvas.drawArc(rect, 360.0f, 360.0f, true, paint)

            paint.color = Color(0xFFF7902B)
            canvas.drawArc(rect, 270.0f, 360.0f * (progress / 100), true, paint)

            paint.color = Color(0xFFFFFFFF)
            canvas.drawArc(rect.inflate(-padding), 360.0f, 360.0f, true, paint)
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MaterialTheme {
        Content(12500.0f, 10000.0f)
    }
}