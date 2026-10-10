package com.asar.app.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.asar.app.data.MockData
import com.asar.app.data.model.HelpTask
import com.asar.app.data.model.Role
import com.asar.app.data.model.TaskCategory
import com.asar.app.ui.AsarViewModel
import com.asar.app.ui.components.Avatar
import com.asar.app.ui.components.EmptyState
import com.asar.app.ui.components.PrimaryButton
import com.asar.app.ui.components.RoleSwitcher
import com.asar.app.ui.components.SectionHeader
import com.asar.app.ui.components.TaskCard
import com.asar.app.ui.components.shortLabel
import com.asar.app.ui.components.style
import com.asar.app.ui.theme.AsarBackground
import com.asar.app.ui.theme.AsarInk
import com.asar.app.ui.theme.AsarInkFaint
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarOutline
import com.asar.app.ui.theme.AsarPrimary
import com.asar.app.ui.theme.AsarPrimaryDark
import com.asar.app.ui.theme.AsarSand
import com.asar.app.ui.theme.AsarSurface
import com.asar.app.ui.theme.Spacing

private enum class SortMode(val label: String) { NEAREST("Nearest"), SOONEST("Soonest") }

@Composable
fun HomeScreen(
    vm: AsarViewModel,
    onOpenTask: (String) -> Unit,
    onApply: (String) -> Unit,
    onCreate: () -> Unit,
    onOpenMyTasks: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf<TaskCategory?>(null) }
    var sort by rememberSaveable { mutableStateOf(SortMode.NEAREST) }
    val me = vm.currentUser
    val role = vm.role

    val visible: List<HelpTask> = vm.feed
        .filter { category == null || it.category == category }
        .filter { task ->
            query.isBlank() || listOf(task.title, task.description, task.area, task.category.label)
                .any { it.contains(query.trim(), ignoreCase = true) }
        }
        .let { list ->
            val (mine, others) = list.partition { it.requester.id == me.id }
            val sorted = when (sort) {
                SortMode.NEAREST -> others.sortedBy { it.distanceKm }
                SortMode.SOONEST -> others.sortedBy { it.dateTime }
            }
            // Your own fresh requests always surface first so you can see them go live.
            mine + sorted
        }

    val pendingForMe = vm.myRequests.filter { !it.isCompleted }.sumOf { it.pendingCount }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(AsarBackground),
        contentPadding = PaddingValues(bottom = Spacing.xxl),
    ) {
        item(key = "header") {
            Column(
                Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.screen)
                    .padding(top = Spacing.md)
            ) {
                Header(
                    fullName = me.name,
                    badge = if (role == Role.REQUESTER) pendingForMe else 0,
                    onBell = onOpenMyTasks,
                )
                Spacer(Modifier.height(Spacing.xl))
                AnimatedContent(
                    targetState = role,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "headline",
                ) { r ->
                    Text(
                        buildAnnotatedString {
                            if (r == Role.VOLUNTEER) {
                                append("Who can you\nhelp ")
                                withStyle(SpanStyle(color = AsarPrimary)) { append("today?") }
                            } else {
                                append("What do you need\n")
                                withStyle(SpanStyle(color = AsarPrimary)) { append("a hand with?") }
                            }
                        },
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
                Spacer(Modifier.height(Spacing.lg))
                RoleSwitcher(role = role, onChange = vm::switchRole)
                Spacer(Modifier.height(Spacing.md))
                SearchBar(query = query, onQueryChange = { query = it })
            }
        }

        item(key = "hero") {
            AnimatedContent(
                targetState = role,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.padding(horizontal = Spacing.screen, vertical = Spacing.lg),
                label = "hero",
            ) { r ->
                if (r == Role.REQUESTER) {
                    RequesterHero(
                        activeRequests = vm.myRequests.count { !it.isCompleted },
                        pending = pendingForMe,
                        onCreate = onCreate,
                        onManage = onOpenMyTasks,
                    )
                } else {
                    VolunteerImpactStrip()
                }
            }
        }

        item(key = "categories") {
            CategoryRow(selected = category, onSelect = { category = it })
            Spacer(Modifier.height(Spacing.xl))
        }

        item(key = "section") {
            SectionHeader(
                title = if (category == null) "Nearby requests" else category!!.label,
                subtitle = "${visible.size} in ${MockData.CITY} · updated just now",
                action = sort.label,
                onAction = { sort = if (sort == SortMode.NEAREST) SortMode.SOONEST else SortMode.NEAREST },
                modifier = Modifier.padding(horizontal = Spacing.screen),
            )
            Spacer(Modifier.height(Spacing.sm))
        }

        if (visible.isEmpty()) {
            item(key = "empty") {
                EmptyState(
                    icon = Icons.Rounded.SearchOff,
                    title = "Nothing here yet",
                    body = "Try another category or search term — or post the first request yourself.",
                )
            }
        }

        items(visible, key = { it.id }) { task ->
            TaskCard(
                task = task,
                currentUserId = me.id,
                role = role,
                onOpen = { onOpenTask(task.id) },
                onApply = { onApply(task.id) },
                modifier = Modifier
                    .animateItem()
                    .padding(horizontal = Spacing.screen, vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun Header(fullName: String, badge: Int, onBell: () -> Unit) {
    val name = fullName.substringBefore(' ')
    Row(verticalAlignment = Alignment.CenterVertically) {
        Avatar(fullName, size = 46.dp)
        Spacer(Modifier.width(Spacing.sm))
        Column(Modifier.weight(1f)) {
            Text("Sälem, $name", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Place, null, tint = AsarPrimary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(3.dp))
                Text("${MockData.CITY}, Kazakhstan", style = MaterialTheme.typography.bodySmall, color = AsarInkMuted)
            }
        }
        Box {
            Surface(
                onClick = onBell,
                shape = CircleShape,
                color = AsarSurface,
                border = BorderStroke(1.dp, AsarOutline),
                modifier = Modifier.size(46.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.NotificationsNone, contentDescription = "Activity", tint = AsarInk)
                }
            }
            if (badge > 0) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .size(12.dp)
                        .border(2.dp, AsarBackground, CircleShape)
                        .clip(CircleShape)
                        .background(AsarSand)
                )
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(12.dp, RoundedCornerShape(18.dp), ambientColor = AsarInk.copy(0.05f), spotColor = AsarInk.copy(0.07f))
            .clip(RoundedCornerShape(18.dp))
            .background(AsarSurface)
            .border(1.dp, AsarOutline, RoundedCornerShape(18.dp))
            .padding(horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.Search, null, tint = AsarInkMuted)
        Spacer(Modifier.width(Spacing.sm))
        Box(Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text("Search groceries, cleanup, tutoring…", style = MaterialTheme.typography.bodyLarge, color = AsarInkFaint)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = AsarInk),
                cursorBrush = SolidColor(AsarPrimary),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (query.isNotEmpty()) {
            Icon(
                Icons.Rounded.Close,
                contentDescription = "Clear search",
                tint = AsarInkMuted,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onQueryChange("") }
                    .padding(4.dp),
            )
        }
    }
}

@Composable
private fun CategoryRow(selected: TaskCategory?, onSelect: (TaskCategory?) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Spacing.screen),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        item {
            CategoryChip("All", Icons.Rounded.Apps, selected == null) { onSelect(null) }
        }
        items(TaskCategory.entries) { c ->
            CategoryChip(c.shortLabel, c.style.icon, selected == c) {
                onSelect(if (selected == c) null else c)
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    val bg by animateColorAsState(if (selected) AsarInk else AsarSurface, label = "chipBg")
    val fg by animateColorAsState(if (selected) AsarSurface else AsarInk, label = "chipFg")
    Row(
        Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, if (selected) AsarInk else AsarOutline, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = fg, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = fg)
    }
}

@Composable
private fun RequesterHero(activeRequests: Int, pending: Int, onCreate: () -> Unit, onManage: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(AsarPrimary)
            .padding(Spacing.lg)
    ) {
        Text("Need a hand?", style = MaterialTheme.typography.titleLarge, color = AsarSurface)
        Spacer(Modifier.height(Spacing.xxs))
        Text(
            "Post a request and neighbours nearby will offer to help — usually within the hour.",
            style = MaterialTheme.typography.bodyMedium,
            color = AsarSurface.copy(alpha = 0.82f),
        )
        Spacer(Modifier.height(Spacing.md))
        Row(verticalAlignment = Alignment.CenterVertically) {
            PrimaryButton(
                "Create request", onCreate,
                height = 44.dp, containerColor = AsarSurface, contentColor = AsarPrimaryDark,
            )
            Spacer(Modifier.width(Spacing.sm))
            Column(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onManage)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(
                    "$activeRequests active · $pending to review",
                    style = MaterialTheme.typography.labelMedium,
                    color = AsarSurface,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Manage", style = MaterialTheme.typography.labelMedium, color = AsarSand)
                    Spacer(Modifier.width(2.dp))
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = AsarSand, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
private fun VolunteerImpactStrip() {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(AsarSurface)
            .border(1.dp, AsarOutline, RoundedCornerShape(20.dp))
            .padding(vertical = Spacing.md),
    ) {
        ImpactStat("128", "neighbours\nhelped this week", Modifier.weight(1f))
        Box(Modifier.width(1.dp).height(40.dp).background(AsarOutline).align(Alignment.CenterVertically))
        ImpactStat("42", "volunteers\nactive nearby", Modifier.weight(1f))
        Box(Modifier.width(1.dp).height(40.dp).background(AsarOutline).align(Alignment.CenterVertically))
        ImpactStat("~25 min", "average time\nto first reply", Modifier.weight(1f))
    }
}

@Composable
private fun ImpactStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = AsarPrimary)
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = AsarInkMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}
