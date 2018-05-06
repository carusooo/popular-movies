package com.example.macarus0.popularmovies.sync;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.macarus0.popularmovies.data.MovieContract;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class PopularMoviesSyncUtils {

    private static final String TAG = PopularMoviesSyncUtils.class.getName();
    private static boolean sInitialized;


    synchronized public static void initialize(@NonNull final Context context) {
        /**
         *  Checks if the database of popular movies needs to be reloaded or not
         * and start an ASyncTask to populate the ContentProvider
         */

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
        sInitialized = true;
    }

    /**
     * Helper method to start a sync immediately
     */
    public static void syncMovieData(@NonNull final Context context, String movieId) {
        Log.d(TAG, "Kicking off sync");
        SyncTask syncTask = new SyncTask(context, movieId);
        syncTask.execute();
    }


    private static class SyncTask extends AsyncTask<Void, Void, Void>
    {
        // Since this is static, keep a reference to the service that started the task
        private WeakReference<Context> mContext;
        private final String mMovieId;

        SyncTask(Context context, String movieId ) {
            mContext = new WeakReference<>(context);
            mMovieId = movieId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(!isCancelled()) {
                PopularMoviesSyncTask.syncMovies(mContext, mMovieId);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mContext = null;
        }
    }


}
