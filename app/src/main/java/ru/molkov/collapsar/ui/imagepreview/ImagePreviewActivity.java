package ru.molkov.collapsar.ui.imagepreview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.molkov.collapsar.Injection;
import ru.molkov.collapsar.R;
import ru.molkov.collapsar.utils.glide.PaletteBitmap;
import ru.molkov.collapsar.utils.glide.PaletteBitmapTranscoder;
import ru.molkov.collapsar.utils.glide.PaletteBitmapViewTarget;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagePreviewActivity extends AppCompatActivity implements ImagePreviewContract.View {
    public static final String ARGUMENT_APOD_DATE = "APOD_DATE";

    private ImagePreviewContract.Presenter mPresenter;

    @BindView(R.id.activity_image_preview_photo)
    ImageView photo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        ButterKnife.bind(this);

        String date = getIntent().getStringExtra(ImagePreviewActivity.ARGUMENT_APOD_DATE);
        mPresenter = new ImagePreviewPresenter(date, Injection.provideApodRepository(getApplicationContext()), this);
        mPresenter.subscribe();
    }

    @Override
    public void setPhoto(String url) {
        Glide.with(this)
                .load(url)
                .asBitmap()
                .transcode(new PaletteBitmapTranscoder(this), PaletteBitmap.class)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.IMMEDIATE)
                .into(new PaletteBitmapViewTarget(photo) {
                    @Override
                    protected void setResource(PaletteBitmap resource) {
                        super.setResource(resource);
                        new PhotoViewAttacher(photo);
                    }
                });
    }

    @Override
    public void setPresenter(ImagePreviewContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
