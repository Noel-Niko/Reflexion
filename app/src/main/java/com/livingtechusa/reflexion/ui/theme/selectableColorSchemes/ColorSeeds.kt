package com.livingtechusa.reflexion.ui.theme.selectableColorSchemes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.livingtechusa.reflexion.util.sharedPreferences.UserPreferencesUtil

@Composable
fun returnPrimaryColors(int: Int): Pair<Color, Color> {
    when (UserPreferencesUtil.getCurrentUserModeSelection(LocalContext.current)) {
        0 -> {
            return when (int) {
                1 -> Pair(md_theme_1_light_primaryContainer, md_theme_1_light_onPrimaryContainer)
                2 -> Pair(md_theme_2_light_primaryContainer, md_theme_2_light_onPrimaryContainer)
                3 -> Pair(md_theme_3_light_primaryContainer, md_theme_3_light_onPrimaryContainer)
                4 -> Pair(md_theme_4_light_primaryContainer, md_theme_4_light_onPrimaryContainer)
                5 -> Pair(md_theme_5_light_primaryContainer, md_theme_5_light_onPrimaryContainer)
                6 -> Pair(md_theme_6_light_primaryContainer, md_theme_6_light_onPrimaryContainer)
                else -> Pair(Color.White, Color.Black)
            }
        }

        1 -> {
            return when (int) {
                1 -> Pair(md_theme_1_dark_primaryContainer, md_theme_1_dark_onPrimaryContainer)
                2 -> Pair(md_theme_2_dark_primaryContainer, md_theme_2_dark_onPrimaryContainer)
                3 -> Pair(md_theme_3_dark_primaryContainer, md_theme_3_dark_onPrimaryContainer)
                4 -> Pair(md_theme_4_dark_primaryContainer, md_theme_4_dark_onPrimaryContainer)
                5 -> Pair(md_theme_5_dark_primaryContainer, md_theme_5_dark_onPrimaryContainer)
                6 -> Pair(md_theme_6_dark_primaryContainer, md_theme_6_dark_onPrimaryContainer)
                else -> Pair(Color.Black, Color.White)
            }
        }

        else -> {
            if (isSystemInDarkTheme()) {
                return when (int) {
                    1 -> Pair(md_theme_1_dark_primaryContainer, md_theme_1_dark_onPrimaryContainer)
                    2 -> Pair(md_theme_2_dark_primaryContainer, md_theme_2_dark_onPrimaryContainer)
                    3 -> Pair(md_theme_3_dark_primaryContainer, md_theme_3_dark_onPrimaryContainer)
                    4 -> Pair(md_theme_4_dark_primaryContainer, md_theme_4_dark_onPrimaryContainer)
                    5 -> Pair(md_theme_5_dark_primaryContainer, md_theme_5_dark_onPrimaryContainer)
                    6 -> Pair(md_theme_6_dark_primaryContainer, md_theme_6_dark_onPrimaryContainer)
                    else -> Pair(Color.Black, Color.White)
                }
            } else {
                return when (int) {
                    1 -> Pair(md_theme_1_light_primaryContainer, md_theme_1_light_onPrimaryContainer)
                    2 -> Pair(md_theme_2_light_primaryContainer, md_theme_2_light_onPrimaryContainer)
                    3 -> Pair(md_theme_3_light_primaryContainer, md_theme_3_light_onPrimaryContainer)
                    4 -> Pair(md_theme_4_light_primaryContainer, md_theme_4_light_onPrimaryContainer)
                    5 -> Pair(md_theme_5_light_primaryContainer, md_theme_5_light_onPrimaryContainer)
                    6 -> Pair(md_theme_6_light_primaryContainer, md_theme_6_light_onPrimaryContainer)
                    else -> Pair(Color.White, Color.Black)
                }
            }
        }
    }
}

