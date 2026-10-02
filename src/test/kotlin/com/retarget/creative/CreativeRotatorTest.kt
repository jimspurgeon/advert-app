package com.retarget.creative

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class CreativeRotatorTest {

    private class FakeLedger : ExposureLedger {
        val exposures = mutableListOf<Triple<String, Channel, Long>>()
        override fun recordExposure(creativeId: String, channel: Channel, atMs: Long) {
            exposures.add(Triple(creativeId, channel, atMs))
        }
        override fun lastShownAt(creativeId: String): Long? =
            exposures.filter { it.first == creativeId }.maxOfOrNull { it.third }
        override fun timesShown(creativeId: String): Int =
            exposures.count { it.first == creativeId }
    }

    private fun creative(id: String, subTheme: String = "default", appeal: Float = 1.0f) = Creative(
        id = id, packId = "pack", goalTheme = GoalTheme.HYDRATION, subTheme = subTheme,
        copyPool = listOf("Refreshing."), imagePath = "/x/$id.jpg",
        attribution = null, licenseUrl = "https://example.org/cc0", baseAppeal = appeal,
    )

    @Test
    fun `empty candidates returns null`() {
        val rotator = CreativeRotator()
        assertNull(rotator.selectNext(emptyList(), FakeLedger(), nowMs = 0))
    }

    @Test
    fun `single candidate is returned`() {
        val rotator = CreativeRotator()
        val only = creative("a")
        assertEquals(only, rotator.selectNext(listOf(only), FakeLedger(), nowMs = 0))
    }

    @Test
    fun `never-shown creative scores higher than recently-shown`() {
        val rotator = CreativeRotator()
        val ledger = FakeLedger()
        val now = 100 * CreativeRotator.MS_PER_HOUR.toLong()
        val fresh = creative("fresh")
        val stale = creative("stale")
        ledger.recordExposure("stale", Channel.WALLPAPER, now - 60 * 3_600_000L) // 60h ago

        val freshScore = rotator.score(fresh, ledger, now)
        val staleScore = rotator.score(stale, ledger, now)
        assertTrue(freshScore > staleScore)
    }

    @Test
    fun `score recovers as time passes after exposure`() {
        val rotator = CreativeRotator()
        val ledger = FakeLedger()
        val shownAt = 0L
        ledger.recordExposure("c", Channel.WALLPAPER, shownAt)
        val c = creative("c")

        val soonAfter = rotator.score(c, ledger, nowMs = 2 * 3_600_000L)
        val muchLater = rotator.score(c, ledger, nowMs = 100 * 3_600_000L)
        assertTrue(muchLater > soonAfter)
    }

    @Test
    fun `cumulative wear reduces score even after long rest`() {
        val rotator = CreativeRotator()
        val ledgerA = FakeLedger()
        val ledgerB = FakeLedger()
        repeat(20) { ledgerB.recordExposure("c", Channel.WALLPAPER, it * 10_000L) }

        val c = creative("c")
        val restedOnce = rotator.score(c, ledgerA, nowMs = 10_000_000_000L)
        val wornTwenty = rotator.score(c, ledgerB, nowMs = 10_000_000_000L)
        assertTrue(restedOnce > wornTwenty)
    }

    @Test
    fun `selection avoids repeating same creative consecutively when alternatives exist`() {
        val rotator = CreativeRotator()
        val ledger = FakeLedger()
        val pool = (1..6).map { creative("c$it") }
        // Show c1 most recently.
        ledger.recordExposure("c1", Channel.WALLPAPER, 1000L)

        val random = Random(42)
        val picks = (1..20).mapNotNull { rotator.selectNext(pool, ledger, nowMs = 2000L, random = random)?.id }
        // With 6 creatives, a fatigue-aware rotator should not pick c1 in all 20 draws.
        assertTrue(picks.none { it == "c1" } || picks.count { it == "c1" } < 20)
    }
}
