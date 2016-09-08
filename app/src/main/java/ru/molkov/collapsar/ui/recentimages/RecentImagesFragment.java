package ru.molkov.collapsar.ui.recentimages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ru.molkov.collapsar.Injection;
import ru.molkov.collapsar.R;
import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.ui.imagedetail.ImageDetailActivity;
import ru.molkov.collapsar.ui.imagedetail.ImageDetailFragment;
import ru.molkov.collapsar.utils.DateUtils;
import ru.molkov.collapsar.views.OnItemClickListener;
import ru.molkov.collapsar.views.OnLoadMoreListener;

public class RecentImagesFragment extends Fragment implements RecentImagesContract.View {
    private RecentImagesContract.Presenter mPresenter;
    private RecentImagesAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public RecentImagesFragment() {

    }

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
        View root = inflater.inflate(R.layout.fragment_recent_images, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.apod_list);
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
        recyclerView.setLayoutManager(gridLayoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mAdapter = new RecentImagesAdapter(getActivity(), recyclerView);
        mAdapter.setOnLoadMoreListener(mOnLoadMoreListener);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        recyclerView.setAdapter(mAdapter);

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
    public void setLoadingIndicator(boolean isActive) {
        mAdapter.setLoading(isActive);
    }

    @Override
    public void setRefreshIndicator(boolean isActive) {
        mSwipeRefreshLayout.setRefreshing(isActive);
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
            args.putString(ImageDetailFragment.ARGUMENT_APOD_DATE, DateUtils.toString(apod.getDate()));
            args.putString(ImageDetailFragment.ARGUMENT_APOD_IMAGE_URL, apod.getUrl());
            intent.putExtras(args);

            startActivity(intent);
        }
    };
}
