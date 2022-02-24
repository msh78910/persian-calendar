package io.github.persiancalendar.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class DependenciesPlugin : Plugin<Project> {

    override fun apply(target: Project) {
    }

    companion object {
        val persianCalendarGroupDeps = listOf(
            "com.github.persian-calendar:equinox:2.0.0",
            "com.github.persian-calendar:calendar:1.2.0",
            "com.github.persian-calendar:praytimes:2.1.2"
        )

        val firebaseDeps = listOf(
            "com.google.firebase:firebase-bom:29.1.0",
            "com.google.firebase:firebase-analytics-ktx",
            "com.google.firebase:firebase-crashlytics-ktx",
            "com.google.firebase:firebase-perf-ktx"
        )
    }
}
