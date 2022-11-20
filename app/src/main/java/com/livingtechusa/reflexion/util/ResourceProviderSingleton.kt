package com.livingtechusa.reflexion.util

import android.content.res.Resources
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

object ResourceProviderSingleton : ResourceProviderInterface {

    var app: BaseApplication = BaseApplication.getInstance()

    private val resources: Resources
        get() = app.resources

    override fun getString(@StringRes resId: Int): String {
        return app.getString(resId)
    }

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return app.getString(resId, *formatArgs)
    }

    override fun getQuantityString(
        @PluralsRes resId: Int,
        quantity: Int,
        vararg formatArgs: Any
    ): String {
        return resources.getQuantityString(resId, quantity, *formatArgs)
    }

    override fun getInteger(@IntegerRes resId: Int): Int {
        return resources.getInteger(resId)
    }

    override fun getDimen(@DimenRes resId: Int): Float {
        return resources.getDimension(resId)
    }
}
