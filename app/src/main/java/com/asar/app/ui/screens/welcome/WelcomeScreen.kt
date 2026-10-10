package com.asar.app.ui.screens.welcome

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.asar.app.data.model.TaskCategory
import com.asar.app.ui.components.AsarWordmark
import com.asar.app.ui.components.Avatar
import com.asar.app.ui.components.CategoryIconTile
import com.asar.app.ui.components.PrimaryCta
import com.asar.app.ui.components.QuietButton
import com.asar.app.ui.components.SecondaryButton
import com.asar.app.ui.theme.AsarBackground
import com.asar.app.ui.theme.AsarInk
import com.asar.app.ui.theme.AsarInkFaint
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarOutline
import com.asar.app.ui.theme.AsarPrimary
import com.asar.app.ui.theme.AsarPrimarySoft
import com.asar.app.ui.theme.AsarSand
import com.asar.app.ui.theme.AsarSurface
import com.asar.app.ui.theme.AsarSurfaceSoft
import com.asar.app.ui.theme.Spacing

@Composable
fun WelcomeScreen(
    onCreateAccount: () -> Unit,
    onLogin: () -> Unit,
    onDemo: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(AsarBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = Spacing.xl),
    ) {
        Spacer(Modifier.height(Spacing.md))
        AsarWordmark()

        CommunityIllustration(
            Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Text(
            buildAnnotatedString {
                append("Help is closer\nthan you ")
                withStyle(SpanStyle(color = AsarPrimary)) { append("think.") }
            },
            style = MaterialTheme.typography.displaySmall,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            "Asar is the Kazakh tradition of neighbours coming together to help each other. " +
                "Ask for a hand or lend one — in a few taps.",
            style = MaterialTheme.typography.bodyLarge,
            color = AsarInkMuted,
        )
        Spacer(Modifier.height(Spacing.xl))
        PrimaryCta("Create account", onCreateAccount)
        Spacer(Modifier.height(Spacing.sm))
        SecondaryButton("I already have an account", onLogin, Modifier.fillMaxWidth())
        Spacer(Modifier.height(Spacing.xxs))
        QuietButton("Explore with a demo account", onDemo, Modifier.align(Alignment.CenterHorizontally))
        Text(
            "Hackathon prototype · demo sign-in only, nothing leaves your device",
            style = MaterialTheme.typography.bodySmall,
            color = AsarInkFaint,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = Spacing.sm),
        )
    }
}

/**
 * Abstract "circle of neighbours": concentric rings with people and the kinds of
 * help they exchange, gently floating.
 */
@Composable
private fun CommunityIllustration(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "float")
    val float by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "floatValue",
    )

    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val d = minOf(maxWidth, maxHeight) * 0.92f
        Box(Modifier.size(d), contentAlignment = Alignment.Center) {
            Ring(d, AsarSurfaceSoft.copy(alpha = 0.5f))
            Ring(d * 0.72f, AsarSurfaceSoft)
            Box(
                Modifier
                    .size(d * 0.44f)
                    .clip(CircleShape)
                    .background(AsarPrimarySoft),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .size(d * 0.24f)
                        .shadow(16.dp, CircleShape, ambientColor = AsarPrimary.copy(0.2f), spotColor = AsarPrimary.copy(0.3f))
                        .clip(CircleShape)
                        .background(AsarPrimary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Favorite, null, tint = AsarSand, modifier = Modifier.size(d * 0.1f))
                }
            }

            val r = d / 2
            Floating(-0.62f * r, -0.58f * r, float * 6) { PersonBubble("Dana Serikova") }
            Floating(0.66f * r, -0.40f * r, -float * 5) { PersonBubble("Arman Tulegenov") }
            Floating(-0.70f * r, 0.42f * r, -float * 4) { PersonBubble("Saule Nurlanovna") }
            Floating(0.74f * r, 0.50f * r, float * 5) { PersonBubble("Madina Zhumabaeva") }

            Floating(0.06f * r, -0.86f * r, float * 3) { CategoryBubble(TaskCategory.ELDERLY_CARE) }
            Floating(0.88f * r, 0.18f * r, -float * 3) { CategoryBubble(TaskCategory.ECO) }
            Floating(-0.92f * r, -0.04f * r, float * 4) { CategoryBubble(TaskCategory.EDUCATION) }
            Floating(-0.10f * r, 0.86f * r, -float * 4) { CategoryBubble(TaskCategory.FOOD) }

            Floating(0.02f * r, 0.56f * r, float * 2) { HelpChip() }
        }
    }
}

@Composable
private fun Ring(size: Dp, color: Color) {
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, AsarOutline.copy(alpha = 0.6f), CircleShape)
    )
}

@Composable
private fun Floating(x: Dp, y: Dp, dy: Float, content: @Composable () -> Unit) {
    Box(
        Modifier
            .offset(x = x, y = y)
            .graphicsLayer { translationY = dy * density }
    ) { content() }
}

@Composable
private fun PersonBubble(name: String) {
    Box(
        Modifier
            .shadow(10.dp, CircleShape, ambientColor = AsarInk.copy(0.08f), spotColor = AsarInk.copy(0.1f))
            .clip(CircleShape)
            .background(AsarSurface)
            .padding(4.dp)
    ) {
        Avatar(name, size = 44.dp)
    }
}

@Composable
private fun CategoryBubble(category: TaskCategory) {
    Box(
        Modifier
            .shadow(8.dp, RoundedCornerShape(16.dp), ambientColor = AsarInk.copy(0.06f), spotColor = AsarInk.copy(0.08f))
            .clip(RoundedCornerShape(16.dp))
            .background(AsarSurface)
            .padding(5.dp)
    ) {
        CategoryIconTile(category, size = 34.dp)
    }
}

@Composable
private fun HelpChip() {
    Row(
        Modifier
            .shadow(10.dp, RoundedCornerShape(50), ambientColor = AsarInk.copy(0.08f), spotColor = AsarInk.copy(0.1f))
            .clip(RoundedCornerShape(50))
            .background(AsarSurface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(AsarPrimary))
        Text("3 neighbours helping", style = MaterialTheme.typography.labelMedium)
    }
}
