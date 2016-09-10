package ru.molkov.collapsar.ui.imagedetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import ru.molkov.collapsar.R;
import ru.molkov.collapsar.utils.palette.PaletteBitmap;
import ru.molkov.collapsar.utils.palette.PaletteBitmapTranscoder;

public class ImageDetailFragment extends Fragment implements ImageDetailContract.View {
    public static final String ARGUMENT_APOD_DATE = "APOD_DATE";
    public static final String ARGUMENT_APOD_IMAGE_URL = "APOD_IMAGE_URL";

    private ImageDetailContract.Presenter mPresenter;

    public static ImageDetailFragment newInstance(String apodDate) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_APOD_DATE, apodDate);
        ImageDetailFragment fragment = new ImageDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_image_detail, container, false);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(ImageDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setTitleContainerColor(String url) {
        final LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.title_container);
        Glide.with(this)
                .load(url)
                .asBitmap()
                .transcode(new PaletteBitmapTranscoder(getActivity()), PaletteBitmap.class)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<PaletteBitmap>() {
                    @Override
                    public void onResourceReady(PaletteBitmap resource, GlideAnimation<? super PaletteBitmap> glideAnimation) {
                        Palette palette = resource.getPalette();
                        if (palette != null) {
                            Palette.Swatch s = palette.getVibrantSwatch();
                            if (s == null) {
                                s = palette.getDarkVibrantSwatch();
                            }
                            if (s == null) {
                                s = palette.getLightVibrantSwatch();
                            }
                            if (s != null) {
                                linearLayout.setBackgroundColor(palette.getVibrantColor(s.getRgb()));
                            }
                        }
                    }
                });
    }

    @Override
    public void setTitle(String title) {
        TextView view = (TextView) getActivity().findViewById(R.id.title);
        if (view != null) {
            view.setText(title);
        }
    }

    @Override
    public void setDate(String date) {
        TextView view = (TextView) getActivity().findViewById(R.id.date);
        if (view != null) {
            view.setText(date);
        }
    }

    @Override
    public void setExplanation(String explanation) {
        TextView view = (TextView) getActivity().findViewById(R.id.explanation);
        if (view != null) {
            view.setText(explanation);
        }
    }

    @Override
    public void setCopyright(String copyright) {
        TextView view = (TextView) getActivity().findViewById(R.id.copyright);
        if (view != null) {
            view.setText(copyright);
        }
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }
}
