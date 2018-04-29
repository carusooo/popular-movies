package com.example.macarus0.popularmovies;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.macarus0.popularmovies.data.MovieContract;
import com.example.macarus0.popularmovies.sync.PopularMoviesSyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieAdapterOnClickHandler{

    private static final String[] POSTER_GRID_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_TITLE
    };

    public static final int INDEX_POSTER_GRID_MOVIE_ID = 0;
    public static final int INDEX_POSTER_GRID_POSTER_PATH = 1;
    public static final int INDEX_POSTER_GRID_TITLE = 2;

    private static final int ID_MOVIE_LOADER = 999;

    private MovieAdapter mMovieAdapter;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);

        PopularMoviesSyncUtils.initialize(this);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d("onLoadFinished", String.format("Swapping Cursor, retrieved %d movies", data.getCount()));
        mMovieAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case ID_MOVIE_LOADER:
                String sortOrder = MovieContract.MovieEntry.COLUMN_USER_RATING + " DESC LIMIT 20";
                return new CursorLoader(this,
                        MovieContract.MovieEntry.POPULAR_URI,
                        POSTER_GRID_PROJECTION,
                        MovieContract.MovieEntry.getSelectionForTodaysMovies(),
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }



    @Override
    public void onClick(long id) {
        Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
        detailIntent.putExtra(DetailActivity.EXTRA_DETAIL_MOVIE_ID, Long.toString(id));
        startActivity(detailIntent);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }




}
