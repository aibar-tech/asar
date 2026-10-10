package com.asar.app.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.asar.app.ai.ReviewVerifier
import com.asar.app.data.DemoAuth
import com.asar.app.data.MockData
import com.asar.app.data.TaskRepository
import com.asar.app.data.model.Achievement
import com.asar.app.data.model.ApplicationStatus
import com.asar.app.data.model.Difficulty
import com.asar.app.data.model.HelpTask
import com.asar.app.data.model.HistoryEntry
import com.asar.app.data.model.Person
import com.asar.app.data.model.Review
import com.asar.app.data.model.Role
import com.asar.app.data.model.TaskApplication
import com.asar.app.data.model.TaskCategory
import com.asar.app.data.model.TaskStatus
import com.asar.app.data.model.VerificationResult
import java.time.LocalDateTime
import java.util.UUID

data class Session(val name: String, val email: String, val isDemo: Boolean)

data class TaskDraft(
    val title: String,
    val description: String,
    val category: TaskCategory,
    val area: String,
    val dateTime: LocalDateTime,
    val volunteersNeeded: Int,
    val skills: List<String>,
    val difficulty: Difficulty,
)

/** A rating the requester gives one accepted volunteer when completing a task. */
data class VolunteerFeedback(val volunteer: Person, val rating: Int, val text: String)

/** One-shot message shown in a snackbar. [id] makes repeated identical messages distinct. */
data class UiMessage(val text: String, val id: Long = System.nanoTime())

/**
 * Single source of truth for the prototype. All state is in memory and resets when
 * the process dies — exactly what a no-backend hackathon demo needs.
 */
class AsarViewModel : ViewModel() {

    private val auth = DemoAuth()

    var session by mutableStateOf<Session?>(null)
        private set

    var role by mutableStateOf(Role.VOLUNTEER)
        private set

    var message by mutableStateOf<UiMessage?>(null)
        private set

    private var me = Person(MockData.CURRENT_USER_ID, "Aigerim Bekova", 4.9, 7, isVerified = true, memberSince = 2024)
    private val repository = TaskRepository(me)

    private val sessionRatings = mutableStateListOf<Int>()
    private val sessionHistory = mutableStateListOf<HistoryEntry>()
    private var sessionHours by mutableIntStateOf(0)

    val currentUser: Person get() = me

    val tasks: List<HelpTask> get() = repository.tasks

    /** Requests visible on Home: everything not completed, newest first. */
    val feed: List<HelpTask> by derivedStateOf {
        repository.tasks.filter { !it.isCompleted }.sortedByDescending { it.createdAt }
    }

    val myRequests: List<HelpTask> by derivedStateOf {
        repository.tasks.filter { it.requester.id == me.id }
            .sortedWith(compareBy<HelpTask> { it.isCompleted }.thenByDescending { it.createdAt })
    }

    val myApplications: List<HelpTask> by derivedStateOf {
        repository.tasks.filter { it.applicationOf(me.id) != null }
            .sortedWith(compareBy<HelpTask> { it.isCompleted }.thenBy { it.dateTime })
    }

    // ---------- Profile metrics (demo baseline + this session) ----------

    val ratings: List<Int> get() = MockData.baselineRatings + sessionRatings
    val averageRating: Double get() = ratings.average()
    val completedCount: Int get() = MockData.history.size + sessionHistory.size
    val hoursVolunteered: Int get() = MockData.baselineHours + sessionHours
    val history: List<HistoryEntry> get() = sessionHistory.reversed() + MockData.history

    val reviewsReceived: List<Pair<HelpTask, Review>>
        get() = repository.tasks.flatMap { task ->
            task.reviews.filter { it.volunteerId == me.id }.map { task to it }
        }

    val achievements: List<Achievement>
        get() = MockData.achievements(
            completedInSession = sessionHistory.size,
            hasEcoTask = sessionHistory.any { it.category == TaskCategory.ECO },
        )

    // ---------- Auth (demo only) ----------

    fun register(name: String, email: String, password: String): String? =
        auth.register(name, email, password).fold(
            onSuccess = { startSession(it, isDemo = false); null },
            onFailure = { it.message },
        )

    fun login(email: String, password: String): String? =
        auth.login(email, password).fold(
            onSuccess = { startSession(it, isDemo = it.email == DemoAuth.DEMO_EMAIL); null },
            onFailure = { it.message },
        )

    fun enterDemo() = startSession(auth.demoAccount(), isDemo = true)

    fun logout() {
        session = null
        role = Role.VOLUNTEER
    }

    private fun startSession(account: DemoAuth.Account, isDemo: Boolean) {
        me = me.copy(name = account.name)
        repository.replaceCurrentUser(me)
        session = Session(account.name, account.email, isDemo)
        role = Role.VOLUNTEER
    }

    // ---------- Roles ----------

    fun switchRole(newRole: Role) {
        if (newRole == role) return
        role = newRole
        notify(
            if (newRole == Role.VOLUNTEER) "Volunteer mode — find someone to help"
            else "Requester mode — manage your requests"
        )
    }

    // ---------- Requester actions ----------

    fun createTask(draft: TaskDraft): HelpTask {
        val task = HelpTask(
            id = "t-" + UUID.randomUUID().toString().take(8),
            title = draft.title.trim(),
            description = draft.description.trim(),
            category = draft.category,
            area = draft.area,
            distanceKm = 0.4,
            dateTime = draft.dateTime,
            durationLabel = when (draft.difficulty) {
                Difficulty.EASY -> "~1 hour"
                Difficulty.MODERATE -> "~2–3 hours"
                Difficulty.DEMANDING -> "~4 hours"
            },
            volunteersNeeded = draft.volunteersNeeded,
            requester = me,
            skills = draft.skills,
            difficulty = draft.difficulty,
            createdAt = LocalDateTime.now(),
        )
        repository.add(task)
        role = Role.REQUESTER
        notify("Request published — it's live on Home")
        return task
    }

    fun acceptApplication(taskId: String, applicationId: String) {
        var name = ""
        repository.update(taskId) { task ->
            if (task.spotsLeft == 0) return@update task
            task.copy(applications = task.applications.map {
                if (it.id == applicationId) it.copy(status = ApplicationStatus.ACCEPTED).also { a -> name = a.volunteer.firstName } else it
            })
        }
        if (name.isNotEmpty()) notify("$name is on your team")
    }

    fun rejectApplication(taskId: String, applicationId: String) {
        repository.update(taskId) { task ->
            task.copy(applications = task.applications.map {
                if (it.id == applicationId) it.copy(status = ApplicationStatus.REJECTED) else it
            })
        }
        notify("Application declined politely")
    }

    /**
     * Marks a task completed and stores one review per accepted volunteer.
     * Each review passes through the simulated review verifier.
     */
    fun completeTask(taskId: String, feedback: List<VolunteerFeedback>): List<Review> {
        val task = repository.find(taskId) ?: return emptyList()
        val reviews = feedback.map { fb ->
            Review(
                volunteerId = fb.volunteer.id,
                volunteerName = fb.volunteer.name,
                authorName = task.requester.name,
                rating = fb.rating,
                text = fb.text.trim(),
                verification = verify(fb.text, fb.rating, task.acceptedCount),
            )
        }
        repository.update(taskId) { it.copy(isCompleted = true, reviews = it.reviews + reviews) }

        // If the local user was on the team (demo: playing both roles), credit their profile.
        reviews.firstOrNull { it.volunteerId == me.id }?.let { review ->
            sessionRatings += review.rating
            val hours = when (task.difficulty) {
                Difficulty.EASY -> 1
                Difficulty.MODERATE -> 3
                Difficulty.DEMANDING -> 4
            }
            sessionHours += hours
            sessionHistory += HistoryEntry(task.title, task.category, "Today", hours, review.rating)
        }
        notify("Task completed — thank you for keeping asar alive")
        return reviews
    }

    // ---------- Volunteer actions ----------

    fun apply(taskId: String, note: String) {
        val task = repository.find(taskId) ?: return
        if (task.applicationOf(me.id) != null || task.status != TaskStatus.OPEN) return
        val application = TaskApplication(
            id = "a-" + UUID.randomUUID().toString().take(8),
            volunteer = me,
            message = note.trim().ifEmpty { "I'd be glad to help!" },
            status = ApplicationStatus.PENDING,
            appliedAt = LocalDateTime.now(),
        )
        repository.update(taskId) { it.copy(applications = it.applications + application) }
        notify("Application sent to ${task.requester.firstName}")
    }

    fun withdraw(taskId: String) {
        repository.update(taskId) { task ->
            task.copy(applications = task.applications.filterNot {
                it.volunteer.id == me.id && it.status == ApplicationStatus.PENDING
            })
        }
        notify("Application withdrawn")
    }

    // ---------- Misc ----------

    fun task(id: String): HelpTask? = repository.find(id)

    fun verify(text: String, rating: Int?, volunteerCount: Int?): VerificationResult =
        ReviewVerifier.verify(text, rating, volunteerCount)

    fun notify(text: String) {
        message = UiMessage(text)
    }

    fun consumeMessage(msg: UiMessage) {
        if (message?.id == msg.id) message = null
    }
}
