package ru.molkov.collapsar.recentimages;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ru.molkov.collapsar.R;
import ru.molkov.collapsar.utils.ActivityUtils;

public class RecentImagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_images);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(getTitle());
        }

        RecentImagesFragment recentImagesFragment = (RecentImagesFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (recentImagesFragment == null) {
            recentImagesFragment = RecentImagesFragment.getInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), recentImagesFragment, R.id.content_frame);
        }
    }
}
