package com.livingtechusa.reflexion.ui.components.menu

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowLeft
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.androidpoet.dropdown.DropDownMenuColors
import com.androidpoet.dropdown.Easing
import com.androidpoet.dropdown.EnterAnimation
import com.androidpoet.dropdown.ExitAnimation
import com.androidpoet.dropdown.MenuItem
import com.androidpoet.dropdown.MenuItemIcon
import com.androidpoet.dropdown.MenuState
import com.androidpoet.dropdown.dropDownMenuColors
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK_STRING

public val MAX_WIDTH: Dp = 192.dp

/*[animateContent]  enter and exit animation for the menu content. */
@ExperimentalAnimationApi
internal fun animateContent(
    animationProp: CustomAnimationProp,
    enterAnimation: EnterAnimation,
    exitAnimation: ExitAnimation
): ContentTransform {
    return getMetaphorEnterAnimation(animationProp, enterAnimation) with
            getMetaphorExitAnimation(animationProp, exitAnimation)
}

/*[isNavigatingBack]  check if menu is navigating back. */
public fun <T : Any> isNavigatingBack(
    currentMenu: MenuItem<T>,
    nextMenu: MenuItem<T>
): Boolean {
    return currentMenu.hasParent() && nextMenu == currentMenu.parent!!
}

/*[Dropdown]  DropdownMenu wrapper claas to add cascade effect and animations . */

@ExperimentalAnimationApi
@Composable
public fun <T : Any> CustomDropdown(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    menu: MenuItem<T>,
    colors: DropDownMenuColors = dropDownMenuColors(),
    offset: DpOffset = DpOffset.Zero,
    enter: EnterAnimation = EnterAnimation.FadeIn,
    exit: ExitAnimation = ExitAnimation.FadeOut,
    easing: Easing = Easing.FastOutLinearInEasing,
    enterDuration: Int = 500,
    exitDuration: Int = 500,
    onItemSelected: (T) -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(colors.backgroundColor),
        expanded = isOpen,
        onDismissRequest = onDismiss,
        offset = offset
    ) {
        val state by remember { mutableStateOf(MenuState(menu)) }
        AnimatedContent(
            targetState = state.currentMenuItem,
            transitionSpec = {
                if (isNavigatingBack(currentMenu = initialState, nextMenu = targetState)) {
                    animateContent(
                        CustomAnimationProp(
                            enterDuration,
                            exitDuration,
                            0,
                            easing,
                        ),
                        enter, exit
                    )
                } else {
                    animateContent(
                        CustomAnimationProp(
                            enterDuration,
                            exitDuration,
                            0,
                            easing,
                        ),
                        enter, exit
                    )
                }
            }
        ) { targetMenu ->
            DropdownContent(
                state = state,
                targetMenu = targetMenu,
                onItemSelected = onItemSelected,
                colors = colors,
                onTitleSelected = onItemSelected
            )
        }
    }
}

/*[DropdownContent] represents menu content properties  . */
@Composable
public fun <T : Any> DropdownContent(
    state: MenuState<T>,
    targetMenu: MenuItem<T>,
    onItemSelected: (T) -> Unit,
    colors: DropDownMenuColors,
    onTitleSelected: (T) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        if (targetMenu.hasParent()) {
            CascadeHeaderItem(
                title = targetMenu.title,
                contentColor = colors.contentColor
            ) {
                state.currentMenuItem = targetMenu.parent!!
            }
        }
        if (targetMenu.hasChildren()) {
            for (item in targetMenu.children!!) {
                if (item.hasChildren()) {
                    ParentItem(
                        id = item.id,
                        title = item.title,
                        icon = item.icon,
                        contentColor = colors.contentColor,
                        onClick = onTitleSelected
                    ) {  id ->
                        val child = targetMenu.getChild(id)
                        if (child != null) {
                           state.currentMenuItem = child
                        } else {
                            throw IllegalStateException("Invalid item id : $id")
                        }
                    }
                } else {
                    ChildItem(
                        id = item.id,
                        title = item.title,
                        icon =  item.icon,
                        contentColor =  colors.contentColor,
                        onClick = onItemSelected
                    )
                }
            }
        }
    }
}

/*[Space] add space content . */
@Composable
public fun Space() {
    Spacer(modifier = Modifier.width(12.dp))
}

/*[MenuItemIcon] icon properties. */
@Composable
public fun CustomMenuItemIcon(icon: ImageVector, tint: Color ) {
    Icon(
        modifier = Modifier.size(24.dp),
        imageVector = icon,
        contentDescription = null,
        tint = tint,
    )
}

/*[MenuItemText] text properties. */
@Composable
public fun MenuItemText(
    modifier: Modifier,
    text: String,
    color: Color,
    isHeaderText: Boolean = false,
) {
    val style = if (isHeaderText) {
        MaterialTheme.typography.subtitle2
    } else {
        MaterialTheme.typography.subtitle1
    }

    Text(
        modifier = modifier,
        text = text,
        style = style,
        color = color,
    )
}

/*[MenuItem] DropdownMenuItem  wrapper to handle onclick and user interaction */
@Composable
public fun CustomMenuItem(onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
    DropdownMenuItem(
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        content = content
    )
}

/*[CascadeHeaderItem] represent header item of the menu */
@Composable
public fun CascadeHeaderItem(
    title: String,
    contentColor: Color,
    onClick: () -> Unit,
) {
    CustomMenuItem(
        onClick = { onClick() }) {
       CustomMenuItemIcon(
            icon = Icons.Rounded.ArrowLeft,
            tint = contentColor.copy(alpha = ContentAlpha.medium)
        )
        Spacer(modifier = Modifier.width(4.dp))
        MenuItemText(
            modifier = Modifier.weight(1f),
            text = title,
            color = contentColor.copy(alpha = ContentAlpha.medium),
            isHeaderText = true,
        )
    }
}

/*[ParentItem] parentItem of the menu*/
@Composable
public fun <T> ParentItem(
    id: T,
    title: String,
    icon: ImageVector?,
    contentColor: Color,
    onClick: (T) -> Unit,
    onTitleSelected: (T) -> Unit
) {
    CustomMenuItem(
        onClick = { onClick(id) }) {
        if (icon != null) {
            CustomMenuItemIcon(icon = icon, tint = contentColor)
            Space()
        }
        MenuItemText(
            modifier = Modifier.weight(1f).clickable(onClick = { onTitleSelected(id) }),
            text = title,
            color = contentColor,
        )
        Space()
        CustomMenuItemIcon(icon = Icons.Rounded.ArrowRight, tint = contentColor)
    }
}

/*[ChildItem] childItem of the menu*/
@Composable
public fun <T> ChildItem(
    id: T,
    title: String,
    icon: ImageVector?,
    contentColor: Color,
    onClick: (T) -> Unit,
) {
    MenuItem(onClick = { onClick(id) }) {
        if (icon != null) {
            CustomMenuItemIcon(icon = icon, tint = contentColor)
            Space()
        }
        MenuItemText(
            modifier = Modifier.weight(1f),
            text = title,
            color = contentColor,
        )
    }
}
