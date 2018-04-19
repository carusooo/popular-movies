package com.example.macarus0.popularmovies.sync;


import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/*
* An (@link IntentService} subclass for handling asynchronous task requests in a service.
 */
public class PopularMoviesSyncIntentService extends IntentService{
    public PopularMoviesSyncIntentService() {
        super("PopularMoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        PopularMoviesSyncTask.syncMovies(this);
    }
}
