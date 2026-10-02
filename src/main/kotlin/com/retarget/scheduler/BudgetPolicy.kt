package com.retarget.scheduler

import com.retarget.creative.Channel

/**
 * Delivery budget enforcement — HARD limits live in code, not just settings.
 *
 * Research basis: docs/research/digital-wellbeing.md §1-2 and interruption-timing.md
 * §7 defaults. User settings may lower these, never raise past the maxima here.
 */
object BudgetPolicy {

    const val DEFAULT_QUIET_START_HOUR = 22
    const val DEFAULT_QUIET_END_HOUR = 7

    const val NOTIFICATION_HARD_MAX_PER_DAY_PER_GOAL = 3
    const val OVERLAY_HARD_MAX_PER_DAY = 1
    const val MIN_COOLDOWN_AFTER_DISMISS_MS = 2 * 60 * 60 * 1000L  // 2h
    const val SATURATION_SKIP_THRESHOLD = 2  // dismissals within window triggers skip
    const val SATURATION_WINDOW_MS = 6 * 60 * 60 * 1000L            // 6h

    fun isQuietHour(hour: Int, quietStart: Int = DEFAULT_QUIET_START_HOUR, quietEnd: Int = DEFAULT_QUIET_END_HOUR): Boolean =
        if (quietStart <= quietEnd) hour >= quietStart || hour < quietEnd
        else hour >= quietStart || hour < quietEnd  // wraps midnight either way

    /**
     * True if delivery is currently allowed on [channel] given today's counters and
     * the post-dismissal cooldown/backoff state.
     */
    fun canDeliver(
        channel: Channel,
        sentTodayOnChannel: Int,
        dismissedRecently: List<Long>,  // timestamps of dismissals in lookback window
        nowMs: Long,
        hardMaxOverride: Int? = null,  // only ever ≤ hard max; never above
    ): Boolean {
        val hardMax = when (channel) {
            Channel.NOTIFICATION -> NOTIFICATION_HARD_MAX_PER_DAY_PER_GOAL
            Channel.OVERLAY -> OVERLAY_HARD_MAX_PER_DAY
            else -> Int.MAX_VALUE  // ambient channels are self-limiting
        }.coerceAtMost(hardMaxOverride ?: hardMax)

        if (sentTodayOnChannel >= hardMax) return false

        // Saturation signal: ≥2 dismissals within 6h → skip next slot.
        val recentDismissals = dismissedRecently.filter { nowMs - it <= SATURATION_WINDOW_MS }
        if (recentDismissals.size >= SATURATION_SKIP_THRESHOLD) return false

        // Cooldown: most recent dismissal within cooldown window blocks.
        val lastDismissal = dismissedRecently.maxOrNull()
        if (lastDismissal != null && nowMs - lastDismissal < MIN_COOLDOWN_AFTER_DISMISS_MS) return false

        return true
    }
}
