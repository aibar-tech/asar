package com.asar.app.data

import com.asar.app.data.model.Achievement
import com.asar.app.data.model.ApplicationStatus
import com.asar.app.data.model.Difficulty
import com.asar.app.data.model.HelpTask
import com.asar.app.data.model.HistoryEntry
import com.asar.app.data.model.Person
import com.asar.app.data.model.TaskApplication
import com.asar.app.data.model.TaskCategory
import com.asar.app.data.model.VerificationLevel
import com.asar.app.data.model.VerificationResult
import com.asar.app.data.model.Review
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** Realistic demo content set in Almaty, Kazakhstan. Nothing here is real user data. */
object MockData {

    const val CURRENT_USER_ID = "me"
    const val CITY = "Almaty"

    val areas = listOf(
        "Almaly district",
        "Bostandyk district",
        "Medeu district",
        "Auezov district",
        "Zhetysu district",
        "Turksib district",
        "Nauryzbay district",
        "Alatau district",
    )

    // Requesters
    private val saule = Person("p1", "Saule Nurlanovna", 4.9, 3, isVerified = true, memberSince = 2023)
    private val dauren = Person("p2", "Dauren Akhmetov", 4.7, 5, isVerified = true, memberSince = 2024)
    private val aliya = Person("p3", "Aliya Seitkali", 4.8, 9, isVerified = true, memberSince = 2022)
    private val gulnara = Person("p4", "Gulnara Omarova", 5.0, 2, memberSince = 2025)
    private val timur = Person("p5", "Timur Zhaksylykov", 4.6, 4, isVerified = true, memberSince = 2024)
    private val ecoAlmaty = Person("p6", "Taza Almaty Initiative", 4.9, 41, isVerified = true, memberSince = 2021)
    private val foodBank = Person("p7", "Meirim Food Bank", 4.9, 63, isVerified = true, memberSince = 2021)

    // Volunteers who apply to requests
    val dana = Person("v1", "Dana Serikova", 4.9, 18, isVerified = true, memberSince = 2023)
    val arman = Person("v2", "Arman Tulegenov", 4.7, 11, isVerified = true, memberSince = 2024)
    val kamila = Person("v3", "Kamila Ospanova", 4.8, 6, memberSince = 2025)
    val nurlan = Person("v4", "Nurlan Bekov", 4.5, 3, memberSince = 2025)
    val madina = Person("v5", "Madina Zhumabaeva", 5.0, 24, isVerified = true, memberSince = 2022)

    private fun at(daysFromNow: Long, hour: Int, minute: Int = 0): LocalDateTime =
        LocalDateTime.of(LocalDate.now().plusDays(daysFromNow), LocalTime.of(hour, minute))

    private fun ago(hours: Long): LocalDateTime = LocalDateTime.now().minusHours(hours)

    fun initialTasks(me: Person): List<HelpTask> = listOf(
        HelpTask(
            id = "t-me-1",
            title = "Help my grandmother with spring cleaning",
            description = "My grandmother (82) lives alone on the 4th floor in Almaly. " +
                "We need two kind people to help wash windows, take old boxes down to the " +
                "recycling point and rearrange a heavy wardrobe. Tea and baursaks guaranteed!",
            category = TaskCategory.ELDERLY_CARE,
            area = "Almaly district",
            distanceKm = 1.2,
            dateTime = at(2, 11),
            durationLabel = "~3 hours",
            volunteersNeeded = 2,
            requester = me,
            skills = listOf("Patience", "Basic cleaning", "Light lifting"),
            difficulty = Difficulty.MODERATE,
            applications = listOf(
                TaskApplication(
                    "a-1", dana,
                    "Sälem! I live nearby and helped my own ateje with the same last month. Happy to come.",
                    ApplicationStatus.PENDING, ago(3)
                ),
                TaskApplication(
                    "a-2", arman,
                    "I can carry the wardrobe and boxes. I have a free Saturday morning.",
                    ApplicationStatus.PENDING, ago(1)
                ),
            ),
            createdAt = ago(20),
        ),
        HelpTask(
            id = "t-1",
            title = "Weekly groceries for a veteran",
            description = "Bring groceries from the Magnum on Abay Ave to an elderly veteran. " +
                "The list is short and paid in advance — you just need to pick up and deliver. " +
                "He loves a short chat about chess.",
            category = TaskCategory.ELDERLY_CARE,
            area = "Bostandyk district",
            distanceKm = 0.8,
            dateTime = at(0, 18),
            durationLabel = "~1 hour",
            volunteersNeeded = 1,
            requester = saule,
            skills = listOf("Reliability", "Kindness"),
            difficulty = Difficulty.EASY,
            createdAt = ago(5),
        ),
        HelpTask(
            id = "t-2",
            title = "Sairan lake shore cleanup",
            description = "Join our Saturday cleanup along the Sairan lake promenade. Gloves, bags and " +
                "water are provided. We will sort plastic for recycling and finish with a group photo.",
            category = TaskCategory.ECO,
            area = "Auezov district",
            distanceKm = 3.4,
            dateTime = at(3, 9),
            durationLabel = "~4 hours",
            volunteersNeeded = 12,
            requester = ecoAlmaty,
            skills = listOf("Teamwork", "Waste sorting"),
            difficulty = Difficulty.MODERATE,
            applications = listOf(
                TaskApplication("a-3", madina, "Count me in!", ApplicationStatus.ACCEPTED, ago(30)),
                TaskApplication("a-4", kamila, "Coming with a friend.", ApplicationStatus.ACCEPTED, ago(26)),
                TaskApplication("a-5", nurlan, "I'll be there.", ApplicationStatus.ACCEPTED, ago(12)),
                TaskApplication(
                    "a-me-1", me, "Happy to help with sorting plastic.",
                    ApplicationStatus.ACCEPTED, ago(8)
                ),
            ),
            createdAt = ago(48),
        ),
        HelpTask(
            id = "t-3",
            title = "Move furniture to a new apartment",
            description = "Young family moving from a 2nd floor flat to a new building nearby. " +
                "We have a truck but need help carrying a sofa, a fridge and about 20 boxes. " +
                "The new building has an elevator.",
            category = TaskCategory.MOVING,
            area = "Bostandyk district",
            distanceKm = 2.1,
            dateTime = at(1, 10),
            durationLabel = "~3 hours",
            volunteersNeeded = 3,
            requester = dauren,
            skills = listOf("Heavy lifting", "Teamwork"),
            difficulty = Difficulty.DEMANDING,
            applications = listOf(
                TaskApplication("a-6", arman, "Strong hands ready.", ApplicationStatus.ACCEPTED, ago(10)),
            ),
            createdAt = ago(14),
        ),
        HelpTask(
            id = "t-4",
            title = "Math tutoring for a 7th grader",
            description = "Looking for someone patient to help my son catch up on fractions and basic " +
                "algebra before exams. Two sessions per week at the local library on Dostyk Ave.",
            category = TaskCategory.EDUCATION,
            area = "Medeu district",
            distanceKm = 2.9,
            dateTime = at(1, 16, 30),
            durationLabel = "1.5 hours",
            volunteersNeeded = 1,
            requester = aliya,
            skills = listOf("Mathematics", "Teaching", "Kazakh or Russian"),
            difficulty = Difficulty.MODERATE,
            applications = listOf(
                TaskApplication(
                    "a-me-2", me, "I studied maths at university and love explaining it.",
                    ApplicationStatus.PENDING, ago(2)
                ),
            ),
            createdAt = ago(9),
        ),
        HelpTask(
            id = "t-5",
            title = "Sort donations at the food bank",
            description = "Help us sort and pack food parcels for 120 families before the weekend " +
                "distribution. Training takes 10 minutes on site.",
            category = TaskCategory.FOOD,
            area = "Turksib district",
            distanceKm = 6.5,
            dateTime = at(4, 14),
            durationLabel = "~3 hours",
            volunteersNeeded = 6,
            requester = foodBank,
            skills = listOf("Organisation", "Teamwork"),
            difficulty = Difficulty.EASY,
            applications = listOf(
                TaskApplication("a-7", dana, "See you there!", ApplicationStatus.ACCEPTED, ago(40)),
                TaskApplication("a-8", kamila, "Available after 14:00.", ApplicationStatus.PENDING, ago(4)),
            ),
            createdAt = ago(60),
        ),
        HelpTask(
            id = "t-6",
            title = "Walk dogs at the city shelter",
            description = "Our shelter dogs need walks and socialisation. Perfect for animal lovers — " +
                "we'll match you with calm dogs first. Please wear comfortable shoes.",
            category = TaskCategory.ANIMALS,
            area = "Nauryzbay district",
            distanceKm = 9.8,
            dateTime = at(5, 11),
            durationLabel = "~2 hours",
            volunteersNeeded = 4,
            requester = gulnara,
            skills = listOf("Love for animals", "Calm attitude"),
            difficulty = Difficulty.EASY,
            createdAt = ago(22),
        ),
        HelpTask(
            id = "t-7",
            title = "Set up a Nauryz courtyard celebration",
            description = "Our courtyard is hosting a small Nauryz gathering for neighbours. We need help " +
                "setting up tables, a yurt-style tent and decorations, then cleaning up afterwards.",
            category = TaskCategory.EVENTS,
            area = "Zhetysu district",
            distanceKm = 4.2,
            dateTime = at(6, 12),
            durationLabel = "~5 hours",
            volunteersNeeded = 5,
            requester = timur,
            skills = listOf("Event setup", "Teamwork", "Creativity"),
            difficulty = Difficulty.MODERATE,
            createdAt = ago(30),
        ),
        HelpTask(
            id = "t-8",
            title = "Teach seniors to use a smartphone",
            description = "A small group of pensioners at the community centre want to learn video calls, " +
                "messengers and online banking safety. Slides are ready — you just guide them.",
            category = TaskCategory.TECH_HELP,
            area = "Alatau district",
            distanceKm = 7.1,
            dateTime = at(2, 15),
            durationLabel = "2 hours",
            volunteersNeeded = 2,
            requester = saule,
            skills = listOf("Smartphones", "Patience", "Clear explanations"),
            difficulty = Difficulty.EASY,
            createdAt = ago(16),
        ),
    )

    /** Seed a completed task so the profile has a review on day one. */
    fun completedTasks(me: Person): List<HelpTask> = listOf(
        HelpTask(
            id = "t-done-1",
            title = "Plant trees in Baum Grove",
            description = "Spring tree planting with neighbours in the historic Baum Grove.",
            category = TaskCategory.ECO,
            area = "Turksib district",
            distanceKm = 5.0,
            dateTime = at(-12, 10),
            durationLabel = "4 hours",
            volunteersNeeded = 8,
            requester = ecoAlmaty,
            skills = listOf("Gardening", "Teamwork"),
            difficulty = Difficulty.MODERATE,
            applications = listOf(
                TaskApplication("a-d1", me, "Ready to plant!", ApplicationStatus.ACCEPTED, ago(400)),
            ),
            isCompleted = true,
            reviews = listOf(
                Review(
                    volunteerId = me.id,
                    volunteerName = me.name,
                    authorName = ecoAlmaty.name,
                    rating = 5,
                    text = "Planted 14 saplings with the team and helped newcomers. Very reliable!",
                    verification = VerificationResult(VerificationLevel.LOOKS_GENUINE, 94, emptyList()),
                )
            ),
            createdAt = ago(500),
        ),
    )

    val history = listOf(
        HistoryEntry("Plant trees in Baum Grove", TaskCategory.ECO, "12 days ago", 4, 5),
        HistoryEntry("Library book sorting", TaskCategory.EDUCATION, "September", 3, 5),
        HistoryEntry("Groceries for Apa Raushan", TaskCategory.ELDERLY_CARE, "September", 1, 5),
        HistoryEntry("Food parcels for 80 families", TaskCategory.FOOD, "August", 4, 4),
        HistoryEntry("Kok-Tobe trail cleanup", TaskCategory.ECO, "August", 5, 5),
        HistoryEntry("Laptop setup for a school", TaskCategory.TECH_HELP, "July", 2, 5),
    )

    /** Ratings received before this session (demo baseline). */
    val baselineRatings = listOf(5, 5, 4, 5, 5, 5, 4, 5, 5, 5, 5)

    val baselineHours = history.sumOf { it.hours }

    fun achievements(completedInSession: Int, hasEcoTask: Boolean): List<Achievement> = listOf(
        Achievement(
            "first", "First helping hand",
            "Completed your first ASAR task", earned = true, verifiedBy = "Taza Almaty Initiative",
        ),
        Achievement(
            "eco", "Green Almaty",
            "Joined 2 eco initiatives", earned = true, verifiedBy = "Taza Almaty Initiative",
        ),
        Achievement(
            "elder", "Respect for elders",
            "Helped an elderly neighbour", earned = true, verifiedBy = "Saule Nurlanovna",
        ),
        Achievement(
            "session", "Asar in action",
            "Complete a task during this demo", earned = completedInSession > 0,
            verifiedBy = if (completedInSession > 0) "ASAR demo" else null,
        ),
        Achievement(
            "ten", "Ten good deeds",
            "Complete 10 tasks", earned = history.size + completedInSession >= 10,
        ),
        Achievement(
            "eco3", "Eco champion",
            "Join 3 eco initiatives", earned = hasEcoTask,
        ),
    )
}
