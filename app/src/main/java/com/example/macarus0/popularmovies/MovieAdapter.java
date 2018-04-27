package com.example.macarus0.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    final private MovieAdapterOnClickHandler mOnClickHandler;
    private Context mContext;
    private Cursor mCursor;

    public MovieAdapter(Context context, MovieAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mOnClickHandler = onClickHandler;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String POSTER_IMAGE_PREFIX = "https://image.tmdb.org/t/p/w185/";
        Picasso.with(mContext).load(POSTER_IMAGE_PREFIX + mCursor.getString(MainActivity.INDEX_POSTER_GRID_POSTER_PATH))
                .into(holder.posterImageView);
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(long id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView posterImageView;
        public long mMovieId;

        public ViewHolder(View v) {
            super(v);
            posterImageView = v.findViewById(R.id.poster_imageview);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long moviePosterId = mCursor.getLong(MainActivity.INDEX_POSTER_GRID_MOVIE_ID);
            mOnClickHandler.onClick(moviePosterId);
        }

    }
}

