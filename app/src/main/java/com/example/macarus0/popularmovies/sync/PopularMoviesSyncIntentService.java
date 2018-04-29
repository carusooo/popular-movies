package com.example.macarus0.popularmovies.sync;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.example.macarus0.popularmovies.DetailActivity;

import java.lang.ref.WeakReference;

/*
 * An (@link IntentService} subclass for handling asynchronous task requests in a service.
 */
public class PopularMoviesSyncIntentService extends IntentService {
    public PopularMoviesSyncIntentService() {
        super("PopularMoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // Unpack intent info
        final String movieId = intent.getStringExtra(DetailActivity.EXTRA_DETAIL_MOVIE_ID);
        // TODO: Add network checks here, find some way to notify the previous activity?
        new SyncTask(this, movieId).execute();
    }

    private static class SyncTask extends AsyncTask<Void, Void, Void>
    {
        // Since this is static, keep a reference to the service that started the task
        private WeakReference<PopularMoviesSyncIntentService> moviesSyncIntentServiceWeakReference;
        private String mMovieId;
        SyncTask(PopularMoviesSyncIntentService intentService, String movieId ) {
            moviesSyncIntentServiceWeakReference = new WeakReference<>(intentService);
            mMovieId = movieId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            IntentService intentService = moviesSyncIntentServiceWeakReference.get();
            Context context = intentService.getApplicationContext();
            PopularMoviesSyncTask.syncMovies(context, mMovieId);
            return null;
        }

        @Override
        protected void onPreExecute() {
        }
    }

}


