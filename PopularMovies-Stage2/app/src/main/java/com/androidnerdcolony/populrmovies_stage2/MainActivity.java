package com.androidnerdcolony.populrmovies_stage2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnerdcolony.populrmovies_stage2.data.TMDMovieDiscover;
import com.androidnerdcolony.populrmovies_stage2.data.TMDUtil;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<TMDMovieDiscover>> {

    private static final int POPULAR_MOVIE_LOADER = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.most_popular_movies_recycler_view)
    RecyclerView mMostPopularMoviesView;
    @BindView(R.id.search_type_list)
    ListView mSearchTypeListView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ButterKnife.bind(this);
        mMostPopularMoviesView.setHasFixedSize(true);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<TMDMovieDiscover> movieDiscoverLoader = loaderManager.getLoader(POPULAR_MOVIE_LOADER);

        if (movieDiscoverLoader == null) {
            Log.i(LOG_TAG, "initLoader");
            loaderManager.initLoader(POPULAR_MOVIE_LOADER, savedInstanceState, MainActivity.this);
        } else {
            loaderManager.restartLoader(POPULAR_MOVIE_LOADER, savedInstanceState, MainActivity.this);
        }
        String[] searchArray;
        searchArray = getResources().getStringArray(R.array.search_type);
        List<String> searchList = new ArrayList<>();
        for (String item : searchArray){
            searchList.add(item);
        }
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, searchList);
        mSearchTypeListView.setAdapter(searchAdapter);

        mSearchTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(MainActivity.this, MovieListActivity.class);
                String searchType = adapterView.getItemAtPosition(position).toString();
                intent.putExtra("sort_by", searchType);
                startActivity(intent);
                Toast.makeText(MainActivity.this, searchType + "Clicked", Toast.LENGTH_SHORT).show();
            }
        });


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
                }else{
                    Log.i(LOG_TAG, "forceLoad");
                    forceLoad();
                }
            }

            @Override
            public List<TMDMovieDiscover> loadInBackground() {
                Log.i(LOG_TAG, "loadingInBackground");
                ContentValues values = new ContentValues();
                values.put(TMDUtil.PATH_DISCOVER, TMDUtil.PATH_DISCOVER);
                values.put(TMDUtil.PATH_MOVIE, TMDUtil.PATH_MOVIE);
                values.put(TMDUtil.INCLUDE_ADULT, true);
                if (mTMDMovieDiscovers == null) {
                    try {
                        mTMDMovieDiscovers = new TMDUtil(getContext()).getMovies(values);
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mMostPopularMoviesView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new TMDMovieRecyclerAdapter(this, movieDiscovers);
        mMostPopularMoviesView.setItemAnimator(new DefaultItemAnimator());
        mMostPopularMoviesView.setAdapter(adapter);

    }

    @Override
    public void onLoaderReset(Loader<List<TMDMovieDiscover>> loader) {
        loader.reset();

    }
}
