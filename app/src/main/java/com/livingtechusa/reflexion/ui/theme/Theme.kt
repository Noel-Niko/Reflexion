package com.livingtechusa.reflexion.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import com.livingtechusa.reflexion.ui.theme.selectableColorSchemes.CustomTheme1
import com.livingtechusa.reflexion.ui.theme.selectableColorSchemes.CustomTheme2
import com.livingtechusa.reflexion.ui.theme.selectableColorSchemes.CustomTheme3
import com.livingtechusa.reflexion.ui.theme.selectableColorSchemes.CustomTheme4
import com.livingtechusa.reflexion.ui.theme.selectableColorSchemes.CustomTheme5
import com.livingtechusa.reflexion.ui.theme.selectableColorSchemes.CustomTheme6
import com.livingtechusa.reflexion.util.sharedPreferences.UserPreferencesUtil

//
//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)
//
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

//object ReflexionItemsColors {
//
//    val salem @Composable get() = colorResource(id = R.color.salem)
//    val rwGrey @Composable get() = colorResource(id = R.color.rw_grey)
//}
//
//private val reflexionItemsShapes
//    get() = Shapes(
//        small = RoundedCornerShape(4.dp),
//        medium = RoundedCornerShape(4.dp),
//        large = RoundedCornerShape(0.dp)
//    )
//
private val reflexionItemsTypography
    get() = Typography(
        bodySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
//
//private val DarkColorPalette
//    @Composable get() = darkColors(
//        primary = ReflexionItemsColors.rwGrey,
//    )
//
//private val LightColorPalette
//    @Composable get() = lightColors(
//        primary = ReflexionItemsColors.rwGrey,
//    )


@Composable
fun ReflexionDynamicTheme(
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isDarkMode: Boolean =
        when (UserPreferencesUtil.getCurrentUserModeSelection(LocalContext.current)) {
            0 -> { false }
            1 -> { true }
            else -> { isSystemInDarkTheme() }
        }

    val context = LocalContext.current
    val selectedTheme = UserPreferencesUtil.getCurrentUserThemeSelection(context = context)
    if (selectedTheme != -1) {
        when (selectedTheme) {
            1 -> {
                return CustomTheme1(useDarkTheme = isDarkMode, content = content)
            }

            2 -> {
                return CustomTheme2(useDarkTheme = isDarkMode, content = content)
            }

            3 -> {
                return CustomTheme3(useDarkTheme = isDarkMode, content = content)
            }

            4 -> {
                return CustomTheme4(useDarkTheme = isDarkMode, content = content)
            }

            5 -> {
                return CustomTheme5(useDarkTheme = isDarkMode, content = content)
            }

            6 -> {
                return CustomTheme6(useDarkTheme = isDarkMode, content = content)
            }

            else -> {
                defaultDynamicTheme(isDarkMode, dynamicColor, content)
            }
        }
    } else {
        defaultDynamicTheme(isDarkMode, dynamicColor, content)
    }


}

@Composable
fun defaultDynamicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}