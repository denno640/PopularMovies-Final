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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.dennis.popularmovies.R;
import com.example.dennis.popularmovies.adapters.TrailersAdapter;
import com.example.dennis.popularmovies.pojos.SingleTrailer;
import com.example.dennis.popularmovies.viewmodels.TrailersViewModel;

import java.util.ArrayList;
import java.util.List;

public class TrailersActivity extends AppCompatActivity implements
        TrailersAdapter.OnTrailerClickedHandler {

    private String movieId;
    private String posterPath;
    private TrailersViewModel viewModel;
    private SwipeRefreshLayout trailersSwipeLayout;
    private TrailersAdapter mAdapter;
    private List<SingleTrailer> resultList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);
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

        trailerRecyclerView.setLayoutManager(manager);
        resultList = new ArrayList<>();
        mAdapter = new TrailersAdapter(this, resultList, posterPath);
        trailerRecyclerView.setAdapter(mAdapter);

        viewModel = ViewModelProviders.of(this).get(TrailersViewModel.class);
        viewModel.loadTrailers(movieId);
        viewModel.getTrailersList().observe(this, this::onTrailersReceived);
        trailersSwipeLayout.setOnRefreshListener(() -> {
            viewModel.refresh(movieId);
            resultList.clear();
            Toast.makeText(this, R.string.reloading, Toast.LENGTH_SHORT).show();
            trailersSwipeLayout.setRefreshing(false);
        });
    }

    private void onTrailersReceived(List<SingleTrailer> results) {
        if (results != null) {
            if (results.size() > 0) {
                resultList.clear();
                trailersSwipeLayout.setRefreshing(false);
                resultList.addAll(results);
                mAdapter.notifyDataSetChanged();
            } else {
                trailersSwipeLayout.setRefreshing(false);
                Toast.makeText(this, "Trailers unavailable!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onTrailerClicked(SingleTrailer result) {
        if (result.getSite().contains(getString(R.string.youtube))) {
           /* Intent intent = new  Intent(Intent.ACTION_VIEW);

            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/watch?v="+result.getKey()));

            startActivity(intent);*/
           //This code offers users the freedom to choose which app to use
            //when opening the trailer
            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse(getString(R.string.youtube_link)+result.getKey())));
        } else {
            Toast.makeText(this, R.string.unable_to_play_trailer, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTrailerShared(SingleTrailer singleTrailer) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "video URL");
        i.putExtra(Intent.EXTRA_TEXT,getString(R.string.youtube_link)+singleTrailer.getKey());
        startActivity(Intent.createChooser(i, "Share video"));
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
