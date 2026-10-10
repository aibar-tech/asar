package com.asar.app.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.asar.app.ai.TaskAnalyzer
import com.asar.app.data.model.ApplicationStatus
import com.asar.app.data.model.HelpTask
import com.asar.app.data.model.Role
import com.asar.app.data.model.TaskStatus
import com.asar.app.ui.AsarViewModel
import com.asar.app.ui.components.AnalysisCard
import com.asar.app.ui.components.ApplicantRow
import com.asar.app.ui.components.ApplicationStatusPill
import com.asar.app.ui.components.AsarCard
import com.asar.app.ui.components.AsarTopBar
import com.asar.app.ui.components.AvatarStack
import com.asar.app.ui.components.CategoryIconTile
import com.asar.app.ui.components.DifficultyPill
import com.asar.app.ui.components.EmptyState
import com.asar.app.ui.components.InfoRow
import com.asar.app.ui.components.PersonLine
import com.asar.app.ui.components.PrimaryButton
import com.asar.app.ui.components.ReviewCard
import com.asar.app.ui.components.SecondaryButton
import com.asar.app.ui.components.SlotsProgress
import com.asar.app.ui.components.StatusPill
import com.asar.app.ui.components.Tag
import com.asar.app.ui.components.TonalButton
import com.asar.app.ui.components.style
import com.asar.app.ui.theme.AsarBackground
import com.asar.app.ui.theme.AsarInfo
import com.asar.app.ui.theme.AsarInfoSoft
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarOutline
import com.asar.app.ui.theme.AsarSurface
import com.asar.app.ui.theme.Spacing
import com.asar.app.ui.util.agoLabel
import com.asar.app.ui.util.friendlyDateTime
import com.asar.app.ui.util.km

@Composable
fun TaskDetailsScreen(
    vm: AsarViewModel,
    taskId: String,
    openApplyOnStart: Boolean,
    onBack: () -> Unit,
) {
    val task = vm.task(taskId)
    if (task == null) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            AsarTopBar("Request", onBack)
            EmptyState(Icons.Rounded.SearchOff, "Request not found", "It may have been removed.")
        }
        return
    }

    val me = vm.currentUser
    val role = vm.role
    val isMine = task.requester.id == me.id
    val myApplication = task.applicationOf(me.id)
    val canApply = myApplication == null && task.status == TaskStatus.OPEN && role == Role.VOLUNTEER

    var showApply by rememberSaveable(taskId) { mutableStateOf(openApplyOnStart && canApply) }
    var showComplete by rememberSaveable(taskId) { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(AsarBackground)) {
        AsarTopBar(
            title = "Request details",
            onBack = onBack,
            modifier = Modifier.statusBarsPadding(),
        )
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screen)
                .padding(bottom = Spacing.xl),
        ) {
            TaskHeader(task)
            Spacer(Modifier.height(Spacing.lg))
            KeyFacts(task)

            if (isMine && role == Role.VOLUNTEER && !task.isCompleted) {
                Spacer(Modifier.height(Spacing.md))
                DemoHint("This is your own request. For the demo you can still apply to it in volunteer mode, then switch back to requester mode to accept yourself.")
            }

            Spacer(Modifier.height(Spacing.xl))
            Text("About this request", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(Spacing.xs))
            Text(task.description, style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(Spacing.xl))
            Skills(task.skills)

            Spacer(Modifier.height(Spacing.xl))
            Text("Requested by", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(Spacing.sm))
            AsarCard(Modifier.fillMaxWidth(), elevated = false) {
                PersonLine(task.requester)
            }

            val analysis = remember(task.title, task.description) { TaskAnalyzer.analyze(task.title, task.description) }
            if (analysis != null) {
                Spacer(Modifier.height(Spacing.xl))
                AnalysisCard(analysis, title = "ASAR Assist check", showSignals = false)
            }

            if (myApplication != null) {
                Spacer(Modifier.height(Spacing.xl))
                Text("Your application", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(Spacing.sm))
                ApplicantRow(myApplication, canAccept = false, onAccept = {}, onReject = {}, readOnly = true)
            }

            if (isMine) {
                Spacer(Modifier.height(Spacing.xl))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Applicants", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                    Text(
                        "${task.pendingCount} pending · ${task.acceptedCount}/${task.volunteersNeeded} accepted",
                        style = MaterialTheme.typography.bodySmall, color = AsarInkMuted,
                    )
                }
                Spacer(Modifier.height(Spacing.sm))
                if (task.applications.isEmpty()) {
                    AsarCard(Modifier.fillMaxWidth(), elevated = false) {
                        EmptyState(
                            Icons.Rounded.HourglassTop,
                            "Waiting for volunteers",
                            "Neighbours usually respond within the hour. Try applying yourself in volunteer mode to test the flow.",
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        task.applications.forEach { app ->
                            ApplicantRow(
                                application = app,
                                canAccept = task.spotsLeft > 0,
                                onAccept = { vm.acceptApplication(task.id, app.id) },
                                onReject = { vm.rejectApplication(task.id, app.id) },
                                readOnly = role != Role.REQUESTER || task.isCompleted,
                            )
                        }
                    }
                    if (role != Role.REQUESTER && task.pendingCount > 0 && !task.isCompleted) {
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            "Switch to requester mode to accept or decline applicants.",
                            style = MaterialTheme.typography.bodySmall, color = AsarInkMuted,
                        )
                    }
                }
            }

            if (task.reviews.isNotEmpty()) {
                Spacer(Modifier.height(Spacing.xl))
                Text("Reviews", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(Spacing.sm))
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    task.reviews.forEach { ReviewCard(it) }
                }
            }
        }

        BottomActions(
            task = task,
            isMine = isMine,
            role = role,
            myStatus = myApplication?.status,
            onApply = { showApply = true },
            onWithdraw = { vm.withdraw(task.id) },
            onComplete = { showComplete = true },
            onSwitchToVolunteer = { vm.switchRole(Role.VOLUNTEER) },
            onSwitchToRequester = { vm.switchRole(Role.REQUESTER) },
        )
    }

    if (showApply) {
        ApplySheet(
            task = task,
            isOwnRequest = isMine,
            onDismiss = { showApply = false },
            onSend = { note ->
                vm.apply(task.id, note)
                showApply = false
            },
        )
    }
    if (showComplete) {
        CompleteSheet(
            task = task,
            vm = vm,
            onDismiss = { showComplete = false },
            onDone = { showComplete = false },
        )
    }
}

@Composable
private fun TaskHeader(task: HelpTask) {
    Column {
        Spacer(Modifier.height(Spacing.xs))
        Row(verticalAlignment = Alignment.CenterVertically) {
            CategoryIconTile(task.category, size = 52.dp)
            Spacer(Modifier.width(Spacing.sm))
            Column(Modifier.weight(1f)) {
                Text(task.category.label, style = MaterialTheme.typography.labelLarge, color = task.category.style.foreground)
                Text("Posted ${task.createdAt.agoLabel()}", style = MaterialTheme.typography.bodySmall, color = AsarInkMuted)
            }
            StatusPill(task.status)
        }
        Spacer(Modifier.height(Spacing.md))
        Text(task.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(Spacing.xs))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            DifficultyPill(task.difficulty)
            Tag(task.durationLabel)
        }
    }
}

@Composable
private fun KeyFacts(task: HelpTask) {
    AsarCard(Modifier.fillMaxWidth()) {
        InfoRow(Icons.Rounded.Place, "Area", "${task.area} · ${task.distanceKm.km()} away")
        Spacer(Modifier.height(Spacing.sm))
        InfoRow(Icons.Rounded.CalendarMonth, "When", "${task.dateTime.friendlyDateTime()} · ${task.durationLabel}")
        Spacer(Modifier.height(Spacing.sm))
        InfoRow(
            Icons.Rounded.Group, "Volunteer slots",
            "${task.acceptedCount} of ${task.volunteersNeeded} confirmed" +
                if (task.status == TaskStatus.OPEN) " · ${task.spotsLeft} open" else "",
        )
        Spacer(Modifier.height(Spacing.sm))
        SlotsProgress(task.acceptedCount, task.volunteersNeeded, height = 8.dp)
        val team = task.applications.filter { it.status == ApplicationStatus.ACCEPTED }.map { it.volunteer.name }
        if (team.isNotEmpty()) {
            Spacer(Modifier.height(Spacing.sm))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarStack(team, size = 28.dp)
                Spacer(Modifier.width(Spacing.xs))
                Text(
                    team.joinToString { it.substringBefore(' ') },
                    style = MaterialTheme.typography.bodySmall, color = AsarInkMuted, maxLines = 1,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Skills(skills: List<String>) {
    Text("Helpful skills", style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(Spacing.sm))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        skills.forEach { Tag(it) }
    }
}

@Composable
internal fun DemoHint(text: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AsarInfoSoft)
            .padding(Spacing.sm),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(Icons.Rounded.Info, null, tint = AsarInfo, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(Spacing.xs))
        Text(text, style = MaterialTheme.typography.bodySmall, color = AsarInfo)
    }
}

@Composable
private fun BottomActions(
    task: HelpTask,
    isMine: Boolean,
    role: Role,
    myStatus: ApplicationStatus?,
    onApply: () -> Unit,
    onWithdraw: () -> Unit,
    onComplete: () -> Unit,
    onSwitchToVolunteer: () -> Unit,
    onSwitchToRequester: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().background(AsarSurface)) {
        HorizontalDivider(color = AsarOutline)
        Column(
            Modifier
                .navigationBarsPadding()
                .padding(horizontal = Spacing.screen, vertical = Spacing.sm)
        ) {
            val full = Modifier.fillMaxWidth()
            when {
                task.isCompleted ->
                    TonalButton("Completed — thank you!", {}, full, icon = Icons.Rounded.TaskAlt, enabled = false, height = 56.dp)

                isMine && role == Role.REQUESTER -> {
                    if (task.acceptedCount > 0) {
                        PrimaryButton("Mark as completed", onComplete, full, icon = Icons.Rounded.CheckCircle)
                    } else {
                        PrimaryButton(
                            if (task.pendingCount > 0) "Accept a volunteer to continue" else "Waiting for volunteers",
                            {}, full, enabled = false,
                        )
                    }
                }

                role == Role.REQUESTER && myStatus == null ->
                    SecondaryButton("Switch to volunteer mode to apply", onSwitchToVolunteer, full, icon = Icons.Rounded.VolunteerActivism)

                myStatus == ApplicationStatus.PENDING -> Row(verticalAlignment = Alignment.CenterVertically) {
                    ApplicationStatusPill(ApplicationStatus.PENDING)
                    Spacer(Modifier.width(Spacing.sm))
                    SecondaryButton("Withdraw application", onWithdraw, Modifier.weight(1f))
                }

                myStatus == ApplicationStatus.ACCEPTED -> {
                    if (isMine) {
                        PrimaryButton("Switch to requester to complete", onSwitchToRequester, full)
                    } else {
                        TonalButton("You're on the team", {}, full, icon = Icons.Rounded.CheckCircle, enabled = false, height = 56.dp)
                    }
                }

                myStatus == ApplicationStatus.REJECTED ->
                    SecondaryButton("Not selected this time", {}, full, enabled = false)

                task.status != TaskStatus.OPEN ->
                    SecondaryButton("Team is complete", {}, full, enabled = false)

                else -> PrimaryButton("Apply to help", onApply, full, icon = Icons.Rounded.VolunteerActivism)
            }
        }
    }
}
