package com.example.macarus0.popularmovies;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.macarus0.popularmovies.data.MovieContract;
import com.example.macarus0.popularmovies.sync.PopularMoviesSyncUtils;
import com.example.macarus0.popularmovies.util.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieAdapterOnClickHandler,
        AdapterView.OnItemSelectedListener {

    public static final int INDEX_POSTER_GRID_MOVIE_ID = 0;
    public static final int INDEX_POSTER_GRID_POSTER_PATH = 1;
    public static final int INDEX_POSTER_GRID_TITLE = 2;
    private static final String[] POSTER_GRID_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_TITLE
    };
    private static final int ID_POPULAR_MOVIE_LOADER = 999;
    private static final int ID_TOP_RATED_MOVIE_LOADER = 998;

    private static final String SCROLL_POSITION = "scroll_position";
    private static final String RECYCLER_STATE = "recycler_state";
    private static final String SPINNER_POSITION = "spinner_position";

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.sort_options)
    Spinner mSpinner;
    @BindView(R.id.main_layout)
    FrameLayout mMainLayout;
    @BindView(R.id.loading_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.offline_error_main)
    ConstraintLayout mOfflineError;
    @BindView(R.id.offline_error_title)
    TextView mOfflineErrorTitle;
    @BindView(R.id.offline_error_text)
    TextView mOfflineErrorText;
    @BindView(R.id.offline_icon_imageview)
    ImageView mOfflineErrorIcon;
    @BindView(R.id.offline_error_retry_button)
    Button mOfflineErrorRetryButton;

    private MovieAdapter mMovieAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private GridLayoutManager mLayoutManager;
    private int mSpinnerPosition = 0;

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
        if (savedInstanceState != null) {
            mSpinnerPosition = savedInstanceState.getInt(SPINNER_POSITION);
        }
        mSpinner.setSelection(mSpinnerPosition, false); // Set the spinner to the default value before attaching the listener
        mSpinner.setOnItemSelectedListener(this);

        // Set up the GridLayout of poster images
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        if (savedInstanceState != null) {
            mLayoutManager.onRestoreInstanceState(savedInstanceState);
            mPosition = savedInstanceState.getInt(SCROLL_POSITION);
        }

        fetchData();

    }

    private void fetchData() {
        showLoading();
        if(NetworkUtils.isOnline(this)) {
            // Set up the CursorLoaderManager to detect changes in the data and update the views
            getSupportLoaderManager().initLoader(ID_POPULAR_MOVIE_LOADER, null, this);
            // Start the data sync to load in the movies
            PopularMoviesSyncUtils.initialize(this);
        } else {
            showOffline();
        }
    }

    private void showLoading() {
        mSpinner.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mOfflineError.setVisibility(View.INVISIBLE);


        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mOfflineError.setVisibility(View.INVISIBLE);

        mSpinner.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showOffline() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mSpinner.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

        mOfflineErrorIcon.setImageResource(R.drawable.ic_cloud_off_grey_24dp);
        mOfflineErrorTitle.setText(getText(R.string.error_offline_title));
        mOfflineErrorText.setText(getText(R.string.error_offline_details_text));
        mOfflineErrorRetryButton.setText(R.string.error_offline_button_label);
        mOfflineErrorRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });
        mOfflineError.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (mPosition < 0) {
            // No items were completely visible, this can happen on smaller phones in landscape
            mPosition = mLayoutManager.findFirstVisibleItemPosition();
        }
        outState.putInt(SCROLL_POSITION, mPosition);
        outState.putInt(SPINNER_POSITION, mSpinnerPosition);
        outState.putParcelable(RECYCLER_STATE, mLayoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(SCROLL_POSITION);
            mSpinnerPosition = savedInstanceState.getInt(SPINNER_POSITION);
            mLayoutManager.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d("onLoadFinished", String.format("Swapping Cursor, retrieved %d movies", data.getCount()));
        mMovieAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        hideLoading();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder;
        switch (id) {
            case ID_POPULAR_MOVIE_LOADER:
                sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC LIMIT 20";
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
    public void onClick(long id, int position) {
        mPosition = position;
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
        mPosition = RecyclerView.NO_POSITION; // Reset the scroll position
        mSpinnerPosition = position;
        if(NetworkUtils.isOnline(this) == true) {
            switch (position) {
                case 0:
                    // Popular was selected
                    getSupportLoaderManager().restartLoader(ID_POPULAR_MOVIE_LOADER, null, this);
                    break;
                case 1:
                    // Top Rated was selected
                    getSupportLoaderManager().restartLoader(ID_TOP_RATED_MOVIE_LOADER, null, this);
                    break;
            }
            showLoading();
        } else {
            showOffline();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
