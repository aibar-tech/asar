package com.asar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.asar.app.data.model.ApplicationStatus
import com.asar.app.data.model.Difficulty
import com.asar.app.data.model.TaskStatus
import com.asar.app.data.model.VerificationLevel
import com.asar.app.ui.theme.AsarAmber
import com.asar.app.ui.theme.AsarDanger
import com.asar.app.ui.theme.AsarDangerSoft
import com.asar.app.ui.theme.AsarInfo
import com.asar.app.ui.theme.AsarInfoSoft
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarPrimary
import com.asar.app.ui.theme.AsarPrimarySoft
import com.asar.app.ui.theme.AsarSandSoft
import com.asar.app.ui.theme.AsarSurfaceSoft

@Composable
fun Pill(
    text: String,
    foreground: Color,
    background: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    showDot: Boolean = false,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (showDot) Box(Modifier.size(6.dp).clip(CircleShape).background(foreground))
        if (icon != null) Icon(icon, null, tint = foreground, modifier = Modifier.size(13.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = foreground, maxLines = 1)
    }
}

@Composable
fun StatusPill(status: TaskStatus, modifier: Modifier = Modifier) {
    val (fg, bg) = when (status) {
        TaskStatus.OPEN -> AsarPrimary to AsarPrimarySoft
        TaskStatus.IN_PROGRESS -> AsarInfo to AsarInfoSoft
        TaskStatus.COMPLETED -> AsarInkMuted to AsarSurfaceSoft
    }
    Pill(status.label, fg, bg, modifier, showDot = true)
}

@Composable
fun ApplicationStatusPill(status: ApplicationStatus, modifier: Modifier = Modifier) {
    val (fg, bg) = when (status) {
        ApplicationStatus.PENDING -> AsarAmber to AsarSandSoft
        ApplicationStatus.ACCEPTED -> AsarPrimary to AsarPrimarySoft
        ApplicationStatus.REJECTED -> AsarDanger to AsarDangerSoft
    }
    Pill(status.label, fg, bg, modifier, showDot = true)
}

@Composable
fun DifficultyPill(difficulty: Difficulty, modifier: Modifier = Modifier) {
    val (fg, bg) = when (difficulty) {
        Difficulty.EASY -> AsarPrimary to AsarPrimarySoft
        Difficulty.MODERATE -> AsarAmber to AsarSandSoft
        Difficulty.DEMANDING -> AsarDanger to AsarDangerSoft
    }
    Pill(difficulty.label, fg, bg, modifier)
}

@Composable
fun VerificationPill(level: VerificationLevel, modifier: Modifier = Modifier) {
    val (fg, bg) = when (level) {
        VerificationLevel.LOOKS_GENUINE -> AsarPrimary to AsarPrimarySoft
        VerificationLevel.REVIEW_SUGGESTED -> AsarAmber to AsarSandSoft
        VerificationLevel.NEEDS_VERIFICATION -> AsarDanger to AsarDangerSoft
    }
    Pill(level.label, fg, bg, modifier, showDot = true)
}

/** Neutral tag for skills and metadata. */
@Composable
fun Tag(text: String, modifier: Modifier = Modifier) {
    Pill(text, MaterialTheme.colorScheme.onSurface, AsarSurfaceSoft, modifier)
}

/** Always-visible label marking simulated AI output. */
@Composable
fun SimulatedAiBadge(modifier: Modifier = Modifier) {
    Pill("Simulated AI", AsarInfo, AsarInfoSoft, modifier, icon = Icons.Rounded.AutoAwesome)
}

@Composable
fun DemoBadge(modifier: Modifier = Modifier, text: String = "Demo data") {
    Pill(text, AsarAmber, AsarSandSoft, modifier, icon = Icons.Rounded.Science)
}
