package com.asar.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.asar.app.ui.theme.AsarBackground
import com.asar.app.ui.theme.AsarInk
import com.asar.app.ui.theme.AsarPrimary
import com.asar.app.ui.theme.AsarSand

/** The ASAR mark: an "A" whose crossbar is two hands meeting, with a warm sun above. */
@Composable
fun AsarLogoMark(modifier: Modifier = Modifier, size: Dp = 40.dp) {
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.3f))
            .background(AsarPrimary),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(size)) {
            val w = this.size.width
            val stroke = Stroke(width = w * 0.075f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val a = Path().apply {
                moveTo(w * 0.30f, w * 0.74f)
                lineTo(w * 0.50f, w * 0.30f)
                lineTo(w * 0.70f, w * 0.74f)
            }
            drawPath(a, AsarBackground, style = stroke)
            val bar = Path().apply {
                moveTo(w * 0.37f, w * 0.60f)
                quadraticTo(w * 0.50f, w * 0.50f, w * 0.63f, w * 0.60f)
            }
            drawPath(bar, AsarBackground, style = stroke)
            drawCircle(AsarSand, radius = w * 0.055f, center = Offset(w * 0.5f, w * 0.16f))
        }
    }
}

@Composable
fun AsarWordmark(modifier: Modifier = Modifier, markSize: Dp = 36.dp) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        AsarLogoMark(size = markSize)
        Spacer(Modifier.width(10.dp))
        Text(
            "asar",
            style = TextStyle(
                fontSize = (markSize.value * 0.66f).sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.03).em,
                color = AsarInk,
            ),
        )
    }
}
