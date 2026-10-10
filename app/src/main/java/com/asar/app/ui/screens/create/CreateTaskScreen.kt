package com.asar.app.ui.screens.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Publish
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.asar.app.ai.TaskAnalyzer
import com.asar.app.data.MockData
import com.asar.app.data.model.Difficulty
import com.asar.app.data.model.TaskCategory
import com.asar.app.ui.AsarViewModel
import com.asar.app.ui.TaskDraft
import com.asar.app.ui.components.AnalysisCard
import com.asar.app.ui.components.AsarTextField
import com.asar.app.ui.components.PrimaryCta
import com.asar.app.ui.components.TonalButton
import com.asar.app.ui.components.shortLabel
import com.asar.app.ui.components.style
import com.asar.app.ui.theme.AsarBackground
import com.asar.app.ui.theme.AsarInfo
import com.asar.app.ui.theme.AsarInfoSoft
import com.asar.app.ui.theme.AsarInk
import com.asar.app.ui.theme.AsarInkFaint
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarOutline
import com.asar.app.ui.theme.AsarPrimary
import com.asar.app.ui.theme.AsarPrimarySoft
import com.asar.app.ui.theme.AsarSurface
import com.asar.app.ui.theme.Spacing
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class Example(val label: String, val title: String, val description: String)

private val examples = listOf(
    Example(
        "Groceries for grandma",
        "Groceries for my grandmother",
        "My grandmother is 79 and lives alone on the 5th floor with no elevator. " +
            "Could someone bring groceries and medicine from the pharmacy this week?",
    ),
    Example(
        "Courtyard cleanup",
        "Courtyard cleanup and tree planting",
        "Our courtyard needs a spring cleanup: collect litter, sort plastic for recycling and " +
            "plant a few trees with the kids from our building.",
    ),
    Example(
        "Move a sofa",
        "Help carry a heavy sofa",
        "Moving a heavy sofa and a wardrobe from the 4th floor to a truck. No lift in the building.",
    ),
)

private val timeSlots = listOf(9, 11, 14, 16, 18, 20)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateTaskScreen(vm: AsarViewModel, onPublished: () -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf<TaskCategory?>(null) }
    var area by rememberSaveable { mutableStateOf(MockData.areas[1]) }
    var dayOffset by rememberSaveable { mutableIntStateOf(1) }
    var hour by rememberSaveable { mutableIntStateOf(11) }
    var volunteers by rememberSaveable { mutableIntStateOf(2) }
    var skills by remember { mutableStateOf<List<String>>(emptyList()) }
    var difficulty by rememberSaveable { mutableStateOf(Difficulty.MODERATE) }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    val analysis = remember(title, description) { TaskAnalyzer.analyze(title, description) }
    val suggestionsApplied = analysis != null && category == analysis.category &&
        volunteers == analysis.suggestedVolunteers && skills == analysis.skills

    val titleError = title.trim().length < 4
    val descriptionError = description.trim().length < 15
    val categoryError = category == null

    fun reset() {
        title = ""; description = ""; category = null; volunteers = 2
        skills = emptyList(); difficulty = Difficulty.MODERATE; showErrors = false
    }

    Column(Modifier.fillMaxSize().background(AsarBackground).imePadding()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = Spacing.screen)
                .padding(top = Spacing.lg, bottom = Spacing.xl)
        ) {
            Text("New request", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(Spacing.xxs))
            Text(
                "Describe what you need. ASAR Assist will suggest the category, team size and skills.",
                style = MaterialTheme.typography.bodyMedium, color = AsarInkMuted,
            )

            Spacer(Modifier.height(Spacing.lg))
            Text("Quick start", style = MaterialTheme.typography.labelMedium, color = AsarInkMuted)
            Spacer(Modifier.height(Spacing.xs))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                items(examples) { ex ->
                    Text(
                        ex.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = AsarInfo,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(AsarInfoSoft)
                            .clickable { title = ex.title; description = ex.description }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
            }

            Spacer(Modifier.height(Spacing.lg))
            AsarTextField(
                value = title,
                onValueChange = { title = it },
                label = "Title",
                placeholder = "e.g. Help carry groceries to the 5th floor",
                isError = showErrors && titleError,
                supportingText = if (showErrors && titleError) "Add a short title (4+ characters)" else null,
            )
            Spacer(Modifier.height(Spacing.md))
            AsarTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                placeholder = "What needs to be done, any access details, what to bring…",
                singleLine = false,
                minLines = 4,
                isError = showErrors && descriptionError,
                supportingText = if (showErrors && descriptionError) "A few more details help volunteers decide" else null,
            )

            AnimatedVisibility(
                visible = analysis != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                if (analysis != null) {
                    AnalysisCard(
                        analysis,
                        modifier = Modifier.padding(top = Spacing.lg),
                        footer = {
                            TonalButton(
                                if (suggestionsApplied) "Suggestions applied" else "Use suggestions",
                                onClick = {
                                    category = analysis.category
                                    volunteers = analysis.suggestedVolunteers
                                    skills = analysis.skills
                                    difficulty = analysis.difficulty
                                },
                                icon = if (suggestionsApplied) Icons.Rounded.Check else Icons.Rounded.AutoAwesome,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },
                    )
                }
            }

            FormSection("Category", error = if (showErrors && categoryError) "Choose a category" else null) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    TaskCategory.entries.forEach { c ->
                        SelectChip(c.shortLabel, selected = category == c, icon = c.style.icon) { category = c }
                    }
                }
            }

            FormSection("Area in ${MockData.CITY}") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    MockData.areas.forEach { a ->
                        SelectChip(a.removeSuffix(" district"), selected = area == a) { area = a }
                    }
                }
            }

            FormSection("Date & time") {
                DayPicker(dayOffset) { dayOffset = it }
                Spacer(Modifier.height(Spacing.sm))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    timeSlots.forEach { h ->
                        SelectChip(String.format(Locale.ENGLISH, "%02d:00", h), selected = hour == h) { hour = h }
                    }
                }
            }

            FormSection("Volunteers needed") {
                Stepper(volunteers, onChange = { volunteers = it.coerceIn(1, 30) })
            }
        }

        // Sticky publish bar
        Column(Modifier.fillMaxWidth().background(AsarSurface)) {
            HorizontalDivider(color = AsarOutline)
            Box(Modifier.padding(horizontal = Spacing.screen, vertical = Spacing.sm)) {
                PrimaryCta(
                    "Publish request",
                    onClick = {
                        if (titleError || descriptionError || categoryError) {
                            showErrors = true
                            vm.notify("Please complete the highlighted fields")
                        } else {
                            vm.createTask(
                                TaskDraft(
                                    title = title,
                                    description = description,
                                    category = category!!,
                                    area = area,
                                    dateTime = LocalDate.now().plusDays(dayOffset.toLong()).atTime(LocalTime.of(hour, 0)),
                                    volunteersNeeded = volunteers,
                                    skills = skills.ifEmpty { analysis?.skills ?: listOf("Kindness") },
                                    difficulty = difficulty,
                                )
                            )
                            reset()
                            onPublished()
                        }
                    },
                    icon = Icons.Rounded.Publish,
                )
            }
        }
    }
}

@Composable
private fun FormSection(title: String, error: String? = null, content: @Composable () -> Unit) {
    Spacer(Modifier.height(Spacing.xl))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
        if (error != null) {
            Text(error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        }
    }
    Spacer(Modifier.height(Spacing.sm))
    content()
}

@Composable
private fun SelectChip(label: String, selected: Boolean, icon: ImageVector? = null, onClick: () -> Unit) {
    val bg by animateColorAsState(if (selected) AsarPrimarySoft else AsarSurface, label = "chip")
    val border by animateColorAsState(if (selected) AsarPrimary else AsarOutline, label = "chipBorder")
    Row(
        Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = if (selected) AsarPrimary else AsarInkMuted, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(label, style = MaterialTheme.typography.labelLarge, color = if (selected) AsarPrimary else AsarInk)
    }
}

@Composable
private fun DayPicker(selected: Int, onSelect: (Int) -> Unit) {
    val today = LocalDate.now()
    val dow = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)
    val mon = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), contentPadding = PaddingValues(end = Spacing.md)) {
        items(14) { offset ->
            val date = today.plusDays(offset.toLong())
            val isSelected = offset == selected
            val bg by animateColorAsState(if (isSelected) AsarPrimary else AsarSurface, label = "day")
            Column(
                Modifier
                    .width(58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bg)
                    .border(1.dp, if (isSelected) AsarPrimary else AsarOutline, RoundedCornerShape(16.dp))
                    .clickable { onSelect(offset) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    if (offset == 0) "Today" else date.format(dow),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) AsarSurface.copy(alpha = 0.85f) else AsarInkMuted,
                )
                Text(
                    "${date.dayOfMonth}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) AsarSurface else AsarInk,
                )
                Text(
                    date.format(mon),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) AsarSurface.copy(alpha = 0.85f) else AsarInkFaint,
                )
            }
        }
    }
}

@Composable
private fun Stepper(value: Int, onChange: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AsarSurface)
            .border(1.dp, AsarOutline, RoundedCornerShape(18.dp))
            .padding(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepButton(Icons.Rounded.Remove, "Fewer", enabled = value > 1) { onChange(value - 1) }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$value", style = MaterialTheme.typography.headlineMedium)
            Text(if (value == 1) "volunteer" else "volunteers", style = MaterialTheme.typography.bodySmall, color = AsarInkMuted)
        }
        StepButton(Icons.Rounded.Add, "More", enabled = value < 30) { onChange(value + 1) }
    }
}

@Composable
private fun StepButton(icon: ImageVector, description: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (enabled) AsarPrimarySoft else AsarOutline.copy(alpha = 0.5f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, description, tint = if (enabled) AsarPrimary else AsarInkFaint)
    }
}
