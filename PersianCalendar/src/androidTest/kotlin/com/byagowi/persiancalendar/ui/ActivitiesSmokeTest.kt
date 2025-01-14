package com.byagowi.persiancalendar.ui

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.byagowi.persiancalendar.ui.athan.AthanActivity
import com.byagowi.persiancalendar.ui.preferences.agewidget.AgeWidgetConfigureActivity
import com.byagowi.persiancalendar.ui.preferences.widgetnotification.WidgetConfigurationActivity
import org.junit.Test

class ActivitiesSmokeTest {
    @Test
    fun test() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java))
        ActivityScenario.launch<AthanActivity>(Intent(context, AthanActivity::class.java))
        ActivityScenario.launch<WidgetConfigurationActivity>(
            Intent(context, WidgetConfigurationActivity::class.java)
        )
        // Doesn't show anything yet just better than nothing for now
        ActivityScenario.launch<AgeWidgetConfigureActivity>(
            Intent(context, AgeWidgetConfigureActivity::class.java)
        )
    }
}
