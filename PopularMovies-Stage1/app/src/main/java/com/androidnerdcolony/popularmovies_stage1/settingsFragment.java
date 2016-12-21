package com.androidnerdcolony.popularmovies_stage1;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.androidnerdcolony.popularmovies_stage1.data.MovieUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by tiger on 12/18/2016.
 */

public class settingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_movie);
        List<String> genres;
        try {
            genres = MovieUtil.get(getContext()).getAllGenres();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
