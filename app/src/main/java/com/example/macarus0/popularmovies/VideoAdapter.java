package com.example.macarus0.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.macarus0.popularmovies.util.NetworkUtils;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private final VideoOnClickHandler mVideoOnClickHandler;
    private final Context mContext;
    private Cursor mCursor;

    public VideoAdapter(Context context, VideoOnClickHandler videoOnClickHandler) {
        mContext = context;
        mVideoOnClickHandler = videoOnClickHandler;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.video_view,
                parent, false);
        v.setFocusable(true);
        return new ViewHolder(v);
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.typeTextView.setText(mCursor.getString(DetailActivity.INDEX_VIDEO_TYPE));
        holder.titleTextView.setText(mCursor.getString(DetailActivity.INDEX_VIDEO_NAME));
        holder.iconImageView.setImageResource(R.drawable.ic_sharp_play_circle_filled_24px);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    interface VideoOnClickHandler {
        void onVideoClick(String url);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView iconImageView;
        final TextView typeTextView;
        final TextView titleTextView;

        ViewHolder(View v) {
            super(v);
            typeTextView = v.findViewById(R.id.type_textview);
            titleTextView = v.findViewById(R.id.title_textview);
            iconImageView = v.findViewById(R.id.play_icon_iv);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String site = mCursor.getString(DetailActivity.INDEX_VIDEO_SITE);
            String key = mCursor.getString(DetailActivity.INDEX_VIDEO_KEY);
            mVideoOnClickHandler.onVideoClick(NetworkUtils.getVideoUrl(site, key));
        }
    }
}
