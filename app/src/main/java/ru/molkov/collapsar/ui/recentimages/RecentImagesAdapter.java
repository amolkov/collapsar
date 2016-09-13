package ru.molkov.collapsar.ui.recentimages;

import android.content.Context;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import ru.molkov.collapsar.R;
import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.utils.DateUtils;
import ru.molkov.collapsar.utils.ImageUtils;
import ru.molkov.collapsar.utils.palette.PaletteBitmap;
import ru.molkov.collapsar.utils.palette.PaletteBitmapTranscoder;
import ru.molkov.collapsar.utils.palette.PaletteBitmapViewTarget;
import ru.molkov.collapsar.views.OnItemClickListener;
import ru.molkov.collapsar.views.OnLoadMoreListener;
import ru.molkov.collapsar.views.adapters.EndlessRecyclerViewAdapter;

public class RecentImagesAdapter extends EndlessRecyclerViewAdapter<Apod> {
    private List<Apod> mData;
    private OnItemClickListener mOnItemClickListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    public RecentImagesAdapter(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        mData = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new RecentImagesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.apod_list_content, parent, false), mOnItemClickListener);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder genericHolder, int position) {
        final RecentImagesViewHolder holder = (RecentImagesViewHolder) genericHolder;
        final Apod apod = mData.get(position);

        holder.mFooter.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
        holder.mImage.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));

        String url;
        if (apod.getMediaType().equalsIgnoreCase("video")) {
            url = ImageUtils.getThumbnailUrl(apod.getUrl());
            holder.mYoutubeIcon.setVisibility(View.VISIBLE);
        } else {
            url = apod.getUrlHd();
            holder.mYoutubeIcon.setVisibility(View.GONE);
        }

        holder.mTitle.setText(apod.getTitle());
        holder.mDate.setText(DateUtils.friendlyFormat(apod.getDate()));
        Glide.with(getContext())
                .load(url)
                .asBitmap()
                .transcode(new PaletteBitmapTranscoder(getContext()), PaletteBitmap.class)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new PaletteBitmapViewTarget(holder.mImage) {
                    @Override
                    protected void setResource(PaletteBitmap resource) {
                        super.setResource(resource);
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
                                holder.mFooter.setBackgroundColor(palette.getVibrantColor(s.getRgb()));
                            }
                        }
                    }
                });
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

    class RecentImagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTitle;
        public TextView mDate;
        public ImageView mImage;
        public ImageView mYoutubeIcon;
        public RelativeLayout mFooter;
        public OnItemClickListener mOnItemClickListener;

        public RecentImagesViewHolder(View itemView, OnItemClickListener onClickListener) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.image);
            mYoutubeIcon = (ImageView) itemView.findViewById(R.id.youtube_icon);

            mTitle = (TextView) itemView.findViewById(R.id.title);
            mDate = (TextView) itemView.findViewById(R.id.date);
            mFooter = (RelativeLayout) itemView.findViewById(R.id.footer);
            mOnItemClickListener = onClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
