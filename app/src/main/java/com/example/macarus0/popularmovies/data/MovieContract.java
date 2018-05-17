package com.example.macarus0.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MovieContract {


    public static final String CONTENT_AUTHORITY = "com.example.macarus0.popularmovies";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_POPULAR_MOVIES = "movies";

    public static final String PATH_MOVIE_DETAILS = "details";

    public static final String PATH_MOVIE_REVIEWS = "reviews";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri POPULAR_URI = BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_POPULAR_MOVIES)
                .build();

        public static final String POPULAR_MOVIE_TABLE_NAME = "popular_movies";

        /* This is the ID given by TMDb */
        public static final String COLUMN_ID = "id";

        /* The title of the movie */
        public static final String COLUMN_TITLE = "title";

        /* The original title of the movie */
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        /* A brief plot synopsis of the movie */
        public static final String COLUMN_OVERVIEW = "overview";

        /* The User Rating of the movie */
        public static final String COLUMN_USER_RATING = "vote_average";

        /* The Release Date of the movie */
        public static final String COLUMN_RELEASE_DATE = "release_date";

        /* The path for the poster image */
        public static final String COLUMN_POSTER_PATH = "poster_path";

        /* The path for the movie popularity */
        public static final String COLUMN_POPULARITY = "popularity";

        /* The path for the movie date of popularity */
        public static final String COLUMN_POPULARITY_DATE = "popularity_date";

        /* Table that stores details that aren't provided with the popular listing */

        public static final String MOVIE_DETAIL_TABLE_NAME = "movie_details";

        /* The Runtime of the movie */
        public static final String COLUMN_RUNTIME = "runtime";

        /*
         * Table for the reviews of a movie
         */
        public static final String MOVIE_REVIEW_TABLE_NAME = "reviews";

        /* The content of the review */
        public static final String COLUMN_REVIEW_CONTENT = "content";

        /* The author of the review */
        public static final String COLUMN_REVIEW_AUTHOR = "author";

        /* The URL of the review */
        public static final String MOVIE_REVIEW_URL = "url";


        /* Get the Uri for a specific movie */
        public static Uri getMovieUri(String movieId) {
            return BASE_CONTENT_URI.buildUpon().
                    appendPath(PATH_POPULAR_MOVIES).
                    appendPath(movieId)
                    .build();

        }

        /* Get the Uri for a specific movie */
        public static Uri getMovieDetailsUri(String movieId) {
            return BASE_CONTENT_URI.buildUpon().
                    appendPath(PATH_MOVIE_DETAILS).
                    appendPath(movieId)
                    .build();

        }

        /* Get the Uri for a movie's reviews */
        public static Uri getMovieReviewsUri(String movieId) {
            return BASE_CONTENT_URI.buildUpon().
                    appendPath(PATH_MOVIE_REVIEWS).
                    appendPath(movieId)
                    .build();
        }

        /* Get the selection statement for today's popular movies */
        public static String getSelectionForTodaysMovies() {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault());
            return "date(" + COLUMN_POPULARITY_DATE + ") >= date(\"" + dateFormat.format(new Date()) + "\")";
        }


    }


}
