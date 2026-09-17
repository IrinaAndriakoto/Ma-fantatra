package com.ma_fantatra.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Espacements constants, échelle unique pour toute l'app. */
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
}

/** Espacement des écrans listes — bas plus grand pour dégager la barre de navigation. */
fun screenPadding(): PaddingValues = PaddingValues(
    start = Spacing.lg,
    top = Spacing.lg,
    end = Spacing.lg,
    bottom = Spacing.xxl + Spacing.lg,
)

fun detailPadding(): PaddingValues = PaddingValues(
    start = Spacing.lg,
    top = Spacing.md,
    end = Spacing.lg,
    bottom = Spacing.xxl + Spacing.lg,
)