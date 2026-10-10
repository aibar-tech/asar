package com.asar.app.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.asar.app.ai.ReviewVerifier
import com.asar.app.data.model.ApplicationStatus
import com.asar.app.data.model.HelpTask
import com.asar.app.ui.AsarViewModel
import com.asar.app.ui.VolunteerFeedback
import com.asar.app.ui.components.AsarTextField
import com.asar.app.ui.components.CategoryIconTile
import com.asar.app.ui.components.PersonLine
import com.asar.app.ui.components.PrimaryCta
import com.asar.app.ui.components.RatingInput
import com.asar.app.ui.components.SimulatedAiBadge
import com.asar.app.ui.components.VerificationSummary
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarInfo
import com.asar.app.ui.theme.AsarInfoSoft
import com.asar.app.ui.theme.AsarOutline
import com.asar.app.ui.theme.AsarSurface
import com.asar.app.ui.theme.AsarSurfaceSoft
import com.asar.app.ui.theme.Spacing
import com.asar.app.ui.util.friendlyDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ApplySheet(
    task: HelpTask,
    isOwnRequest: Boolean,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var note by remember { mutableStateOf("Sälem! I'm nearby and free at that time — happy to help.") }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = state, containerColor = AsarSurface) {
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.lg)
        ) {
            Text("Offer your help", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(Spacing.md))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(AsarSurfaceSoft)
                    .padding(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CategoryIconTile(task.category, size = 40.dp)
                Spacer(Modifier.width(Spacing.sm))
                Column {
                    Text(task.title, style = MaterialTheme.typography.titleSmall, maxLines = 2)
                    Text(
                        "${task.dateTime.friendlyDateTime()} · ${task.area}",
                        style = MaterialTheme.typography.bodySmall, color = AsarInkMuted,
                    )
                }
            }
            Spacer(Modifier.height(Spacing.lg))
            AsarTextField(
                value = note,
                onValueChange = { note = it },
                label = "Message to ${task.requester.firstName}",
                singleLine = false,
                minLines = 3,
                supportingText = "A short, friendly note helps the requester choose.",
            )
            if (isOwnRequest) {
                Spacer(Modifier.height(Spacing.sm))
                DemoHint("Demo: you're applying to your own request so you can test both sides of ASAR.")
            }
            Spacer(Modifier.height(Spacing.lg))
            PrimaryCta("Send application", { onSend(note) }, icon = Icons.AutoMirrored.Rounded.Send)
        }
    }
}

private class FeedbackDraft(val volunteerIndex: Int, rating: Int, text: String) {
    var rating by mutableStateOf(rating)
    var text by mutableStateOf(text)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CompleteSheet(
    task: HelpTask,
    vm: AsarViewModel,
    onDismiss: () -> Unit,
    onDone: () -> Unit,
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val team = remember(task.id) {
        task.applications.filter { it.status == ApplicationStatus.ACCEPTED }.map { it.volunteer }
    }
    val drafts = remember(task.id) {
        mutableStateListOf<FeedbackDraft>().apply {
            team.indices.forEach { add(FeedbackDraft(it, 5, "Arrived on time, kind and very helpful. Rahmet!")) }
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = state, containerColor = AsarSurface) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.lg)
        ) {
            Text("How did it go?", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(Spacing.xxs))
            Text(
                "Rate your volunteers. Each review is checked by a simulated plausibility analyzer before it's published.",
                style = MaterialTheme.typography.bodyMedium, color = AsarInkMuted,
            )
            Spacer(Modifier.height(Spacing.lg))

            drafts.forEachIndexed { index, draft ->
                val volunteer = team[draft.volunteerIndex]
                if (index > 0) {
                    HorizontalDivider(color = AsarOutline, modifier = Modifier.padding(vertical = Spacing.lg))
                }
                PersonLine(volunteer, avatarSize = 40.dp)
                Spacer(Modifier.height(Spacing.sm))
                RatingInput(draft.rating, onChange = { draft.rating = it })
                Spacer(Modifier.height(Spacing.sm))
                AsarTextField(
                    value = draft.text,
                    onValueChange = { draft.text = it },
                    placeholder = "Share what ${volunteer.firstName} did",
                    singleLine = false,
                    minLines = 2,
                )
                Spacer(Modifier.height(Spacing.xs))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Try an unusual claim",
                        style = MaterialTheme.typography.labelMedium,
                        color = AsarInfo,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(AsarInfoSoft)
                            .clickable { draft.text = ReviewVerifier.sampleClaims.first() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
                Spacer(Modifier.height(Spacing.sm))
                val preview = vm.verify(draft.text, draft.rating, team.size)
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AsarSurfaceSoft.copy(alpha = 0.6f))
                        .padding(Spacing.sm)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Live check", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                        SimulatedAiBadge()
                    }
                    Spacer(Modifier.height(Spacing.xs))
                    VerificationSummary(preview, compact = true)
                }
            }

            Spacer(Modifier.height(Spacing.xl))
            PrimaryCta(
                "Complete & publish reviews",
                onClick = {
                    vm.completeTask(
                        task.id,
                        drafts.map { VolunteerFeedback(team[it.volunteerIndex], it.rating, it.text) },
                    )
                    onDone()
                },
                icon = Icons.Rounded.TaskAlt,
            )
        }
    }
}
