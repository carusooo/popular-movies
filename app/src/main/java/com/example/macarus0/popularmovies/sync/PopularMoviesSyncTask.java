package com.example.macarus0.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.macarus0.popularmovies.R;
import com.example.macarus0.popularmovies.data.MovieContract;
import com.example.macarus0.popularmovies.util.MovieJSONUtilities;
import com.example.macarus0.popularmovies.util.NetworkUtils;

import java.util.Objects;


class PopularMoviesSyncTask {

    private static final String TAG = PopularMoviesSyncTask.class.getName();

    synchronized public static void syncMovies(Context context, String movieId) {
        try {

            String api_key = context.getString(R.string.tmbd_api_key);
            if(Objects.equals(api_key, context.getString(R.string.tmbd_api_key_dummy))) {
                throw new UnsupportedOperationException("Missing proper API key");
            }
            String requestUrl;
            if(null == movieId) {
                // Update the popular movies with both popular and top-rated
                requestUrl = NetworkUtils.getPopularMoviesUrl(context.getString(R.string.tmbd_api_key));
                String jsonPopularMoviesResponse = NetworkUtils.getStringFromUrl(requestUrl);
                ContentValues[] popularMovieValues = MovieJSONUtilities.parsePopularJSON(jsonPopularMoviesResponse);


                requestUrl = NetworkUtils.getTopRatedMoviesUrl(context.getString(R.string.tmbd_api_key));
                String jsonTopMoviesResponse = NetworkUtils.getStringFromUrl(requestUrl);
                ContentValues[] topMovieValues = MovieJSONUtilities.parsePopularJSON(jsonTopMoviesResponse);

                if(popularMovieValues != null && popularMovieValues.length != 0) {
                    ContentResolver contentResolver = context.getContentResolver();
                    int rowsInserted = contentResolver.bulkInsert(
                            MovieContract.MovieEntry.POPULAR_URI,
                            popularMovieValues);

                    rowsInserted += contentResolver.bulkInsert(
                            MovieContract.MovieEntry.POPULAR_URI,
                            topMovieValues);

                    Log.d(TAG, String.format("Added %d movies", rowsInserted));

                } else {
                    Log.d(TAG, "Got no movies");
                }
            } else {
                // Populate the details for a specific movie
                requestUrl = NetworkUtils.getMoviesUrl(context.getString(R.string.tmbd_api_key), movieId);
                String jsonMovieDetailResponse = NetworkUtils.getStringFromUrl(requestUrl);
                ContentValues movieValues = MovieJSONUtilities.parseMovie(jsonMovieDetailResponse);

                if(movieValues != null) {
                    ContentResolver contentResolver = context.getContentResolver();

                    Uri newRow = contentResolver.insert(
                            MovieContract.MovieEntry.getMovieDetailsUri(movieId),
                            movieValues);

                } else {
                    Log.d(TAG, String.format("Got no movie for id %s", movieId));
                }

            }



        } catch (Exception e) {
            Log.d(TAG, "Error syncing movies");
            e.printStackTrace();
        }
    }
}
