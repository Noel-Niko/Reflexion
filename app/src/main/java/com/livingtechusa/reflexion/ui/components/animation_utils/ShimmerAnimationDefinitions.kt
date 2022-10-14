//package com.livingtechusa.reflexion.ui.components.animation_utils//
//import android.graphics.drawable.TransitionDrawable
//import android.util.FloatProperty
//import androidx.compose.animation.*
//import androidx.compose.animation.core.AnimationVector
//import androidx.compose.animation.core.AnimationVector1D
//import androidx.compose.animation.core.TwoWayConverter
//import androidx.compose.animation.core.VectorConverter
//import androidx.constraintlayout.compose.override
//
//class ShimmerAnimationDefinitions(
//    private val widthPx: Float,
//    private val heightPx: Float
//) {
//    var gradientWidth: Float
//
//    init {
//        gradientWidth = 0.2f * heightPx
//    }
//
//    enum class AnimationState {
//        START, END
//    }
//
//    val xShimmerPropKey = FloatPropKey("xShimmer")
//    val yShimmerPropKey = FloatPropKey("yShimmer")
//
//    val shimmerTransitionDefinition = transitionDefinition<AnimationState> {
//
//    }
//
//}
//
////class FloatPropKey(
////    override val label: String = "FloatPropKey"
////) : PropKey<Float, AnimationVector1D> {
////    override val typeConverter = Float.VectorConverter
////}
////
////interface PropKey<T, V : AnimationVector> {
////    val typeConverter: TwoWayConverter<T, V>
////    val label: String get() = "Property"
////}
////
////fun <T> transitionDefinition(init: TransitionDrawable<T>, () -> Unit = TransitionDefinition<T>().apply(init)