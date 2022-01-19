package com.byagowi.persiancalendar.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.PathParser
import androidx.core.graphics.set
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.byagowi.persiancalendar.R
import com.byagowi.persiancalendar.Variants.debugAssertNotNull
import com.byagowi.persiancalendar.utils.calculatePrayTimes
import com.byagowi.persiancalendar.utils.logException
import io.github.persiancalendar.praytimes.Coordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.util.*
import java.util.zip.GZIPInputStream

class MapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val imageView = ZoomableImageView(inflater.context)

        lifecycleScope.launch {
            runCatching {
                val bitmap = withContext(Dispatchers.IO) { createMap() }
                imageView.setImageDrawable(BitmapDrawable(resources, bitmap))
                val dayNightMap = withContext(Dispatchers.IO) { createDayNightMap(bitmap) }
                imageView.setImageDrawable(BitmapDrawable(resources, dayNightMap))
            }.onFailure(logException).getOrNull().debugAssertNotNull // handle production OOM and so
        }

        return imageView
    }

    @WorkerThread
    private fun createMap(): Bitmap {
        val zippedMapPath = resources.openRawResource(R.raw.worldmap).use { it.readBytes() }
        val mapPathString = String(GZIPInputStream(ByteArrayInputStream(zippedMapPath)).readBytes())
        val mapPath = PathParser.createPathFromPathData(mapPathString)
        val bitmap = Bitmap.createBitmap(4378, 2435, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).also {
            it.drawPath(mapPath, Paint().apply { color = 0xffbcbcbc.toInt() })
        }
        return bitmap.copy(Bitmap.Config.ARGB_8888, false)
    }

    @WorkerThread
    private fun createDayNightMap(originalMap: Bitmap): Bitmap {
        val nightMask = Bitmap.createBitmap(360, 180, Bitmap.Config.ARGB_8888)
        val calendar = GregorianCalendar()
        (-90 until 90).forEach { lat ->
            (-180 until 180).forEach { long ->
                val coordination = Coordinates(lat.toDouble(), long.toDouble(), .0)
                val times = coordination.calculatePrayTimes(calendar)
                if (calendar.get(Calendar.HOUR).toDouble() !in times.fajr..times.maghrib)
                    nightMask[long + 180, lat + 90] = Color.BLACK
            }
        }
        val result = originalMap.copy(Bitmap.Config.ARGB_8888, true)
        Canvas(result).also {
            val maskRect = Rect(0, 0, nightMask.width, nightMask.height)
            val originalRect = Rect(0, 0, result.width, result.height)
            it.drawBitmap(nightMask, maskRect, originalRect, Paint().apply { alpha = 0xB0 })
        }
        return result
    }
}

// Stub
class ZoomableImageView(context: Context, attrs: AttributeSet? = null) :
    AppCompatImageView(context, attrs) {

    private var scaleFactor = 1f
    private val scaleGestureDetector = ScaleGestureDetector(
        context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                scaleFactor = (scaleFactor + (detector?.scaleFactor ?: 1f)).coerceIn(.9f, 1.1f)
                postInvalidate()
                return true
            }
        }
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        return true
    }
}