package com.androidnerdcolony.popularmovies_stage1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidnerdcolony.popularmovies_stage1.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by tiger on 12/16/2016.
 */

public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.ViewHolder> {

    private static final String LOG_TAG = MovieRecyclerAdapter.class.getSimpleName();
    private List<Movie> mMovieList;
    private Movie mMovie;
    private Context mContext;

    public MovieRecyclerAdapter(Context context, List<Movie> movies) {
        mMovieList = movies;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_cover_image_item, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        mMovie = mMovieList.get(position);
        String baseImageURL = "https://image.tmdb.org/t/p/w500";
        String poster = baseImageURL + mMovie.getCoverImage();

        Picasso.with(mContext)
                .load(poster)
                .placeholder(R.drawable.noposteravailable2)
                .error(R.drawable.noposteravailable2)
                .into(holder.coverImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                String movieId = String.valueOf(mMovieList.get(position).getId());
                intent.putExtra("movieId", movieId);
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mMovieList != null) {
            return mMovieList.size();
        }
        return 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView coverImageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            coverImageView = (ImageView) itemView.findViewById(R.id.movie_list_item_post_image);
        }

    }
}
