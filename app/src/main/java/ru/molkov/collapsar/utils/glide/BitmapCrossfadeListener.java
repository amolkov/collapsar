package ru.molkov.collapsar.utils.glide;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.DrawableCrossFadeFactory;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.animation.GlideAnimationFactory;
import com.bumptech.glide.request.target.Target;

public class BitmapCrossfadeListener<T> implements RequestListener<T, PaletteBitmap> {
    private final GlideAnimationFactory<PaletteBitmap> CROSS_FADE_FACTORY = new GlideAnimationFactory<PaletteBitmap>() {
        private final GlideAnimationFactory<Drawable> realFactory = new DrawableCrossFadeFactory<>();

        @Override
        public GlideAnimation<PaletteBitmap> build(boolean isFromMemoryCache, boolean isFirstResource) {
            final GlideAnimation<Drawable> transition = realFactory.build(isFromMemoryCache, isFirstResource);
            return (PaletteBitmap current, GlideAnimation.ViewAdapter adapter) -> {
                Resources resources = adapter.getView().getResources();
                Drawable currentBitmap = new BitmapDrawable(resources, current.getBitmap());
                return transition.animate(currentBitmap, adapter);
            };
        }
    };

    @Override
    public boolean onException(Exception e, T model, Target<PaletteBitmap> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(PaletteBitmap resource, T model, Target<PaletteBitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
        return CROSS_FADE_FACTORY
                .build(isFromMemoryCache, isFirstResource)
                .animate(resource, (GlideAnimation.ViewAdapter) target);
    }
}
