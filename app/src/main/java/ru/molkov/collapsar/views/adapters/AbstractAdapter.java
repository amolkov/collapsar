package ru.molkov.collapsar.views.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import ru.molkov.collapsar.R;
import ru.molkov.collapsar.views.OnLoadMoreListener;

public abstract class AbstractAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;

    private final int VISIBLE_THRESHOLD = 10;
    private final int ITEM_VIEW_TYPE_ITEM = 0;
    private final int ITEM_VIEW_TYPE_PROGRESS = 1;

    private OnLoadMoreListener mOnLoadMoreListener;
    private int mLastVisibleItem, mTotalItemCount = 0;
    private boolean mIsLoading = false;

    public abstract RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindItemView(RecyclerView.ViewHolder genericHolder, int position);

    public abstract List<T> getData();

    public AbstractAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    mTotalItemCount = linearLayoutManager.getItemCount();
                    mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (getData().size() > 0 && !mIsLoading && mTotalItemCount <= (mLastVisibleItem + VISIBLE_THRESHOLD)) {
                        mOnLoadMoreListener.onLoadMore();
                        mIsLoading = true;
                        addItem(null);
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_ITEM) {
            return onCreateBasicItemViewHolder(parent, viewType);
        } else {
            return onCreateProgressViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder genericHolder, int position) {
        if (getItemViewType(position) == ITEM_VIEW_TYPE_ITEM) {
            onBindItemView(genericHolder, position);
        } else {
            onBindProgressView(genericHolder, position);
        }
    }

    @Override
    public int getItemCount() {
        return getData().size();
    }

    @Override
    public int getItemViewType(int position) {
        return getData().get(position) != null ? ITEM_VIEW_TYPE_ITEM : ITEM_VIEW_TYPE_PROGRESS;
    }

    public RecyclerView.ViewHolder onCreateProgressViewHolder(ViewGroup parent, int viewType) {
        return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar, parent, false));
    }

    public void onBindProgressView(RecyclerView.ViewHolder genericHolder, int position) {
        ProgressViewHolder holder = (ProgressViewHolder) genericHolder;
        holder.progressBar.setIndeterminate(true);
        holder.progressBar.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public void addItem(T item) {
        if (!getData().contains(item)) {
            getData().add(item);
            notifyItemInserted(getData().size() - 1);
        }
    }

    public void removeItem(T item) {
        int index = getData().indexOf(item);
        if (index != -1) {
            getData().remove(index);
            notifyItemRemoved(index);
        }
    }

    public Context getContext() {
        return mContext;
    }

    public void setLoading(boolean loading) {
        mIsLoading = loading;
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            this.progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }
}
