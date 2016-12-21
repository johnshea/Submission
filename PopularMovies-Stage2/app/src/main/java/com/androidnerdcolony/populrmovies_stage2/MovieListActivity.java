package com.androidnerdcolony.populrmovies_stage2;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.androidnerdcolony.populrmovies_stage2.data.TMDMovieDiscover;
import com.androidnerdcolony.populrmovies_stage2.data.TMDUtil;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<TMDMovieDiscover>> {
    public static final String LOG_TAG = MovieListActivity.class.getSimpleName();
    private static final int POPULAR_MOVIE_LIST_LOADER = 2;
    ContentValues mValues;

    @BindView(R.id.movie_list_recycler_view) RecyclerView mMovieRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);
        Intent intent = getIntent();

        String sort_by = intent.getStringExtra("sort_by");
        mValues = new ContentValues();

        if (TextUtils.equals(sort_by, getString(R.string.top_rated_movies))) {
            mValues.put(TMDUtil.SORT_BY, getString(R.string.top_rated_movies_key));
        } else if (TextUtils.equals(sort_by, getString(R.string.lowest_rated_movies))) {
            mValues.put(TMDUtil.SORT_BY, getString(R.string.lowest_rated_movies_key));
        } else if (TextUtils.equals(sort_by, getString(R.string.most_popular_movies))) {
            mValues.put(TMDUtil.SORT_BY, getString(R.string.most_popular_movies_key));
        } else if (TextUtils.equals(sort_by, getString(R.string.lowest_popular_movies))) {
            mValues.put(TMDUtil.SORT_BY, getString(R.string.lowest_popular_movies_key));
        } else if (TextUtils.equals(sort_by, getString(R.string.newest_movies))) {
            mValues.put(TMDUtil.SORT_BY, getString(R.string.newest_movies_key));
        } else if (TextUtils.equals(sort_by, getString(R.string.oldest_movies))) {
            mValues.put(TMDUtil.SORT_BY, getString(R.string.oldest_movies_key));
        }
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<TMDMovieDiscover> movieDiscoverLoader = loaderManager.getLoader(POPULAR_MOVIE_LIST_LOADER);

        if (movieDiscoverLoader == null) {
            Log.i(LOG_TAG, "initLoader");
            loaderManager.initLoader(POPULAR_MOVIE_LIST_LOADER, savedInstanceState, MovieListActivity.this);
        } else {
            loaderManager.restartLoader(POPULAR_MOVIE_LIST_LOADER, savedInstanceState, MovieListActivity.this);
        }


    }

    @Override
    public Loader<List<TMDMovieDiscover>> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<List<TMDMovieDiscover>>(this) {
            List<TMDMovieDiscover> mTMDMovieDiscovers;

            @Override
            public void deliverResult(List<TMDMovieDiscover> data) {
                Log.i(LOG_TAG, "DeliverResult");
                mTMDMovieDiscovers = data;
                super.deliverResult(data);
            }

            @Override
            protected void onStartLoading() {
                Log.i(LOG_TAG, "onStartLoading");
//                if (bundle == null) {
//                    return;
//                }
                super.onStartLoading();
                if (mTMDMovieDiscovers != null) {
                    deliverResult(mTMDMovieDiscovers);
                } else {
                    Log.i(LOG_TAG, "forceLoad");
                    forceLoad();
                }
            }

            @Override
            public List<TMDMovieDiscover> loadInBackground() {
                Log.i(LOG_TAG, "loadingInBackground");
                mValues.put(TMDUtil.PATH_DISCOVER, TMDUtil.PATH_DISCOVER);
                mValues.put(TMDUtil.PATH_MOVIE, TMDUtil.PATH_MOVIE);
                mValues.put(TMDUtil.INCLUDE_ADULT, true);

                if (mTMDMovieDiscovers == null) {
                    try {
                        mTMDMovieDiscovers = new TMDUtil(getContext()).getMovies(mValues);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                return mTMDMovieDiscovers;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<TMDMovieDiscover>> loader, List<TMDMovieDiscover> movieDiscovers) {
        Log.i(LOG_TAG, "onLoadFinished");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mMovieRecyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new TMDMovieListAdapter(this, movieDiscovers);
        mMovieRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMovieRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onLoaderReset(Loader<List<TMDMovieDiscover>> loader) {
        loader.reset();

    }
}
