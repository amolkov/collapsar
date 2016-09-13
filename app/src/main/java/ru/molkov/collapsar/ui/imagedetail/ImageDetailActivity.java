package ru.molkov.collapsar.ui.imagedetail;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import ru.molkov.collapsar.Injection;
import ru.molkov.collapsar.R;
import ru.molkov.collapsar.utils.ActivityUtils;
import ru.molkov.collapsar.utils.ImageUtils;
import ru.molkov.collapsar.utils.palette.PaletteBitmap;
import ru.molkov.collapsar.utils.palette.PaletteBitmapTranscoder;
import ru.molkov.collapsar.utils.palette.PaletteBitmapViewTarget;

public class ImageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        String date = getIntent().getStringExtra(ImageDetailFragment.ARGUMENT_APOD_DATE);
        String imageUrl = getIntent().getStringExtra(ImageDetailFragment.ARGUMENT_APOD_IMAGE_URL);
        String mediaType = getIntent().getStringExtra(ImageDetailFragment.ARGUMENT_APOD_MEDIA_TYPE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        String url;
        if (mediaType.equalsIgnoreCase("video")) {
            url = ImageUtils.getThumbnailUrl(imageUrl);
            fab.setImageDrawable(getResources().getDrawable(R.drawable.play));
        } else {
            url = imageUrl;
        }

        ImageDetailFragment imageDetailFragment = (ImageDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_frame);

        if (imageDetailFragment == null) {
            imageDetailFragment = ImageDetailFragment.newInstance(date);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), imageDetailFragment, R.id.content_frame);
        }

        new ImageDetailPresenter(date, Injection.provideApodRepository(getApplicationContext()), imageDetailFragment);

        ImageView image = (ImageView) findViewById(R.id.image);
        Glide.with(this)
                .load(url)
                .asBitmap()
                .transcode(new PaletteBitmapTranscoder(this), PaletteBitmap.class)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new PaletteBitmapViewTarget(image) {
                    @Override
                    protected void setResource(final PaletteBitmap resource) {
                        super.setResource(resource);
                    }
                });
    }
}
