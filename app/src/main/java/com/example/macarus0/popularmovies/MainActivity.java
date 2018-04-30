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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.macarus0.popularmovies.data.MovieContract;
import com.example.macarus0.popularmovies.sync.PopularMoviesSyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieAdapterOnClickHandler,
        AdapterView.OnItemSelectedListener{

    private static final String[] POSTER_GRID_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_TITLE
    };

    public static final int INDEX_POSTER_GRID_MOVIE_ID = 0;
    public static final int INDEX_POSTER_GRID_POSTER_PATH = 1;
    public static final int INDEX_POSTER_GRID_TITLE = 2;

    private static final int ID_POPULAR_MOVIE_LOADER = 999;
    private static final int ID_TOP_RATED_MOVIE_LOADER = 998;

    private MovieAdapter mMovieAdapter;
    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.sort_options)
    Spinner mSpinner;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initial MainActivity setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Set up the toolbar to support changing the sort order
        setSupportActionBar(mToolbar);
        ArrayAdapter<CharSequence> sortOptionsAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, R.layout.spinner_item);
        sortOptionsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpinner.setAdapter(sortOptionsAdapter);
        mSpinner.setOnItemSelectedListener(this);


        // Set up the Gridded layout of poster images
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        // Set up the CursorLoaderManager to detect changes in the data and update the views
        getSupportLoaderManager().initLoader(ID_POPULAR_MOVIE_LOADER, null, this);

        // Start the data sync to load in the movies
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
        String sortOrder;

        switch (id) {
            case ID_POPULAR_MOVIE_LOADER:
                sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY+ " DESC LIMIT 20";
                break;
            case ID_TOP_RATED_MOVIE_LOADER:
                sortOrder = MovieContract.MovieEntry.COLUMN_USER_RATING + " DESC LIMIT 20";
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
        return new CursorLoader(this,
                MovieContract.MovieEntry.POPULAR_URI,
                POSTER_GRID_PROJECTION,
                MovieContract.MovieEntry.getSelectionForTodaysMovies(),
                null,
                sortOrder);
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0:
                // Popular was selected
                getSupportLoaderManager().restartLoader(ID_POPULAR_MOVIE_LOADER, null, this);
                break;
            case 1:
                // Top Rated was selected
                getSupportLoaderManager().restartLoader(ID_TOP_RATED_MOVIE_LOADER, null, this);
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
