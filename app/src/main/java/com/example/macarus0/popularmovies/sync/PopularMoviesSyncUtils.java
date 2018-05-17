package com.example.macarus0.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.macarus0.popularmovies.R;
import com.example.macarus0.popularmovies.data.MovieContract;
import com.example.macarus0.popularmovies.util.MovieJSONUtilities;
import com.example.macarus0.popularmovies.util.NetworkUtils;

public class PopularMoviesSyncUtils {

    private static final String TAG = PopularMoviesSyncUtils.class.getName();


    synchronized public static void initialize(@NonNull final Context context) {
        /**
         *  Checks if the database of popular movies needs to be reloaded or not
         * and start an ASyncTask to populate the ContentProvider
         **/

        Log.d(TAG, "initializing");

        Thread checkForContent = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri popularMoviesQueryUri = MovieContract.MovieEntry.POPULAR_URI;

                String[] projectionColumns = {MovieContract.MovieEntry.COLUMN_ID};
                String selection = MovieContract.MovieEntry.getSelectionForTodaysMovies();
                /*
                 * This query is to detect whether the today's data is present in the database or
                 * if it needs to be retrieved.
                 */
                Cursor cursor = context.getContentResolver().query(
                        popularMoviesQueryUri,
                        projectionColumns,
                        selection,
                        null,
                        null
                );

                if (null == cursor || cursor.getCount() == 0) {
                    syncPopularMovies(context);
                }
                if (null != cursor) {
                    cursor.close();
                }

            }
        });
        checkForContent.start();
    }

    /*
     * Start a sync for popular movies
     */
    private static void syncPopularMovies(@NonNull final Context context) {
        Log.d(TAG, "Kicking off sync");
        MovieJSONUtilities.JSONParser jsonParser = new MovieJSONUtilities.JSONParser() {
            @Override
            public ContentValues[] parserFunction(String jsonString) {

                return new MovieJSONUtilities().parsePopularJSON(jsonString);
            }
        };
        NetworkUtils networkUtils = NetworkUtils.getInstance(context.getString(R.string.tmbd_api_key));

        String[] urls = new String[]{
                networkUtils.getPopularMoviesUrl(),
                networkUtils.getTopRatedMoviesUrl()
        };

        PopularMoviesASyncTask popularMoviesASyncTask = new PopularMoviesASyncTask(
                context.getContentResolver(),
                networkUtils,
                jsonParser,
                MovieContract.MovieEntry.POPULAR_URI,
                urls
                );
        popularMoviesASyncTask.execute();
    }

    /*
     * Start a sync for movie details
     */
    public static void syncMovieDetails(@NonNull final Context context, String movieId ) {
        Log.d(TAG, "Kicking off sync");
        MovieJSONUtilities.JSONParser jsonParser = new MovieJSONUtilities.JSONParser() {
            @Override
            public ContentValues[] parserFunction(String jsonString) {

                return new MovieJSONUtilities().parseMovie(jsonString);
            }
        };
        NetworkUtils networkUtils = NetworkUtils.getInstance(context.getString(R.string.tmbd_api_key));

        String[] urls = new String[]{
                networkUtils.getMovieDetailsUrl(movieId)
        };

        PopularMoviesASyncTask popularMoviesASyncTask = new PopularMoviesASyncTask(
                context.getContentResolver(),
                networkUtils,
                jsonParser,
                MovieContract.MovieEntry.getMovieDetailsUri(movieId),
                urls
        );
        popularMoviesASyncTask.execute();
    }

    /*
     * Start a sync for movie reviews
     */
    public static void syncMovieReview(@NonNull final Context context, String movieId ) {
        Log.d(TAG, "Kicking off sync");
        MovieJSONUtilities.JSONParser jsonParser = new MovieJSONUtilities.JSONParser() {
            @Override
            public ContentValues[] parserFunction(String jsonString) {

                return new MovieJSONUtilities().parseReviews(jsonString);
            }
        };
        NetworkUtils networkUtils = NetworkUtils.getInstance(context.getString(R.string.tmbd_api_key));

        String[] urls = new String[]{
                networkUtils.getMovieReviewsUrl(movieId)
        };

        PopularMoviesASyncTask popularMoviesASyncTask = new PopularMoviesASyncTask(
                context.getContentResolver(),
                networkUtils,
                jsonParser,
                MovieContract.MovieEntry.getMovieReviewsUri(movieId),
                urls
        );
        popularMoviesASyncTask.execute();
    }



    private static class PopularMoviesASyncTask extends AsyncTask<Void, Void, Void>
    {

        private final ContentResolver mContentResolver;
        private final NetworkUtils mNetworkUtils;
        private final MovieJSONUtilities.JSONParser mJsonParser;
        private final Uri mContentUri;
        private final String[] mUrls;


        PopularMoviesASyncTask(ContentResolver contentResolver, NetworkUtils networkUtils,
                               MovieJSONUtilities.JSONParser jsonParser, Uri contentUri, String[] urls) {
            mContentResolver = contentResolver;
            mNetworkUtils = networkUtils;
            mJsonParser = jsonParser;
            mContentUri = contentUri;
            mUrls = urls;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(!isCancelled()) {
                PopularMoviesSyncTask.syncMovies(mContentResolver, mNetworkUtils, mJsonParser,
                        mContentUri, mUrls);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

        }
    }


}
