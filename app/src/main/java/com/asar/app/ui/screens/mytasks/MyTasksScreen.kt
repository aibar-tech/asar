package com.asar.app.ui.screens.mytasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.asar.app.data.model.ApplicationStatus
import com.asar.app.data.model.HelpTask
import com.asar.app.data.model.Role
import com.asar.app.ui.AsarViewModel
import com.asar.app.ui.components.ApplicantRow
import com.asar.app.ui.components.ApplicationStatusPill
import com.asar.app.ui.components.AsarCard
import com.asar.app.ui.components.CategoryIconTile
import com.asar.app.ui.components.EmptyState
import com.asar.app.ui.components.PrimaryButton
import com.asar.app.ui.components.Segment
import com.asar.app.ui.components.SegmentedControl
import com.asar.app.ui.components.SlotsProgress
import com.asar.app.ui.components.StatusPill
import com.asar.app.ui.components.TonalButton
import com.asar.app.ui.theme.AsarBackground
import com.asar.app.ui.theme.AsarInkFaint
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarPrimary
import com.asar.app.ui.theme.AsarSurface
import com.asar.app.ui.theme.AsarOutline
import com.asar.app.ui.theme.Spacing
import com.asar.app.ui.util.friendlyDateTime

@Composable
fun MyTasksScreen(
    vm: AsarViewModel,
    onOpenTask: (String) -> Unit,
    onBrowse: () -> Unit,
    onCreate: () -> Unit,
) {
    // Default tab follows the active role, but the user can peek at the other side.
    var tab by rememberSaveable(vm.role) { mutableIntStateOf(if (vm.role == Role.REQUESTER) 0 else 1) }
    val requests = vm.myRequests
    val applications = vm.myApplications
    val meId = vm.currentUser.id

    LazyColumn(
        Modifier.fillMaxSize().background(AsarBackground),
        contentPadding = PaddingValues(bottom = Spacing.xxl),
    ) {
        item(key = "header") {
            Column(
                Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.screen)
                    .padding(top = Spacing.lg)
            ) {
                Text("My tasks", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(Spacing.xxs))
                Text(
                    "Track your requests and the help you've offered.",
                    style = MaterialTheme.typography.bodyMedium, color = AsarInkMuted,
                )
                Spacer(Modifier.height(Spacing.lg))
                SummaryRow(
                    pending = requests.filter { !it.isCompleted }.sumOf { it.pendingCount },
                    active = requests.count { !it.isCompleted },
                    accepted = applications.count { it.applicationOf(meId)?.status == ApplicationStatus.ACCEPTED && !it.isCompleted },
                )
                Spacer(Modifier.height(Spacing.lg))
                SegmentedControl(
                    options = listOf(
                        Segment("My requests", Icons.Rounded.Campaign),
                        Segment("Applications", Icons.Rounded.VolunteerActivism),
                    ),
                    selectedIndex = tab,
                    onSelect = { tab = it },
                )
                Spacer(Modifier.height(Spacing.md))
            }
        }

        if (tab == 0) {
            if (requests.isEmpty()) {
                item(key = "empty-req") {
                    EmptyState(
                        Icons.Rounded.Inbox, "No requests yet",
                        "Post what you need and neighbours nearby will offer to help.",
                    ) { PrimaryButton("Create a request", onCreate, icon = Icons.Rounded.Add, height = 48.dp) }
                }
            }
            items(requests, key = { "r-" + it.id }) { task ->
                RequestManageCard(
                    task = task,
                    canAct = vm.role == Role.REQUESTER,
                    onOpen = { onOpenTask(task.id) },
                    onAccept = { vm.acceptApplication(task.id, it) },
                    onReject = { vm.rejectApplication(task.id, it) },
                    onSwitchRole = { vm.switchRole(Role.REQUESTER) },
                    modifier = Modifier.animateItem().padding(horizontal = Spacing.screen, vertical = 6.dp),
                )
            }
        } else {
            if (applications.isEmpty()) {
                item(key = "empty-app") {
                    EmptyState(
                        Icons.Rounded.Search, "You haven't applied yet",
                        "Browse nearby requests and offer a hand — it takes a minute.",
                    ) { PrimaryButton("Browse requests", onBrowse, height = 48.dp) }
                }
            }
            items(applications, key = { "a-" + it.id }) { task ->
                ApplicationCard(
                    task = task,
                    status = task.applicationOf(meId)?.status ?: ApplicationStatus.PENDING,
                    onOpen = { onOpenTask(task.id) },
                    modifier = Modifier.animateItem().padding(horizontal = Spacing.screen, vertical = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(pending: Int, active: Int, accepted: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        SummaryTile("$pending", "to review", Modifier.weight(1f), highlight = pending > 0)
        SummaryTile("$active", "active requests", Modifier.weight(1f))
        SummaryTile("$accepted", "upcoming helps", Modifier.weight(1f))
    }
}

@Composable
private fun SummaryTile(value: String, label: String, modifier: Modifier = Modifier, highlight: Boolean = false) {
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (highlight) AsarPrimary else AsarSurface)
            .border(1.dp, if (highlight) AsarPrimary else AsarOutline, RoundedCornerShape(18.dp))
            .padding(horizontal = Spacing.sm, vertical = Spacing.sm)
    ) {
        Text(
            value, style = MaterialTheme.typography.headlineSmall,
            color = if (highlight) AsarSurface else AsarPrimary,
        )
        Text(
            label, style = MaterialTheme.typography.bodySmall,
            color = if (highlight) AsarSurface.copy(alpha = 0.85f) else AsarInkMuted,
        )
    }
}

@Composable
private fun RequestManageCard(
    task: HelpTask,
    canAct: Boolean,
    onOpen: () -> Unit,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit,
    onSwitchRole: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AsarCard(modifier.fillMaxWidth(), onClick = onOpen) {
        TaskSummaryHeader(task) { StatusPill(task.status) }
        Spacer(Modifier.height(Spacing.sm))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${task.acceptedCount}/${task.volunteersNeeded} volunteers",
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(Modifier.weight(1f))
            Text(
                if (task.pendingCount > 0) "${task.pendingCount} new applicant${if (task.pendingCount > 1) "s" else ""}" else "No new applicants",
                style = MaterialTheme.typography.bodySmall,
                color = if (task.pendingCount > 0) AsarPrimary else AsarInkFaint,
            )
        }
        Spacer(Modifier.height(6.dp))
        SlotsProgress(task.acceptedCount, task.volunteersNeeded)

        val pending = task.applications.filter { it.status == ApplicationStatus.PENDING }
        if (pending.isNotEmpty() && !task.isCompleted) {
            Spacer(Modifier.height(Spacing.sm))
            if (canAct) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    pending.take(2).forEach { app ->
                        ApplicantRow(
                            application = app,
                            canAccept = task.spotsLeft > 0,
                            onAccept = { onAccept(app.id) },
                            onReject = { onReject(app.id) },
                        )
                    }
                }
            } else {
                TonalButton("Switch to requester mode to review", onSwitchRole, Modifier.fillMaxWidth())
            }
        }
        if (!task.isCompleted && task.acceptedCount > 0 && pending.isEmpty() && canAct) {
            Spacer(Modifier.height(Spacing.sm))
            TonalButton("Open to mark completed", onOpen, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ApplicationCard(task: HelpTask, status: ApplicationStatus, onOpen: () -> Unit, modifier: Modifier = Modifier) {
    AsarCard(modifier.fillMaxWidth(), onClick = onOpen) {
        TaskSummaryHeader(task) {
            if (task.isCompleted) StatusPill(task.status) else ApplicationStatusPill(status)
        }
        Spacer(Modifier.height(Spacing.sm))
        StatusTimeline(status, task.isCompleted)
    }
}

@Composable
private fun TaskSummaryHeader(task: HelpTask, trailing: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CategoryIconTile(task.category, size = 42.dp)
        Spacer(Modifier.width(Spacing.sm))
        Column(Modifier.weight(1f)) {
            Text(task.title, style = MaterialTheme.typography.titleSmall, maxLines = 2)
            Text(
                "${task.dateTime.friendlyDateTime()} · ${task.area.removeSuffix(" district")}",
                style = MaterialTheme.typography.bodySmall, color = AsarInkMuted, maxLines = 1,
            )
        }
        Spacer(Modifier.width(Spacing.xs))
        trailing()
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = AsarInkFaint)
    }
}

/** Applied → Reviewed → Confirmed → Completed */
@Composable
private fun StatusTimeline(status: ApplicationStatus, completed: Boolean) {
    val steps = listOf("Applied", "Reviewed", if (status == ApplicationStatus.REJECTED) "Declined" else "Confirmed", "Completed")
    val reached = when {
        completed -> 4
        status == ApplicationStatus.ACCEPTED -> 3
        status == ApplicationStatus.REJECTED -> 3
        else -> 1
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        steps.forEachIndexed { i, label ->
            val done = i < reached
            val color = when {
                status == ApplicationStatus.REJECTED && i == 2 -> MaterialTheme.colorScheme.error
                done -> AsarPrimary
                else -> AsarOutline
            }
            Column(Modifier.weight(1f)) {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 4.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(color)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    label, style = MaterialTheme.typography.labelSmall,
                    color = if (done) MaterialTheme.colorScheme.onSurface else AsarInkFaint,
                )
            }
        }
    }
}
