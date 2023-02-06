package com.livingtechusa.reflexion.ui.components.menu

import com.androidpoet.dropdown.Easing

/*[AnimationProp] is properties of animation as compose has experimental apis we can change the internal code when needed.*/
internal data class CustomAnimationProp(
    val enterDuration: Int,
    val exitDuration: Int,
    val delay: Int = 0,
    val easing: Easing,
    val initialAlpha: Float = 0.92f
)