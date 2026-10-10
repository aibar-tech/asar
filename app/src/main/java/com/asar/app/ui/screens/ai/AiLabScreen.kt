package com.asar.app.ui.screens.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.asar.app.ai.ReviewVerifier
import com.asar.app.ai.TaskAnalyzer
import com.asar.app.ui.AsarViewModel
import com.asar.app.ui.components.AnalysisCard
import com.asar.app.ui.components.AsarCard
import com.asar.app.ui.components.AsarTextField
import com.asar.app.ui.components.AsarTopBar
import com.asar.app.ui.components.RatingInput
import com.asar.app.ui.components.SimulatedAiBadge
import com.asar.app.ui.components.VerificationSummary
import com.asar.app.ui.theme.AsarBackground
import com.asar.app.ui.theme.AsarInfo
import com.asar.app.ui.theme.AsarInfoSoft
import com.asar.app.ui.theme.AsarInk
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarSurface
import com.asar.app.ui.theme.Spacing

private val taskSamples = listOf(
    "Need 3 people to carry a piano to the 4th floor, no elevator",
    "Teach my grandfather to use video calls on his smartphone",
    "Saturday cleanup of plastic litter around Sairan lake",
    "Walk shelter dogs and help clean the kennels",
)

@Composable
fun AiLabScreen(vm: AsarViewModel, onBack: () -> Unit) {
    var taskText by rememberSaveable { mutableStateOf(taskSamples.first()) }
    var reviewText by rememberSaveable { mutableStateOf(ReviewVerifier.sampleClaims.first()) }
    var rating by rememberSaveable { mutableIntStateOf(5) }

    val analysis = remember(taskText) { TaskAnalyzer.analyze("", taskText) }
    val verification = remember(reviewText, rating) { vm.verify(reviewText, rating, null) }

    Column(Modifier.fillMaxSize().background(AsarBackground).imePadding()) {
        AsarTopBar("ASAR Assist lab", onBack, Modifier.statusBarsPadding()) {
            SimulatedAiBadge(Modifier.padding(end = Spacing.md))
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = Spacing.screen)
                .padding(bottom = Spacing.xxl)
        ) {
            Disclaimer()

            // ---- Task analyzer ----
            Spacer(Modifier.height(Spacing.xl))
            Text("1 · Task analyzer", style = MaterialTheme.typography.titleLarge)
            Text(
                "Suggests a category, difficulty, team size and skills from keywords.",
                style = MaterialTheme.typography.bodyMedium, color = AsarInkMuted,
            )
            Spacer(Modifier.height(Spacing.sm))
            SampleRow(taskSamples) { taskText = it }
            Spacer(Modifier.height(Spacing.sm))
            AsarTextField(
                value = taskText,
                onValueChange = { taskText = it },
                placeholder = "Describe a task…",
                singleLine = false,
                minLines = 3,
            )
            AnimatedVisibility(
                visible = analysis != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                if (analysis != null) AnalysisCard(analysis, Modifier.padding(top = Spacing.md), title = "Analysis")
            }

            // ---- Review verification ----
            Spacer(Modifier.height(Spacing.xxl))
            Text("2 · Review verification", style = MaterialTheme.typography.titleLarge)
            Text(
                "Flags physically unusual or inconsistent claims for a human to double-check. " +
                    "It never labels anyone a fraud.",
                style = MaterialTheme.typography.bodyMedium, color = AsarInkMuted,
            )
            Spacer(Modifier.height(Spacing.sm))
            SampleRow(ReviewVerifier.sampleClaims) { reviewText = it }
            Spacer(Modifier.height(Spacing.sm))
            AsarTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                placeholder = "Write a review or impact claim…",
                singleLine = false,
                minLines = 3,
            )
            Spacer(Modifier.height(Spacing.sm))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Rating", style = MaterialTheme.typography.titleSmall, modifier = Modifier.width(64.dp))
                RatingInput(rating, { rating = it })
            }
            Spacer(Modifier.height(Spacing.md))
            AsarCard(Modifier.fillMaxWidth(), elevated = false) {
                Text("Result", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(Spacing.sm))
                VerificationSummary(verification)
            }
        }
    }
}

@Composable
private fun Disclaimer() {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AsarInfoSoft)
            .padding(Spacing.md),
    ) {
        Icon(Icons.Rounded.Info, null, tint = AsarInfo, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(Spacing.sm))
        Column {
            Text("Simulated AI demonstration", style = MaterialTheme.typography.titleSmall, color = AsarInfo)
            Spacer(Modifier.height(2.dp))
            Text(
                "These features use transparent, rule-based logic that runs on your phone. They are " +
                    "not connected to any real AI model or online service — they show how ASAR could " +
                    "assist people in a production version.",
                style = MaterialTheme.typography.bodySmall, color = AsarInk,
            )
        }
    }
}

@Composable
private fun SampleRow(samples: List<String>, onPick: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        items(samples) { sample ->
            Text(
                sample,
                style = MaterialTheme.typography.labelMedium,
                color = AsarInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .width(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AsarSurface)
                    .clickable { onPick(sample) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            )
        }
    }
}

