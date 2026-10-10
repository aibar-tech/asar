package com.asar.app.ai

import com.asar.app.data.model.Difficulty
import com.asar.app.data.model.TaskAnalysis
import com.asar.app.data.model.TaskCategory

/**
 * SIMULATED AI — a transparent, rule-based keyword analyzer that runs on-device.
 * It is not connected to any machine-learning model or external service.
 *
 * It scores each category by keyword hits (English, Russian and a few Kazakh words),
 * then derives difficulty, team size and skills from additional signals in the text.
 */
object TaskAnalyzer {

    private val categoryKeywords: Map<TaskCategory, List<String>> = mapOf(
        TaskCategory.ELDERLY_CARE to listOf(
            "grandmother", "grandfather", "grandma", "grandpa", "elderly", "senior", "pensioner",
            "veteran", "old lady", "old man", "ateje", "apa", "babushka", "бабушк", "дедушк",
            "пожил", "пенсионер", "әже", "ата", "care", "medicine", "pharmacy", "groceries",
        ),
        TaskCategory.MOVING to listOf(
            "move", "moving", "furniture", "sofa", "wardrobe", "fridge", "piano", "boxes",
            "carry", "lift", "truck", "переезд", "мебел", "грузч", "assemble", "ikea",
        ),
        TaskCategory.ECO to listOf(
            "cleanup", "clean up", "litter", "trash", "garbage", "recycl", "plastic", "tree",
            "plant", "park", "lake", "river", "субботник", "мусор", "eco", "green", "waste",
        ),
        TaskCategory.EDUCATION to listOf(
            "tutor", "teach", "lesson", "homework", "math", "english", "exam", "school",
            "student", "study", "reading", "репетитор", "урок", "learn", "library",
        ),
        TaskCategory.FOOD to listOf(
            "food", "meal", "cook", "parcel", "donation", "kitchen", "bread", "soup",
            "hungry", "еда", "продукт", "iftar", "food bank", "packing",
        ),
        TaskCategory.ANIMALS to listOf(
            "dog", "cat", "shelter", "animal", "pet", "puppy", "kitten", "vet", "walk the",
            "собак", "кошк", "приют",
        ),
        TaskCategory.EVENTS to listOf(
            "event", "festival", "nauryz", "наурыз", "concert", "celebration", "decorat",
            "marathon", "registration desk", "tent", "stage", "fair",
        ),
        TaskCategory.TECH_HELP to listOf(
            "smartphone", "phone", "laptop", "computer", "internet", "app", "whatsapp",
            "telegram", "video call", "printer", "wifi", "online", "телефон", "компьютер",
        ),
    )

    private val heavySignals = listOf(
        "heavy", "piano", "fridge", "sofa", "wardrobe", "no elevator", "no lift", "stairs",
        "5th floor", "4th floor", "тяжел", "washing machine", "cement", "construction",
    )
    private val longSignals = listOf("all day", "whole day", "weekend", "6 hours", "8 hours", "every day")
    private val careSignals = listOf("disabled", "wheelchair", "hospital", "medical", "sick", "инвалид")
    private val largeGroupSignals = listOf("park", "lake", "festival", "families", "community", "district", "marathon")

    fun analyze(title: String, description: String): TaskAnalysis? {
        val text = "$title $description".lowercase()
        if (text.isBlank() || text.trim().length < 8) return null

        val scores = categoryKeywords.mapValues { (_, words) -> words.count { text.hasKeyword(it) } }
        val best = scores.maxByOrNull { it.value } ?: return null
        val totalHits = scores.values.sum()
        val category = if (best.value == 0) TaskCategory.EVENTS else best.key
        val confidence = when {
            best.value == 0 -> 35
            else -> (55 + best.value * 12 - (totalHits - best.value) * 5).coerceIn(45, 97)
        }

        val signals = mutableListOf<String>()
        if (best.value > 0) {
            val matched = categoryKeywords.getValue(category).filter { text.hasKeyword(it) }.take(3)
            signals += "Matched “${matched.joinToString("”, “")}” → ${category.label}"
        } else {
            signals += "No strong keywords yet — add more details for a better match"
        }

        val heavy = heavySignals.filter { text.hasKeyword(it) }
        val long = longSignals.any { text.hasKeyword(it) }
        val care = careSignals.any { text.hasKeyword(it) }

        var effort = baseEffort(category)
        if (heavy.isNotEmpty()) {
            effort += 2
            signals += "Physical effort detected (“${heavy.first()}”)"
        }
        if (long) {
            effort += 1
            signals += "Long duration mentioned"
        }
        if (care) {
            effort += 1
            signals += "Sensitive care context — experience helps"
        }
        val difficulty = when {
            effort >= 4 -> Difficulty.DEMANDING
            effort >= 2 -> Difficulty.MODERATE
            else -> Difficulty.EASY
        }

        val explicitCount = Regex("(\\d{1,2})\\s*(volunteers|people|helpers|человек)")
            .find(text)?.groupValues?.get(1)?.toIntOrNull()
        var volunteers = baseVolunteers(category)
        if (heavy.isNotEmpty()) volunteers += 1
        if (largeGroupSignals.any { text.hasKeyword(it) }) volunteers += 2
        if (explicitCount != null) {
            volunteers = explicitCount
            signals += "Team size taken from your text"
        }
        volunteers = volunteers.coerceIn(1, 30)

        val skills = buildList {
            addAll(baseSkills(category))
            if (heavy.isNotEmpty()) add("Heavy lifting")
            if (listOf("car", "drive", "driving", "truck").any { text.hasWord(it) }) add("Driving licence")
            if (text.hasKeyword("english")) add("English")
            if (text.hasKeyword("kazakh") || text.hasKeyword("қазақ")) add("Kazakh language")
            if (text.hasKeyword("kid") || text.hasKeyword("child")) add("Childcare experience")
            if (care) add("First aid basics")
        }.distinct().take(5)

        return TaskAnalysis(category, confidence, difficulty, volunteers, skills, signals)
    }

    /** Keyword must start at a word boundary ("app" matches "application", not "happy"). */
    private fun String.hasKeyword(keyword: String): Boolean =
        Regex("(?<![\\p{L}\\d])" + Regex.escape(keyword)).containsMatchIn(this)

    /** Whole-word match ("car" matches "car", not "carry"). */
    private fun String.hasWord(word: String): Boolean =
        Regex("(?<![\\p{L}\\d])" + Regex.escape(word) + "(?![\\p{L}\\d])").containsMatchIn(this)

    private fun baseEffort(c: TaskCategory) = when (c) {
        TaskCategory.MOVING -> 2
        TaskCategory.ECO, TaskCategory.EVENTS, TaskCategory.ELDERLY_CARE -> 1
        else -> 0
    }

    private fun baseVolunteers(c: TaskCategory) = when (c) {
        TaskCategory.MOVING -> 3
        TaskCategory.ECO -> 8
        TaskCategory.EVENTS -> 5
        TaskCategory.FOOD -> 4
        TaskCategory.ANIMALS -> 2
        TaskCategory.ELDERLY_CARE, TaskCategory.EDUCATION, TaskCategory.TECH_HELP -> 1
    }

    private fun baseSkills(c: TaskCategory) = when (c) {
        TaskCategory.ELDERLY_CARE -> listOf("Patience", "Kindness")
        TaskCategory.MOVING -> listOf("Teamwork", "Physical fitness")
        TaskCategory.ECO -> listOf("Teamwork", "Waste sorting")
        TaskCategory.EDUCATION -> listOf("Teaching", "Clear explanations")
        TaskCategory.FOOD -> listOf("Organisation", "Food hygiene")
        TaskCategory.ANIMALS -> listOf("Love for animals", "Calm attitude")
        TaskCategory.EVENTS -> listOf("Event setup", "Communication")
        TaskCategory.TECH_HELP -> listOf("Smartphones", "Patience")
    }
}
