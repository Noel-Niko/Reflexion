package com.livingtechusa.reflexion.ui.components.menu

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.DeleteSweep
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.FileCopy
import androidx.compose.material.icons.twotone.Language
import androidx.compose.material.icons.twotone.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.androidpoet.dropdown.Easing
import com.androidpoet.dropdown.EnterAnimation
import com.androidpoet.dropdown.ExitAnimation
import com.androidpoet.dropdown.MenuItem
import com.androidpoet.dropdown.dropDownMenu
import com.androidpoet.dropdown.dropDownMenuColors
import com.livingtechusa.reflexion.ui.viewModels.BuildItemViewModel


fun getMenu(): MenuItem<String> {
    val menu = dropDownMenu<String> {
        item("about", "About") {
            icon(Icons.TwoTone.Language)
        }
        item("copy", "Copy") {
            icon(Icons.TwoTone.FileCopy)
        }
        item("share", "Share") {
            icon(Icons.TwoTone.Share)
            item("to_clipboard", "To clipboard") {
                item("pdf", "PDF")
                item("epub", "EPUB")
                item("web_page", "Web page")
                item("microsoft_word", "Microsoft word")
            }
            item("as_a_file", "As a file") {
                item("pdf", "PDF")
                item("epub", "EPUB")
                item("web_page", "Web page")
                item("microsoft_word", "Microsoft word")
            }
        }
        item("remove", "Remove") {
            icon(Icons.TwoTone.DeleteSweep)
            item("yep", "Yep") {
                icon(Icons.TwoTone.Done)
            }
            item("go_back", "Go back") {
                icon(Icons.TwoTone.Close)
            }
        }
    }
    return menu
}

@ExperimentalAnimationApi
@Composable
fun CustomDropDownMenu(modifier: Modifier, isOpen: Boolean = false, setIsOpen: (Boolean) -> Unit, itemSelected: (String) -> Unit, menu: MenuItem<String> ) {
    val width = LocalConfiguration.current.screenWidthDp
    CustomDropdown(
        modifier = modifier,
        isOpen = isOpen,
        menu = menu,
        colors = dropDownMenuColors(),
        onItemSelected = itemSelected,
        onDismiss = { setIsOpen(false) },
        offset = DpOffset((width * .5).dp, 4.dp),
        enter = EnterAnimation.ElevationScale,
        exit = ExitAnimation.ElevationScale,
        easing = Easing.FastOutSlowInEasing,
        enterDuration = 400,
        exitDuration = 400
    )
}
/*
Enter Animations
 EnterAnimation.FadeIn
 EnterAnimation.SharedAxisXForward
 EnterAnimation.SharedAxisYForward
 EnterAnimation.SharedAxisZForward
 EnterAnimation.ElevationScale
 EnterAnimation.SlideIn
 EnterAnimation.SlideInHorizontally
 EnterAnimation.SlideInVertically
 EnterAnimation.ScaleIn
 EnterAnimation.ExpandIn
 EnterAnimation.ExpandHorizontally
 EnterAnimation.ExpandVertically
Exit Animations
 ExitAnimation.FadeOut
 ExitAnimation.SharedAxisXBackward
 ExitAnimation.SharedAxisYBackward
 ExitAnimation.SharedAxisZBackward
 ExitAnimation.ElevationScale
 ExitAnimation.SlideOut
 ExitAnimation.SlideOutHorizontally
 ExitAnimation.SlideOutVertically
 ExitAnimation.ScaleOut
 ExitAnimation.ShrinkOut
 ExitAnimation.ShrinkHorizontally
 ExitAnimation.ShrinkVertically
Easing
 Easing.FastOutSlowInEasing
 Easing.LinearOutSlowInEasing
 Easing.FastOutLinearInEasing
 Easing.LinearEasing
 */