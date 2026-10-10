package com.asar.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

private val Family = FontFamily.Default

val AsarTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = Family, fontWeight = FontWeight.Bold,
        fontSize = 36.sp, lineHeight = 42.sp, letterSpacing = (-0.03).em
    ),
    headlineLarge = TextStyle(
        fontFamily = Family, fontWeight = FontWeight.Bold,
        fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = (-0.025).em
    ),
    headlineMedium = TextStyle(
        fontFamily = Family, fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp, lineHeight = 32.sp, letterSpacing = (-0.02).em
    ),
    headlineSmall = TextStyle(
        fontFamily = Family, fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.015).em
    ),
    titleLarge = TextStyle(
        fontFamily = Family, fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp, lineHeight = 25.sp, letterSpacing = (-0.01).em
    ),
    titleMedium = TextStyle(
        fontFamily = Family, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = (-0.005).em
    ),
    titleSmall = TextStyle(
        fontFamily = Family, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Family, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Family, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 21.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Family, fontWeight = FontWeight.Normal,
        fontSize = 12.5.sp, lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Family, fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp, lineHeight = 20.sp, letterSpacing = 0.005.em
    ),
    labelMedium = TextStyle(
        fontFamily = Family, fontWeight = FontWeight.Medium,
        fontSize = 13.sp, lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Family, fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.04.em
    ),
)
