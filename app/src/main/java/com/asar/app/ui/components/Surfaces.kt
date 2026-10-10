package com.asar.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.asar.app.data.model.TaskCategory
import com.asar.app.ui.theme.AsarInk
import com.asar.app.ui.theme.AsarInkFaint
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarOutline
import com.asar.app.ui.theme.AsarPrimary
import com.asar.app.ui.theme.AsarSand
import com.asar.app.ui.theme.AsarSurface
import com.asar.app.ui.theme.AsarSurfaceSoft
import com.asar.app.ui.theme.Spacing

val CardShape = RoundedCornerShape(22.dp)

/** White card with hairline border and a very soft ambient shadow. */
@Composable
fun AsarCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: Dp = Spacing.md,
    elevated: Boolean = true,
    color: Color = AsarSurface,
    content: @Composable ColumnScope.() -> Unit,
) {
    val base = modifier
        .then(
            if (elevated) Modifier.shadow(
                elevation = 10.dp, shape = CardShape,
                ambientColor = AsarInk.copy(alpha = 0.05f),
                spotColor = AsarInk.copy(alpha = 0.06f),
            ) else Modifier
        )
    val border = BorderStroke(1.dp, AsarOutline)
    if (onClick != null) {
        Surface(onClick = onClick, modifier = base, shape = CardShape, color = color, border = border) {
            Column(Modifier.padding(padding), content = content)
        }
    } else {
        Surface(modifier = base, shape = CardShape, color = color, border = border) {
            Column(Modifier.padding(padding), content = content)
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: String? = null,
    onAction: () -> Unit = {},
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = AsarInkMuted)
            }
        }
        if (action != null) {
            Text(
                action,
                style = MaterialTheme.typography.labelLarge,
                color = AsarPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onAction)
                    .padding(horizontal = 6.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
fun CategoryIconTile(category: TaskCategory, modifier: Modifier = Modifier, size: Dp = 44.dp) {
    val style = category.style
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.32f))
            .background(style.background),
        contentAlignment = Alignment.Center,
    ) {
        Icon(style.icon, contentDescription = category.label, tint = style.foreground, modifier = Modifier.size(size * 0.5f))
    }
}

@Composable
fun MetaRow(icon: ImageVector, text: String, modifier: Modifier = Modifier, color: Color = AsarInkMuted) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = AsarInkFaint, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = color, maxLines = 1)
    }
}

/** Larger icon + label + value row used in details screens. */
@Composable
fun InfoRow(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(AsarSurfaceSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = AsarPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(Spacing.sm))
        Column {
            Text(label, style = MaterialTheme.typography.bodySmall, color = AsarInkMuted)
            Text(value, style = MaterialTheme.typography.titleSmall)
        }
    }
}

/** Animated progress bar showing filled volunteer slots. */
@Composable
fun SlotsProgress(filled: Int, total: Int, modifier: Modifier = Modifier, height: Dp = 6.dp) {
    val fraction by animateFloatAsState(
        targetValue = if (total == 0) 0f else (filled.toFloat() / total).coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "slots",
    )
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(AsarSurfaceSoft)
    ) {
        Box(
            Modifier
                .fillMaxWidth(fraction)
                .height(height)
                .clip(CircleShape)
                .background(AsarPrimary)
        )
    }
}

@Composable
fun RatingStars(rating: Int, modifier: Modifier = Modifier, size: Dp = 16.dp) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        repeat(5) { i ->
            Icon(
                if (i < rating) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                contentDescription = null,
                tint = if (i < rating) AsarSand else AsarInkFaint,
                modifier = Modifier.size(size),
            )
        }
    }
}

@Composable
fun RatingInput(rating: Int, onChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(5) { i ->
            val selected = i < rating
            val scale by animateFloatAsState(if (selected) 1f else 0.88f, label = "star")
            Icon(
                if (selected) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                contentDescription = "${i + 1} stars",
                tint = if (selected) AsarSand else AsarInkFaint,
                modifier = Modifier
                    .size(36.dp * scale)
                    .clip(CircleShape)
                    .clickable { onChange(i + 1) },
            )
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier.fillMaxWidth().padding(vertical = Spacing.xxl, horizontal = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(64.dp).clip(CircleShape).background(AsarSurfaceSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = AsarPrimary, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(Spacing.md))
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(Spacing.xxs))
        Text(body, style = MaterialTheme.typography.bodyMedium, color = AsarInkMuted, textAlign = TextAlign.Center)
        if (action != null) {
            Spacer(Modifier.height(Spacing.md))
            action()
        }
    }
}

/** Minimal top bar for pushed screens. */
@Composable
fun AsarTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(AsarSurface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = AsarInk)
            }
        }
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f).padding(start = Spacing.xxs),
            maxLines = 1,
        )
        trailing()
    }
}
