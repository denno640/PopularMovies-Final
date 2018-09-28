package com.example.dennis.popularmovies.ui;

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


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.dennis.popularmovies.R;
import com.example.dennis.popularmovies.adapters.ReviewsAdapter;
import com.example.dennis.popularmovies.pojos.SingleReview;
import com.example.dennis.popularmovies.viewmodels.ReviewsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReviewsActivity extends AppCompatActivity {
    private String movieId;
    private String posterPath;
    private ReviewsViewModel viewModel;
    private SwipeRefreshLayout trailersSwipeLayout;
    private ReviewsAdapter mAdapter;
    private List<SingleReview> resultList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        trailersSwipeLayout = findViewById(R.id.trailerSwipeLayout);
        RecyclerView trailerRecyclerView = findViewById(R.id.trailerRvItem);
        trailersSwipeLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark, android.R.color.holo_blue_dark, android.R.color.holo_orange_dark,
                android.R.color.holo_purple);
        trailersSwipeLayout.setRefreshing(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        trailerRecyclerView.setHasFixedSize(true);
        movieId = getIntent().getStringExtra("movieId");
        posterPath = getIntent().getStringExtra("posterPath");
        if (posterPath == null) {
            if (savedInstanceState != null) {
                posterPath = savedInstanceState.getString("posterPath");
            }
        }
        if(movieId ==null) Toast.makeText(this, "movieId is null", Toast.LENGTH_SHORT).show();

        trailerRecyclerView.setLayoutManager(manager);
        resultList = new ArrayList<>();
        mAdapter = new ReviewsAdapter(resultList, posterPath);
        trailerRecyclerView.setAdapter(mAdapter);

        viewModel = ViewModelProviders.of(this).get(ReviewsViewModel.class);
        viewModel.loadReviews(movieId);
        viewModel.getReviewsList().observe(this, this::onReviewsReceived);
        trailersSwipeLayout.setOnRefreshListener(() -> {
            resultList.clear();
            viewModel.refresh(movieId);
            Toast.makeText(this, R.string.reloading, Toast.LENGTH_SHORT).show();
            trailersSwipeLayout.setRefreshing(false);
        });
    }

    private void onReviewsReceived(List<SingleReview> results) {
        if (results != null) {
            if (results.size() > 0) {
                resultList.clear();
                trailersSwipeLayout.setRefreshing(false);
                resultList.addAll(results);
                mAdapter.notifyDataSetChanged();
            } else {
                trailersSwipeLayout.setRefreshing(false);
                Toast.makeText(this, "Reviews unavailable!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (posterPath != null)
            outState.putString("posterPath", posterPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }
}
