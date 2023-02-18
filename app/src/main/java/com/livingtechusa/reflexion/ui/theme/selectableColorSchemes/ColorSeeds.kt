package com.livingtechusa.reflexion.ui.theme.selectableColorSchemes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.livingtechusa.reflexion.util.sharedPreferences.UserPreferencesUtil

@Composable
fun returnSecondaryColors(int: Int): Pair<Color, Color> {
    when (UserPreferencesUtil.getCurrentUserModeSelection(LocalContext.current)) {
        0 -> {
            return when (int) {
                1 -> Pair(md_theme_1_light_secondaryContainer, md_theme_1_light_onSecondaryContainer)
                2 -> Pair(md_theme_2_light_secondaryContainer, md_theme_2_light_onSecondaryContainer)
                3 -> Pair(md_theme_3_light_secondaryContainer, md_theme_3_light_onSecondaryContainer)
                4 -> Pair(md_theme_4_light_secondaryContainer, md_theme_4_light_onSecondaryContainer)
                5 -> Pair(md_theme_5_light_secondaryContainer, md_theme_5_light_onSecondaryContainer)
                6 -> Pair(md_theme_6_light_secondaryContainer, md_theme_6_light_onSecondaryContainer)
                else -> Pair(Color.White, Color.Black)
            }
        }

        1 -> {
            return when (int) {
                1 -> Pair(md_theme_1_dark_secondaryContainer, md_theme_1_dark_onSecondaryContainer)
                2 -> Pair(md_theme_2_dark_secondaryContainer, md_theme_2_dark_onSecondaryContainer)
                3 -> Pair(md_theme_3_dark_secondaryContainer, md_theme_3_dark_onSecondaryContainer)
                4 -> Pair(md_theme_4_dark_secondaryContainer, md_theme_4_dark_onSecondaryContainer)
                5 -> Pair(md_theme_5_dark_secondaryContainer, md_theme_5_dark_onSecondaryContainer)
                6 -> Pair(md_theme_6_dark_secondaryContainer, md_theme_6_dark_onSecondaryContainer)
                else -> Pair(Color.Black, Color.White)
            }
        }

        else -> {
            if (isSystemInDarkTheme()) {
                return when (int) {
                    1 -> Pair(md_theme_1_dark_secondaryContainer, md_theme_1_dark_onSecondaryContainer)
                    2 -> Pair(md_theme_2_dark_secondaryContainer, md_theme_2_dark_onSecondaryContainer)
                    3 -> Pair(md_theme_3_dark_secondaryContainer, md_theme_3_dark_onSecondaryContainer)
                    4 -> Pair(md_theme_4_dark_secondaryContainer, md_theme_4_dark_onSecondaryContainer)
                    5 -> Pair(md_theme_5_dark_secondaryContainer, md_theme_5_dark_onSecondaryContainer)
                    6 -> Pair(md_theme_6_dark_secondaryContainer, md_theme_6_dark_onSecondaryContainer)
                    else -> Pair(Color.Black, Color.White)
                }
            } else {
                return when (int) {
                    1 -> Pair(md_theme_1_light_secondaryContainer, md_theme_1_light_onSecondaryContainer)
                    2 -> Pair(md_theme_2_light_secondaryContainer, md_theme_2_light_onSecondaryContainer)
                    3 -> Pair(md_theme_3_light_secondaryContainer, md_theme_3_light_onSecondaryContainer)
                    4 -> Pair(md_theme_4_light_secondaryContainer, md_theme_4_light_onSecondaryContainer)
                    5 -> Pair(md_theme_5_light_secondaryContainer, md_theme_5_light_onSecondaryContainer)
                    6 -> Pair(md_theme_6_light_secondaryContainer, md_theme_6_light_onSecondaryContainer)
                    else -> Pair(Color.White, Color.Black)
                }
            }
        }
    }
}

