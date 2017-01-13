package ru.molkov.collapsar.ui.imagedetail;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.molkov.collapsar.Injection;
import ru.molkov.collapsar.R;
import ru.molkov.collapsar.ui.imagepreview.ImagePreviewActivity;
import ru.molkov.collapsar.utils.AnimUtils;
import ru.molkov.collapsar.utils.ImageUtils;
import ru.molkov.collapsar.utils.ThemeUtils;
import ru.molkov.collapsar.utils.download.DownloadHelper;
import ru.molkov.collapsar.utils.glide.PaletteBitmap;
import ru.molkov.collapsar.utils.glide.PaletteBitmapTranscoder;
import ru.molkov.collapsar.utils.glide.PaletteBitmapViewTarget;

public class ImageDetailActivity extends AppCompatActivity implements ImageDetailContract.View {
    public static final String ARGUMENT_APOD_DATE = "APOD_DATE";
    public static final String ARGUMENT_APOD_MEDIA_TYPE = "APOD_MEDIA_TYPE";
    public static final String ARGUMENT_APOD_URL = "APOD_URL";
    public static final String ARGUMENT_IS_POSTPONE_TRANSITION = "IS_POSTPONE_TRANSITION";

    private ImageDetailContract.Presenter mPresenter;
    private String mDate;
    private String mMediaType;
    private String mUrl;
    private boolean mIsPostponeTransition;

    @BindView(R.id.activity_image_detail_app_bar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.activity_image_detail_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.activity_image_detail_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_image_detail_fab)
    FloatingActionButton mFab;

    @OnClick(R.id.activity_image_detail_fab)
    public void onFabClick() {
        switch (mMediaType) {
            case "image":
                Intent imageIntent = new Intent(this, ImagePreviewActivity.class);
                Bundle args = new Bundle();
                args.putString(ImagePreviewActivity.ARGUMENT_APOD_DATE, mDate);
                imageIntent.putExtras(args);
                startActivity(imageIntent);
                break;
            case "video":
                Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
                startActivity(videoIntent);
                break;
        }
    }

    @OnClick(R.id.container_image_detail_download_btn)
    public void download() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            downloadApod();
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, DownloadHelper.DOWNLOAD_ACTION);
        }
    }

    @OnClick(R.id.container_image_detail_share_btn)
    public void share() {
        mPresenter.shareApod(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        ButterKnife.bind(this);

        mDate = getIntent().getStringExtra(ImageDetailActivity.ARGUMENT_APOD_DATE);
        mMediaType = getIntent().getStringExtra(ImageDetailActivity.ARGUMENT_APOD_MEDIA_TYPE);
        mUrl = getIntent().getStringExtra(ImageDetailActivity.ARGUMENT_APOD_URL);
        mIsPostponeTransition = getIntent().getBooleanExtra(ImageDetailActivity.ARGUMENT_IS_POSTPONE_TRANSITION, false);

        initView();
        initTheme();
        initAnimation();

        new ImageDetailPresenter(mDate, Injection.provideApodRepository(getApplicationContext()), this);
        mPresenter.subscribe();
        if (mIsPostponeTransition) {
            postponeEnterTransition();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mFab.setVisibility(View.INVISIBLE);
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
                            setLightFab();
                            mFab.setBackgroundTintList(ColorStateList.valueOf(palette.getVibrantColor(swatch.getRgb())));
                        }
                        if (mIsPostponeTransition) {
                            startPostponedEnterTransition();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(String permission, int requestCode) {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission}, requestCode);
        } else {
            downloadApod();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            switch (permissions[i]) {
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        downloadApod();
                    } else {
                        showError(getString(R.string.message_need_permission));
                    }
                    break;
            }
        }
    }

    private void downloadApod() {
        mPresenter.downloadApod(this);
    }

    private void initView() {
        final String toolbarTitle = mMediaType.equalsIgnoreCase("image") ? getString(R.string.title_photo) : getString(R.string.title_video);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbarLayout.setTitle(toolbarTitle);
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        if (!mMediaType.equalsIgnoreCase("image")) {
            findViewById(R.id.container_image_detail_download_action).setVisibility(View.GONE);
        }
    }

    private void initTheme() {
        if (ThemeUtils.isLightTheme(this)) {
            ((ImageButton) findViewById(R.id.container_image_detail_download_btn)).setImageResource(R.drawable.ic_download_light);
            ((ImageButton) findViewById(R.id.container_image_detail_share_btn)).setImageResource(R.drawable.ic_send_light);
            setLightFab();
        } else {
            ((ImageButton) findViewById(R.id.container_image_detail_download_btn)).setImageResource(R.drawable.ic_download_dark);
            ((ImageButton) findViewById(R.id.container_image_detail_share_btn)).setImageResource(R.drawable.ic_send_dark);
            setDarkFab();
        }
    }

    private void initAnimation() {
        View titleContainer = findViewById(R.id.container_image_detail_title_container);
        View actionContainer = findViewById(R.id.container_image_detail_action_container);
        View contentContainer = findViewById(R.id.container_image_detail_content_container);

        AnimUtils.translationView(titleContainer, 300);
        AnimUtils.translationView(actionContainer, 350);
        AnimUtils.translationView(contentContainer, 400);
        AnimUtils.initFabShow(mFab, 400);
    }

    private void setLightFab() {
        if (mMediaType.equalsIgnoreCase("image")) {
            mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_dark));
        } else {
            mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_dark));
        }
    }

    private void setDarkFab() {
        if (mMediaType.equalsIgnoreCase("image")) {
            mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_light));
        } else {
            mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_light));
        }
    }
}
