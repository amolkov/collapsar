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
    private static ThemeUtils INSTANCE = null;
    private final Context mContext;

    public static ThemeUtils getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ThemeUtils(context);
        }
        return INSTANCE;
    }

    private ThemeUtils(Context context) {
        mContext = context;
    }

    @ColorInt
    public int getThemeColor(int attributeColor) {
        TypedValue value = new TypedValue();
        mContext.getTheme().resolveAttribute(attributeColor, value, true);
        return value.data;
    }

    public ColorDrawable[] getPlaceholderColors() {
        ColorDrawable[] result;

        final TypedArray typedArray = mContext.obtainStyledAttributes(R.styleable.placeholderColors);
        final int colorArrayId = typedArray.getResourceId(R.styleable.placeholderColors_placeholderColors, 0);
        if (colorArrayId != 0) {
            int[] placeholderColors = mContext.getResources().getIntArray(colorArrayId);
            result = new ColorDrawable[placeholderColors.length];
            for (int i = 0; i < placeholderColors.length; i++) {
                result[i] = new ColorDrawable(placeholderColors[i]);
            }
        } else {
            result = new ColorDrawable[]{new ColorDrawable(Color.DKGRAY)};
        }

        return result;
    }

    public boolean isLightTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean(mContext.getString(R.string.key_light_theme), false);
    }
}
