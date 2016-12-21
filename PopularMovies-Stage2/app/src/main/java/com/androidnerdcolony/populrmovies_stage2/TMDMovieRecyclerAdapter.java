package com.androidnerdcolony.populrmovies_stage2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

public class TMDMovieRecyclerAdapter extends RecyclerView.Adapter<TMDMovieRecyclerAdapter.ViewHolder> {

    private List<TMDMovieDiscover> mTMDMovieDiscovers;
    private Context mContext;

    public TMDMovieRecyclerAdapter(Context context, List<TMDMovieDiscover> movieDiscovers){
        mContext = context;
        mTMDMovieDiscovers = movieDiscovers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_movie_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TMDMovieDiscover movieDiscover = mTMDMovieDiscovers.get(position);
        String baseImageURL = "https://image.tmdb.org/t/p/w500";
        String poster = baseImageURL + movieDiscover.getPosterPath();
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
                int movieId = mTMDMovieDiscovers.get(position).getId();
                intent.putExtra("movieId", movieId);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (mTMDMovieDiscovers != null){
            return mTMDMovieDiscovers.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView posterView;
        TextView titleView;
        public ViewHolder(View itemView) {
            super(itemView);
            posterView = (ImageView) itemView.findViewById(R.id.poster_image);
            titleView = (TextView) itemView.findViewById(R.id.list_title);
        }
    }
}
