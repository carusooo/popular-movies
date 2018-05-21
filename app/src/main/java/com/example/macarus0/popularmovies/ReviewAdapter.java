package com.example.macarus0.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private final Context mContext;
    private Cursor mCursor;

    public ReviewAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.review_view,
                parent, false);
        v.setFocusable(false);
        return new ViewHolder(v);
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.reviewContentView.setText(mCursor.getString(DetailActivity.INDEX_REVIEW_CONTENT));
        holder.reviewAuthorView.setText(mCursor.getString(DetailActivity.INDEX_REVIEW_AUTHOR));
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView reviewContentView;
        final TextView reviewAuthorView;

        ViewHolder(View v) {
            super(v);
            reviewAuthorView = v.findViewById(R.id.author_textview);
            reviewContentView = v.findViewById(R.id.content_textview);
        }
    }
}
