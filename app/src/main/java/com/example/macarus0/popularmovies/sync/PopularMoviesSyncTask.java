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

    interface SyncTask {
        void syncFunction();
    }

    synchronized static public void syncListOfMovies(ContentResolver contentResolver, NetworkUtils networkUtils,
                                               MovieJSONUtilities.JSONParser jsonParser,
                                               Uri contentUri, String[] urls) {
        try {
            int rowsInserted = 0;
            for(String url : urls) {
                String jsonResponse = networkUtils.getStringFromUrl(url);
                ContentValues[] responseValues = jsonParser.parserFunction(jsonResponse);

                if (responseValues != null && responseValues.length != 0) {
                    rowsInserted += contentResolver.bulkInsert(
                            contentUri,
                            responseValues);
                }
            }
            Log.d(TAG, String.format("Added %d movies", rowsInserted));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized static public void syncMovieDetails(ContentResolver contentResolver, NetworkUtils networkUtils,
                                                     String url) {
        try {
            String jsonResponse = networkUtils.getStringFromUrl(url);
            MovieJSONUtilities movieJSONUtilities = new MovieJSONUtilities(jsonResponse);

            int rowsInserted = 0;
            ContentValues[] responseValues = movieJSONUtilities.getMovieDetails();
            if (responseValues != null && responseValues.length != 0) {
                rowsInserted += contentResolver.bulkInsert(
                        MovieContract.MovieEntry.getMovieDetailsUri(movieJSONUtilities.getMovieId()),
                        responseValues);
            }
            Log.d(TAG, String.format("Added %d rows of details", rowsInserted));

            rowsInserted = 0;
            responseValues = movieJSONUtilities.getReviews();
            if (responseValues != null && responseValues.length != 0) {
                rowsInserted += contentResolver.bulkInsert(
                        MovieContract.MovieEntry.getMovieReviewsUri(movieJSONUtilities.getMovieId()),
                        responseValues);
            }
            Log.d(TAG, String.format("Added %d rows of reviews", rowsInserted));

            rowsInserted = 0;
            responseValues = movieJSONUtilities.getVideos();
            if (responseValues != null && responseValues.length != 0) {
                rowsInserted += contentResolver.bulkInsert(
                        MovieContract.MovieEntry.getMovieVideosUri(movieJSONUtilities.getMovieId()),
                        responseValues);
            }
            Log.d(TAG, String.format("Added %d rows of videos", rowsInserted));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

