package ru.molkov.collapsar.utils;

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
}
