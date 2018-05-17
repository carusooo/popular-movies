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

    private final String mApiKey;

    private static NetworkUtils sNetworkUtils;

    public static NetworkUtils getInstance(String apiKey) {
        if(sNetworkUtils == null) {
            sNetworkUtils = new NetworkUtils(apiKey);
        }
        return sNetworkUtils;
    }

    private NetworkUtils(String apiKey) {
        mApiKey = apiKey;
    }

    public  String getPopularMoviesUrl() {
        return String.format("https://api.themoviedb.org/3/movie/popular?api_key=%s", mApiKey);
    }

    public  String getTopRatedMoviesUrl() {
        return String.format("https://api.themoviedb.org/3/movie/top_rated?api_key=%s", mApiKey);
    }

    public  String getMovieDetailsUrl(String movieId) {
        return String.format("https://api.themoviedb.org/3/movie/%s?api_key=%s", movieId, mApiKey);
    }

    public String getMovieReviewsUrl(String movieId) {
        return String.format("https://api.themoviedb.org/3/movie/%s/reviews?api_key=%s", movieId, mApiKey);

    }
        public  String getPosterUrl(String movieId) {
         return String.format("https://image.tmdb.org/t/p/w185/%s?api_key=%s", movieId, mApiKey);
    }


    public String getStringFromUrl(String urlString) throws IOException {
        String response;
        URL url = new URL(urlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        response = streamToString(httpURLConnection.getInputStream());
        httpURLConnection.disconnect();
        return response;
    }

    private String streamToString(InputStream inputStream) throws IOException {
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
