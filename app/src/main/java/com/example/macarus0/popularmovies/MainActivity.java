package com.example.macarus0.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macarus0.popularmovies.data.MovieContract;
import com.example.macarus0.popularmovies.sync.PopularMoviesSyncUtils;
import com.example.macarus0.popularmovies.util.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieAdapterOnClickHandler,
        BottomNavigationView.OnNavigationItemSelectedListener{

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
    private static final int ID_FAVORITE_MOVIE_LOADER = 997;

    private static final String SCROLL_POSITION = "scroll_position";
    private static final String RECYCLER_STATE = "recycler_state";
    private static final String NAVIGATION_SELECTION = "navigation_selection";

    @BindView(R.id.loading_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.navigation)
    BottomNavigationView mBottomNavigationView;
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
    private int mSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initial MainActivity setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Set up the GridLayout of poster images
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this, this,
                NetworkUtils.getInstance(getString(R.string.tmbd_api_key)));
        mRecyclerView.setAdapter(mMovieAdapter);

        if (savedInstanceState != null) {
            mLayoutManager.onRestoreInstanceState(savedInstanceState);
            mPosition = savedInstanceState.getInt(SCROLL_POSITION);
            mBottomNavigationView.setSelectedItemId(mSelectedItem);
            mSelectedItem = savedInstanceState.getInt(NAVIGATION_SELECTION);
            this.onBottomNavItemSelected(mSelectedItem);
        }

        showLoading();
        fetchData();

    }

    private void fetchData() {
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
        mRecyclerView.setVisibility(View.INVISIBLE);
        mOfflineError.setVisibility(View.INVISIBLE);


        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mOfflineError.setVisibility(View.INVISIBLE);

        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showOffline() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

        mOfflineErrorIcon.setImageResource(R.drawable.ic_cloud_off_grey_24dp);
        mOfflineErrorTitle.setText(getText(R.string.error_offline_title));
        mOfflineErrorText.setText(getText(R.string.error_offline_main_text));
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
        outState.putInt(NAVIGATION_SELECTION, mBottomNavigationView.getSelectedItemId());
        outState.putParcelable(RECYCLER_STATE, mLayoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(SCROLL_POSITION);
            mSelectedItem = savedInstanceState.getInt(NAVIGATION_SELECTION);
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
        String[] selectionArgs = null;
        String selection = MovieContract.MovieEntry.getSelectionForTodaysMovies();
        Uri uri = MovieContract.MovieEntry.POPULAR_URI;
        sortOrder = null;
        switch (id) {
            case ID_POPULAR_MOVIE_LOADER:
                sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC LIMIT 20";
                break;
            case ID_TOP_RATED_MOVIE_LOADER:
                sortOrder = MovieContract.MovieEntry.COLUMN_USER_RATING + " DESC LIMIT 20";
                break;
            case ID_FAVORITE_MOVIE_LOADER:
                uri = MovieContract.MovieEntry.FAVORITE_URI;
                selection = null;
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
        return new CursorLoader(this,
                uri,
                POSTER_GRID_PROJECTION,
                selection,
                selectionArgs,
                sortOrder);
    }


    @Override
    public void onPosterClick(long id, int position) {
        mPosition = position;
        Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
        detailIntent.putExtra(DetailActivity.EXTRA_DETAIL_MOVIE_ID, Long.toString(id));
        startActivity(detailIntent);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader){
        mMovieAdapter.swapCursor(null);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mSelectedItem = item.getItemId();
        if(NetworkUtils.isOnline(this)) {
            onBottomNavItemSelected(mSelectedItem);
            mPosition = RecyclerView.NO_POSITION; // Reset the scroll position
            showLoading();
        } else {
            showOffline();
        }
        return true;
    }

    private void onBottomNavItemSelected(int id) {
        mSelectedItem = id;
        switch (mSelectedItem) {
            case R.id.action_popular:
                getSupportLoaderManager().destroyLoader(ID_FAVORITE_MOVIE_LOADER);
                getSupportLoaderManager().restartLoader(ID_POPULAR_MOVIE_LOADER, null, this);
                break;
            case R.id.action_top_rated:
                getSupportLoaderManager().destroyLoader(ID_FAVORITE_MOVIE_LOADER);
                getSupportLoaderManager().restartLoader(ID_TOP_RATED_MOVIE_LOADER, null, this);
                break;
            case R.id.action_favorites:
                getSupportLoaderManager().restartLoader(ID_FAVORITE_MOVIE_LOADER, null, this);
                break;
        }

    }
}
