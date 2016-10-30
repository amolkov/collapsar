package ru.molkov.collapsar.ui.imagedetail;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import ru.molkov.collapsar.Injection;
import ru.molkov.collapsar.R;
import ru.molkov.collapsar.utils.AnimUtils;
import ru.molkov.collapsar.utils.ImageUtils;
import ru.molkov.collapsar.utils.ThemeUtils;
import ru.molkov.collapsar.utils.palette.PaletteBitmap;
import ru.molkov.collapsar.utils.palette.PaletteBitmapTranscoder;
import ru.molkov.collapsar.utils.palette.PaletteBitmapViewTarget;

public class ImageDetailActivity extends AppCompatActivity implements ImageDetailContract.View {
    public static final String ARGUMENT_APOD_DATE = "APOD_DATE";
    public static final String ARGUMENT_APOD_MEDIA_TYPE = "APOD_MEDIA_TYPE";

    private String date;
    private String mediaType;

    private ImageDetailContract.Presenter mPresenter;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        date = getIntent().getStringExtra(ImageDetailActivity.ARGUMENT_APOD_DATE);
        mediaType = getIntent().getStringExtra(ImageDetailActivity.ARGUMENT_APOD_MEDIA_TYPE);

        initView();
        initUI();
        initAnimation();

        mPresenter = new ImageDetailPresenter(date, Injection.provideApodRepository(getApplicationContext()), this);
        mPresenter.subscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fab.setVisibility(View.INVISIBLE);
    }

    private void initView() {
        final String toolbarTitle = mediaType.equalsIgnoreCase("image") ? getString(R.string.title_photo) : getString(R.string.title_video);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.activity_image_detail_collapsing_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.activity_image_detail_app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(toolbarTitle);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_image_detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        fab = (FloatingActionButton) findViewById(R.id.activity_image_detail_fab);
    }

    private void initUI() {
        if (ThemeUtils.isLightTheme(this)) {
            ((ImageButton) findViewById(R.id.container_image_detail_download_btn)).setImageResource(R.drawable.ic_download_light);
            ((ImageButton) findViewById(R.id.container_image_detail_share_btn)).setImageResource(R.drawable.ic_send_light);
        } else {
            ((ImageButton) findViewById(R.id.container_image_detail_download_btn)).setImageResource(R.drawable.ic_download_dark);
            ((ImageButton) findViewById(R.id.container_image_detail_share_btn)).setImageResource(R.drawable.ic_send_dark);
        }

        if (mediaType.equalsIgnoreCase("image")) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_dark));
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_dark));
        }
    }

    private void initAnimation() {
        View titleContainer = findViewById(R.id.container_image_detail_title_container);
        View actionContainer = findViewById(R.id.container_image_detail_action_container);
        View contentContainer = findViewById(R.id.container_image_detail_content_container);

        AnimUtils.initShow(titleContainer, 200);
        AnimUtils.initShow(actionContainer, 300);
        AnimUtils.initShow(contentContainer, 400);
        AnimUtils.initFabShow(fab, 400);
    }

    @Override
    public void setPresenter(ImageDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setPhoto(String url) {
        Glide.with(this)
                .load(url)
                .asBitmap()
                .transcode(new PaletteBitmapTranscoder(this), PaletteBitmap.class)
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
                        }
                    }
                });
    }

    @Override
    public void setTitle(String title) {
        TextView view = (TextView) findViewById(R.id.container_image_detail_title);
        if (view != null) {
            view.setText(title);
        }
    }

    @Override
    public void setSubtitle(String subtitle) {
        TextView view = (TextView) findViewById(R.id.container_image_detail_subtitle);
        if (view != null) {
            view.setText(subtitle);
        }
    }

    @Override
    public void setExplanation(String explanation) {
        TextView view = (TextView) findViewById(R.id.container_image_detail_explanation);
        if (view != null) {
            view.setText(explanation);
        }
    }

    @Override
    public void setCopyright(String copyright) {
        TextView viewLabel = (TextView) findViewById(R.id.container_image_detail_copyright_label);
        TextView view = (TextView) findViewById(R.id.container_image_detail_copyright);
        if (viewLabel != null && view != null) {
            if (copyright.isEmpty()) {
                viewLabel.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            }

            view.setText(copyright);
        }
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
