package com.example.macarus0.popularmovies.util;

import android.content.ContentValues;
import android.util.Log;

import com.example.macarus0.popularmovies.data.MovieContract;
import com.google.gson.Gson;

public class MovieJSONUtilities {

    public MovieJSONUtilities() {
    }

    /*
     * This method parses the JSON returned from tMDB for the listing of popular movies
     */
    public ContentValues[] parsePopularJSON(String jsonResponse) {
        Gson gson = new Gson();
        PopularMoviePage popularMoviePage = gson.fromJson(jsonResponse, PopularMoviePage.class);

        ContentValues[] popularContentValues = new ContentValues[popularMoviePage.results.length];
        Log.d("parsePopularJSON", String.format("Parsing %d popular movies", popularMoviePage.results.length));
        for (int i = 0; i < popularMoviePage.results.length; i++) {
            popularContentValues[i] = popularMoviePage.results[i].toContentValues();
        }
        return popularContentValues;
    }

    public ContentValues[] parseMovie(String movieJson) {
        Gson gson = new Gson();
        MovieDetails movieDetails = gson.fromJson(movieJson, MovieDetails.class);
        ContentValues movieContentValues = movieDetails.toContentValues();
        return new ContentValues[]{movieContentValues};
    }

    public ContentValues[] parseReviews(String movieJson) {
        Gson gson = new Gson();
        MovieReviewPage movieReviews = gson.fromJson(movieJson, MovieReviewPage.class);
        ContentValues[] reviewContentValues = new ContentValues[movieReviews.results.length];
        Log.d("parseReviews", String.format("Parsing %d movie reviews", movieReviews.results.length));
        for (int i = 0; i < movieReviews.results.length; i++) {
            reviewContentValues[i] = movieReviews.results[i].toContentValues();
        }
        return reviewContentValues;
    }

    public interface JSONParser {
        ContentValues[] parserFunction(String jsonString);
    }

    class PopularMoviePage {
        PopularMovie[] results;
    }

    class PopularMovie {
        int id;
        String title;
        String overview;
        String release_date;
        float vote_average;
        String poster_path;
        String popularity;

        ContentValues toContentValues() {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_ID, this.id);
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, this.title);
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, this.overview);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, this.release_date);
            contentValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, this.vote_average);
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, this.poster_path);
            contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, this.popularity);
            return contentValues;

        }
    }

    class MovieDetails {
        int id;
        String runtime;

        ContentValues toContentValues() {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_ID, this.id);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, this.runtime);
            return contentValues;
        }
    }

    class MovieReviewPage {
        MovieReview[] results;
    }

    class MovieReview {
        int id;
        String author;
        String content;
        String url;

        ContentValues toContentValues() {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_ID, this.id);
            contentValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR, this.author);
            contentValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_CONTENT, this.content);
            contentValues.put(MovieContract.MovieEntry.MOVIE_REVIEW_URL, this.url);
            return contentValues;

        }
    }

}
