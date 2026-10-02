/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * Retarget — turning advertising's own toolbox toward your goals.
 * Copyright (C) 2026 Jim Spurgeon. For license text see LICENSE.
 */

package com.retarget.goal

import com.retarget.creative.GoalTheme

/**
 * The factory preset catalog: pick a goal, get a full campaign with sane,
 * research-aligned defaults. Voice per docs/research/tone-guide.md; pacing
 * per BudgetPolicy caps and interruption-timing.md §7 defaults.
 */
object PresetCatalog {

    val HYDRATION = PresetCampaign(
        id = "hydration",
        goalTheme = GoalTheme.HYDRATION,
        displayName = "Hydration",
        emoji = "💧",
        blurb = "Your body's oldest brand loyalty program.",
        imageKeywords = listOf("water glass", "drinking water", "river stream"),
        wallpaperTargetsPerDay = 4,
        notificationTargetsPerDay = 2,
        copyLines = listOf(
            PresetCopyLine("Water. It's what plants crave.", "classic", "3.1 Minimalist Confidence", "advertising-psychology.md §1"),
            PresetCopyLine("Hydration: featuring award-winning wetness.", "absurdity", "3.2 Absurd Boast", "advertising-psychology.md §2"),
            PresetCopyLine("A truly artisanal glass of water.", "luxury", "3.3 Luxury Repositioning", "advertising-psychology.md §2"),
            PresetCopyLine("Drink water now-ish. No pressure.", "cta-parody", "3.5 Call-to-Action Parody", "behavior-change-science.md §3"),
            PresetCopyLine("Cold water. Loudly refreshing.", "sensory", "3.6 Sensory Close-Up", "advertising-psychology.md §6"),
            PresetCopyLine("Hydration: a classic for a reason.", "heritage", "3.7 Heritage/Nostalgia Brand", "advertising-psychology.md §1"),
            PresetCopyLine("Introducing water 2.0. Same great taste.", "launch", "3.8 New Product Launch", "advertising-psychology.md §3b"),
            PresetCopyLine("\"Best water I've had all day.\" — You, in a minute", "testimonial", "3.9 Testimonial Parody", "advertising-psychology.md §5"),
        ),
    )

    val FRESH_AIR = PresetCampaign(
        id = "fresh-air",
        goalTheme = GoalTheme.NATURE_TIME,
        displayName = "Fresh Air",
        emoji = "🌤️",
        blurb = "The outdoors. Now available outdoors.",
        imageKeywords = listOf("sunlight forest", "golden hour field", "mountain morning"),
        wallpaperTargetsPerDay = 3,
        notificationTargetsPerDay = 1,
        copyLines = listOf(
            PresetCopyLine("Sunlight. Free range. Free.", "minimal", "3.1 Minimalist Confidence", "advertising-psychology.md §1"),
            PresetCopyLine("Fresh air: now with more outdoors.", "absurdity", "3.2 Absurd Boast", "advertising-psychology.md §2"),
            PresetCopyLine("Go outside. Briefly. We'll take it.", "cta-parody", "3.5 Call-to-Action Parody", "behavior-change-science.md §3"),
            PresetCopyLine("Warm sun on forearms. Apply generously.", "sensory", "3.6 Sensory Close-Up", "advertising-psychology.md §6"),
            PresetCopyLine("Walking outdoors: trusted by humans since forever.", "heritage", "3.7 Heritage/Nostalgia Brand", "advertising-psychology.md §1"),
            PresetCopyLine("\"I went outside and my mood noticed.\" — You, soon", "testimonial", "3.9 Testimonial Parody", "advertising-psychology.md §5"),
            PresetCopyLine("Golden hour, proudly sponsored by the sun.", "sponsorship", "3.11 Sponsorship Parody", "advertising-psychology.md §2"),
            PresetCopyLine("The river has never once missed a day.", "mascot", "3.12 Mascot Wisdom", "advertising-psychology.md §1"),
        ),
    )

    val FRUIT = PresetCampaign(
        id = "fruit",
        goalTheme = GoalTheme.PLANT_BASED_WHOLE_FOODS,
        displayName = "More Fruit",
        emoji = "🍎",
        blurb = "Fruit: the other fast food.",
        imageKeywords = listOf("fresh fruit bowl", "ripe peaches", "berry market"),
        wallpaperTargetsPerDay = 3,
        notificationTargetsPerDay = 1,
        copyLines = listOf(
            PresetCopyLine("Apples. Nature's original snack food.", "minimal", "3.1 Minimalist Confidence", "advertising-psychology.md §1"),
            PresetCopyLine("The all-new apple. No subscription required.", "launch", "3.8 New Product Launch", "advertising-psychology.md §3b"),
            PresetCopyLine("Eat a peach. It's not going to eat itself.", "fine-print", "3.10 Fine Print Joke", "advertising-psychology.md §2"),
            PresetCopyLine("Fruit: the other fast food.", "comparative", "3.17 Comparative Ad", "advertising-psychology.md §2"),
            PresetCopyLine("Crisp apples, crunching at volume.", "sensory", "3.6 Sensory Close-Up", "advertising-psychology.md §6"),
            PresetCopyLine("\"Turns out I like fruit.\" — You, eventually", "testimonial", "3.9 Testimonial Parody", "advertising-psychology.md §5"),
            PresetCopyLine("Step 1: fruit. Step 2: enjoy. That's the ad.", "drtv", "3.16 As-Seen-On-TV", "advertising-psychology.md §3b"),
            PresetCopyLine("Fruit: a family tradition, generations strong.", "heritage", "3.7 Heritage/Nostalgia Brand", "advertising-psychology.md §1"),
        ),
    )

    val VEGETABLES = PresetCampaign(
        id = "vegetables",
        goalTheme = GoalTheme.PLANT_BASED_WHOLE_FOODS,
        displayName = "More Vegetables",
        emoji = "🥕",
        blurb = "Redesigned by 400 million years of R&D.",
        imageKeywords = listOf("fresh vegetables", "farmers market greens", "colorful salad"),
        wallpaperTargetsPerDay = 3,
        notificationTargetsPerDay = 1,
        copyLines = listOf(
            PresetCopyLine("Vegetables: redesigned by evolution.", "absurdity", "3.2 Absurd Boast", "advertising-psychology.md §2"),
            PresetCopyLine("Eat a vegetable today. Start anywhere.", "cta-parody", "3.5 Call-to-Action Parody", "behavior-change-science.md §3"),
            PresetCopyLine("Craving crunch? Carrots heard you.", "infomercial", "3.19 Infomercial Problem-Solution", "digital-wellbeing.md §3"),
            PresetCopyLine("Verdict's in: vegetables rated delicious.", "comparative", "3.17 Comparative Ad", "advertising-psychology.md §2"),
            PresetCopyLine("Green things. Eat some.", "minimalism", "3.15 Elegant Minimalism", "advertising-psychology.md §3b"),
            PresetCopyLine("\"I ate a carrot and felt powerful.\" — You, soon", "testimonial", "3.9 Testimonial Parody", "advertising-psychology.md §5"),
            PresetCopyLine("Bespoke produce, hand-grown by the Earth™.", "luxury", "3.3 Luxury Repositioning", "advertising-psychology.md §2"),
            PresetCopyLine("Farmers markets: the original pop-up.", "lifestyle", "3.14 Lifestyle Integration", "interruption-timing.md §4"),
        ),
    )

    val ALL: List<PresetCampaign> = listOf(HYDRATION, FRESH_AIR, FRUIT, VEGETABLES)

    fun byId(id: String): PresetCampaign? = ALL.firstOrNull { it.id == id }

    fun byTheme(theme: GoalTheme): List<PresetCampaign> = ALL.filter { it.goalTheme == theme }
}
