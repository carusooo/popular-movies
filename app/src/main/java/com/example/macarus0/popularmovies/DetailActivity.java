package com.example.macarus0.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.macarus0.popularmovies.data.MovieContract;
import com.example.macarus0.popularmovies.sync.PopularMoviesSyncIntentService;
import com.example.macarus0.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] POPULAR_MOVIE_DETAIL_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_USER_RATING,
    };

    public static final int INDEX_MOVIE_DETAIL_MOVIE_ID = 0;
    private static final int INDEX_MOVIE_DETAIL_POSTER_PATH = 1;
    private static final int INDEX_MOVIE_DETAIL_TITLE = 2;
    public static final int INDEX_MOVIE_DETAIL_ORIGINAL_TITLE = 3;
    private static final int INDEX_MOVIE_DETAIL_OVERVIEW = 4;
    private static final int INDEX_MOVIE_DETAIL_RELEASE_DATE = 5;
    private static final int INDEX_MOVIE_DETAIL_USER_RATING = 6;

    private static final String[] MOVIE_DETAIL_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_RUNTIME,
    };

    private static final int INDEX_MOVIE_DETAIL_RUNTIME = 1;

    private static final int ID_DETAIL_LOADER = 444;


    public static final String EXTRA_DETAIL_MOVIE_ID = "detail_id";
    private String mMovieId;
    @BindView(R.id.poster_imageview)
    ImageView mPoster;
    @BindView(R.id.description_textview)
    TextView mDescription;
    @BindView(R.id.year_textview)
    TextView mYear;
    @BindView(R.id.runtime_textview)
    TextView mRuntime;
    @BindView(R.id.rating_textview)
    TextView mRating;
    @BindView(R.id.detail_title)
    TextView mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        // Get the movie ID and then look up that ID in the ContentResolver
        Intent intent = getIntent();
        mMovieId = intent.getStringExtra(EXTRA_DETAIL_MOVIE_ID);

        // Check if the movie details are available
        Cursor detailCursor = getContentResolver().query(
                MovieContract.MovieEntry.getMovieDetailsUri(mMovieId),
                MOVIE_DETAIL_PROJECTION,
                null,
                null,
                null
        );
        // If the details are not available, send a request to fetch them
        if (null == detailCursor || detailCursor.getCount() == 0) {
            // Set up a Loader to alert when the fetch is complete
            getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
            // Kick off the request
            Intent detailSyncIntent = new Intent(this, PopularMoviesSyncIntentService.class);
            detailSyncIntent.putExtra(DetailActivity.EXTRA_DETAIL_MOVIE_ID, mMovieId);
            this.startService(detailSyncIntent);

            // TODO: Add loading animation here
        } else {
            detailCursor.moveToFirst();
        }
        Cursor baseCursor = getContentResolver().query(
                MovieContract.MovieEntry.getMovieUri(mMovieId),
                POPULAR_MOVIE_DETAIL_PROJECTION,
                null,
                null,
                null
        );
        baseCursor.moveToFirst();

        populateUI(baseCursor, detailCursor);
    }


    private void populateUI(Cursor baseCursor, Cursor detailCursor) {
        mTitle.setText(baseCursor.getString(INDEX_MOVIE_DETAIL_TITLE));
        mDescription.setText(baseCursor.getString(INDEX_MOVIE_DETAIL_OVERVIEW));
        String fullDate = baseCursor.getString(INDEX_MOVIE_DETAIL_RELEASE_DATE);
        // Trim the date down to just the year
        mYear.setText(fullDate.substring(0, fullDate.indexOf('-')));

        mRating.setText(getString(R.string.rating_suffix, baseCursor.getString(INDEX_MOVIE_DETAIL_USER_RATING)));

        String posterUrl = NetworkUtils.getPosterUrl(getString(R.string.tmbd_api_key),
                baseCursor.getString(INDEX_MOVIE_DETAIL_POSTER_PATH));
        loadImageSetPalette(mPoster, mTitle, posterUrl);


        if (null != detailCursor && detailCursor.getCount() > 0) {
            populateDetails(detailCursor);
        }
    }

    private void populateDetails(Cursor detailCursor) {
        // TODO: Stop the loading animation (if running)
        mRuntime.setText(getString(R.string.runtime_units,
                detailCursor.getString(INDEX_MOVIE_DETAIL_RUNTIME)));
    }

    private void loadImageSetPalette(final ImageView imageView, final TextView paletteView, String url) {
        Picasso.with(this).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImageBitmap(bitmap);
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@NonNull Palette palette) {
                        paletteView.setBackgroundColor(palette.getDarkVibrantColor(
                                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark)));
                    }
                });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                paletteView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.colorPrimaryDark));
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        MovieContract.MovieEntry.getMovieDetailsUri(mMovieId),
                        MOVIE_DETAIL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Once the details have loaded, populate those fields in the UI
        if( data != null && data.moveToFirst() ) {
            populateDetails(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // No need to rerun anything here, just show the loading indicators

    }
}
