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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dennis.popularmovies.PopularMoviesApplication;
import com.example.dennis.popularmovies.pojos.SingleReview;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.example.dennis.popularmovies.R;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<SingleReview> resultList;
    private String posterPath;

    public ReviewsAdapter(List<SingleReview> resultList
            ,String posterPath) {
        this.resultList = resultList;
        this.posterPath=posterPath;
       // setHasStableIds(true);
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.review_item
                , parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.onBind(resultList.get(position));

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public  class ReviewViewHolder extends RecyclerView.ViewHolder{
        private TextView reviewAuthor,reviewContent;
        private ImageView photo;

        public ReviewViewHolder(View view) {
            super(view);
            reviewAuthor=view.findViewById(R.id.review_author);
            reviewContent = view.findViewById(R.id.review_content);
            photo = view.findViewById(R.id.photo);
        }


        private void onBind(SingleReview result){
            reviewAuthor.setText(String.format("Author: %s", result.getAuthor()));
            reviewContent.setText(String.format("Review: %s", result.getContent()));
            PopularMoviesApplication.getApp()
                    .getPicassoWithCache()
                    .load(itemView.getContext().getString(R.string.base_image_url)+posterPath)
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
                                    .load(itemView.getContext().getString(R.string.base_image_url)+posterPath)
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
