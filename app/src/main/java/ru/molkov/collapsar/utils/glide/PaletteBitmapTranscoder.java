package ru.molkov.collapsar.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

public class PaletteBitmapTranscoder implements ResourceTranscoder<Bitmap, PaletteBitmap> {
    private final BitmapPool mBitmapPool;

    public PaletteBitmapTranscoder(Context context) {
        mBitmapPool = Glide.get(context).getBitmapPool();
    }

    @Override
    public Resource<PaletteBitmap> transcode(Resource<Bitmap> toTranscode) {
        Palette palette = new Palette.Builder(toTranscode.get()).generate();
        PaletteBitmap result = new PaletteBitmap(toTranscode.get(), palette);
        return new PaletteBitmapResource(result, mBitmapPool);
    }

    @Override
    public String getId() {
        return "";
    }
}
