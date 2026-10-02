package com.retarget.scheduler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class FreshStartCalendarTest {

    @Test
    fun `mondays first of month and new year are landmarks`() {
        assertTrue(FreshStartCalendar.isFreshStartDay(LocalDate.of(2025, 9, 1)))    // 1st
        assertTrue(FreshStartCalendar.isFreshStartDay(LocalDate.of(2025, 9, 8)))    // Monday
        assertTrue(FreshStartCalendar.isFreshStartDay(LocalDate.of(2025, 1, 1)))    // New Year
        assertEquals(false, FreshStartCalendar.isFreshStartDay(LocalDate.of(2025, 9, 3))) // Wed
    }

    @Test
    fun `boost multiplier is bounded and gentle`() {
        val onLandmark = FreshStartCalendar.boostMultiplier(LocalDate.of(2025, 9, 1))
        val ordinary = FreshStartCalendar.boostMultiplier(LocalDate.of(2025, 9, 3))
        assertEquals(1.25, onLandmark, 0.001)
        assertEquals(1.0, ordinary, 0.001)
    }

    @Test
    fun `days until landmark is small and positive`() {
        val from = LocalDate.of(2025, 9, 2) // Tuesday
        val until = FreshStartCalendar.daysUntilNextLandmark(from)
        assertTrue(until in 1..7)
    }
}
