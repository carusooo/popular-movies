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

    public static final String PATH_MOVIE_FAVORITES = "favorites";

    public static final String PATH_MOVIE_REVIEWS = "reviews";

    public static final String PATH_MOVIE_VIDEOS = "videos";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri POPULAR_URI = BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_POPULAR_MOVIES)
                .build();

        public static final Uri FAVORITE_URI = BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_MOVIE_FAVORITES)
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

        /* This is the ID of the review */
        public static final String COLUMN_REVIEW_ID = "review_id";

        /* This is the ID given by TMDb */
        public static final String COLUMN_REVIEW_MOVIE_ID = "review_movie_id";

        /* The content of the review */
        public static final String COLUMN_REVIEW_CONTENT = "content";

        /* The author of the review */
        public static final String COLUMN_REVIEW_AUTHOR = "author";

        /* The URL of the review */
        public static final String COLUMN_REVIEW_URL = "url";

        /*
         * Table for the videos of a movie
         */
        public static final String MOVIE_VIDEO_TABLE_NAME = "videos";

        /* This is the ID of the video*/
        public static final String COLUMN_VIDEO_ID = "video_id";

        /* This is the movie ID given by TMDb */
        public static final String COLUMN_VIDEO_MOVIE_ID = "video_movie_id";

        /* The site hosting the video */
        public static final String COLUMN_VIDEO_SITE = "site";

        /* The key for the video */
        public static final String COLUMN_VIDEO_KEY = "video_key";

        /* The title for the video */
        public static final String COLUMN_VIDEO_NAME = "video_name";

        /* The type of the video */
        public static final String COLUMN_VIDEO_TYPE = "video_type";

        /*
         * Table for storing favorite status
         */
        public static final String MOVIE_FAVORITE_TABLE_NAME = "favorites";

        /* This is the movie ID given by TMDb */
        public static final String COLUMN_FAVORITE_MOVIE_ID = "favorite_movie_id";

        /* Whether or not the video is a favorite */
        public static final String COLUMN_FAVORITE_STATUS = "favorite";

        /* When the video was marked as favorite */
        public static final String COLUMN_FAVORITE_DATE = "favorite_datea";


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

        /* Get the Uri for a movie's videos */
        public static Uri getMovieVideosUri(String movieId) {
            return BASE_CONTENT_URI.buildUpon().
                    appendPath(PATH_MOVIE_VIDEOS).
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
