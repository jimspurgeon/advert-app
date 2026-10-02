package com.retarget.creative

import kotlin.math.exp
import kotlin.math.ln

/**
 * Fatigue-aware creative rotation engine — the app's algorithmic heart.
 *
 * Scores candidate creatives by:
 *   score = baseAppeal × restRecovery × wearPenalty
 *
 * and picks randomly among the top-K scorers for variety. Rest recovery
 * penalizes creatives shown recently; cumulative wear slightly tires a
 * creative forever (wearout avoidance — advertising psychology §4,
 * docs/research/advertising-psychology.md).
 *
 * TODO(Phase 1): sub-theme diversity factor to spread selections across a pack
 * (see DEVELOPMENT.md “rotation engine”).
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

    fun score(
        creative: Creative,
        ledger: ExposureLedger,
        nowMs: Long,
    ): Double {
        val lastShown = ledger.lastShownAt(creative.id)
        val timesShown = ledger.timesShown(creative.id)
        val recencyHours =
            if (lastShown == null) {
                Double.POSITIVE_INFINITY
            } else {
                (nowMs - lastShown) / MS_PER_HOUR
            }

        // Rest-recovery curve: a creative just shown is fully suppressed (0) and
        // recovers half the remaining distance to full freshness (1) every
        // HALF_LIFE_HOURS of rest. Never-shown creatives are fully rested (1).
        // (Wearout avoidance — advertising psychology §4, docs/research/
        // advertising-psychology.md.)
        val restRecovery = 1.0 - exp(-ln(2.0) * recencyHours / HALF_LIFE_HOURS)
        // Cumulative wear: each showing slightly tires the creative forever (small effect).
        val wearPenalty = 1.0 / (1.0 + WEAR_RATE * timesShown)

        return creative.baseAppeal * restRecovery * wearPenalty
    }

    companion object {
        const val TOP_K = 3
        const val HALF_LIFE_HOURS = 72.0 // 3 days per creative freshness half-life
        const val WEAR_RATE = 0.03 // gentle cumulative wear
        const val MS_PER_HOUR = 3_600_000.0
    }
}

/** Append-only, on-device impression log. Never leaves the device. */
interface ExposureLedger {
    fun recordExposure(
        creativeId: String,
        channel: Channel,
        atMs: Long,
    )

    fun lastShownAt(creativeId: String): Long?

    fun timesShown(creativeId: String): Int
}

enum class Channel { WALLPAPER, NOTIFICATION, OVERLAY, WIDGET }
