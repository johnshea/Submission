package com.androidnerdcolony.popularmovies_stage1;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidnerdcolony.popularmovies_stage1.data.Movie;
import com.androidnerdcolony.popularmovies_stage1.data.MovieUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    @BindView(R.id.movie_detail_title)
    TextView mTitleView;
    @BindView(R.id.movie_detail_original_title)
    TextView mOriginalTitleView;
    @BindView(R.id.movie_detail_release_date)
    TextView mReleaseDateView;
    @BindView(R.id.movie_detail_over_view)
    TextView mOverViewView;
    @BindView(R.id.movie_detail_poster)
    ImageView mPosterView;
    @BindView(R.id.movie_detail_genre)
    TextView mGenreView;
    @BindView(R.id.movie_detail_popularity)
    ProgressBar mPopularityView;
    @BindView(R.id.movie_detail_vote)
    RatingBar mVoteView;
    @BindView(R.id.movie_detail_vote_count)
    TextView mVoteCountView;
    Movie mMovie;

    Context mContext;

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String movieId = "";
        if (intent.hasExtra("movieId")) {
            movieId = (String) intent.getSerializableExtra("movieId");
        }
        if (movieId != null) {
            mMovie = MovieUtil.get(this).getMovie(movieId);
        }
        String title = mMovie.getTitle();

        setTitle(title);

        String originalTitle = mMovie.getOriginalTitle();
        String posterLink = mMovie.getCoverImage();
         float popularity = mMovie.getPopularity();
        float vote = mMovie.getVote();
        String voteCount = String.valueOf(mMovie.getVoteCount());
        String overView = mMovie.getOverView();
        String releaseDate = String.valueOf(mMovie.getReleaseData());
        boolean adult = mMovie.isAdult();
        ColorDrawable drawable;
            if (adult) {
                drawable  = new ColorDrawable(Color.parseColor("#ff2a4a"));
                getSupportActionBar().setBackgroundDrawable(drawable);

            } else {
                drawable = new ColorDrawable(Color.parseColor("#2a8570"));
                getSupportActionBar().setBackgroundDrawable(drawable);
            }

        JSONArray genres = mMovie.getGenres();

        new getGenreListTask().execute(genres);

        mTitleView.setText(title);
        String baseImageURL = "https://image.tmdb.org/t/p/w500";
        if (!TextUtils.equals(mMovie.getCoverImage(), "null")) {
            String poster = baseImageURL + posterLink;
            Picasso.with(this).load(poster).into(mPosterView);
        } else {
            Picasso.with(this).load(R.drawable.noposteravailable2).into(mPosterView);
        }
        mOriginalTitleView.setText(originalTitle);

        mOverViewView.setText(overView);
        mPopularityView.setProgress((int) popularity);
        mPopularityView.setMax(50);
        mVoteView.setRating(vote);
        mVoteView.setMax(10);
        mVoteCountView.setText(voteCount);
        mReleaseDateView.setText(releaseDate);}

    private class getGenreListTask extends AsyncTask<JSONArray, Void, List<String>> {

        @Override
        protected void onPostExecute(List<String> strings) {

            if (!strings.isEmpty()) {
                StringBuilder builder = new StringBuilder();

                for (String genreName : strings) {
                    builder.append(genreName);
                    builder.append(", ");

                }
                builder.deleteCharAt(builder.lastIndexOf(","));

                String genreString = builder.toString().trim();

                mGenreView.setText(genreString);
            }
            super.onPostExecute(strings);
        }

        @Override
        protected List<String> doInBackground(JSONArray... jsonArrays) {
            List<String> geneList = new ArrayList<>();
            try {
                geneList = MovieUtil.get(mContext).getGenres(jsonArrays[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return geneList;
        }

    }
}
