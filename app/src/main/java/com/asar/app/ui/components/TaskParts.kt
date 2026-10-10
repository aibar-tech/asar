package com.asar.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asar.app.data.model.ApplicationStatus
import com.asar.app.data.model.Person
import com.asar.app.data.model.Review
import com.asar.app.data.model.TaskAnalysis
import com.asar.app.data.model.TaskApplication
import com.asar.app.data.model.VerificationLevel
import com.asar.app.data.model.VerificationResult
import com.asar.app.ui.theme.AsarAmber
import com.asar.app.ui.theme.AsarDanger
import com.asar.app.ui.theme.AsarInfo
import com.asar.app.ui.theme.AsarInk
import com.asar.app.ui.theme.AsarInkFaint
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarPrimary
import com.asar.app.ui.theme.AsarSand
import com.asar.app.ui.theme.AsarSurfaceSoft
import com.asar.app.ui.theme.Spacing
import com.asar.app.ui.util.agoLabel
import com.asar.app.ui.util.oneDecimal

@Composable
fun PersonLine(person: Person, modifier: Modifier = Modifier, avatarSize: androidx.compose.ui.unit.Dp = 44.dp, subtitle: String? = null) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Avatar(person.name, size = avatarSize)
        Spacer(Modifier.width(Spacing.sm))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(person.name, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                if (person.isVerified) {
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Rounded.Verified, "Verified", tint = AsarPrimary, modifier = Modifier.size(15.dp))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Star, null, tint = AsarSand, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(3.dp))
                Text(
                    subtitle ?: "${person.rating.oneDecimal()} · ${person.completedTasks} tasks · since ${person.memberSince}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AsarInkMuted,
                    maxLines = 1,
                )
            }
        }
    }
}

/** Applicant with message and accept / decline actions for the requester. */
@Composable
fun ApplicantRow(
    application: TaskApplication,
    canAccept: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AsarSurfaceSoft.copy(alpha = 0.55f))
            .padding(Spacing.sm + 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PersonLine(application.volunteer, Modifier.weight(1f), avatarSize = 40.dp)
            if (application.status != ApplicationStatus.PENDING || readOnly) {
                ApplicationStatusPill(application.status)
            } else {
                Text(application.appliedAt.agoLabel(), style = MaterialTheme.typography.bodySmall, color = AsarInkFaint)
            }
        }
        Spacer(Modifier.height(Spacing.xs))
        Text("“${application.message}”", style = MaterialTheme.typography.bodyMedium, color = AsarInk)
        AnimatedVisibility(visible = application.status == ApplicationStatus.PENDING && !readOnly) {
            Row(Modifier.padding(top = Spacing.sm), horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                SecondaryButton(
                    "Decline", onReject, Modifier.weight(1f),
                    icon = Icons.Rounded.Close, height = 42.dp, contentColor = AsarDanger,
                )
                PrimaryButton(
                    if (canAccept) "Accept" else "Team full", onAccept, Modifier.weight(1f),
                    icon = Icons.Rounded.Check, height = 42.dp, enabled = canAccept,
                )
            }
        }
    }
}

@Composable
fun ReviewCard(review: Review, modifier: Modifier = Modifier, title: String? = null) {
    AsarCard(modifier.fillMaxWidth(), elevated = false) {
        if (title != null) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = AsarInkMuted)
            Spacer(Modifier.height(6.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(review.authorName, size = 32.dp)
            Spacer(Modifier.width(Spacing.xs))
            Column(Modifier.weight(1f)) {
                Text(review.authorName, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                Text("about ${review.volunteerName}", style = MaterialTheme.typography.bodySmall, color = AsarInkMuted)
            }
            RatingStars(review.rating, size = 15.dp)
        }
        if (review.text.isNotBlank()) {
            Spacer(Modifier.height(Spacing.xs))
            Text(review.text, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(Spacing.sm))
        VerificationSummary(review.verification, compact = true)
    }
}

/** Shows the simulated review-verification outcome with any flags. */
@Composable
fun VerificationSummary(result: VerificationResult, modifier: Modifier = Modifier, compact: Boolean = false) {
    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            VerificationPill(result.level)
            Spacer(Modifier.width(Spacing.xs))
            Text(
                "Plausibility ${result.plausibility}%",
                style = MaterialTheme.typography.bodySmall,
                color = AsarInkMuted,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
        }
        result.flags.forEach { flag ->
            Spacer(Modifier.height(Spacing.xs))
            Row {
                Icon(
                    Icons.Rounded.Flag, null,
                    tint = if (result.level == VerificationLevel.NEEDS_VERIFICATION) AsarDanger else AsarAmber,
                    modifier = Modifier.size(16.dp).padding(top = 2.dp),
                )
                Spacer(Modifier.width(6.dp))
                Column {
                    Text(flag.title, style = MaterialTheme.typography.titleSmall)
                    Text(flag.detail, style = MaterialTheme.typography.bodySmall, color = AsarInkMuted)
                }
            }
        }
        if (result.level == VerificationLevel.NEEDS_VERIFICATION) {
            Spacer(Modifier.height(Spacing.xs))
            Text(
                "Sent for additional verification by a moderator. This is not an accusation of fraud.",
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                color = AsarInkMuted,
            )
        }
    }
}

/** Card presenting the simulated task analysis. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalysisCard(
    analysis: TaskAnalysis,
    modifier: Modifier = Modifier,
    title: String = "ASAR Assist",
    showSignals: Boolean = true,
    footer: (@Composable () -> Unit)? = null,
) {
    AsarCard(modifier.fillMaxWidth(), elevated = false) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            SimulatedAiBadge()
        }
        Spacer(Modifier.height(Spacing.sm))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            AnalysisStat("Category", analysis.category.shortLabel, Modifier.weight(1f))
            AnalysisStat("Difficulty", analysis.difficulty.label, Modifier.weight(1f))
            AnalysisStat("Team", "${analysis.suggestedVolunteers} ppl", Modifier.weight(1f))
        }
        Spacer(Modifier.height(Spacing.sm))
        Text("Suggested skills", style = MaterialTheme.typography.labelMedium, color = AsarInkMuted)
        Spacer(Modifier.height(6.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            analysis.skills.forEach { Tag(it) }
        }
        if (showSignals) {
            Spacer(Modifier.height(Spacing.sm))
            analysis.signals.forEach {
                Text("•  $it", style = MaterialTheme.typography.bodySmall, color = AsarInkMuted)
            }
            Text(
                "Confidence ${analysis.confidence}% · keyword rules on-device, not a real AI model",
                style = MaterialTheme.typography.bodySmall,
                color = AsarInfo,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        if (footer != null) {
            Spacer(Modifier.height(Spacing.sm))
            footer()
        }
    }
}

@Composable
private fun AnalysisStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AsarSurfaceSoft)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = AsarInkMuted, maxLines = 1)
        Text(value, style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.sp), maxLines = 1, softWrap = false)
    }
}

