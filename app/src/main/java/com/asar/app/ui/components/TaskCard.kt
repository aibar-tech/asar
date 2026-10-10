package com.asar.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.asar.app.data.model.ApplicationStatus
import com.asar.app.data.model.HelpTask
import com.asar.app.data.model.Role
import com.asar.app.data.model.TaskStatus
import com.asar.app.ui.theme.AsarInkFaint
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarOutline
import com.asar.app.ui.theme.AsarPrimary
import com.asar.app.ui.theme.AsarPrimarySoft
import com.asar.app.ui.theme.Spacing
import com.asar.app.ui.util.agoLabel
import com.asar.app.ui.util.friendlyDateTime
import com.asar.app.ui.util.km

private enum class CardAction { APPLY, OPEN }

@Composable
fun TaskCard(
    task: HelpTask,
    currentUserId: String,
    role: Role,
    onOpen: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isMine = task.requester.id == currentUserId
    val myApplication = task.applicationOf(currentUserId)
    val (label, action) = when {
        task.isCompleted -> "View summary" to CardAction.OPEN
        isMine && role == Role.REQUESTER ->
            (if (task.pendingCount > 0) "Review ${task.pendingCount}" else "Manage") to CardAction.OPEN
        myApplication != null -> "View" to CardAction.OPEN
        task.status == TaskStatus.OPEN -> "Apply" to CardAction.APPLY
        else -> "Details" to CardAction.OPEN
    }

    AsarCard(modifier = modifier.fillMaxWidth(), onClick = onOpen) {
        Column(Modifier.animateContentSize()) {
            // Header: category + status
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryIconTile(task.category, size = 42.dp)
                Spacer(Modifier.width(Spacing.sm))
                Column(Modifier.weight(1f)) {
                    Text(
                        task.category.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = task.category.style.foreground,
                    )
                    Text(
                        "Posted ${task.createdAt.agoLabel()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AsarInkFaint,
                    )
                }
                if (myApplication != null && !task.isCompleted) {
                    ApplicationStatusPill(myApplication.status)
                } else {
                    StatusPill(task.status)
                }
            }

            Spacer(Modifier.height(Spacing.sm))
            Text(
                task.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(Spacing.xs))

            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                MetaRow(Icons.Rounded.Place, "${task.area.removeSuffix(" district")} · ${task.distanceKm.km()}")
                MetaRow(Icons.Rounded.Schedule, task.dateTime.friendlyDateTime())
            }

            Spacer(Modifier.height(Spacing.sm))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Group, null, tint = AsarPrimary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "${task.acceptedCount} of ${task.volunteersNeeded} volunteers",
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(Modifier.weight(1f))
                if (task.status == TaskStatus.OPEN) {
                    Text(
                        if (task.spotsLeft == 1) "1 spot left" else "${task.spotsLeft} spots left",
                        style = MaterialTheme.typography.bodySmall,
                        color = AsarInkMuted,
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            SlotsProgress(task.acceptedCount, task.volunteersNeeded)

            Spacer(Modifier.height(Spacing.sm))
            HorizontalDivider(color = AsarOutline)
            Spacer(Modifier.height(Spacing.sm))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    if (isMine) {
                        Pill("Your request", AsarPrimary, AsarPrimarySoft)
                    } else {
                        Avatar(task.requester.name, size = 28.dp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            task.requester.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = AsarInkMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Spacer(Modifier.width(Spacing.sm))
                val pendingMine = myApplication?.status == ApplicationStatus.PENDING
                if (action == CardAction.APPLY) {
                    PrimaryButton(label, onApply, height = 40.dp)
                } else {
                    TonalButton(if (pendingMine) "View" else label, onOpen, height = 40.dp)
                }
            }
        }
    }
}
