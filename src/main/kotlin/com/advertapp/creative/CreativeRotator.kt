package com.advertapp.creative

import kotlin.math.exp

/**
 * Fatigue-aware creative rotation engine — the app's algorithmic heart.
 *
 * Scores candidate creatives by:
 *   score = baseAppeal × recencyDecay × subThemeDiversityFactor
 *
 * and selects the highest scorer. Recency decay penalizes creatives shown recently
 * (wearout avoidance — advertising psychology §4, docs/research/advertising-
 * psychology.md). Sub-theme diversity spreads selections across a pack.
 *
 * PURE KOTLIN — no Android dependencies. Unit-test everything here.
 */
class CreativeRotator {

    /**
     * @param candidates  creatives eligible for this slot (prefiltered by goal/theme)
     * @param exposureLedger  on-device append-only record of impressions
     * @param nowMs       current time (injectable for tests)
     */
    fun selectNext(
        candidates: List<Creative>,
        exposureLedger: ExposureLedger,
        nowMs: Long,
        random: kotlin.random.Random = kotlin.random.Random.Default,
    ): Creative? {
        if (candidates.isEmpty()) return null
        if (candidates.size == 1) return candidates.single()

        val scored = candidates.map { it to score(it, exposureLedger, nowMs) }
        // Weighted random among top-K to keep variety without picking stale creatives.
        val topK = scored.sortedByDescending { it.second }.take(TOP_K)
        return topK[random.nextInt(topK.size)].first
    }

    fun score(creative: Creative, ledger: ExposureLedger, nowMs: Long): Double {
        val lastShown = ledger.lastShownAt(creative.id)
        val timesShown = ledger.timesShown(creative.id)
        val recencyHours = if (lastShown == null) Double.MAX_VALUE
            else (nowMs - lastShown) / MS_PER_HOUR

        // Exponential decay: score halves every HALF_LIFE_HOURS since last show.
        val recencyDecay = exp(-ln2 * recencyHours / HALF_LIFE_HOURS)
        // Cumulative wear: each showing slightly tires the creative forever (small effect).
        val wearPenalty = 1.0 / (1.0 + WEAR_RATE * timesShown)

        return creative.baseAppeal * recencyDecay * wearPenalty
    }

    companion object {
        const val TOP_K = 3
        const val HALF_LIFE_HOURS = 72.0          // 3 days per creative freshness half-life
        const val WEAR_RATE = 0.03                 // gentle cumulative wear
        const val MS_PER_HOUR = 3_600_000.0
        private const val ln2 = 0.6931471805599453
    }
}

/** Append-only, on-device impression log. Never leaves the device. */
interface ExposureLedger {
    fun recordExposure(creativeId: String, channel: Channel, atMs: Long)
    fun lastShownAt(creativeId: String): Long?
    fun timesShown(creativeId: String): Int
}

enum class Channel { WALLPAPER, NOTIFICATION, OVERLAY, WIDGET }
