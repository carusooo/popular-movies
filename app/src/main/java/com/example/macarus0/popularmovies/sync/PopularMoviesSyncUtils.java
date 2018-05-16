package com.example.macarus0.popularmovies.sync;

import android.content.ContentResolver;
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
                    syncMovieData(context, null);
                }
                if (null != cursor) {
                    cursor.close();
                }

            }
        });
        checkForContent.start();
    }

    /**
     * Helper method to start a sync immediately
     */
    public static void syncMovieData(@NonNull final Context context, String movieId) {
        Log.d(TAG, "Kicking off sync");
        PopularMoviesASyncTask popularMoviesASyncTask = new PopularMoviesASyncTask(context.getContentResolver(),
                NetworkUtils.getInstance(context.getString(R.string.tmbd_api_key)),
                new MovieJSONUtilities(),
                movieId);
        popularMoviesASyncTask.execute();
    }


    private static class PopularMoviesASyncTask extends AsyncTask<Void, Void, Void>
    {

        private final ContentResolver mContentResolver;
        private final NetworkUtils mNetworkUtils;
        private final MovieJSONUtilities mMovieJSONUtilities;
        private final String mMovieId;


        PopularMoviesASyncTask(ContentResolver contentResolver, NetworkUtils networkUtils,
                               MovieJSONUtilities movieJSONUtilities, String movieId) {
            mContentResolver = contentResolver;
            mNetworkUtils = networkUtils;
            mMovieJSONUtilities = movieJSONUtilities;
            mMovieId = movieId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(!isCancelled()) {
                PopularMoviesSyncTask.syncMovies(mContentResolver, mNetworkUtils,
                        mMovieJSONUtilities, mMovieId);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

        }
    }


}
