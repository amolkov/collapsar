package ru.molkov.collapsar.ui.imagedetail;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import ru.molkov.collapsar.Injection;
import ru.molkov.collapsar.R;
import ru.molkov.collapsar.utils.ActivityUtils;
import ru.molkov.collapsar.utils.ImageUtils;
import ru.molkov.collapsar.utils.ThemeUtils;
import ru.molkov.collapsar.utils.palette.PaletteBitmap;
import ru.molkov.collapsar.utils.palette.PaletteBitmapTranscoder;
import ru.molkov.collapsar.utils.palette.PaletteBitmapViewTarget;

import static ru.molkov.collapsar.App.getContext;

public class ImageDetailActivity extends AppCompatActivity {
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        String date = getIntent().getStringExtra(ImageDetailFragment.ARGUMENT_APOD_DATE);
        String imageUrl = getIntent().getStringExtra(ImageDetailFragment.ARGUMENT_APOD_IMAGE_URL);
        String mediaType = getIntent().getStringExtra(ImageDetailFragment.ARGUMENT_APOD_MEDIA_TYPE);

        final boolean isPhoto = mediaType.equalsIgnoreCase("image");
        String url = isPhoto ? imageUrl : ImageUtils.getThumbnailUrl(imageUrl);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_image_detail_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (ThemeUtils.getInstance(this).isLightTheme()) {
            setupLightTheme(isPhoto);
        } else {
            setupDarkTheme(isPhoto);
        }

        ImageDetailFragment imageDetailFragment = (ImageDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_image_detail_content_frame);

        if (imageDetailFragment == null) {
            imageDetailFragment = ImageDetailFragment.newInstance(date);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), imageDetailFragment, R.id.activity_image_detail_content_frame);
        }

        new ImageDetailPresenter(date, Injection.provideApodRepository(getApplicationContext()), imageDetailFragment);

        Glide.with(getContext())
                .load(url)
                .asBitmap()
                .transcode(new PaletteBitmapTranscoder(getContext()), PaletteBitmap.class)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.IMMEDIATE)
                .into(new PaletteBitmapViewTarget((ImageView) findViewById(R.id.activity_image_detail_photo)) {
                    @Override
                    protected void setResource(PaletteBitmap resource) {
                        super.setResource(resource);
                        Palette palette = resource.getPalette();
                        Palette.Swatch swatch = ImageUtils.getImageColor(palette);
                        if (swatch != null) {
                            fab.setBackgroundTintList(ColorStateList.valueOf(palette.getVibrantColor(swatch.getRgb())));
                            setupLightTheme(isPhoto);
                        }
                    }
                });
    }

    private void setupLightTheme(boolean isPhoto) {
        if (isPhoto) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_dark));
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_dark));
        }
    }

    private void setupDarkTheme(boolean isPhoto) {
        if (isPhoto) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_light));
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_light));
        }
    }
}
