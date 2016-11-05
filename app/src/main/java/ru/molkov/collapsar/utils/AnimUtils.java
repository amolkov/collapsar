package ru.molkov.collapsar.utils;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.support.design.widget.FloatingActionButton;
import android.util.Property;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class AnimUtils {
    private static Interpolator mFastOutSlowIn;
    private static Interpolator mLinear;

    public static Interpolator getFastOutSlowInInterpolator(Context context) {
        if (mFastOutSlowIn == null) {
            mFastOutSlowIn = AnimationUtils.loadInterpolator(context,
                    android.R.interpolator.fast_out_slow_in);
        }
        return mFastOutSlowIn;
    }

    public static Interpolator getLinearInterpolator() {
        if (mLinear == null) {
            mLinear = new LinearInterpolator();
        }
        return mLinear;
    }

    public static void translationView(final View view, int delay) {
        view.setVisibility(View.INVISIBLE);
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(view, "translationY", dpToPx(view, 72), 0)
                .setDuration(300);

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setStartDelay(delay);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setVisibility(View.VISIBLE);
            }
        });
        animator.start();
    }

    public static void initFabShow(final FloatingActionButton fab, int delay) {
        fab.animate().cancel();
        fab.setScaleX(0f);
        fab.setScaleY(0f);
        fab.setAlpha(0f);
        fab.setVisibility(View.VISIBLE);
        fab.animate()
                .setDuration(200)
                .setStartDelay(delay)
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setInterpolator(getLinearInterpolator());

    }

    public static void saturationImage(final ImageView view) {
        view.setHasTransientState(true);
        final ObservableColorMatrix colorMatrix = new ObservableColorMatrix();
        final ObjectAnimator animator = ObjectAnimator.ofFloat(
                colorMatrix, ObservableColorMatrix.SATURATION, 0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            }
        });
        animator.setDuration(2000L);
        animator.setInterpolator(getFastOutSlowInInterpolator(view.getContext()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.clearColorFilter();
                view.setHasTransientState(false);
            }
        });
        animator.start();
    }

    public static void saturationBackground(final View view, int colorEnd) {
        int colorStart = view.getSolidColor();

        ValueAnimator animator = ObjectAnimator.ofInt(view, "backgroundColor", colorStart, colorEnd);
        animator.setDuration(1500L);
        animator.setInterpolator(getFastOutSlowInInterpolator(view.getContext()));
        animator.setEvaluator(new ArgbEvaluator());
        animator.start();
    }

    private static float dpToPx(View view, int dp) {
        int dpi = view.getContext().getResources().getDisplayMetrics().densityDpi;
        if (dpi == 0) {
            return 0;
        }
        return (float) (dp * (dpi / 160.0));
    }

    public static abstract class FloatProperty<T> extends Property<T, Float> {
        public FloatProperty(String name) {
            super(Float.class, name);
        }

        public abstract void setValue(T object, float value);

        @Override
        final public void set(T object, Float value) {
            setValue(object, value);
        }
    }
}
