package ru.molkov.collapsar.utils;


import android.graphics.ColorMatrix;
import android.util.Property;

public class ObservableColorMatrix extends ColorMatrix {
    private float mSaturation = 1f;

    public ObservableColorMatrix() {
        super();
    }

    public float getSaturation() {
        return mSaturation;
    }

    @Override
    public void setSaturation(float saturation) {
        mSaturation = saturation;
        super.setSaturation(saturation);
    }

    public static final Property<ObservableColorMatrix, Float> SATURATION
            = new AnimUtils.FloatProperty<ObservableColorMatrix>("saturation") {

        @Override
        public void setValue(ObservableColorMatrix cm, float value) {
            cm.setSaturation(value);
        }

        @Override
        public Float get(ObservableColorMatrix cm) {
            return cm.getSaturation();
        }
    };

}

