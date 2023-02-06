package com.livingtechusa.reflexion.navigation


/**
 * Different type of navigation supported by app depending on size and state.
 */
enum class ReflexionNavigationType {
    BOTTOM_NAVIGATION, NAVIGATION_RAIL, PERMANENT_NAVIGATION_DRAWER
}

/**
 * Content shown depending on size and state of device.
 */
enum class ReflexionContentType {
    LIST_ONLY, LIST_AND_DETAIL, BUILD, HOME_SCREEN
}
