package com.asar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asar.app.ui.theme.AsarSurface
import com.asar.app.ui.theme.TintClay
import com.asar.app.ui.theme.TintPlum
import com.asar.app.ui.theme.TintSage
import com.asar.app.ui.theme.TintSky
import com.asar.app.ui.theme.TintTeal
import com.asar.app.ui.util.initials
import kotlin.math.absoluteValue

private val avatarTints = listOf(TintSage, TintClay, TintSky, TintPlum, TintTeal)

/** Initials avatar placeholder; colour is stable per name. */
@Composable
fun Avatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    ring: Boolean = false,
) {
    val (fg, bg) = avatarTints[name.hashCode().absoluteValue % avatarTints.size]
    Box(
        modifier = modifier
            .size(size)
            .then(if (ring) Modifier.border(3.dp, AsarSurface, CircleShape) else Modifier)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            name.initials(),
            style = TextStyle(
                color = fg,
                fontWeight = FontWeight.SemiBold,
                fontSize = (size.value * 0.36f).sp,
            ),
        )
    }
}

/** Overlapping avatars, e.g. for a task's accepted team. */
@Composable
fun AvatarStack(names: List<String>, modifier: Modifier = Modifier, size: Dp = 26.dp, max: Int = 4) {
    Box(modifier) {
        names.take(max).forEachIndexed { i, name ->
            Avatar(
                name, size = size, ring = true,
                modifier = Modifier.padding(start = (size.value * 0.68f * i).dp),
            )
        }
    }
}
