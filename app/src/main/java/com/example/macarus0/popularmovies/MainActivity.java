package com.example.macarus0.popularmovies;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.macarus0.popularmovies.data.MovieContract;
import com.example.macarus0.popularmovies.sync.PopularMoviesSyncUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String[] POSTER_GRID_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_TITLE,
    };

    public static final int INDEX_POSTER_GRID_POSTER_PATH = 0;
    public static final int INDEX_POSTER_GRID_TITLE = 1;

    private static final int ID_MOVIE_LOADER = 999;
    GridLayoutManager mLayoutManager;
    MovieAdapter mMovieAdapter;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);

        PopularMoviesSyncUtils.initialize(this);


    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d("onLoadFinished", "Swapping Cursor");
        mMovieAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case ID_MOVIE_LOADER:
                String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
                return new CursorLoader(this,
                        MovieContract.MovieEntry.CONTENT_URI,
                        POSTER_GRID_PROJECTION,
                        null,
                        null,
                       sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
        private Context mContext;
        private Cursor mCursor;

        public MovieAdapter(Context context) {
            mContext = context;
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
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            String POSTER_IMAGE_PREFIX = "https://image.tmdb.org/t/p/w185/";
            Picasso.with(mContext).load(POSTER_IMAGE_PREFIX + mCursor.getString(INDEX_POSTER_GRID_POSTER_PATH))
                    .into(holder.posterImageView);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView posterImageView;

            public ViewHolder(View v) {
                super(v);
                posterImageView = v.findViewById(R.id.poster_imageview);
            }
        }
    }


}
