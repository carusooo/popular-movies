package com.example.macarus0.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class MovieProvider extends ContentProvider {
    private static final int CODE_MOVIES_POPULAR = 100;

    private static final int CODE_MOVIE_POPULAR = 101;
    private static final int CODE_MOVIE_DETAILS = 102;
    private static final int CODE_MOVIE_REVIEWS = 103;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        /* This URI is content://com.example.macarus0.popularmovies/movies/ */
        matcher.addURI(authority, MovieContract.PATH_POPULAR_MOVIES, CODE_MOVIES_POPULAR);
        Log.d("buildUriMatcher", String.format("Built URI matcher %s/%s", authority, MovieContract.PATH_POPULAR_MOVIES));

        /* This URI is content://com.example.macarus0.popularmovies/movies/#### where #### is the
         *  ID of a movie.
         */
        matcher.addURI(authority, MovieContract.PATH_POPULAR_MOVIES + "/#", CODE_MOVIE_POPULAR);
        Log.d("buildUriMatcher", String.format("Built URI matcher %s/%s", authority, MovieContract.PATH_POPULAR_MOVIES + "/#"));

        /* This URI is content://com.example.macarus0.popularmovies/details/#### where #### is the
         *  ID of a movie.
         */
        matcher.addURI(authority, MovieContract.PATH_MOVIE_DETAILS + "/#", CODE_MOVIE_DETAILS);
        Log.d("buildUriMatcher", String.format("Built URI matcher %s/%s", authority, MovieContract.PATH_MOVIE_DETAILS + "/#"));

        /* This URI is content://com.example.macarus0.popularmovies/reviews/#### where #### is the
         *  ID of a movie.
         */
        matcher.addURI(authority, MovieContract.PATH_MOVIE_REVIEWS + "/#", CODE_MOVIE_REVIEWS);
        Log.d("buildUriMatcher", String.format("Built URI matcher %s/%s", authority, MovieContract.PATH_MOVIE_REVIEWS + "/#"));

        return matcher;

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d("delete", String.format("Attempting delete to %s", uri.toString()));
        int numRowsDeleted;

        /* This is so the delete operation will return the number of rows deleted */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIES_POPULAR:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.POPULAR_MOVIE_TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;

    }

    @Override
    public String getType(@NonNull Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d("insert", String.format("Attempting insert %s to %s %s",
                values.get(MovieContract.MovieEntry.COLUMN_ID), uri.toString(), values.toString()));
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsInserted = 0;
        String tableName;
        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIES_POPULAR:
                Log.d("bulkInsert", String.format("Attempting bulkInsert %d PopularMovies to %s",
                        values.length, uri.toString()));
                tableName = MovieContract.MovieEntry.POPULAR_MOVIE_TABLE_NAME;
                break;

            case CODE_MOVIE_DETAILS:
                Log.d("bulkInsert", String.format("Attempting bulkInsert %d MovieDetails to %s",
                        values.length, uri.toString()));
                tableName = MovieContract.MovieEntry.MOVIE_DETAIL_TABLE_NAME;
                break;

            case CODE_MOVIE_REVIEWS:
                Log.d("bulkInsert", String.format("Attempting bulkInsert %d Reviews to %s",
                        values.length, uri.toString()));
                tableName = MovieContract.MovieEntry.MOVIE_REVIEW_TABLE_NAME;
                break;

            default:
                return super.bulkInsert(uri, values);
        }
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long _id = db.insertWithOnConflict(
                        tableName,
                        null,
                        value,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (_id != -1) {
                    rowsInserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (rowsInserted > 0) {
            Log.d("bulkInsert", String.format("Inserted %d rows", rowsInserted));
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsInserted;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor;
        String[] selectionArguments;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES_POPULAR:
                cursor = db.query(MovieContract.MovieEntry.POPULAR_MOVIE_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIE_POPULAR:
                Log.d("MovieQuery", String.format("Looking for %s", uri.getLastPathSegment()));
                selectionArguments = new String[]{uri.getLastPathSegment()};
                cursor = db.query(MovieContract.MovieEntry.POPULAR_MOVIE_TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_ID + " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_MOVIE_DETAILS:
                Log.d("DetailsQuery", String.format("Looking for %s", uri.getLastPathSegment()));
                selectionArguments = new String[]{uri.getLastPathSegment()};
                cursor = db.query(MovieContract.MovieEntry.MOVIE_DETAIL_TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_ID + " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIE_REVIEWS:
                Log.d("ReviewsQuery", String.format("Looking for %s", uri.getLastPathSegment()));
                selectionArguments = new String[]{uri.getLastPathSegment()};
                cursor = db.query(MovieContract.MovieEntry.MOVIE_REVIEW_TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_REVIEW_MOVIE_ID + " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                Log.d("Reviews", String.format("Retrieved %d reviews", cursor.getCount()));
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class MovieDbHelper extends SQLiteOpenHelper {
        static final String MOVIE_DB_NAME = "movies.db";
        static final int MOVIE_DB_VERSION = 1;
        private final String TAG = MovieDbHelper.class.getName();

        MovieDbHelper(Context context) {
            super(context, MOVIE_DB_NAME, null, MOVIE_DB_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "Creating Database");
            final String SQL_CREATE_POPULAR_MOVIE_TABLE =
                    "CREATE TABLE " + MovieContract.MovieEntry.POPULAR_MOVIE_TABLE_NAME + "(" +
                            /* Reuse the existing IDs from TMDb */
                            MovieContract.MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                            MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                            MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                            MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                            MovieContract.MovieEntry.COLUMN_RUNTIME + " TEXT, " +
                            MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " DATE NOT NULL, " +
                            MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                            MovieContract.MovieEntry.COLUMN_USER_RATING + " REAL NOT NULL," +
                            MovieContract.MovieEntry.COLUMN_POPULARITY_DATE + " DATETIME DEFAULT CURRENT_DATE);";
            Log.d("onCreate", String.format("Creating table %s as %s",
                    MovieContract.MovieEntry.POPULAR_MOVIE_TABLE_NAME, SQL_CREATE_POPULAR_MOVIE_TABLE));
            db.execSQL(SQL_CREATE_POPULAR_MOVIE_TABLE);

            final String SQL_CREATE_MOVIE_DETAIL_TABLE =
                    "CREATE TABLE " + MovieContract.MovieEntry.MOVIE_DETAIL_TABLE_NAME + "(" +
                            MovieContract.MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                            MovieContract.MovieEntry.COLUMN_RUNTIME + " TEXT NOT NULL" +
                            ");";
            db.execSQL(SQL_CREATE_MOVIE_DETAIL_TABLE);

            final String SQL_CREATE_MOVIE_REVIEW_TABLE =
                    "CREATE TABLE " + MovieContract.MovieEntry.MOVIE_REVIEW_TABLE_NAME + "(" +
                            MovieContract.MovieEntry.COLUMN_REVIEW_ID + " TEXT PRIMARY KEY," +
                            MovieContract.MovieEntry.COLUMN_REVIEW_MOVIE_ID + " INTEGER NOT NULL," +
                            MovieContract.MovieEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +
                            MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                            MovieContract.MovieEntry.COLUMN_REVIEW_URL + " TEXT NOT NULL " +
                            ");";
            db.execSQL(SQL_CREATE_MOVIE_REVIEW_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.POPULAR_MOVIE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.MOVIE_DETAIL_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.MOVIE_REVIEW_TABLE_NAME);
            onCreate(db);

        }
    }
}
