/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * Retarget — turning advertising's own toolbox toward your goals.
 * Copyright (C) 2026 Jim Spurgeon. For license text see LICENSE.
 */

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
    const val MIN_COOLDOWN_AFTER_DISMISS_MS = 2 * 60 * 60 * 1000L // 2h
    const val SATURATION_SKIP_THRESHOLD = 2 // dismissals within window triggers skip
    const val SATURATION_WINDOW_MS = 6 * 60 * 60 * 1000L // 6h

    fun isQuietHour(
        hour: Int,
        quietStart: Int = DEFAULT_QUIET_START_HOUR,
        quietEnd: Int = DEFAULT_QUIET_END_HOUR,
    ): Boolean =
        if (quietStart <= quietEnd) {
            // Same-day window, e.g. 02:00–06:00.
            hour >= quietStart && hour < quietEnd
        } else {
            // Window wraps midnight, e.g. 22:00–07:00.
            hour >= quietStart || hour < quietEnd
        }

    /**
     * True if delivery is currently allowed on [channel] given today's counters and
     * the post-dismissal cooldown/backoff state.
     */
    fun canDeliver(
        channel: Channel,
        sentTodayOnChannel: Int,
        dismissedRecently: List<Long>, // timestamps of dismissals in lookback window
        nowMs: Long,
        hardMaxOverride: Int? = null, // only ever ≤ hard max; never above
    ): Boolean {
        val channelHardMax =
            when (channel) {
                Channel.NOTIFICATION -> NOTIFICATION_HARD_MAX_PER_DAY_PER_GOAL
                Channel.OVERLAY -> OVERLAY_HARD_MAX_PER_DAY
                else -> Int.MAX_VALUE // ambient channels are self-limiting
            }

        // User overrides may only lower a cap, never raise it past the hard max.
        val hardMax =
            if (hardMaxOverride != null) {
                hardMaxOverride.coerceAtMost(channelHardMax)
            } else {
                channelHardMax
            }

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
