package com.asar.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.VolunteerActivism
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role as SemanticsRole
import androidx.compose.ui.unit.dp
import com.asar.app.data.model.Role
import com.asar.app.ui.theme.AsarInk
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarPrimary
import com.asar.app.ui.theme.AsarSurface
import com.asar.app.ui.theme.AsarSurfaceSoft

/** Segmented control with a sliding thumb for switching Volunteer / Requester mode. */
@Composable
fun RoleSwitcher(role: Role, onChange: (Role) -> Unit, modifier: Modifier = Modifier) {
    SegmentedControl(
        options = listOf(
            Segment("Volunteer", Icons.Rounded.VolunteerActivism),
            Segment("Requester", Icons.Rounded.Campaign),
        ),
        selectedIndex = if (role == Role.VOLUNTEER) 0 else 1,
        onSelect = { onChange(if (it == 0) Role.VOLUNTEER else Role.REQUESTER) },
        modifier = modifier,
    )
}

data class Segment(val label: String, val icon: ImageVector? = null)

@Composable
fun SegmentedControl(
    options: List<Segment>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(AsarSurfaceSoft)
            .padding(4.dp)
    ) {
        val segmentWidth = maxWidth / options.size
        val offset by animateDpAsState(
            targetValue = segmentWidth * selectedIndex,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 500f),
            label = "thumb",
        )
        Box(
            Modifier
                .offset(x = offset)
                .width(segmentWidth)
                .fillMaxHeight()
                .shadow(4.dp, RoundedCornerShape(12.dp), ambientColor = AsarInk.copy(0.06f), spotColor = AsarInk.copy(0.08f))
                .clip(RoundedCornerShape(12.dp))
                .background(AsarSurface)
        )
        Row(Modifier.fillMaxWidth().fillMaxHeight()) {
            options.forEachIndexed { index, option ->
                val selected = index == selectedIndex
                val color by animateColorAsState(if (selected) AsarPrimary else AsarInkMuted, label = "segColor")
                Row(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .selectable(
                            selected = selected,
                            onClick = { onSelect(index) },
                            role = SemanticsRole.Tab,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (option.icon != null) {
                        Icon(option.icon, null, tint = color, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(
                        option.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) AsarInk else AsarInkMuted,
                    )
                }
            }
        }
    }
}
