package ru.molkov.collapsar.utils.palette;

import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Util;

public class PaletteBitmapResource implements Resource<PaletteBitmap> {
    private PaletteBitmap mPaletteBitmap;
    private BitmapPool mBitmapPool;

    public PaletteBitmapResource(PaletteBitmap paletteBitmap, BitmapPool bitmapPool) {
        mPaletteBitmap = paletteBitmap;
        mBitmapPool = bitmapPool;
    }

    @Override
    public PaletteBitmap get() {
        return mPaletteBitmap;
    }

    @Override
    public int getSize() {
        return Util.getBitmapByteSize(mPaletteBitmap.getBitmap());
    }

    @Override
    public void recycle() {
        if (!mBitmapPool.put(mPaletteBitmap.getBitmap())) {
            mPaletteBitmap.getBitmap().recycle();
        }
    }
}
