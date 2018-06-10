package com.example.macarus0.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.macarus0.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.ViewHolder> {
    final private MovieAdapterOnClickHandler mOnClickHandler;
    private final Context mContext;
    private Cursor mCursor;
    private final NetworkUtils mNetworkUtils;

    public PosterAdapter(Context context, MovieAdapterOnClickHandler onClickHandler,
                         NetworkUtils networkUtils) {
        mContext = context;
        mOnClickHandler = onClickHandler;
        mNetworkUtils = networkUtils;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.poster_view, parent, false);
        v.setFocusable(true);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String title = mCursor.getString(MainActivity.INDEX_POSTER_GRID_TITLE);
        holder.titleTextView.setText(mCursor.getString(MainActivity.INDEX_POSTER_GRID_TITLE));
        holder.posterImageView.setContentDescription(title);
        Picasso.with(mContext).load(mNetworkUtils.getPosterUrl(
                mCursor.getString(MainActivity.INDEX_POSTER_GRID_POSTER_PATH)))
                .placeholder(R.drawable.placeholder)
                .into(holder.posterImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.titleTextView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.titleTextView.setVisibility(View.VISIBLE);
                    }
                });
    }

    public interface MovieAdapterOnClickHandler {
        void onPosterClick(long id, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView posterImageView;
        final TextView titleTextView;

        ViewHolder(View v) {
            super(v);
            posterImageView = v.findViewById(R.id.poster_imageview);
            titleTextView = v.findViewById(R.id.poster_textview);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long moviePosterId = mCursor.getLong(MainActivity.INDEX_POSTER_GRID_MOVIE_ID);
            Log.d("onClick Poster", String.format("Clicking for %s", moviePosterId ));
            mOnClickHandler.onPosterClick(moviePosterId, adapterPosition);
        }

    }
}

