package com.androidnerdcolony.populrmovies_stage2.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tiger on 12/20/2016.
 */

public class TMDUtil {

    //set base
    private static final String BASE_URL = "api.themoviedb.org";
    private static final String VERSION = "3";
    private static final String API_KEY = "c479f9ce20ccbcc06dbcce991a238120";
    private static final String LOG_TAG = TMDUtil.class.getSimpleName();
    private static TMDUtil sTMDUtil;
    private static List<TMDMovieDiscover> sTMDMovieDiscover;

    //API Definition
    public static final String PATH_DISCOVER = "discover";
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_GENRE = "genre";
    public static final String PATH_SEARCH = "search";
    public static final String PATH_UPCOMING = "upcoming";
    public static final String PATH_VIDEOS = "videos";
    public static final String PATH_SIMILAR = "similar";
    public static final String MOVIEID = "movie_id";
    public static final String LANGUAGE = "language";
    public static final String SORT_BY = "sort_by";
    public static final String INCLUDE_ADULT = "include_adult";
    public static final String RELEASE_DATE = "release_date";
    public static final String VOTE_COUNT = "vote_count";
    public static final String WITH_GENRES = "with_genres";


    public TMDUtil(Context context) {
    }

    public static List<TMDMovieDiscover> extreactMovie(String movieJson) {
        sTMDMovieDiscover = new ArrayList<>();

        return sTMDMovieDiscover;
    }

    public static List<TMDMovieDiscover> getTMDMovieDiscover() {
        return sTMDMovieDiscover;
    }

    public static TMDUtil get(Context context) {
        if (sTMDUtil == null) {
            sTMDUtil = new TMDUtil(context);
        }
        return sTMDUtil;
    }

    private static URL createURL(ContentValues values) throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BASE_URL)
                .appendPath(VERSION);
        if (values.containsKey(PATH_DISCOVER)) {
            builder.appendPath(values.get(PATH_DISCOVER).toString());
        }else if (values.containsKey(PATH_GENRE)){
            builder.appendPath(values.get(PATH_GENRE).toString());
        }else if (values.containsKey(PATH_SEARCH)){
            builder.appendPath(values.get(PATH_SEARCH).toString());
        }
        if (values.containsKey(PATH_MOVIE)){
            builder.appendPath(values.get(PATH_MOVIE).toString());
        }
        if (values.containsKey(MOVIEID)){
            builder.appendPath(values.get(MOVIEID).toString());
        }
        if (values.containsKey(PATH_VIDEOS)){
            builder.appendPath(values.get(PATH_VIDEOS).toString());
        }else if (values.containsKey(PATH_SIMILAR)){
            builder.appendPath(values.get(PATH_SIMILAR).toString());
        }else if (values.containsKey(PATH_UPCOMING))
        {
            builder.appendPath(values.getAsString(PATH_UPCOMING));
        }
        if (values.containsKey(INCLUDE_ADULT)){
            builder.appendQueryParameter(INCLUDE_ADULT, values.get(INCLUDE_ADULT).toString());
        }if (values.containsKey(SORT_BY)){
            builder.appendQueryParameter(SORT_BY, values.getAsString(SORT_BY));
        }
        builder.appendQueryParameter("api_key", API_KEY);
        return new URL(builder.toString());
    }

    public List<TMDMovieDiscover> getMovies(ContentValues values) throws MalformedURLException {

        URL url = createURL(values);
        return fetchMovieDate(url);
    }

    private static List<TMDMovieDiscover> fetchMovieDate(URL url) {

        String jsonResponse = makeHttpRequest(url);

        sTMDMovieDiscover = extractMovie(jsonResponse);

        return sTMDMovieDiscover;

    }

    private static List<TMDMovieDiscover> extractMovie(String jsonResponse) {
        sTMDMovieDiscover = new ArrayList<>();

        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }

        try{
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray results = root.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject movies = results.getJSONObject(i);
               String posterPath = movies.getString("poster_path");
               boolean adult = movies.getBoolean("adult");
               String overview = movies.getString("overview");
               String releaseDate = movies.getString("release_date");
               JSONArray genre_ids = movies.getJSONArray("genre_ids");
               int movieId = movies.getInt("id");
               String originalTitle = movies.getString("original_title");
               String originalLanguage = movies.getString("original_language");
               String title = movies.getString("title");
               String backdropPath = movies.getString("backdrop_path");
               float popularity = (float) movies.getDouble("popularity");
               int voteCount = movies.getInt("vote_count");
               boolean video = movies.getBoolean("video");
               float voteAverage = (float) movies.getDouble("vote_average");

                TMDMovieDiscover movieDiscover = new TMDMovieDiscover(posterPath, adult,
                        overview,releaseDate,genre_ids,movieId,originalTitle,originalLanguage,
                        title,backdropPath,popularity,voteCount,video,voteAverage);
                sTMDMovieDiscover.add(movieDiscover);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sTMDMovieDiscover;
    }

    private static String makeHttpRequest(URL url) {
        String response = "";
        if (url == null){
            return null;
        }
        HttpURLConnection connection;
        InputStream inputStream;
        Log.i(LOG_TAG, "url: " + url.toString());
        try
        {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                inputStream = connection.getInputStream();
                response = readFromStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();

        if (inputStream != null){
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader buffer = new BufferedReader(reader);
            String line = buffer.readLine();

            while (line != null){
                builder.append(line);
                line = buffer.readLine();
            }
        }
        return builder.toString();
    }

}
