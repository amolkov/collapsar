package ru.molkov.collapsar.ui.recentimages;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.molkov.collapsar.R;
import ru.molkov.collapsar.utils.ActivityUtils;

public class RecentImagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_images);

        RecentImagesFragment recentImagesFragment = (RecentImagesFragment) getSupportFragmentManager().findFragmentById(R.id.activity_recent_images_content_frame);
        if (recentImagesFragment == null) {
            recentImagesFragment = RecentImagesFragment.getInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), recentImagesFragment, R.id.activity_recent_images_content_frame);
        }
    }
}
