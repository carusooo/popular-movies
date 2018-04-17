package com.example.macarus0.popularmovies;

import android.support.v4.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import com.example.macarus0.popularmovies.util.MovieJSONUtilities;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

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
    private static String POSTER_IMAGE_PREFIX = "https://image.tmdb.org/t/p/w185/";

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

        ContentResolver contentResolver = getContentResolver();

        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);

        try {
            ContentValues[] popularMovies = MovieJSONUtilities.parsePopularJSON(getString(R.string.mockPopularJSON));
            contentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, popularMovies);

        } catch (JSONException exception) {
            Log.e("ParseJSON",  String.format("%s %s:%s",
                    exception.getMessage(),
                    exception.getStackTrace()[0].getFileName(),
                    exception.getStackTrace()[0].getLineNumber()));
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("onLoadFinished", "Swapping Cursor");
        mMovieAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case ID_MOVIE_LOADER:
                return new CursorLoader(this,
                        MovieContract.MovieEntry.CONTENT_URI,
                        POSTER_GRID_PROJECTION,
                        null,
                        null,
                       null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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
            return mCursor.getCount();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.poster_view, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
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
