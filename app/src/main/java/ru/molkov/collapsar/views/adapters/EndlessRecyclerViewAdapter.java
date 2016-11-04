package ru.molkov.collapsar.views.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import ru.molkov.collapsar.R;
import ru.molkov.collapsar.utils.ThemeUtils;

public abstract class EndlessRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_PROGRESS = 1;
    private static final int VISIBLE_THRESHOLD = 2;

    private Context mContext;
    private int mItemCount;
    private int mLastVisibleItemPosition;
    private boolean mLoading;

    public abstract void onLoadMore();

    public abstract List<T> getItems();

    public abstract RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindItemViewHolder(RecyclerView.ViewHolder genericHolder, int position);

    public EndlessRecyclerViewAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    mItemCount = linearLayoutManager.getItemCount();
                    mLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                    if (getItems().size() > 0 && !mLoading && mItemCount <= (mLastVisibleItemPosition + VISIBLE_THRESHOLD)) {
                        onLoadMore();
                        setLoading(true);
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            return onCreateItemViewHolder(parent, viewType);
        } else {
            return onCreateProgressViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            onBindItemViewHolder(holder, position);
        } else {
            onBindProgressView(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    @Override
    public int getItemViewType(int position) {
        return getItems().get(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_PROGRESS;
    }

    public Context getContext() {
        return mContext;
    }

    public void setLoading(boolean loading) {
        if (loading) {
            getItems().add(null);
        } else {
            getItems().remove(null);
        }
        mLoading = loading;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void addItems(List<T> items) {
        getItems().addAll(items);
        notifyDataSetChanged();
    }

    public void setItems(List<T> items) {
        getItems().clear();
        getItems().addAll(items);
        notifyDataSetChanged();
    }

    private RecyclerView.ViewHolder onCreateProgressViewHolder(ViewGroup parent, int viewType) {
        return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar, parent, false));
    }

    private void onBindProgressView(RecyclerView.ViewHolder genericHolder, int position) {
        ProgressViewHolder holder = (ProgressViewHolder) genericHolder;
        holder.mProgressBar.setIndeterminate(true);
        holder.mProgressBar.getIndeterminateDrawable().setColorFilter(ThemeUtils.getThemeColor(mContext, R.attr.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }
}
