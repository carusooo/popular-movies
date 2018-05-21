package com.example.macarus0.popularmovies.util;

import android.content.ContentValues;
import android.util.Log;

import com.example.macarus0.popularmovies.data.MovieContract;
import com.google.gson.Gson;

public class MovieJSONUtilities {

    private MovieDetails mMovieDetails;

    public MovieJSONUtilities() {
    }

    public MovieJSONUtilities(String jsonString) {
        Gson gson = new Gson();
        mMovieDetails = gson.fromJson(jsonString, MovieDetails.class);
    }

    public String getMovieId() {
        return Integer.toString(mMovieDetails.id);
    }

    public ContentValues[] getMovieDetails() {
        ContentValues movieContentValues = mMovieDetails.toContentValues();
        return new ContentValues[]{movieContentValues};
    }

    public ContentValues[] getReviews() {
        ContentValues[] reviews = new ContentValues[mMovieDetails.reviews.results.length];
        for(int i = 0; i < mMovieDetails.reviews.results.length; i++) {
            reviews[i] = mMovieDetails.reviews.results[i].toContentValues(Integer.toString(this.mMovieDetails.id));
        }
        return reviews;
    }

    public ContentValues[] getVideos() {
        ContentValues[] videos = new ContentValues[mMovieDetails.videos.results.length];
        for(int i = 0; i < mMovieDetails.videos.results.length; i++) {
             videos[i] = mMovieDetails.videos.results[i].toContentValues(Integer.toString(this.mMovieDetails.id));
        }
        return videos;
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
        MovieReviewPage reviews;
        VideoPage videos;

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
        String id;
        String author;
        String content;
        String url;

        ContentValues toContentValues(String movieId) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_ID, this.id);
            contentValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_MOVIE_ID, movieId);
            contentValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR, this.author);
            contentValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_CONTENT, this.content);
            contentValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_URL, this.url);
            return contentValues;

        }
    }

    class VideoPage {
        Video[] results;
    }

    class Video {

        String id;
        String site;
        String key;
        String name;
        String type;

        ContentValues toContentValues(String movieId) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_ID, this.id);
            contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_MOVIE_ID, movieId);
            contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_KEY, this.key);
            contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_SITE, this.site);
            contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_NAME, this.name);
            contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_TYPE, this.type);
            return contentValues;

        }
    }

}
