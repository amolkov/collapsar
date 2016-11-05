package ru.molkov.collapsar.utils.glide;

import android.widget.ImageView;

import com.bumptech.glide.request.target.ImageViewTarget;

public class PaletteBitmapViewTarget extends ImageViewTarget<PaletteBitmap> {

    public PaletteBitmapViewTarget(ImageView view) {
        super(view);
    }

    @Override
    protected void setResource(PaletteBitmap resource) {
        view.setImageBitmap(resource.getBitmap());
    }
}
