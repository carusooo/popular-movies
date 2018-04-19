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

import java.util.Objects;

public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIES_POPULAR = 100;
    public static final int CODE_MOVIE = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    MovieDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        /* This URI is content://com.example.macarus0.popularmovies/movies/ */
        matcher.addURI(authority, MovieContract.PATH_POPULAR_MOVIES, CODE_MOVIES_POPULAR);

        Log.d("buildUriMatcher", String.format("Built URI matcher %s/%s", authority, MovieContract.PATH_POPULAR_MOVIES));
        /* This URI is content://com.example.macarus0.popularmovies/movies/#### where #### is the
         *  ID of a movie.
         */
        matcher.addURI(authority, MovieContract.PATH_POPULAR_MOVIES + "/#", CODE_MOVIE);

        Log.d("buildUriMatcher", String.format("Built URI matcher %s/%s", authority, MovieContract.PATH_POPULAR_MOVIES + "/#"));

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
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
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
        Log.d("insert", String.format("Attempting insert to %s", uri.toString()));
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES_POPULAR:
                Log.d("bulkInsert", String.format("Attempting bulkInsert %d PopularMovies to %s",
                        values.length, uri.toString()));
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME, null,
                                value, SQLiteDatabase.CONFLICT_REPLACE);
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
                    Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
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
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES_POPULAR:
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);


        }
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    class MovieDbHelper extends SQLiteOpenHelper {
        public static final String MOVIE_DB_NAME = "movies.db";

        public static final int MOVIE_DB_VERSION = 1;

        MovieDbHelper(Context context) {
            super(context, MOVIE_DB_NAME, null, MOVIE_DB_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("onCreate", String.format("Creating table %s", MovieContract.MovieEntry.TABLE_NAME));

            final String SQL_CREATE_MOVIE_TABLE =
                    "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + "(" +
                            /* Reuse the existing IDs from TMDb */
                            MovieContract.MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                            MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                            MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                            MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                            MovieContract.MovieEntry.COLUMN_RUNTIME + " TEXT, " +
                            MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " DATE NOT NULL, " +
                            MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                            MovieContract.MovieEntry.COLUMN_USER_RATING + " REAL NOT NULL);";

            db.execSQL(SQL_CREATE_MOVIE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
            onCreate(db);

        }
    }
}
