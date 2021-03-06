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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dennis.popularmovies.PopularMoviesApplication;
import com.example.dennis.popularmovies.pojos.SingleReview;
import com.example.dennis.popularmovies.pojos.SingleTrailer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.example.dennis.popularmovies.R;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnTrailerClickedHandler mHandler;
    private List<Object> resultList;
    private String posterPath;

    public TrailersAdapter(OnTrailerClickedHandler mHandler, List<Object> resultList
            , String posterPath) {
        this.mHandler = mHandler;
        this.resultList = resultList;
        this.posterPath = posterPath;
        //setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == R.layout.trailer_item) {
            view = layoutInflater.inflate(R.layout.trailer_item, parent, false);
            return new TrailerViewHolder(view);
        } else if (viewType == R.layout.review_item) {
            view = layoutInflater.inflate(R.layout.review_item, parent, false);
            return new ReviewViewHolder(view);
        } else {
            throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.trailer_item:
                ((TrailerViewHolder) holder).onBind((SingleTrailer) resultList.get(position));
                break;
            case R.layout.review_item:
                ((ReviewViewHolder) holder).onBind((SingleReview) resultList.get(position));
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (resultList.get(position) instanceof SingleTrailer) {
            return R.layout.trailer_item;
        } else {
            return R.layout.review_item;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public interface OnTrailerClickedHandler {
        void onTrailerClicked(SingleTrailer trailer);

        void onTrailerShared(SingleTrailer singleTrailer);
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView trailerName, trailerType;
        private ImageView photo;
        private ImageButton shareButton;
        private TextView shareButtonText;

        public TrailerViewHolder(View view) {
            super(view);
            trailerName = view.findViewById(R.id.trailerName);
            trailerType = view.findViewById(R.id.trailerType);
            photo = view.findViewById(R.id.photo);
            shareButton = view.findViewById(R.id.share_btn);
            shareButtonText = view.findViewById(R.id.trailerShareText);
            view.setOnClickListener(this);
            shareButtonText.setOnClickListener(this);
            shareButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.share_btn:
                    mHandler.onTrailerShared((SingleTrailer) resultList.get(getAdapterPosition()));
                    break;
                case R.id.trailerShareText:
                    mHandler.onTrailerShared((SingleTrailer) resultList.get(getAdapterPosition()));
                    break;
                default:
                    mHandler.onTrailerClicked((SingleTrailer) resultList.get(getAdapterPosition()));

            }
        }

        private void onBind(SingleTrailer result) {
            trailerName.setText(result.getName());
            trailerType.setText(String.format("Video type: %s", result.getType()));
            PopularMoviesApplication.getApp()
                    .getPicassoWithCache()
                    .load(itemView.getContext().getString(R.string.base_image_url) + posterPath)
                    .priority(Picasso.Priority.HIGH)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(photo, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            PopularMoviesApplication.getApp()
                                    .getPicassoWithCache()
                                    .load(itemView.getContext().getString(R.string.base_image_url) + posterPath)
                                    .priority(Picasso.Priority.HIGH)
                                    // .networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(photo, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });
                        }
                    });
        }
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView reviewAuthor, reviewContent;
        private ImageView photo;

        public ReviewViewHolder(View view) {
            super(view);
            reviewAuthor = view.findViewById(R.id.review_author);
            reviewContent = view.findViewById(R.id.review_content);
            photo = view.findViewById(R.id.photo);
        }


        private void onBind(SingleReview result) {
            reviewAuthor.setText(String.format("Author: %s", result.getAuthor()));
            reviewContent.setText(String.format("Review: %s", result.getContent()));
            PopularMoviesApplication.getApp()
                    .getPicassoWithCache()
                    .load(itemView.getContext().getString(R.string.base_image_url) + posterPath)
                    .priority(Picasso.Priority.HIGH)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(photo, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            PopularMoviesApplication.getApp()
                                    .getPicassoWithCache()
                                    .load(itemView.getContext().getString(R.string.base_image_url) + posterPath)
                                    .priority(Picasso.Priority.HIGH)
                                    // .networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(photo, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });
                        }
                    });
        }
    }
}
