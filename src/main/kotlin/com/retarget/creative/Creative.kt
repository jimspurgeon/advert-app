package com.retarget.creative

/**
 * A single advertisable "creative": an image paired with optional copy, organized
 * by sub-theme for fatigue-aware rotation.
 *
 * License metadata is mandatory — creatives without provenance must never be
 * ingested (see AGENTS.md / imagery-domains.md curation rules).
 */
data class Creative(
    val id: String,
    val packId: String,
    val goalTheme: GoalTheme,
    val subTheme: String,
    val copyPool: List<String>,   // candidate one-liners, rotated by fatigue engine
    val imagePath: String,        // resolved asset path (bundled or cached)
    val attribution: String?,
    val licenseUrl: String,       // required — CC0/PDM/licensed source
    val baseAppeal: Float = 1.0f, // seed quality score, may be learned later
)

enum class GoalTheme {
    HYDRATION,
    PLANT_BASED_WHOLE_FOODS,
    NATURE_TIME,
    BREATHING,
    GENERAL_WELLNESS,
}
