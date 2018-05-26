package com.example.macarus0.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.macarus0.popularmovies.data.MovieContract;
import com.example.macarus0.popularmovies.sync.PopularMoviesSyncUtils;
import com.example.macarus0.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements
        VideoAdapter.VideoOnClickHandler {

    public static final String EXTRA_DETAIL_MOVIE_ID = "detail_id";
    public static final int INDEX_REVIEW_AUTHOR = 0;
    public static final int INDEX_REVIEW_CONTENT = 1;
    public static final int INDEX_VIDEO_SITE = 0;
    public static final int INDEX_VIDEO_KEY = 1;
    public static final int INDEX_VIDEO_TYPE = 2;
    public static final int INDEX_VIDEO_NAME = 3;
    private static final String[] POPULAR_MOVIE_DETAIL_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_USER_RATING,
    };
    private static final int INDEX_MOVIE_DETAIL_POSTER_PATH = 1;
    private static final int INDEX_MOVIE_DETAIL_TITLE = 2;
    private static final int INDEX_MOVIE_DETAIL_OVERVIEW = 3;
    private static final int INDEX_MOVIE_DETAIL_RELEASE_DATE = 4;
    private static final int INDEX_MOVIE_DETAIL_USER_RATING = 5;
    private static final String[] MOVIE_DETAIL_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_RUNTIME,
    };
    private static final int INDEX_MOVIE_DETAIL_RUNTIME = 1;
    private static final String[] MOVIE_REVIEW_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR,
            MovieContract.MovieEntry.COLUMN_REVIEW_CONTENT,
    };
    private static final String[] MOVIE_VIDEO_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_VIDEO_SITE,
            MovieContract.MovieEntry.COLUMN_VIDEO_KEY,
            MovieContract.MovieEntry.COLUMN_VIDEO_TYPE,
            MovieContract.MovieEntry.COLUMN_VIDEO_NAME,
    };
    private static final int ID_DETAIL_LOADER = 444;
    private static final int ID_REVIEW_LOADER = 445;
    private static final int ID_VIDEO_LOADER = 446;

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
    @BindView(R.id.detail_content)
    ConstraintLayout mContent;
    @BindView(R.id.videos_rv)
    RecyclerView mVideos;
    @BindView(R.id.review_rv)
    RecyclerView mReviews;
    @BindView(R.id.detail_scrollview)
    ScrollView mScrollView;
    @BindView(R.id.details_loading)
    ProgressBar mDetailsLoading;
    @BindView(R.id.offline_error_details)
    ConstraintLayout mOfflineError;
    @BindView(R.id.offline_error_title)
    TextView mOfflineErrorTitle;
    @BindView(R.id.offline_error_text)
    TextView mOfflineErrorText;
    @BindView(R.id.offline_icon_imageview)
    ImageView mOfflineErrorIcon;
    @BindView(R.id.offline_error_retry_button)
    Button mOfflineErrorRetryButton;

    private String mMovieId;
    private final LoaderManager.LoaderCallbacks<Cursor> DetailsCallbacks = new
            LoaderManager.LoaderCallbacks<Cursor>() {
                @NonNull
                @Override
                public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
                    return new CursorLoader(getApplicationContext(),
                            MovieContract.MovieEntry.getMovieDetailsUri(mMovieId),
                            MOVIE_DETAIL_PROJECTION,
                            null,
                            null,
                            null);
                }

                @Override
                public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
                    // Once the details have loaded, populate those fields in the UI
                    if (data != null && data.moveToFirst()) {
                        Log.d("onLoadFinished", "Loading Details");
                        populateDetails(data);
                    }
                }

                @Override
                public void onLoaderReset(@NonNull Loader<Cursor> loader) {
                    // No need to rerun anything here, just show the loading indicators
                }
            };
    private NetworkUtils mNetworkUtils;
    private ReviewAdapter mReviewAdapter;
    private final LoaderManager.LoaderCallbacks<Cursor> ReviewsCallbacks = new
            LoaderManager.LoaderCallbacks<Cursor>() {
                @NonNull
                @Override
                public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
                    return new CursorLoader(getApplicationContext(),
                            MovieContract.MovieEntry.getMovieReviewsUri(mMovieId),
                            MOVIE_REVIEW_PROJECTION,
                            null,
                            null,
                            null);
                }

                @Override
                public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
                    // Once the details have loaded, populate those fields in the UI
                    if (data != null && data.moveToFirst()) {
                        Log.d("onLoadFinished", "Loading Details");
                        mReviewAdapter.swapCursor(data);
                    }
                    showReviews();
                }

                @Override
                public void onLoaderReset(@NonNull Loader<Cursor> loader) {
                    // No need to rerun anything here, just show the loading indicators
                }
            };
    private VideoAdapter mVideoAdapter;
    private final LoaderManager.LoaderCallbacks<Cursor> VideosCallbacks = new
            LoaderManager.LoaderCallbacks<Cursor>() {
                @NonNull
                @Override
                public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
                    return new CursorLoader(getApplicationContext(),
                            MovieContract.MovieEntry.getMovieVideosUri(mMovieId),
                            MOVIE_VIDEO_PROJECTION,
                            null,
                            null,
                            null);
                }

                @Override
                public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
                    // Once the details have loaded, populate those fields in the UI
                    if (data != null && data.moveToFirst()) {
                        Log.d("onLoadFinished", "Loading Videos");
                        mVideoAdapter.swapCursor(data);
                    }
                    showReviews();
                }

                @Override
                public void onLoaderReset(@NonNull Loader<Cursor> loader) {
                    // No need to rerun anything here, just show the loading indicators
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        // Get the movie ID and then look up that ID in the ContentResolver
        Intent intent = getIntent();
        mMovieId = intent.getStringExtra(EXTRA_DETAIL_MOVIE_ID);

        mNetworkUtils = NetworkUtils.getInstance(getString(R.string.tmbd_api_key));
        showLoading();

        Cursor baseCursor = getContentResolver().query(
                MovieContract.MovieEntry.getMovieUri(mMovieId),
                POPULAR_MOVIE_DETAIL_PROJECTION,
                null,
                null,
                null
        );
        baseCursor.moveToFirst();
        populateUI(baseCursor);

        /*
         *  Create the layout manager for the videos and attach to the recyclerView
         *  Scrolling is disabled here since the content is wrapped by a ScrollView
         */
        LinearLayoutManager mVideoLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mVideoLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mVideos.setLayoutManager(mVideoLayoutManager);
        mVideos.setHasFixedSize(true);
        mVideoAdapter = new VideoAdapter(this, this);
        mVideos.setAdapter(mVideoAdapter);


        /*
         *  Create the layout manager for the reviews and attach to the recyclerView
         *  Scrolling is disabled here since the content is wrapped by a ScrollView
         */
        LinearLayoutManager mReviewLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mReviewLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mReviews.setLayoutManager(mReviewLayoutManager);
        mReviews.setHasFixedSize(true);
        mReviewAdapter = new ReviewAdapter(this);
        mReviews.setAdapter(mReviewAdapter);


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
            fetchDetails();
        } else {
            detailCursor.moveToPosition(0);
            populateDetails(detailCursor);

            Cursor reviewCursor = getContentResolver().query(
                    MovieContract.MovieEntry.getMovieReviewsUri(mMovieId),
                    MOVIE_REVIEW_PROJECTION,
                    null,
                    null,
                    null
            );
            mReviewAdapter.swapCursor(reviewCursor);
            showReviews();

            Cursor videoCursor = getContentResolver().query(
                    MovieContract.MovieEntry.getMovieVideosUri(mMovieId),
                    MOVIE_VIDEO_PROJECTION,
                    null,
                    null,
                    null
            );
            mVideoAdapter.swapCursor(videoCursor);
            showVideos();
        }
    }

    private void fetchDetails() {
        if (NetworkUtils.isOnline(this)) {
            // Set up a Loader to alert when the detail and review fetch is complete
            getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, DetailsCallbacks);
            getSupportLoaderManager().initLoader(ID_REVIEW_LOADER, null, ReviewsCallbacks);
            getSupportLoaderManager().initLoader(ID_VIDEO_LOADER, null, VideosCallbacks);

            // Kick off the request
            PopularMoviesSyncUtils.syncMovieDetails(this, mMovieId);
        } else {
            showOffline();
        }
    }

    private void showLoading() {
        mContent.setVisibility(View.INVISIBLE);
        mOfflineError.setVisibility(View.INVISIBLE);
        mReviews.setVisibility(View.INVISIBLE);

        mDetailsLoading.setVisibility(View.VISIBLE);
    }

    private void showContent() {
        mDetailsLoading.setVisibility(View.INVISIBLE);
        mOfflineError.setVisibility(View.INVISIBLE);

        mContent.setVisibility(View.VISIBLE);
    }

    private void showOffline() {
        mContent.setVisibility(View.INVISIBLE);
        mDetailsLoading.setVisibility(View.INVISIBLE);

        mOfflineErrorIcon.setImageResource(R.drawable.ic_cloud_off_grey_24dp);
        mOfflineErrorTitle.setText(getText(R.string.error_offline_title));
        mOfflineErrorText.setText(getText(R.string.error_offline_details_text));
        mOfflineErrorRetryButton.setText(R.string.error_offline_button_label);
        mOfflineErrorRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDetails();
            }
        });
        mOfflineError.setVisibility(View.VISIBLE);
    }


    private void populateUI(Cursor baseCursor) {
        mTitle.setText(baseCursor.getString(INDEX_MOVIE_DETAIL_TITLE));
        mDescription.setText(baseCursor.getString(INDEX_MOVIE_DETAIL_OVERVIEW));
        String fullDate = baseCursor.getString(INDEX_MOVIE_DETAIL_RELEASE_DATE);
        // Trim the date down to just the year
        mYear.setText(fullDate.substring(0, fullDate.indexOf('-')));

        mRating.setText(getString(R.string.rating_suffix, baseCursor.getString(INDEX_MOVIE_DETAIL_USER_RATING)));

        String posterUrl = mNetworkUtils.getPosterUrl(
                baseCursor.getString(INDEX_MOVIE_DETAIL_POSTER_PATH));
        TextView[] textViews = {mTitle, mRuntime, mRating, mYear};
        loadImageSetPalette(mPoster, textViews, getWindow(), posterUrl);
    }

    private void populateDetails(Cursor detailCursor) {

        mRuntime.setText(getString(R.string.runtime_units,
                detailCursor.getString(INDEX_MOVIE_DETAIL_RUNTIME)));
        showContent();
    }

    private void showReviews() {
        mReviews.setVisibility(View.VISIBLE);
    }

    private void showVideos() {
        mVideos.setVisibility(View.VISIBLE);
    }


    private void loadImageSetPalette(final ImageView imageView, final TextView[] textViews, final Window window, String url) {
        Picasso.with(this).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImageBitmap(bitmap);
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@NonNull Palette palette) {
                        Palette.Swatch swatch = palette.getMutedSwatch();
                        if (null != swatch) {
                            for (TextView textView : textViews) {
                                textView.setBackgroundColor(swatch.getRgb());
                                textView.setTextColor(swatch.getBodyTextColor());
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                window.setStatusBarColor(swatch.getRgb());
                            }
                        }
                    }
                });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @Override
    public void onVideoClick(String url) {
        this.launchUrl(url);
    }

    private void launchUrl(String urlString) {
        Uri url = Uri.parse(urlString);

        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }
}
