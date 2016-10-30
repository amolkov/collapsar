package ru.molkov.collapsar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.util.TypedValue;

import ru.molkov.collapsar.R;

public class ThemeUtils {

    private ThemeUtils() {
    }

    @ColorInt
    public static int getThemeColor(Context context, int attributeColor) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attributeColor, value, true);
        return value.data;
    }

    public static ColorDrawable[] getPlaceholderColors(Context context) {
        ColorDrawable[] result;

        final TypedArray typedArray = context.obtainStyledAttributes(R.styleable.placeholderColors);
        final int colorArrayId = typedArray.getResourceId(R.styleable.placeholderColors_placeholderColors, 0);
        if (colorArrayId != 0) {
            int[] placeholderColors = context.getResources().getIntArray(colorArrayId);
            result = new ColorDrawable[placeholderColors.length];
            for (int i = 0; i < placeholderColors.length; i++) {
                result[i] = new ColorDrawable(placeholderColors[i]);
            }
        } else {
            result = new ColorDrawable[]{new ColorDrawable(Color.DKGRAY)};
        }

        return result;
    }

    public static boolean isLightTheme(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.key_light_theme), false);
    }
}
