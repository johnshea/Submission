package com.androidnerdcolony.populrmovies_stage2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnerdcolony.populrmovies_stage2.data.TMDMovieDiscover;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by tiger on 12/20/2016.
 */

public class TMDMovieListAdapter extends RecyclerView.Adapter<TMDMovieListAdapter.ViewHolder> {
    private static final String LOG_TAG = TMDMovieListAdapter.class.getSimpleName();

    private List<TMDMovieDiscover> mMoviewDiscover;
    private Context mContext;

    public TMDMovieListAdapter(Context context, List<TMDMovieDiscover> movieDiscover) {
        mMoviewDiscover = movieDiscover;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TMDMovieDiscover movieDiscover = mMoviewDiscover.get(position);
        String baseImageURL = "https://image.tmdb.org/t/p/w500";
        String poster = baseImageURL + movieDiscover.getPosterPath();

        Log.i(LOG_TAG, "poster: " + poster);
        String title = position + ". " + movieDiscover.getTitle();

        Picasso.with(mContext)
                .load(poster)
                .placeholder(R.drawable.no_poster_available)
                .error(R.drawable.no_poster_available)
                .into(holder.posterView);
        holder.titleView.setText(title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                int movieId = mMoviewDiscover.get(position).getId();
                intent.putExtra("movieId", movieId);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMoviewDiscover.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView posterView;
        TextView titleView;

        public ViewHolder(View itemView) {
            super(itemView);
            posterView = (ImageView) itemView.findViewById(R.id.movie_list_poster);
            titleView = (TextView) itemView.findViewById(R.id.movie_list_title);
        }
    }
}

