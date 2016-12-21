package com.androidnerdcolony.popularmovies_stage1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.androidnerdcolony.popularmovies_stage1.data.Movie;
import com.androidnerdcolony.popularmovies_stage1.data.MovieUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_SEARCH_LOADER = 1;
    private static String SORT_BY_STATE;
    private static String GENRE_STATE;
    private static String ADULT_STATE;
    private static String VIDOE_STATE;
    Spinner mSortBySpinner;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private ProgressBar mProgressBar;
    private int mSortBy;
    private boolean mAdultCheck;
    private String mGener;
    private boolean mVideoCheck;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();

        switch (menuId) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSortBy = mSortBySpinner.getSelectedItemPosition();
        outState.putInt(SORT_BY_STATE, mSortBy);
        Log.i(LOG_TAG, "onSaveInstanceState outState: mSortBy = " + mSortBy);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SORT_BY_STATE = getString(R.string.pref_sort_by_key);
        GENRE_STATE = getString(R.string.pref_genre_key);
        ADULT_STATE = getString(R.string.pref_adult_key);
        VIDOE_STATE = getString(R.string.pref_video_key);


        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mContext = this;
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.bible_text_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        String[] sortByList = getResources().getStringArray(R.array.sort_by);
        mSortBy = 1;
        if (savedInstanceState != null) {
            mSortBy = savedInstanceState.getInt(SORT_BY_STATE);
            Log.i(LOG_TAG, "onSaveInstanceState if is not null: mSortBy = " + mSortBy);
        } else if (preferences != null) {
            mSortBy = preferences.getInt(SORT_BY_STATE, 1);
            savedInstanceState = new Bundle();
            savedInstanceState.putInt(SORT_BY_STATE, mSortBy);
        }
        ArrayAdapter<String> sortByAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortByList);
        mSortBySpinner = (Spinner) findViewById(R.id.movie_list_sort_by);
        mSortBySpinner.setAdapter(sortByAdapter);
        mSortBySpinner.setSelection(mSortBy);
        Log.i(LOG_TAG, "spinner set Selection : mSortBy = " + mSortBy);


        final LoaderManager loaderManager = getSupportLoaderManager();
        final Loader<Movie> movieLoader = loaderManager.getLoader(MOVIE_SEARCH_LOADER);

        mSortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt(SORT_BY_STATE, position);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(SORT_BY_STATE, position);
                editor.apply();
                setupPreference();
                loaderManager.restartLoader(MOVIE_SEARCH_LOADER, bundle, MainActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if (movieLoader == null) {
            loaderManager.initLoader(MOVIE_SEARCH_LOADER, null, MainActivity.this);
        } else {
            loaderManager.restartLoader(MOVIE_SEARCH_LOADER, savedInstanceState, MainActivity.this);
        }


    }

    private void setupPreference() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSortBy = preferences.getInt(getString(R.string.pref_sort_by_key), R.integer.pref_sort_by_default);
        mAdultCheck = preferences.getBoolean(getString(R.string.pref_adult_key), getResources().getBoolean(R.bool.pref_default_adult_content));
        mVideoCheck = preferences.getBoolean(getString(R.string.pref_video_key), getResources().getBoolean(R.bool.pref_default_video_included));
        mGener = preferences.getString(getString(R.string.pref_genre_key), getResources().getString(R.string.genre));

        preferences.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<List<Movie>>(this) {
            List<Movie> mMovieList;

            @Override
            protected void onStartLoading() {
                if (bundle == null) {
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);
                super.onStartLoading();
                if (mMovieList != null) {
                    deliverResult(mMovieList);
                } else {
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(List<Movie> movieList) {
                mMovieList = movieList;
                super.deliverResult(movieList);

            }

            @Override
            public List<Movie> loadInBackground() {
                int sortBy = 1;
                if (bundle != null) {
                    sortBy = bundle.getInt(SORT_BY_STATE);
                }
                Log.i(LOG_TAG, "loadingBackground: sortBy = " + sortBy);
                if (sortBy == -1) {
                    return null;
                }
                String[] sortByList = getResources().getStringArray(R.array.sort_by);
                String sortByString = sortByList[sortBy];
                if (mMovieList == null) {
                    mMovieList = new MovieUtil(getContext()).getMovies(sortByString, mAdultCheck, mVideoCheck, mGener);
                }
                return mMovieList;
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        mProgressBar.setVisibility(View.INVISIBLE);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new MovieRecyclerAdapter(mContext, movies);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        loader.reset();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Bundle bundle = new Bundle();
        if (key.equals(getString(R.string.pref_sort_by_key))) {
            bundle.putInt(getString(R.string.pref_sort_by_key), mSortBy);
        }else if (key.equals(getString(R.string.pref_adult_key))){
            bundle.putBoolean(getString(R.string.pref_adult_key), mAdultCheck);
        }else if (key.equals(getString(R.string.pref_genre_key))){
            bundle.putString(getString(R.string.pref_genre_key), mGener);
        }
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(MOVIE_SEARCH_LOADER, bundle, MainActivity.this);
    }
}
