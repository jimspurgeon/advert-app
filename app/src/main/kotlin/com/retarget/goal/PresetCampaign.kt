/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * Retarget — turning advertising's own toolbox toward your goals.
 * Copyright (C) 2026 Jim Spurgeon. For license text see LICENSE.
 */

package com.retarget.goal

import com.retarget.creative.GoalTheme

/**
 * One copy line in a preset campaign, tagged for the fatigue-aware rotator.
 *
 * [tonePattern] must reference a pattern in docs/research/tone-guide.md §3.1–3.20;
 * [citation] must reference the docs/research/ file+section grounding the line's
 * mechanism. Both are enforced by PresetCatalogTest (tone-guide.md §5).
 */
data class PresetCopyLine(
    val text: String,
    val subTheme: String, // rotation bucket for diversity (e.g. "refresh", "absurdity")
    val tonePattern: String, // e.g. "3.2 Absurd Boast"
    val citation: String, // e.g. "advertising-psychology.md §1"
)

/**
 * A factory-default campaign: everything needed to run a goal out of the box.
 *
 * Presets are curated like an agency's account plan (DEVELOPMENT.md domain model);
 * the user's only setup gesture is picking one. All pacing defaults comply with
 * BudgetPolicy hard caps — they may be lowered by the user, never raised.
 */
data class PresetCampaign(
    val id: String,
    val goalTheme: GoalTheme,
    val displayName: String,
    val emoji: String,
    val blurb: String, // one-line installer-facing description
    val imageKeywords: List<String>, // Unsplash search terms (issue #4 pipeline)
    val copyLines: List<PresetCopyLine>,
    /** Daily impression targets per channel; every value ≤ BudgetPolicy hard caps. */
    val wallpaperTargetsPerDay: Int,
    val notificationTargetsPerDay: Int,
)
