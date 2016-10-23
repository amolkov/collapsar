package ru.molkov.collapsar.utils;

import android.support.v7.graphics.Palette;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtils {

    public static String getThumbnailUrl(String videoUrl) {
        String id;
        String pattern = "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(videoUrl);
        if (matcher.matches()) {
            id = matcher.group(1);
            return Constants.YOUTUBE_VIDEO_THUMBNAIL.replace("VIDEO_ID", id);
        }
        return null;
    }

    public static Palette.Swatch getImageColor(Palette palette) {
        if (palette != null) {
            Palette.Swatch swatch = palette.getVibrantSwatch();
            if (swatch == null) {
                swatch = palette.getDarkVibrantSwatch();
            }
            if (swatch == null) {
                swatch = palette.getDarkMutedSwatch();
            }
            if (swatch == null) {
                swatch = palette.getLightVibrantSwatch();
            }
            if (swatch == null) {
                swatch = palette.getLightMutedSwatch();
            }
            return swatch;
        }
        return null;
    }
}
