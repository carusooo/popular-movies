package com.example.macarus0.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.example.macarus0.popularmovies.util.MovieJSONUtilities;
import com.example.macarus0.popularmovies.util.NetworkUtils;


public class PopularMoviesSyncTask {

    private static final String TAG = PopularMoviesSyncTask.class.getName();

    synchronized static public void syncMovies(ContentResolver contentResolver, NetworkUtils networkUtils,
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
}
