package com.asar.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.asar.app.ui.theme.AsarInkFaint
import com.asar.app.ui.theme.AsarOutline
import com.asar.app.ui.theme.AsarPrimary
import com.asar.app.ui.theme.AsarPrimarySoft
import com.asar.app.ui.theme.AsarSand
import com.asar.app.ui.theme.AsarSurface
import com.asar.app.ui.theme.AsarInk

@Composable
fun AsarBottomBar(
    selected: MainTab,
    onSelect: (MainTab) -> Unit,
    badges: Map<MainTab, Int> = emptyMap(),
) {
    Column(Modifier.fillMaxWidth().background(AsarSurface)) {
        HorizontalDivider(color = AsarOutline)
        Row(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(68.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MainTab.entries.forEach { tab ->
                val isSelected = tab == selected
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .selectable(
                            selected = isSelected,
                            onClick = { onSelect(tab) },
                            role = Role.Tab,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        )
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (tab == MainTab.CREATE) CreateItem(isSelected) else TabItem(tab, isSelected, badges[tab] ?: 0)
                }
            }
        }
    }
}

@Composable
private fun TabItem(tab: MainTab, selected: Boolean, badge: Int) {
    val tint by animateColorAsState(if (selected) AsarPrimary else AsarInkFaint, label = "tabTint")
    val pill by animateColorAsState(if (selected) AsarPrimarySoft else AsarSurface, label = "tabPill")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(pill)
                    .padding(horizontal = 18.dp, vertical = 4.dp)
            ) {
                Icon(tab.icon, contentDescription = tab.label, tint = tint, modifier = Modifier.size(22.dp))
            }
            if (badge > 0) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = 0.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(AsarSand),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        badge.coerceAtMost(9).toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = AsarInk,
                    )
                }
            }
        }
        Spacer(Modifier.height(3.dp))
        Text(
            tab.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = MaterialTheme.typography.labelMedium.letterSpacing,
            ),
            color = tint,
        )
    }
}

@Composable
private fun CreateItem(selected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .shadow(8.dp, RoundedCornerShape(14.dp), ambientColor = AsarPrimary.copy(0.3f), spotColor = AsarPrimary.copy(0.35f))
                .size(width = 52.dp, height = 34.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AsarPrimary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(MainTab.CREATE.icon, contentDescription = "Create request", tint = AsarSurface, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(3.dp))
        Text(
            MainTab.CREATE.label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
            color = if (selected) AsarPrimary else AsarInkFaint,
        )
    }
}
