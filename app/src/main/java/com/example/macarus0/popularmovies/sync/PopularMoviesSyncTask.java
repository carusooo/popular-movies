package com.example.macarus0.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.example.macarus0.popularmovies.data.MovieContract;
import com.example.macarus0.popularmovies.util.MovieJSONUtilities;
import com.example.macarus0.popularmovies.util.NetworkUtils;


public class PopularMoviesSyncTask {

    private static final String TAG = PopularMoviesSyncTask.class.getName();

    synchronized static public void syncMovies(ContentResolver contentResolver, NetworkUtils networkUtils,
                                               MovieJSONUtilities movieJSONUtilities, String movieId) {

        try {
            String requestUrl;
            if (null == movieId) {
                // Update the popular movies with both popular and top-rated
                requestUrl = networkUtils.getPopularMoviesUrl();
                String jsonPopularMoviesResponse = networkUtils.getStringFromUrl(requestUrl);
                ContentValues[] popularMovieValues = movieJSONUtilities.parsePopularJSON(jsonPopularMoviesResponse);


                requestUrl = networkUtils.getTopRatedMoviesUrl();
                String jsonTopMoviesResponse = networkUtils.getStringFromUrl(requestUrl);
                ContentValues[] topMovieValues = movieJSONUtilities.parsePopularJSON(jsonTopMoviesResponse);

                if (popularMovieValues != null && popularMovieValues.length != 0) {
                    int rowsInserted = contentResolver.bulkInsert(
                            MovieContract.MovieEntry.POPULAR_URI,
                            popularMovieValues);

                    rowsInserted += contentResolver.bulkInsert(
                            MovieContract.MovieEntry.POPULAR_URI,
                            topMovieValues);

                    Log.d(TAG, String.format("Added %d movies", rowsInserted));

                }
            } else {
                // Populate the details for a specific movie
                requestUrl = networkUtils.getMoviesUrl(movieId);
                String jsonMovieDetailResponse = networkUtils.getStringFromUrl(requestUrl);
                ContentValues movieValues = movieJSONUtilities.parseMovie(jsonMovieDetailResponse);

                if (movieValues != null) {
                    Uri newRow = contentResolver.insert(
                            MovieContract.MovieEntry.getMovieDetailsUri(movieId),
                            movieValues);

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
