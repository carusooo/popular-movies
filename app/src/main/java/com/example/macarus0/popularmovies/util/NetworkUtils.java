package com.example.macarus0.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    public static String getPopularMoviesUrl(String key) {
        return String.format("https://api.themoviedb.org/3/movie/popular?api_key=%s", key);
    }

    public static String getTopRatedMoviesUrl(String key) {
        return String.format("https://api.themoviedb.org/3/movie/top_rated?api_key=%s", key);
    }

    public static String getMoviesUrl(String key, String movieId) {
        return String.format("https://api.themoviedb.org/3/movie/%s?api_key=%s", movieId, key);
    }

    public static String getPosterUrl(String key, String movieId) {
         return String.format("https://image.tmdb.org/t/p/w185/%s?api_key=%s", movieId, key);
    }


    public static String getStringFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        return streamToString(httpURLConnection.getInputStream());
    }

    private static String streamToString(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder(inputStream.available());
        String line;
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line).append('\n');
        }
        return stringBuilder.toString();
    }


    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            // If no connectivity, return false
            Log.d("NetworkUtils", "No Connection");
            return false;
        } else {
            return  true;
        }
    }
}
