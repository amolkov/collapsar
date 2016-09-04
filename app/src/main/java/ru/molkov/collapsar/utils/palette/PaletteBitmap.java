package ru.molkov.collapsar.utils.palette;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

public class PaletteBitmap {
    public final Bitmap mBitmap;
    public final Palette mPalette;

    public PaletteBitmap(Bitmap bitmap, Palette palette) {
        mBitmap = bitmap;
        mPalette = palette;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public Palette getPalette() {
        return mPalette;
    }
}
