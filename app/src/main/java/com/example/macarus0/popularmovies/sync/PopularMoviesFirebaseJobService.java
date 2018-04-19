package com.example.macarus0.popularmovies.sync;

import android.content.Context;
import android.os.AsyncTask;

public class PopularMoviesFirebaseJobService extends com.firebase.jobdispatcher.JobService {

    private AsyncTask<Void, Void, Void> mFetchMovieTask;

    @Override
    public boolean onStartJob(final com.firebase.jobdispatcher.JobParameters job) {
        mFetchMovieTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                PopularMoviesSyncTask.syncMovies(context);
                jobFinished(job, false);
                return null;
            }

            @Override
            protected void onPreExecute() {
                jobFinished(job, false);
            }
        };
        mFetchMovieTask.execute();
        return true;    }

    @Override
    public boolean onStopJob(final com.firebase.jobdispatcher.JobParameters job) {
        if (mFetchMovieTask != null) {
            mFetchMovieTask.cancel(true);
        }
        return true;    }
}
