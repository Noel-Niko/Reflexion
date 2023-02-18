package com.livingtechusa.reflexion.util.scopedStorageUtils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

object ImageUtils {
    fun getRandomColor(): Int {
        return Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
    }

    suspend fun generateImage(color: Int, width: Int, height: Int): Bitmap {
        return withContext(Dispatchers.Default) {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(color)
            return@withContext bitmap
        }
    }

    private const val OPTIONS_NONE = 0x0
    private const val OPTIONS_SCALE_UP = 0x1

    /**
     * Constant used to indicate we should recycle the input in
     * [.extractThumbnail] unless the output is the input.
     */
    private const val OPTIONS_RECYCLE_INPUT = 0x2
    fun extractThumbnail(
        source: Bitmap?, width: Int, height: Int, options: Int = OPTIONS_NONE
    ): Bitmap? {
        if (source == null) {
            return null
        }
        val scale: Float = if (source.width < source.height) {
            width / source.width.toFloat()
        } else {
            height / source.height.toFloat()
        }
        val matrix = Matrix()
        matrix.setScale(scale, scale)
        return transform(
            matrix, source, width, height,
            OPTIONS_SCALE_UP or options
        )
    }

    /**
     * Transform source Bitmap to targeted width and height.
     */
    @Deprecated("")
    private fun transform(
        scaler: Matrix,
        source: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        options: Int
    ): Bitmap? {
        var scaler: Matrix? = scaler
        val scaleUp = options and OPTIONS_SCALE_UP != 0
        val recycle = options and OPTIONS_RECYCLE_INPUT != 0
        val deltaX = source.width - targetWidth
        val deltaY = source.height - targetHeight
        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
            /*
            * In this case the bitmap is smaller, at least in one dimension,
            * than the target.  Transform it by placing as much of the image
            * as possible into the target and leaving the top/bottom or
            * left/right (or both) black.
            */
            val b2 = Bitmap.createBitmap(
                targetWidth, targetHeight,
                Bitmap.Config.ARGB_8888
            )
            val c = Canvas(b2)
            val deltaXHalf = Math.max(0, deltaX / 2)
            val deltaYHalf = Math.max(0, deltaY / 2)
            val src = Rect(
                deltaXHalf,
                deltaYHalf,
                deltaXHalf + Math.min(targetWidth, source.width),
                deltaYHalf + Math.min(targetHeight, source.height)
            )
            val dstX = (targetWidth - src.width()) / 2
            val dstY = (targetHeight - src.height()) / 2
            val dst = Rect(
                dstX,
                dstY,
                targetWidth - dstX,
                targetHeight - dstY
            )
            c.drawBitmap(source, src, dst, null)
            if (recycle) {
                source.recycle()
            }
            c.setBitmap(null)
            return b2
        }
        val bitmapWidthF = source.width.toFloat()
        val bitmapHeightF = source.height.toFloat()
        val bitmapAspect = bitmapWidthF / bitmapHeightF
        val viewAspect = targetWidth.toFloat() / targetHeight
        if (bitmapAspect > viewAspect) {
            val scale = targetHeight / bitmapHeightF
            if (scale < .9f || scale > 1f) {
                scaler!!.setScale(scale, scale)
            } else {
                scaler = null
            }
        } else {
            val scale = targetWidth / bitmapWidthF
            if (scale < .9f || scale > 1f) {
                scaler!!.setScale(scale, scale)
            } else {
                scaler = null
            }
        }
        val b1: Bitmap = if (scaler != null) {
            // this is used for minithumb and crop, so we want to filter here.
            Bitmap.createBitmap(
                source, 0, 0,
                source.width, source.height, scaler, true
            )
        } else {
            source
        }
        if (recycle && b1 != source) {
            source.recycle()
        }
        val dx1 = Math.max(0, b1.width - targetWidth)
        val dy1 = Math.max(0, b1.height - targetHeight)
        val b2 = Bitmap.createBitmap(
            b1,
            dx1 / 2,
            dy1 / 2,
            targetWidth,
            targetHeight
        )
        if (b2 != b1) {
            if (recycle || b1 != source) {
                b1.recycle()
            }
        }
        b2.config
        return b2
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}