package com.byagowi.persiancalendar.ui.shared

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.byagowi.persiancalendar.R
import com.byagowi.persiancalendar.databinding.CalendarsViewBinding
import com.byagowi.persiancalendar.entities.CalendarType
import com.byagowi.persiancalendar.entities.Clock
import com.byagowi.persiancalendar.entities.Jdn
import com.byagowi.persiancalendar.global.coordinates
import com.byagowi.persiancalendar.global.isForcedIranTimeEnabled
import com.byagowi.persiancalendar.global.mainCalendar
import com.byagowi.persiancalendar.global.spacedColon
import com.byagowi.persiancalendar.ui.utils.layoutInflater
import com.byagowi.persiancalendar.ui.utils.setupExpandableAccessibilityDescription
import com.byagowi.persiancalendar.utils.EventsStore
import com.byagowi.persiancalendar.utils.calculateDaysDifference
import com.byagowi.persiancalendar.utils.formatDate
import com.byagowi.persiancalendar.utils.formatNumber
import com.byagowi.persiancalendar.utils.getA11yDaySummary
import com.byagowi.persiancalendar.utils.getSpringEquinox
import com.byagowi.persiancalendar.utils.getZodiacInfo
import com.byagowi.persiancalendar.utils.toCivilDate
import com.cepmuvakkit.times.posAlgo.SunMoonPosition
import io.github.persiancalendar.calendar.PersianDate
import java.util.*
import kotlin.math.abs
import kotlin.math.min

class CalendarsView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private val binding = CalendarsViewBinding.inflate(context.layoutInflater, this, true).also {
        it.root.setOnClickListener { toggle() }
        it.root.setupExpandableAccessibilityDescription()
        it.extraInformationContainer.isVisible = false
    }
    private var isExpanded = false

    fun toggle() {
        isExpanded = !isExpanded

        binding.expansionArrow
            .animateTo(if (isExpanded) ArrowView.Direction.UP else ArrowView.Direction.DOWN)
        TransitionManager.beginDelayedTransition(binding.root, ChangeBounds())

        binding.extraInformationContainer.isVisible = isExpanded
        binding.moonPhase.setImageDrawable(object : Drawable() {
            override fun setAlpha(a: Int) = Unit
            override fun setColorFilter(colorFilter: ColorFilter?) = Unit
            override fun getOpacity() = PixelFormat.OPAQUE
            override fun draw(canvas: Canvas) {
                canvas.drawMoon(storedJdn ?: return)
            }
        })
    }

    fun hideMoreIcon() {
        binding.expansionArrow.isVisible = false
    }

    private val moonPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = Color.WHITE
        it.style = Paint.Style.FILL_AND_STROKE
    }

    // moon Paint Black
    private val moonPaintB = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = Color.BLACK
        it.style = Paint.Style.FILL_AND_STROKE
    }

    // moon Paint for Oval
    private val moonPaintO = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = Color.WHITE
        it.style = Paint.Style.FILL_AND_STROKE
    }

    // moon Paint for Diameter
    private val moonPaintD = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = Color.GRAY
        it.style = Paint.Style.STROKE
    }

    private fun Canvas.drawMoon(jdn: Jdn) {
        val coordinates = coordinates ?: return
        val sunMoonPosition = SunMoonPosition(
            jdn.value.toDouble(), coordinates.latitude, coordinates.longitude,
            coordinates.elevation, 0.0
        )
        val moonRect = RectF()
        val moonOval = RectF()
        val cx = width / 2
        val cy = height / 2
        val r = min(cx, cy)
        val moonPhase = sunMoonPosition.moonPhase
        val arcWidth = ((moonPhase - .5) * (4 * r)).toInt()
        // elevation Offset 0 for 0 degree; r for 90 degree
        moonRect.set(0f, 0f, width.toFloat(), height.toFloat())
        drawArc(moonRect, 90f, 180f, false, moonPaint)
        drawArc(moonRect, 270f, 180f, false, moonPaintB)
        moonOval.set(
            cx - abs(arcWidth) / 2f, 0f, cx + abs(arcWidth) / 2f, height.toFloat()
        )
        moonPaintO.color = if (arcWidth < 0) Color.BLACK else Color.WHITE
        drawArc(moonOval, 0f, 360f, false, moonPaintO)
        drawArc(moonRect, 0f, 360f, false, moonPaintD)
    }

    var storedJdn: Jdn? = null

    fun showCalendars(
        jdn: Jdn, chosenCalendarType: CalendarType, calendarsToShow: List<CalendarType>
    ) {
        val context = context ?: return

        binding.calendarsFlow.update(calendarsToShow, jdn)
        binding.weekDayName.text = jdn.dayOfWeekName
        storedJdn = jdn

        binding.zodiac.also {
            val zodiacInfo = getZodiacInfo(context, jdn, withEmoji = true, short = false)
            it.text = zodiacInfo
            it.isVisible = zodiacInfo.isNotEmpty()
            binding.moonPhase.isVisible = zodiacInfo.isNotEmpty()
            binding.moonPhase.postInvalidate()
        }

        val isToday = Jdn.today() == jdn
        if (isToday) {
            if (isForcedIranTimeEnabled) binding.weekDayName.text = "%s (%s)".format(
                jdn.dayOfWeekName, context.getString(R.string.iran_time)
            )
            binding.diffDate.isVisible = false
        } else {
            binding.also {
                it.diffDate.isVisible = true
                it.diffDate.text = listOf(
                    context.getString(R.string.days_distance), spacedColon,
                    calculateDaysDifference(resources, jdn)
                ).joinToString("")
            }
        }

        val date = jdn.toCalendar(chosenCalendarType)
        val startOfYearJdn = Jdn(chosenCalendarType, date.year, 1, 1)
        val endOfYearJdn = Jdn(chosenCalendarType, date.year + 1, 1, 1) - 1
        val currentWeek = jdn.getWeekOfYear(startOfYearJdn)
        val weeksCount = endOfYearJdn.getWeekOfYear(startOfYearJdn)

        val startOfYearText = context.getString(
            R.string.start_of_year_diff, formatNumber(jdn - startOfYearJdn + 1),
            formatNumber(currentWeek), formatNumber(date.month)
        )
        val endOfYearText = context.getString(
            R.string.end_of_year_diff, formatNumber(endOfYearJdn - jdn),
            formatNumber(weeksCount - currentWeek), formatNumber(12 - date.month)
        )
        binding.startAndEndOfYearDiff.text =
            listOf(startOfYearText, endOfYearText).joinToString("\n")

        var equinox = ""
        if (mainCalendar == chosenCalendarType && chosenCalendarType == CalendarType.SHAMSI) {
            if (date.month == 12 && date.dayOfMonth >= 20 || date.month == 1 && date.dayOfMonth == 1) {
                val addition = if (date.month == 12) 1 else 0
                val springEquinox = jdn.toGregorianCalendar().getSpringEquinox()
                equinox = context.getString(
                    R.string.spring_equinox,
                    formatNumber(date.year + addition),
                    Clock(springEquinox[Calendar.HOUR_OF_DAY], springEquinox[Calendar.MINUTE])
                        .toFormattedString(forcedIn12 = true) + " " +
                            formatDate(
                                Jdn(springEquinox.toCivilDate()).toCalendar(mainCalendar),
                                forceNonNumerical = true
                            )
                )
            }
        }
        binding.equinox.also {
            it.text = equinox
            it.isVisible = equinox.isNotEmpty()
        }

        binding.root.contentDescription = getA11yDaySummary(
            context, jdn, isToday, EventsStore.empty(),
            withZodiac = true, withOtherCalendars = true, withTitle = true
        )

        val persian = (date as? PersianDate) ?: jdn.toPersianCalendar()
        val season = (persian.month - 1) / 3
        val seasonMonthsLength = if (season < 2) 31 else 30
        binding.seasonProgress.enableAnimation = isExpanded
        binding.seasonProgress.max = seasonMonthsLength * 3
        binding.seasonProgress.progress = (persian.month - season * 3 - 1) * seasonMonthsLength +
                persian.dayOfMonth

        binding.monthProgress.enableAnimation = isExpanded
        binding.monthProgress.max = mainCalendar.getMonthLength(date.year, date.month)
        binding.monthProgress.progress = date.dayOfMonth
        binding.yearProgress.enableAnimation = isExpanded
        binding.yearProgress.max = endOfYearJdn - startOfYearJdn
        binding.yearProgress.progress = jdn - startOfYearJdn
    }

    companion object {
        val moonPhasesEmojis = listOf("🌑", "🌒", "🌓", "🌔", "🌕", "🌖", "🌗", "🌘", "🌑")
    }
}
