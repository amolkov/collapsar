package ru.molkov.collapsar.recentimages;

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
import ru.molkov.collapsar.utils.palette.PaletteBitmap;
import ru.molkov.collapsar.utils.palette.PaletteBitmapTranscoder;
import ru.molkov.collapsar.utils.palette.PaletteBitmapViewTarget;
import ru.molkov.collapsar.views.OnClickListener;
import ru.molkov.collapsar.views.adapters.AbstractAdapter;

public class RecentImagesAdapter extends AbstractAdapter<Apod> {
    private List<Apod> mData;
    private OnClickListener mOnClickListener;

    public RecentImagesAdapter(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        this.mData = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        return new RecentImagesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.apod_list_content, parent, false), mOnClickListener);
    }

    @Override
    public void onBindItemView(RecyclerView.ViewHolder genericHolder, int position) {
        final RecentImagesViewHolder holder = (RecentImagesViewHolder) genericHolder;
        final Apod apod = mData.get(position);

        holder.mImage.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
        holder.mFooter.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));

        holder.mTitle.setText(apod.getTitle());
        holder.mDate.setText(DateUtils.friendlyFormat(apod.getDate()));
        Glide.with(getContext())
                .load(apod.getUrl())
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
    public List<Apod> getData() {
        return mData;
    }

    public void setData(List<Apod> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<Apod> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    class RecentImagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTitle;
        public TextView mDate;
        public ImageView mImage;
        public RelativeLayout mFooter;
        public OnClickListener mOnClickListener;

        public RecentImagesViewHolder(View itemView, OnClickListener onClickListener) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mDate = (TextView) itemView.findViewById(R.id.date);
            mImage = (ImageView) itemView.findViewById(R.id.image);
            mFooter = (RelativeLayout) itemView.findViewById(R.id.footer);
            mOnClickListener = onClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onClick(v, getAdapterPosition());
        }
    }
}
