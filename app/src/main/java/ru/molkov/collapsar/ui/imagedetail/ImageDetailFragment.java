package ru.molkov.collapsar.ui.imagedetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import ru.molkov.collapsar.R;
import ru.molkov.collapsar.utils.ThemeUtils;

public class ImageDetailFragment extends Fragment implements ImageDetailContract.View {
    public static final String ARGUMENT_APOD_DATE = "APOD_DATE";
    public static final String ARGUMENT_APOD_IMAGE_URL = "APOD_IMAGE_URL";
    public static final String ARGUMENT_APOD_MEDIA_TYPE = "APOD_MEDIA_TYPE";

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

        if (ThemeUtils.getInstance(getActivity()).isLightTheme()) {
            ((ImageButton) root.findViewById(R.id.fragment_image_detail_download_btn)).setImageResource(R.drawable.ic_download_light);
            ((ImageButton) root.findViewById(R.id.fragment_image_detail_share_btn)).setImageResource(R.drawable.ic_send_light);
        } else {
            ((ImageButton) root.findViewById(R.id.fragment_image_detail_download_btn)).setImageResource(R.drawable.ic_download_dark);
            ((ImageButton) root.findViewById(R.id.fragment_image_detail_share_btn)).setImageResource(R.drawable.ic_send_dark);
        }

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
    public void setTitle(String title) {
        TextView view = (TextView) getActivity().findViewById(R.id.fragment_image_detail_title);
        if (view != null) {
            view.setText(title);
        }
    }

    @Override
    public void setSubtitle(String subtitle) {
        TextView view = (TextView) getActivity().findViewById(R.id.fragment_image_detail_subtitle);
        if (view != null) {
            view.setText(subtitle);
        }
    }

    @Override
    public void setExplanation(String explanation) {
        TextView view = (TextView) getActivity().findViewById(R.id.fragment_image_detail_explanation);
        if (view != null) {
            view.setText(explanation);
        }
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }
}
