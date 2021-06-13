package com.itba.runningMate.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;

public class ImageProcessing {


    /*
        Other references:
            - https://stackoverflow.com/questions/7200535/how-to-convert-views-to-bitmaps
            - https://stackoverflow.com/questions/5536066/convert-view-to-bitmap-on-android
     */

    /**
     * Converts DP into pixels.
     * <p>
     * Ref: https://github.com/pranavpandey/dynamic-utils/blob/df2fa843cd1ed0b9fd7c80e236bc99a40d546bba/dynamic-utils/src/main/java/com/pranavpandey/android/dynamic/utils/DynamicUnitUtils.java#L34
     *
     * @param dp The value in DP to be converted into pixels.
     * @return The converted value in pixels.
     */
    public static int convertDpToPixels(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, Resources.getSystem().getDisplayMetrics()));
    }

    /**
     * Creates a bitmap from the supplied view.
     * <p>
     * Ref: https://dev.to/pranavpandey/android-create-bitmap-from-a-view-3lck
     *
     * @param view   The view to get the bitmap.
     * @param width  The width for the bitmap.
     * @param height The height for the bitmap.
     * @return The bitmap from the supplied drawable.
     */
    public @NonNull
    static Bitmap createBitmapFromView(@NonNull View view, int width, int height) {
        if (width > 0 && height > 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(convertDpToPixels(width), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(convertDpToPixels(height), View.MeasureSpec.EXACTLY));
        }
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //second, set the width and height of inflated view
//        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
//                View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
}
