package com.asar.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Elderly
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.asar.app.data.model.Achievement
import com.asar.app.data.model.HistoryEntry
import com.asar.app.ui.AsarViewModel
import com.asar.app.ui.components.AsarCard
import com.asar.app.ui.components.Avatar
import com.asar.app.ui.components.CategoryIconTile
import com.asar.app.ui.components.DemoBadge
import com.asar.app.ui.components.Pill
import com.asar.app.ui.components.RatingStars
import com.asar.app.ui.components.ReviewCard
import com.asar.app.ui.components.RoleSwitcher
import com.asar.app.ui.components.SecondaryButton
import com.asar.app.ui.components.SectionHeader
import com.asar.app.ui.components.SimulatedAiBadge
import com.asar.app.ui.theme.AsarBackground
import com.asar.app.ui.theme.AsarDanger
import com.asar.app.ui.theme.AsarInfo
import com.asar.app.ui.theme.AsarInfoSoft
import com.asar.app.ui.theme.AsarInkFaint
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarOutline
import com.asar.app.ui.theme.AsarPrimary
import com.asar.app.ui.theme.AsarPrimarySoft
import com.asar.app.ui.theme.AsarSand
import com.asar.app.ui.theme.AsarSandSoft
import com.asar.app.ui.theme.AsarAmber
import com.asar.app.ui.theme.AsarSurface
import com.asar.app.ui.theme.AsarSurfaceSoft
import com.asar.app.ui.theme.Spacing
import com.asar.app.ui.util.oneDecimal

@Composable
fun ProfileScreen(
    vm: AsarViewModel,
    onOpenAiLab: () -> Unit,
    onOpenTask: (String) -> Unit,
) {
    val session = vm.session ?: return
    val me = vm.currentUser

    Column(
        Modifier
            .fillMaxSize()
            .background(AsarBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = Spacing.screen)
            .padding(top = Spacing.lg, bottom = Spacing.xxl)
    ) {
        // Identity
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box {
                Box(
                    Modifier.size(104.dp).clip(CircleShape).background(AsarPrimarySoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Avatar(me.name, size = 92.dp, ring = true)
                }
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(AsarPrimary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Verified, "Verified", tint = AsarSandSoft, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(Spacing.sm))
            Text(me.name, style = MaterialTheme.typography.headlineSmall)
            Text(session.email, style = MaterialTheme.typography.bodyMedium, color = AsarInkMuted)
            Spacer(Modifier.height(Spacing.xs))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Pill("Member since ${me.memberSince}", AsarPrimary, AsarPrimarySoft, icon = Icons.Rounded.VolunteerActivism)
                DemoBadge(text = if (session.isDemo) "Demo account" else "Local account")
            }
        }

        Spacer(Modifier.height(Spacing.lg))
        RoleSwitcher(vm.role, vm::switchRole)

        // Impact
        Spacer(Modifier.height(Spacing.xl))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Your impact", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            DemoBadge()
        }
        Spacer(Modifier.height(Spacing.sm))
        Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            StatTile(
                value = vm.averageRating.oneDecimal(),
                label = "${vm.ratings.size} reviews",
                icon = Icons.Rounded.Star, iconTint = AsarSand,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            StatTile(
                value = "${vm.completedCount}",
                label = "tasks done",
                icon = Icons.Rounded.EmojiEvents, iconTint = AsarPrimary,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            StatTile(
                value = "${vm.hoursVolunteered}h",
                label = "hours given",
                icon = Icons.Rounded.VolunteerActivism, iconTint = AsarPrimary,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }

        // Achievements
        Spacer(Modifier.height(Spacing.xl))
        SectionHeader("Verified achievements", subtitle = "Confirmed by the people you helped (demo)")
        Spacer(Modifier.height(Spacing.sm))
        vm.achievements.chunked(2).forEach { pair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                modifier = Modifier.height(IntrinsicSize.Min).padding(bottom = Spacing.xs),
            ) {
                pair.forEach { AchievementTile(it, Modifier.weight(1f).fillMaxHeight()) }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        // AI lab entry
        Spacer(Modifier.height(Spacing.lg))
        AsarCard(Modifier.fillMaxWidth(), onClick = onOpenAiLab, elevated = false, color = AsarInfoSoft) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(AsarInfo),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = AsarInfoSoft)
                }
                Spacer(Modifier.width(Spacing.sm))
                Column(Modifier.weight(1f)) {
                    Text("ASAR Assist lab", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Try the task analyzer and review verification",
                        style = MaterialTheme.typography.bodySmall, color = AsarInkMuted,
                    )
                }
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = AsarInfo)
            }
            Spacer(Modifier.height(Spacing.xs))
            SimulatedAiBadge()
        }

        // Reviews
        val reviews = vm.reviewsReceived
        if (reviews.isNotEmpty()) {
            Spacer(Modifier.height(Spacing.xl))
            SectionHeader("Reviews about you", subtitle = "${reviews.size} published")
            Spacer(Modifier.height(Spacing.sm))
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                reviews.forEach { (task, review) ->
                    Box(Modifier.clip(RoundedCornerShape(22.dp)).clickable { onOpenTask(task.id) }) {
                        ReviewCard(review, title = task.title)
                    }
                }
            }
        }

        // History
        Spacer(Modifier.height(Spacing.xl))
        SectionHeader("Participation history", subtitle = "Your recent helping hands")
        Spacer(Modifier.height(Spacing.sm))
        AsarCard(Modifier.fillMaxWidth(), elevated = false, padding = Spacing.xs) {
            vm.history.forEachIndexed { i, entry ->
                HistoryRow(entry)
                if (i < vm.history.lastIndex) {
                    Box(Modifier.fillMaxWidth().padding(start = 64.dp).height(1.dp).background(AsarOutline))
                }
            }
        }

        Spacer(Modifier.height(Spacing.xl))
        Text(
            "All ratings, counts and achievements on this screen are demo data kept in memory " +
                "on this device. ASAR is a hackathon prototype.",
            style = MaterialTheme.typography.bodySmall,
            color = AsarInkFaint,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Spacing.md))
        SecondaryButton(
            "Sign out", vm::logout, Modifier.fillMaxWidth(),
            icon = Icons.AutoMirrored.Rounded.Logout, contentColor = AsarDanger,
        )
    }
}

@Composable
private fun StatTile(
    value: String,
    label: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    AsarCard(modifier, padding = Spacing.sm) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(Spacing.xs))
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Text(label, style = MaterialTheme.typography.bodySmall, color = AsarInkMuted, maxLines = 2)
    }
}

@Composable
private fun AchievementTile(achievement: Achievement, modifier: Modifier = Modifier) {
    val icon = when (achievement.id) {
        "eco", "eco3" -> Icons.Rounded.Eco
        "elder" -> Icons.Rounded.Elderly
        "ten" -> Icons.Rounded.WorkspacePremium
        "session" -> Icons.Rounded.AutoAwesome
        else -> Icons.Rounded.EmojiEvents
    }
    val earned = achievement.earned
    AsarCard(modifier, padding = Spacing.sm, elevated = false, color = if (earned) AsarSurface else AsarSurfaceSoft.copy(alpha = 0.5f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (earned) AsarSandSoft else AsarSurfaceSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (earned) icon else Icons.Rounded.Lock, null,
                    tint = if (earned) AsarAmber else AsarInkFaint,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(
            achievement.title, style = MaterialTheme.typography.titleSmall,
            color = if (earned) MaterialTheme.colorScheme.onSurface else AsarInkMuted, maxLines = 1,
        )
        Text(achievement.description, style = MaterialTheme.typography.bodySmall, color = AsarInkMuted, maxLines = 2)
        if (achievement.verifiedBy != null) {
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Verified, null, tint = AsarPrimary, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    "by ${achievement.verifiedBy}", style = MaterialTheme.typography.labelSmall,
                    color = AsarPrimary, maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun HistoryRow(entry: HistoryEntry) {
    Row(
        Modifier.fillMaxWidth().padding(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryIconTile(entry.category, size = 40.dp)
        Spacer(Modifier.width(Spacing.sm))
        Column(Modifier.weight(1f)) {
            Text(entry.title, style = MaterialTheme.typography.titleSmall, maxLines = 1)
            Text("${entry.dateLabel} · ${entry.hours}h", style = MaterialTheme.typography.bodySmall, color = AsarInkMuted)
        }
        if (entry.rating != null) RatingStars(entry.rating, size = 13.dp)
    }
}
