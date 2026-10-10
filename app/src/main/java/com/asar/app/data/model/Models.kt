package com.asar.app.data.model

import java.time.LocalDateTime

enum class Role { VOLUNTEER, REQUESTER }

enum class TaskCategory(val label: String) {
    ELDERLY_CARE("Elderly care"),
    MOVING("Moving & lifting"),
    ECO("Eco & cleanup"),
    EDUCATION("Education"),
    FOOD("Food aid"),
    ANIMALS("Animal care"),
    EVENTS("Community events"),
    TECH_HELP("Digital help"),
}

enum class Difficulty(val label: String) {
    EASY("Easy"),
    MODERATE("Moderate"),
    DEMANDING("Demanding"),
}

enum class TaskStatus(val label: String) {
    OPEN("Open"),
    IN_PROGRESS("Team ready"),
    COMPLETED("Completed"),
}

enum class ApplicationStatus(val label: String) {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Declined"),
}

data class Person(
    val id: String,
    val name: String,
    val rating: Double,
    val completedTasks: Int,
    val isVerified: Boolean = false,
    val memberSince: Int = 2024,
) {
    val firstName: String get() = name.substringBefore(' ')
}

data class TaskApplication(
    val id: String,
    val volunteer: Person,
    val message: String,
    val status: ApplicationStatus,
    val appliedAt: LocalDateTime,
)

data class Review(
    val volunteerId: String,
    val volunteerName: String,
    val authorName: String,
    val rating: Int,
    val text: String,
    val verification: VerificationResult,
)

data class HelpTask(
    val id: String,
    val title: String,
    val description: String,
    val category: TaskCategory,
    val area: String,
    val distanceKm: Double,
    val dateTime: LocalDateTime,
    val durationLabel: String,
    val volunteersNeeded: Int,
    val requester: Person,
    val skills: List<String>,
    val difficulty: Difficulty,
    val applications: List<TaskApplication> = emptyList(),
    val isCompleted: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    val acceptedCount: Int get() = applications.count { it.status == ApplicationStatus.ACCEPTED }
    val pendingCount: Int get() = applications.count { it.status == ApplicationStatus.PENDING }
    val spotsLeft: Int get() = (volunteersNeeded - acceptedCount).coerceAtLeast(0)

    val status: TaskStatus
        get() = when {
            isCompleted -> TaskStatus.COMPLETED
            spotsLeft == 0 -> TaskStatus.IN_PROGRESS
            else -> TaskStatus.OPEN
        }

    fun applicationOf(personId: String): TaskApplication? =
        applications.firstOrNull { it.volunteer.id == personId }
}

/** Past participation shown on the profile. */
data class HistoryEntry(
    val title: String,
    val category: TaskCategory,
    val dateLabel: String,
    val hours: Int,
    val rating: Int?,
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val earned: Boolean,
    val verifiedBy: String? = null,
)

// ---- Simulated AI results ----

data class TaskAnalysis(
    val category: TaskCategory,
    val confidence: Int,
    val difficulty: Difficulty,
    val suggestedVolunteers: Int,
    val skills: List<String>,
    val signals: List<String>,
)

enum class VerificationLevel(val label: String) {
    LOOKS_GENUINE("Looks consistent"),
    REVIEW_SUGGESTED("Light review suggested"),
    NEEDS_VERIFICATION("Needs additional verification"),
}

data class VerificationFlag(val title: String, val detail: String)

data class VerificationResult(
    val level: VerificationLevel,
    val plausibility: Int,
    val flags: List<VerificationFlag>,
)
