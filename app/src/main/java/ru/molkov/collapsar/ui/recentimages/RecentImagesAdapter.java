package ru.molkov.collapsar.ui.recentimages;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.molkov.collapsar.R;
import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.utils.AnimUtils;
import ru.molkov.collapsar.utils.DateUtils;
import ru.molkov.collapsar.utils.ImageUtils;
import ru.molkov.collapsar.utils.ThemeUtils;
import ru.molkov.collapsar.utils.glide.BitmapCrossfadeListener;
import ru.molkov.collapsar.utils.glide.PaletteBitmap;
import ru.molkov.collapsar.utils.glide.PaletteBitmapTranscoder;
import ru.molkov.collapsar.utils.glide.PaletteBitmapViewTarget;
import ru.molkov.collapsar.views.OnItemClickListener;
import ru.molkov.collapsar.views.OnLoadMoreListener;
import ru.molkov.collapsar.views.adapters.EndlessRecyclerViewAdapter;

public class RecentImagesAdapter extends EndlessRecyclerViewAdapter<Apod> {
    private List<Apod> mData;
    private ColorDrawable[] mPlaceholderColors;
    private OnItemClickListener mOnItemClickListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    public RecentImagesAdapter(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);

        mData = new ArrayList<>();
        mPlaceholderColors = ThemeUtils.getPlaceholderColors(getContext());
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new RecentImagesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false), mOnItemClickListener);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder genericHolder, final int position) {
        RecentImagesViewHolder holder = (RecentImagesViewHolder) genericHolder;
        Apod apod = mData.get(position);
        boolean isPhoto = apod.getMediaType().equalsIgnoreCase("image");
        String url = isPhoto ? apod.getUrl() : ImageUtils.getThumbnailUrl(apod.getUrl());
        holder.setupDefaultValues(position);

        Glide.with(getContext())
                .load(url)
                .asBitmap()
                .transcode(new PaletteBitmapTranscoder(getContext()), PaletteBitmap.class)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .dontAnimate()
                .listener(new BitmapCrossfadeListener<>())
                .into(new PaletteBitmapViewTarget(holder.mPhoto) {
                    @Override
                    protected void setResource(PaletteBitmap resource) {
                        super.setResource(resource);
                        Palette palette = resource.getPalette();
                        Palette.Swatch swatch = ImageUtils.getImageColor(palette);

                        holder.mTitle.setText(apod.getTitle());
                        holder.mSubtitle.setText(DateUtils.friendlyFormat(apod.getDate(), true));
                        if (!isPhoto) {
                            holder.mVideoIcon.setVisibility(View.VISIBLE);
                        }

                        if (!apod.isHasFadedIn()) {
                            AnimUtils.saturationImage(holder.mPhoto);
                            if (swatch != null) {
                                AnimUtils.saturationBackground(holder.mContentContainer, palette.getVibrantColor(swatch.getRgb()));
                            }
                            apod.setHasFadedIn(true);
                        } else {
                            if (swatch != null) {
                                holder.mContentContainer.setBackgroundColor(palette.getVibrantColor(swatch.getRgb()));
                            }
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);

                        holder.mTitle.setText(apod.getTitle());
                        holder.mSubtitle.setText(DateUtils.friendlyFormat(apod.getDate(), true));
                        if (!isPhoto) {
                            holder.mVideoIcon.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof RecentImagesViewHolder) {
            RecentImagesViewHolder recentImagesViewHolder = (RecentImagesViewHolder) holder;
            Glide.clear(recentImagesViewHolder.mPhoto);
        }
    }

    @Override
    public void onLoadMore() {
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    @Override
    public List<Apod> getItems() {
        return mData;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public class RecentImagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View mView;
        public OnItemClickListener mOnItemClickListener;

        @BindView(R.id.item_grid_content_container) View mContentContainer;
        @BindView(R.id.item_grid_photo) ImageView mPhoto;
        @BindView(R.id.item_grid_title) TextView mTitle;
        @BindView(R.id.item_grid_subtitle) TextView mSubtitle;
        @BindView(R.id.item_grid_video_icon) ImageView mVideoIcon;

        public RecentImagesViewHolder(View view, OnItemClickListener onClickListener) {
            super(view);
            ButterKnife.bind(this, view);

            mOnItemClickListener = onClickListener;
            mView = view;
            mView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        public void setupDefaultValues(int position) {
            mView.setBackground(mPlaceholderColors[position % mPlaceholderColors.length]);
            mContentContainer.setBackground(null);
            mTitle.setText(null);
            mSubtitle.setText(null);
            mVideoIcon.setVisibility(View.GONE);
        }
    }
}
