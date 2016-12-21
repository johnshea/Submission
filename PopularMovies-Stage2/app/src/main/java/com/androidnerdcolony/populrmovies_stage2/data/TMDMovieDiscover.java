package com.androidnerdcolony.populrmovies_stage2.data;

import org.json.JSONArray;

/**
 * Created by tiger on 12/20/2016.
 */

public class TMDMovieDiscover {
    private String mPosterPath;
    private boolean mAdult;
    private String mOverview;
    private String mReleaseDate;
    private JSONArray mGenre_ids;
    private int mId;
    private String mOriginalTitle;
    private String mOriginalLanguage;
    private String mTitle;
    private String mBackdropPath;
    private float mPopularity;
    private int mVoteCount;
    private boolean mVideo;
    private float mVoteAverage;

    public TMDMovieDiscover(String posterPath, boolean adult, String overview, String releaseDate, JSONArray genre_ids, int id, String originalTitle, String originalLanguage, String title, String backdropPath, float popularity, int voteCount, boolean video, float voteAverage) {
        mPosterPath = posterPath;
        mAdult = adult;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mGenre_ids = genre_ids;
        mId = id;
        mOriginalTitle = originalTitle;
        mOriginalLanguage = originalLanguage;
        mTitle = title;
        mBackdropPath = backdropPath;
        mPopularity = popularity;
        mVoteCount = voteCount;
        mVideo = video;
        mVoteAverage = voteAverage;
    }

    public int getId() {
        return mId;
    }

    public float getPopularity() {
        return mPopularity;
    }

    public float getVoteAverage() {
        return mVoteAverage;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public JSONArray getGenre_ids() {
        return mGenre_ids;
    }

    public boolean isAdult() {
        return mAdult;
    }

    public boolean isVideo() {
        return mVideo;
    }
}
