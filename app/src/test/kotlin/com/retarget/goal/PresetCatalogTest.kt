/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * Retarget — turning advertising's own toolbox toward your goals.
 * Copyright (C) 2026 Jim Spurgeon. For license text see LICENSE.
 */

package com.retarget.goal

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

/**
 * Preset catalog conformance: the tone guide (docs/research/tone-guide.md §5)
 * and issue #2 acceptance criteria, enforced in code. If a line you love fails
 * here, the resolution is a guide amendment — never a validator weakening.
 */
class PresetCatalogTest {

    private val catalog = PresetCatalog.ALL

    @Test
    fun `catalog contains the four launch presets`() {
        assertEquals(listOf("hydration", "fresh-air", "fruit", "vegetables"), catalog.map { it.id })
    }

    @Test
    fun `lookup by id works and rejects unknown ids`() {
        assertEquals(PresetCatalog.HYDRATION, PresetCatalog.byId("hydration"))
        assertEquals(null, PresetCatalog.byId("not-a-preset"))
    }

    @Test
    fun `every preset has at least 8 copy lines`() {
        catalog.forEach { preset ->
            assertTrue(
                "${preset.id} has only ${preset.copyLines.size} lines; need >= 8 for rotation",
                preset.copyLines.size >= 8,
            )
        }
    }

    @Test
    fun `every preset covers at least 4 distinct sub-themes`() {
        catalog.forEach { preset ->
            val distinct = preset.copyLines.map { it.subTheme }.distinct()
            assertTrue(
                "${preset.id} covers only ${distinct.size} sub-themes ($distinct); need >= 4 " +
                    "(advertising-psychology.md §4: rotating themes delays wearout)",
                distinct.size >= 4,
            )
        }
    }

    @Test
    fun `every copy line is within the 64 character hard cap`() {
        catalog.flatMap { it.copyLines }.forEach { line ->
            assertTrue(
                "\"${line.text}\" is ${line.text.length} chars; hard cap 64 (tone-guide.md §2)",
                line.text.length <= 64,
            )
        }
    }

    @Test
    fun `every copy line references an approved tone pattern`() {
        val approved = (1..20).map { n ->
            val titles = mapOf(
                1 to "Minimalist Confidence", 2 to "Absurd Boast", 3 to "Luxury Repositioning",
                4 to "Corporate Synergy", 5 to "Call-to-Action Parody", 6 to "Sensory Close-Up",
                7 to "Heritage/Nostalgia Brand", 8 to "New Product Launch", 9 to "Testimonial Parody",
                10 to "Fine Print Joke", 11 to "Sponsorship Parody", 12 to "Mascot Wisdom",
                13 to "Underpromise", 14 to "Lifestyle Integration", 15 to "Elegant Minimalism",
                16 to "As-Seen-On-TV", 17 to "Comparative Ad", 18 to "Seasonal Campaign",
                19 to "Infomercial Problem-Solution", 20 to "Milestone Celebration",
            )
            "3.$n ${titles[n]}"
        }
        catalog.flatMap { it.copyLines }.forEach { line ->
            assertTrue(
                "\"${line.text}\" cites unapproved pattern '${line.tonePattern}'",
                line.tonePattern in approved,
            )
        }
    }

    @Test
    fun `every copy line carries a research citation`() {
        catalog.flatMap { it.copyLines }.forEach { line ->
            assertTrue(
                "\"${line.text}\" has malformed citation '${line.citation}'",
                line.citation.matches(Regex("[a-z-]+\\.md §[0-9A-Za-z.]+")),
            )
            assertTrue(
                "\"${line.text}\" citation must name a docs/research/ file",
                line.citation.substringBefore(" §").endsWith(".md"),
            )
        }
    }

    @Test
    fun `no copy line trips the anti-pattern keyword wire`() {
        val tripwire = listOf(
            "should", "don't break", "last chance", "limited time", "only ", "left",
            "danger", "risk of", "guilty", "shame",
        )
        catalog.flatMap { it.copyLines }.forEach { line ->
            val lower = line.text.lowercase()
            tripwire.forEach { kw ->
                assertTrue(
                "\"${line.text}\" contains anti-pattern keyword '$kw' (tone-guide.md §4)",
                !lower.contains(kw),
            )
            }
        }
    }

    @Test
    fun `daily targets respect BudgetPolicy hard caps`() {
        catalog.forEach { preset ->
            assertTrue(
                "${preset.id} notification target ${preset.notificationTargetsPerDay} " +
                    "exceeds hard cap 3 (BudgetPolicy.NOTIFICATION_HARD_MAX_PER_DAY_PER_GOAL)",
                preset.notificationTargetsPerDay <= 3,
            )
        }
    }

    @Test
    fun `presets have distinct image keywords for the pipeline`() {
        catalog.forEach { preset ->
            assertTrue("${preset.id} has no image keywords (issue #4)", preset.imageKeywords.isNotEmpty())
            assertEquals(preset.imageKeywords.size, preset.imageKeywords.distinct().size)
        }
    }
}
