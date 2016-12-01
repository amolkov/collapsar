package ru.molkov.collapsar.ui.recentimages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.molkov.collapsar.Injection;
import ru.molkov.collapsar.R;
import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.ui.imagedetail.ImageDetailActivity;
import ru.molkov.collapsar.utils.DateUtils;
import ru.molkov.collapsar.utils.ThemeUtils;
import ru.molkov.collapsar.views.OnItemClickListener;
import ru.molkov.collapsar.views.OnLoadMoreListener;

public class RecentImagesFragment extends Fragment implements RecentImagesContract.View {
    private RecentImagesContract.Presenter mPresenter;
    private RecentImagesAdapter mAdapter;
    private Unbinder mUnbinder;

    @BindView(R.id.fragment_recent_images_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.fragment_recent_images_progress_bar)
    ProgressBar mFirsLoadProgress;

    @BindView(R.id.fragment_recent_images_recycler_view)
    RecyclerView mRecyclerView;

    public static RecentImagesFragment getInstance() {
        return new RecentImagesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new RecentImagesPresenter(Injection.provideApodRepository(getContext()), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recent_images, container, false);
        mUnbinder = ButterKnife.bind(this, v);

        initView();
        initTheme();
        return v;
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
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void setPresenter(RecentImagesContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoadedApods(List<Apod> apods) {
        mAdapter.addItems(apods);
    }

    @Override
    public void showUpdatedApods(List<Apod> apods) {
        mAdapter.setItems(apods);
    }

    @Override
    public void setFirstLoadProgress(boolean isActive) {
        if (isActive) {
            mFirsLoadProgress.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setEnabled(false);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mFirsLoadProgress.setVisibility(View.GONE);
            mSwipeRefreshLayout.setEnabled(true);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setLoadingIndicator(boolean isActive) {
        mAdapter.setLoading(isActive);
    }

    @Override
    public void setRefreshIndicator(boolean isActive) {
        mSwipeRefreshLayout.setRefreshing(isActive);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)) {
                    case RecentImagesAdapter.VIEW_TYPE_ITEM:
                        return 1;
                    case RecentImagesAdapter.VIEW_TYPE_PROGRESS:
                        return 2;
                    default:
                        return -1;
                }
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mAdapter = new RecentImagesAdapter(getActivity(), mRecyclerView);
        mAdapter.setOnLoadMoreListener(mOnLoadMoreListener);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initTheme() {
        mFirsLoadProgress.getIndeterminateDrawable().setColorFilter(ThemeUtils.getThemeColor(getActivity(), R.attr.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);

        if (ThemeUtils.isLightTheme(getActivity())) {
            // TODO
        } else {
            mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary_dark);
            mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorContent_dark));
        }
    }

    private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            mPresenter.loadApods();
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mPresenter.refreshApods(true);
        }
    };

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            Apod apod = mAdapter.getItems().get(position);

            Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
            Bundle args = new Bundle();
            args.putString(ImageDetailActivity.ARGUMENT_APOD_DATE, DateUtils.toString(apod.getDate()));
            args.putString(ImageDetailActivity.ARGUMENT_APOD_MEDIA_TYPE, apod.getMediaType());
            args.putString(ImageDetailActivity.ARGUMENT_APOD_URL, apod.getUrl());
            intent.putExtras(args);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    Pair.create(v.findViewById(R.id.item_grid_photo), getString(R.string.transition_photo)),
                    Pair.create(v.findViewById(R.id.item_grid_photo), getString(R.string.transition_background)));

            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        }
    };
}
