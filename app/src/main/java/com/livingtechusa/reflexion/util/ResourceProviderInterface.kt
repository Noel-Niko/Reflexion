package com.livingtechusa.reflexion.util

import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

interface ResourceProviderInterface {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any): String
    fun getInteger(@IntegerRes resId: Int): Int
    fun getDimen(@DimenRes resId: Int): Float
}
