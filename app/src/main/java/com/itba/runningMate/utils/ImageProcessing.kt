package com.itba.runningMate.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.TypedValue
import android.view.View
import java.io.FileOutputStream
import kotlin.math.roundToInt

object ImageProcessing {
    /*
        Other references:
            - https://stackoverflow.com/questions/7200535/how-to-convert-views-to-bitmaps
            - https://stackoverflow.com/questions/5536066/convert-view-to-bitmap-on-android
     */
    /**
     * Converts DP into pixels.
     *
     *
     * Ref: https://github.com/pranavpandey/dynamic-utils/blob/df2fa843cd1ed0b9fd7c80e236bc99a40d546bba/dynamic-utils/src/main/java/com/pranavpandey/android/dynamic/utils/DynamicUnitUtils.java#L34
     *
     * @param dp The value in DP to be converted into pixels.
     * @return The converted value in pixels.
     */
    fun convertDpToPixels(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp, Resources.getSystem().displayMetrics
        ).roundToInt()
    }

    /**
     * Creates a bitmap from the supplied view.
     *
     *
     * Ref: https://dev.to/pranavpandey/android-create-bitmap-from-a-view-3lck
     *
     * @param view   The view to get the bitmap.
     * @param width  The width for the bitmap.
     * @param height The height for the bitmap.
     * @return The bitmap from the supplied drawable.
     */
    fun createBitmapFromView(view: View, width: Int, height: Int): Bitmap {
        if (width > 0 && height > 0) {
            view.measure(
                View.MeasureSpec.makeMeasureSpec(
                    convertDpToPixels(width.toFloat()),
                    View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.makeMeasureSpec(
                    convertDpToPixels(height.toFloat()),
                    View.MeasureSpec.EXACTLY
                )
            )
        }
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //second, set the width and height of inflated view
//        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
//                View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    @Throws(Exception::class)
    fun compress(bitmap: Bitmap, outputStream: FileOutputStream) {
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
        outputStream.flush()
        outputStream.close()
    }
}