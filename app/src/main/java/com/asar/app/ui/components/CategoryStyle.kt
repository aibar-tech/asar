package com.asar.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Elderly
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.asar.app.data.model.TaskCategory
import com.asar.app.ui.theme.TintClay
import com.asar.app.ui.theme.TintOchre
import com.asar.app.ui.theme.TintPlum
import com.asar.app.ui.theme.TintRose
import com.asar.app.ui.theme.TintSage
import com.asar.app.ui.theme.TintSky
import com.asar.app.ui.theme.TintSlate
import com.asar.app.ui.theme.TintTeal

data class CategoryStyle(val icon: ImageVector, val foreground: Color, val background: Color)

val TaskCategory.style: CategoryStyle
    get() {
        val (icon, tint) = when (this) {
            TaskCategory.ELDERLY_CARE -> Icons.Rounded.Elderly to TintRose
            TaskCategory.MOVING -> Icons.Rounded.Inventory2 to TintClay
            TaskCategory.ECO -> Icons.Rounded.Eco to TintSage
            TaskCategory.EDUCATION -> Icons.Rounded.School to TintSky
            TaskCategory.FOOD -> Icons.Rounded.Restaurant to TintOchre
            TaskCategory.ANIMALS -> Icons.Rounded.Pets to TintPlum
            TaskCategory.EVENTS -> Icons.Rounded.Celebration to TintTeal
            TaskCategory.TECH_HELP -> Icons.Rounded.Smartphone to TintSlate
        }
        return CategoryStyle(icon, tint.first, tint.second)
    }

/** Short label for chips where space is tight. */
val TaskCategory.shortLabel: String
    get() = when (this) {
        TaskCategory.ELDERLY_CARE -> "Elderly"
        TaskCategory.MOVING -> "Moving"
        TaskCategory.ECO -> "Eco"
        TaskCategory.EDUCATION -> "Education"
        TaskCategory.FOOD -> "Food"
        TaskCategory.ANIMALS -> "Animals"
        TaskCategory.EVENTS -> "Events"
        TaskCategory.TECH_HELP -> "Digital"
    }
