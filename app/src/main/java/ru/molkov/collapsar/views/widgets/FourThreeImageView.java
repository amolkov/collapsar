package ru.molkov.collapsar.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FourThreeImageView extends ImageView {

    public FourThreeImageView(Context context) {
        super(context);
    }

    public FourThreeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FourThreeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredWidth() / 3 * 2;
        setMeasuredDimension(getMeasuredWidth(), height);
    }
}
