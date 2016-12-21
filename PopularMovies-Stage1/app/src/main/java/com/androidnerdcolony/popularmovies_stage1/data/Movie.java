package com.androidnerdcolony.popularmovies_stage1.data;

import org.json.JSONArray;

/**
 * Created by tiger on 12/16/2016.
 */

public class Movie {
    private int mId;
    private String mTitle;
    private String mOriginalTitle;
    private String mReleaseData;
    private String mOverView;
    private String mCoverImage;
    private float mPopularity;
    private float mVote;
    private int mVoteCount;
    private JSONArray mGenres;
    private boolean mAdult;

    public Movie(int id, String title, String originalTitle, String releaseData, String overView, String coverImage, float popularity, float vote, int voteCount, JSONArray genres, boolean adult) {
        mId = id;
        mTitle = title;
        mOriginalTitle = originalTitle;
        mReleaseData = releaseData;
        mOverView = overView;
        mCoverImage = coverImage;
        mPopularity = popularity;
        mVote = vote;
        mVoteCount = voteCount;
        mGenres = genres;
        mAdult = adult;
    }

    public float getPopularity() {
        return mPopularity;
    }

    public float getVote() {
        return mVote;
    }

    public int getId() {
        return mId;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    public JSONArray getGenres() {
        return mGenres;
    }

    public String getCoverImage() {
        return mCoverImage;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getOverView() {
        return mOverView;
    }

    public String getReleaseData() {
        return mReleaseData;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean isAdult() {
        return mAdult;
    }
}
