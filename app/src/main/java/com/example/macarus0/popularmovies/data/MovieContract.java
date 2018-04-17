package com.example.macarus0.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {



    public static final String CONTENT_AUTHORITY = "com.example.macarus0.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_POPULAR_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_POPULAR_MOVIES)
                .build();

        public static final String TABLE_NAME = "movies";

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

        /* The Runtime of the movie */
        public static final String COLUMN_RUNTIME = "runtime";

        /* The path for the poster image */
        public static final String COLUMN_POSTER_PATH = "poster_path";

    }



}
