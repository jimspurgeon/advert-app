package com.retarget.scheduler

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Fresh-start windows: temporal landmarks that motivate aspirational behavior.
 *
 * Research: Dai, Milkman & Riis (2014) — the fresh start effect. See
 * docs/research/behavior-change-science.md §4.
 */
object FreshStartCalendar {

    /** Landmark days where goal-priming gets a gentle boost. */
    fun isFreshStartDay(date: LocalDate): Boolean {
        val isFirstOfMonth = date.dayOfMonth == 1
        val isMonday = date.dayOfWeek == DayOfWeek.MONDAY
        val isNewYear = date.dayOfYear == 1
        return isFirstOfMonth || isMonday || isNewYear
    }

    /** Days until the next landmark — used to taper boost intensity approaching one. */
    fun daysUntilNextLandmark(from: LocalDate): Long {
        var d = from.plusDays(1)
        var days = 1L
        while (!isFreshStartDay(d) && days < 8) {
            d = d.plusDays(1)
            days++
        }
        return days
    }

    /** Boost multiplier applied to goal content prominence (bounded, gentle). */
    fun boostMultiplier(date: LocalDate): Double {
        if (isFreshStartDay(date)) return 1.25
        val until = daysUntilNextLandmark(date)
        return if (until <= 1) 1.1 else 1.0
    }

    /** Suggest an appropriate fresh-start themed copy theme. */
    fun copyTheme(date: LocalDate): String = when {
        isFreshStartDay(date) -> "fresh_start"
        else -> "standard"
    }
}
