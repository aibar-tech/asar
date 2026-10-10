package com.asar.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Rounded.Home),
    MY_TASKS("My Tasks", Icons.AutoMirrored.Rounded.Assignment),
    CREATE("Create", Icons.Rounded.Add),
    PROFILE("Profile", Icons.Rounded.Person),
}

/** Screens pushed on top of the tab shell. */
sealed interface Overlay {
    data class TaskDetails(val taskId: String, val openApply: Boolean = false) : Overlay
    data object AiLab : Overlay
}
