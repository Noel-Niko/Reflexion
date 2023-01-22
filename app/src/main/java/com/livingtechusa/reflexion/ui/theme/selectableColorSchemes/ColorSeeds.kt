package com.livingtechusa.reflexion.ui.theme.selectableColorSchemes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun returnPrimaryColors(int: Int): Pair<Color , Color>{
    if(isSystemInDarkTheme()) {
        return when (int) {
            1 -> Pair(md_theme_1_light_primary, md_theme_1_light_onPrimary)
            2 -> Pair(md_theme_2_light_primary, md_theme_2_light_onPrimary)
            3 -> Pair(md_theme_3_light_primary, md_theme_3_light_onPrimary)
            4 -> Pair(md_theme_4_light_primary, md_theme_4_light_onPrimary)
            5 -> Pair(md_theme_5_light_primary, md_theme_5_light_onPrimary)
            6 -> Pair(md_theme_6_light_primary, md_theme_6_light_onPrimary)
            else -> Pair(md_theme_1_light_primary, md_theme_1_light_onPrimary)
        }
    } else {
        return when (int) {
            1 -> Pair(md_theme_1_dark_primary, md_theme_1_dark_onPrimary)
            2 -> Pair(md_theme_2_dark_primary, md_theme_2_dark_onPrimary)
            3 -> Pair(md_theme_3_dark_primary, md_theme_3_dark_onPrimary)
            4 -> Pair(md_theme_4_dark_primary, md_theme_4_dark_onPrimary)
            5 -> Pair(md_theme_5_dark_primary, md_theme_5_dark_onPrimary)
            6 -> Pair(md_theme_6_dark_primary, md_theme_6_dark_onPrimary)
            else -> Pair(md_theme_1_dark_primary, md_theme_1_dark_onPrimary)
        }
    }
}

