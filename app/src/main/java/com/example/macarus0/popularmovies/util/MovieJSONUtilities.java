package com.example.macarus0.popularmovies.util;

import android.content.ContentValues;
import android.util.Log;

import com.example.macarus0.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class MovieJSONUtilities {

    private static final String TMDB_ID = "id";
    private static final String TMDB_TITLE = "title";
    private static final String TMDB_TAGLINE = "tagline";
    private static final String TMDB_OVERVIEW = "overview";
    private static final String TMDB_RELEASE_DATE = "release_date";
    private static final String TMDB_USER_RATING = "vote_average";
    private static final String TMDB_RUNTIME = "runtime";
    private static final String TMDB_POSTER_PATH = "poster_path";
    private static final String TMDB_POPULARITY = "popularity";
    private static final String TMDB_BACKDROP_PATH = "backdrop_path";

    private static final String TMDB_RESULTS = "results";

    /*
     * This method parses the JSON returned from tMDB for the listing of popular movies
     */
    public static ContentValues[] parsePopularJSON(String jsonResponse) throws JSONException {

        JSONObject popularJson = new JSONObject(jsonResponse);
        JSONArray popularJsonArray = popularJson.getJSONArray(TMDB_RESULTS);

        ContentValues[] popularContentValues = new ContentValues[popularJsonArray.length()];
        Log.d("parsePopularJSON", String.format("Parsing %d popular movies", popularJsonArray.length()));
        for (int i = 0; i < popularJsonArray.length(); i++) {
            ContentValues movieContentValues = new ContentValues();
            JSONObject movie = popularJsonArray.getJSONObject(i);
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_ID, movie.getInt(TMDB_ID));
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getString(TMDB_TITLE));
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getString(TMDB_OVERVIEW));
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getString(TMDB_RELEASE_DATE));
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, movie.getDouble(TMDB_USER_RATING));
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getString(TMDB_POSTER_PATH));
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getString(TMDB_POPULARITY));
            popularContentValues[i] = movieContentValues;
        }

        return popularContentValues;
    }


    public static ContentValues parseMovie(String movieJson) throws JSONException {
        ContentValues movieContentValues = new ContentValues();

        JSONObject movie = new JSONObject(movieJson);
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_ID, movie.getInt(TMDB_ID));
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getString(TMDB_TITLE));
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getString(TMDB_OVERVIEW));
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getString(TMDB_RELEASE_DATE));
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, movie.getDouble(TMDB_USER_RATING));
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getString(TMDB_POSTER_PATH));
       // movieContentValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, movie.getString(TMDB_RUNTIME));

        return movieContentValues;
    }

}
