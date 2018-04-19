package com.example.macarus0.popularmovies.util;

import com.example.macarus0.popularmovies.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    public static String getPopularMoviesUrl(String key) {
        String urlString = "https://api.themoviedb.org/3/movie/popular?api_key=" + key;
        return urlString;
    }


    public static String getStringFromUrl(String urlString) throws MalformedURLException, IOException{
        URL url = new URL(urlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        String resultString = streamToString(httpURLConnection.getInputStream());
        return resultString;
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

}
