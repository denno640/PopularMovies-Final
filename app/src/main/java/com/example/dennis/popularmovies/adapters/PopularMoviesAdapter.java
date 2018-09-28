package com.example.dennis.popularmovies.adapters;

/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dennis.popularmovies.PopularMoviesApplication;
import com.example.dennis.popularmovies.R;
import com.example.dennis.popularmovies.api.NetworkState;
import com.example.dennis.popularmovies.pojos.SingleMovie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class PopularMoviesAdapter extends PagedListAdapter<SingleMovie, RecyclerView.ViewHolder> {

    private NetworkState networkState;
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w185";
    private final OnMoviePosterClicked mHandler;

    public PopularMoviesAdapter(OnMoviePosterClicked mHandler) {
        super(SingleMovie.DIFF_CALLBACK);
        //setHasStableIds(true);
        this.mHandler = mHandler;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == R.layout.movie_poster) {
            view = layoutInflater.inflate(R.layout.movie_poster, parent, false);
            return new SingleMovieViewHolder(view);
        } else if (viewType == R.layout.network_state_item) {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false);
            return new NetworkStateItemViewHolder(view);
        } else {
            throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position) + 1509902364000L;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.movie_poster:
                ((SingleMovieViewHolder) holder).bindData(getItem(position));
                break;
            case R.layout.network_state_item:
                ((NetworkStateItemViewHolder) holder).bindView(networkState);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.network_state_item;
        } else {
            return R.layout.movie_poster;
        }
    }

    private boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public interface OnMoviePosterClicked {
        void onMoviePosterClicked(SingleMovie singleMovie);
    }

    public class SingleMovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView movie_poster_iv;
        private TextView movie_title_tv;

        private SingleMovieViewHolder(View itemView) {
            super(itemView);
            movie_poster_iv = itemView.findViewById(R.id.movie_poster_iv);
            movie_title_tv = itemView.findViewById(R.id.movie_title_tv);
            itemView.setOnClickListener(v -> {
               // Log.d("ADAPTER", "clicked:");

                SingleMovie singleMovie = getItem(getAdapterPosition());
                //fire interface method whenever poster is clicked
                mHandler.onMoviePosterClicked(singleMovie);
            });

        }

        private void bindData(final SingleMovie singleMovie) {

           /* Picasso
                    .with(itemView.getContext())
                    .load(BASE_URL_IMG + singleMovie.getPosterPath())
                    .into(movie_poster_iv);*/
            PopularMoviesApplication.getApp()
                    .getPicassoWithCache()
                    .load(BASE_URL_IMG + singleMovie.getPosterPath())
                    .priority(Picasso.Priority.HIGH)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(movie_poster_iv, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            PopularMoviesApplication.getApp()
                                    .getPicassoWithCache()
                                    .load(BASE_URL_IMG + singleMovie.getPosterPath())
                                    .priority(Picasso.Priority.HIGH)
                                    // .networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(movie_poster_iv, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });
                        }
                    });
            movie_title_tv.setText(singleMovie.getTitle());
        }


    }

    static class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {

        private final ProgressBar progressBar;
        private final TextView errorMsg;

        public NetworkStateItemViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
            errorMsg = itemView.findViewById(R.id.error_msg);

        }


        public void bindView(NetworkState networkState) {
            if (networkState != null && networkState.getStatus().equals("RUNNING")) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus().equals("FAILED")) {
                errorMsg.setVisibility(View.VISIBLE);
                errorMsg.setText(networkState.getMsg());
            } else {
                errorMsg.setVisibility(View.GONE);
            }
        }
    }
}
