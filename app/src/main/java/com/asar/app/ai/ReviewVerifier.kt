package com.asar.app.ai

import com.asar.app.data.model.VerificationFlag
import com.asar.app.data.model.VerificationLevel
import com.asar.app.data.model.VerificationResult

/**
 * SIMULATED AI — rule-based plausibility check for reviews and impact claims.
 *
 * It never declares a review fraudulent. Unusual claims are only routed to
 * "additional verification" so a human moderator (or the requester) can confirm them.
 */
object ReviewVerifier {

    /** Roughly the most a single person can reasonably carry unaided. */
    private const val MAX_SOLO_LOAD_KG = 80

    private val soloWords = listOf(
        "alone", "by himself", "by herself", "by myself", "single-handedly", "on his own",
        "on her own", "one volunteer", "один", "одна", "сам ", "сама ",
    )
    private val superlatives = listOf(
        "best ever", "perfect", "incredible", "amazing", "unbelievable", "superhero",
        "miracle", "legendary", "100%", "flawless",
    )
    private val negativeWords = listOf("late", "rude", "didn't come", "did not come", "no-show", "careless", "bad")

    fun verify(text: String, rating: Int? = null, volunteerCount: Int? = null): VerificationResult {
        val lower = text.lowercase()
        val flags = mutableListOf<VerificationFlag>()
        var plausibility = 96

        // 1. Physical load per person
        val kg = Regex("(\\d{2,4})\\s*(kg|kilo|кг)").findAll(lower)
            .mapNotNull { it.groupValues[1].toIntOrNull() }.maxOrNull()
        val isSolo = soloWords.any { it in lower } || volunteerCount == 1
        if (kg != null && isSolo && kg > MAX_SOLO_LOAD_KG) {
            plausibility -= 55
            flags += VerificationFlag(
                "Physically unusual claim",
                "One person moving ~$kg kg unaided is far above typical safe limits " +
                    "(~$MAX_SOLO_LOAD_KG kg). It may be a typo, exaggeration or missing context.",
            )
        } else if (kg != null && kg > 300) {
            plausibility -= 15
            flags += VerificationFlag("Large weight mentioned", "Confirm how many people shared the load.")
        }

        // 2. Duration
        val hours = Regex("(\\d{1,3})\\s*(hours|hrs|часов)").findAll(lower)
            .mapNotNull { it.groupValues[1].toIntOrNull() }.maxOrNull()
        if (hours != null && hours > 16) {
            plausibility -= 30
            flags += VerificationFlag(
                "Unusual duration",
                "$hours hours of continuous volunteering in one task is uncommon.",
            )
        }

        // 3. Scale of impact
        val helped = Regex("(?:helped|fed|saved|cleaned)\\s+(\\d{3,6})").find(lower)
            ?.groupValues?.get(1)?.toIntOrNull()
        if (helped != null && helped >= 500 && isSolo) {
            plausibility -= 30
            flags += VerificationFlag(
                "Large impact by one person",
                "Helping $helped people single-handedly would normally need a team.",
            )
        }

        // 4. Tone signals
        val superlativeHits = superlatives.count { it in lower }
        if (superlativeHits >= 3) {
            plausibility -= 12
            flags += VerificationFlag(
                "Very promotional tone",
                "Several superlatives in one short review. Usually fine — just worth a glance.",
            )
        }
        if (rating != null) {
            val negativeHits = negativeWords.count { it in lower }
            if (rating >= 5 && negativeHits >= 2) {
                plausibility -= 15
                flags += VerificationFlag("Rating and text disagree", "5 stars, but the text sounds negative.")
            } else if (rating <= 2 && superlativeHits >= 1 && negativeHits == 0) {
                plausibility -= 15
                flags += VerificationFlag("Rating and text disagree", "Low rating, but the text is very positive.")
            }
        }
        if (lower.trim().length in 1..12) {
            plausibility -= 6
            flags += VerificationFlag("Very short review", "Short reviews carry less evidence.")
        }

        plausibility = plausibility.coerceIn(3, 99)
        val level = when {
            plausibility < 60 -> VerificationLevel.NEEDS_VERIFICATION
            flags.isNotEmpty() && plausibility < 90 -> VerificationLevel.REVIEW_SUGGESTED
            else -> VerificationLevel.LOOKS_GENUINE
        }
        return VerificationResult(level, plausibility, flags)
    }

    val sampleClaims = listOf(
        "One volunteer carried a 500 kg piano alone up to the 3rd floor.",
        "Dana helped with groceries, arrived on time and was very kind to my grandmother.",
        "Amazing, incredible, perfect, best ever volunteer!!! A real superhero.",
        "We worked 20 hours straight without a break and cleaned the whole park.",
    )
}
