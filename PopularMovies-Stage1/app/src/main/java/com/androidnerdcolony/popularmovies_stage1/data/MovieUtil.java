package com.androidnerdcolony.popularmovies_stage1.data;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.androidnerdcolony.popularmovies_stage1.MainActivity;

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

import static com.androidnerdcolony.popularmovies_stage1.R.array.sort_by;

/**
 * Created by tiger on 12/16/2016.
 */

public class MovieUtil {

    private static final String BASE_URL = "api.themoviedb.org";
    private static final String VERSION = "3";
    private static final String DISCOVER = "discover";
    private static final String GENRE = "genre";
    private static final String MOVIE = "movie";
    private static final String API_KEY = "api_key";
    private static final String API_KEY_VALUE = "c479f9ce20ccbcc06dbcce991a238120";
    private static final String SORT_BY = "sort_by";
    private static final String ADULT = "include_adult";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String LIST = "list";
    private static final String LANGUAGE = "language";
    private static final String LANGUAGE_EN = "en-US";
    private static final String VIDEO = "include_video";

    private static List<Movie> sMovies;
    private static MovieUtil sMovieUtil;

    public MovieUtil(Context context) {
    }

    private static List<Movie> extractMovie(String movieJson) {
        sMovies = new ArrayList<>();

        if (TextUtils.isEmpty(movieJson)) {
            return null;
        }

        try {
            JSONObject root = new JSONObject(movieJson);
            JSONArray results = root.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject movies = results.getJSONObject(i);
                int id = movies.getInt("id");
                String title = movies.getString("title");
                String originalTitle = movies.getString("original_title");
                JSONArray genreIds = movies.getJSONArray("genre_ids");
                String releaseDate = movies.getString("release_date");
                String overView = movies.getString("overview");
                String coverImage = "";
                if (movies.has("poster_path")) {
                    coverImage = movies.getString("poster_path");
                }
                float popularity = (float) movies.getDouble("popularity");
                float vote = (float) movies.getDouble("vote_average");
                int voteCount = movies.getInt("vote_count");
                boolean adult = movies.getBoolean("adult");

                Movie thisMovie = new Movie(id, title, originalTitle, releaseDate, overView, coverImage, popularity, vote, voteCount, genreIds, adult);

                sMovies.add(thisMovie);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sMovies;
    }

    public static List<Movie> fetchMovieData(String sort_by, boolean adultCheck, boolean videoCheck, String genre) {
        URL url = null;
        try {
            url = createUrl(sort_by, adultCheck, videoCheck, genre);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String jsonResponse = "";

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "makeHttpRequest Error: ", e);

        }

        extractMovie(jsonResponse);

        return sMovies;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return null;
        }
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response Message: " + connection.getResponseMessage());
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader buffer = new BufferedReader(reader);
            String line = buffer.readLine();

            while (line != null) {
                builder.append(line);
                line = buffer.readLine();
            }
        }
        return builder.toString();
    }

    private static URL createUrl(String sort_by, boolean adultCheck, boolean videoCheck, String genre) throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BASE_URL)
                .appendPath(VERSION)
                .appendPath(DISCOVER)
                .appendPath(MOVIE)
        .appendQueryParameter(API_KEY, API_KEY_VALUE);

         builder.appendQueryParameter(ADULT, String.valueOf(adultCheck));
         builder.appendQueryParameter(VIDEO, String.valueOf(videoCheck));
        if (!TextUtils.isEmpty(genre)){
            builder.appendQueryParameter(GENRE, genre);
        }
                builder.appendQueryParameter(SORT_BY, sort_by).build();

        String url = builder.toString();

        return new URL(url);
    }

    public static MovieUtil get(Context context) {
        if (sMovieUtil == null) {
            sMovieUtil = new MovieUtil(context);
        }
        return sMovieUtil;
    }

    public List<Movie> getMovies(String sort_by, boolean adultCheck, boolean videoCheck, String genre) {
        return fetchMovieData(sort_by, adultCheck, videoCheck, genre);
    }

    public Movie getMovie(String movieId) {
        for (Movie movie : sMovies) {
            if (String.valueOf(movie.getId()).equals(movieId)) {
                return movie;
            }
        }
        return null;
    }


    public List<String> getAllGenres() throws IOException{

        URL createUrl = null;
        createUrl = createGenreUrl();

        String jsonResponse = makeHttpRequest(createUrl);

        return getAllGenreList(jsonResponse);


    }

    private URL createGenreUrl() throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder();
        String url;
        builder.scheme("https")
                .authority(BASE_URL)
                .appendPath(VERSION)
                .appendPath(GENRE)
                .appendPath(MOVIE)
                .appendPath(LIST)
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .appendQueryParameter(LANGUAGE, LANGUAGE_EN)
                .build();
        url = builder.toString();
        return new URL(url);
    }

    public List<String> getGenres(JSONArray genres) throws IOException {

        URL createUrl = createGenreUrl();

        String genreJsonResponse = makeHttpRequest(createUrl);

        return getGenreList(genreJsonResponse, genres);
    }

    private List<String> getAllGenreList(String genreJsonResponse){
        List<String> genreLIst = new ArrayList<>();
        if (genreJsonResponse.equals("")){
            return null;
        }
        try {
            JSONObject root = new JSONObject(genreJsonResponse);
            JSONArray genresArray = root.getJSONArray("genres");
            for (int i = 0; i < genresArray.length(); i++) {
                JSONObject genreObject = genresArray.getJSONObject(i);
                String genreName = genreObject.getString("name");
                genreLIst.add(genreName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return genreLIst;
    }

    private List<String> getGenreList(String genreJsonResponse, JSONArray genres) {

        List<String> genreList = new ArrayList<>();
        if (genreJsonResponse.equals("")) {
            return null;
        }
        try {
            JSONObject root = new JSONObject(genreJsonResponse);
            JSONArray genresArray = root.getJSONArray("genres");
            for (int i = 0; i < genresArray.length(); i++) {
                JSONObject genreObject = genresArray.getJSONObject(i);

                for (int j = 0; j < genres.length(); j++) {
                    if (genres.getInt(j) == genreObject.getInt("id")) {
                        String thisGenre = genreObject.getString("name");
                        genreList.add(thisGenre);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return genreList;
    }

}
