package com.byagowi.persiancalendar.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.byagowi.persiancalendar.R
import com.byagowi.persiancalendar.ui.about.AboutFragment
import com.byagowi.persiancalendar.ui.astronomy.AstronomyFragment
import com.byagowi.persiancalendar.ui.astronomy.AstronomyFragmentArgs
import com.byagowi.persiancalendar.ui.calendar.CalendarFragment
import com.byagowi.persiancalendar.ui.compass.CompassFragment
import com.byagowi.persiancalendar.ui.converter.ConverterFragment
import com.byagowi.persiancalendar.ui.level.LevelFragment
import com.byagowi.persiancalendar.ui.map.MapFragment
import com.byagowi.persiancalendar.ui.map.PanoRendoFragment
import com.byagowi.persiancalendar.ui.map.PanoRendoFragmentArgs
import com.byagowi.persiancalendar.ui.preferences.PreferencesFragment
import com.byagowi.persiancalendar.ui.preferences.PreferencesFragmentArgs
import com.byagowi.persiancalendar.ui.preferences.agewidget.AgeWidgetConfigureFragment
import com.byagowi.persiancalendar.ui.preferences.interfacecalendar.InterfaceCalendarFragment
import com.byagowi.persiancalendar.ui.preferences.locationathan.LocationAthanFragment
import com.byagowi.persiancalendar.ui.preferences.widgetnotification.WidgetNotificationFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FragmentsSmokeTest {

    @Test
    fun themesSmokeTest() {
        listOf(
            R.style.DynamicLightTheme, R.style.DynamicDarkTheme, R.style.LightTheme,
            R.style.DarkTheme, R.style.ModernTheme, R.style.BlueTheme, R.style.BlackTheme
        ).forEach { launchFragmentInContainer<CalendarFragment>(themeResId = it) }
    }

    @Test
    fun fragmentsSmokeTest() {
        launchFragmentInContainer<CalendarFragment>(themeResId = R.style.LightTheme)
        launchFragmentInContainer<AboutFragment>(themeResId = R.style.LightTheme)
        launchFragmentInContainer<AstronomyFragment>(
            themeResId = R.style.LightTheme,
            fragmentArgs = AstronomyFragmentArgs(0).toBundle()
        )
        launchFragmentInContainer<CompassFragment>(themeResId = R.style.LightTheme)
        launchFragmentInContainer<ConverterFragment>(themeResId = R.style.LightTheme)
        launchFragmentInContainer<LevelFragment>(themeResId = R.style.LightTheme)
        launchFragmentInContainer<MapFragment>(themeResId = R.style.LightTheme)
        launchFragmentInContainer<PanoRendoFragment>(
            themeResId = R.style.LightTheme,
            fragmentArgs = PanoRendoFragmentArgs(0).toBundle()
        )
        (0..3).forEach {
            launchFragmentInContainer<PreferencesFragment>(
                themeResId = R.style.LightTheme,
                fragmentArgs = PreferencesFragmentArgs(it).toBundle()
            )
        }
        launchFragmentInContainer<AgeWidgetConfigureFragment>(themeResId = R.style.LightTheme)
        launchFragmentInContainer<InterfaceCalendarFragment>(themeResId = R.style.LightTheme)
        launchFragmentInContainer<LocationAthanFragment>(themeResId = R.style.LightTheme)
        launchFragmentInContainer<WidgetNotificationFragment>(themeResId = R.style.LightTheme)
    }
}