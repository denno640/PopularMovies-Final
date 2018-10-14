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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dennis.popularmovies.PopularMoviesApplication;
import com.example.dennis.popularmovies.R;
import com.example.dennis.popularmovies.adapters.TrailersAdapter;
import com.example.dennis.popularmovies.local_data.Database;
import com.example.dennis.popularmovies.pojos.FavouriteStatus;
import com.example.dennis.popularmovies.pojos.SingleMovie;
import com.example.dennis.popularmovies.pojos.SingleReview;
import com.example.dennis.popularmovies.pojos.SingleTrailer;
import com.example.dennis.popularmovies.utils.PopularMoviesAppExecutors;
import com.example.dennis.popularmovies.viewmodels.DetailsViewModel;
import com.example.dennis.popularmovies.viewmodels.ReviewsViewModel;
import com.example.dennis.popularmovies.viewmodels.TrailersViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * could not set ButterKnife with gradle 3.1.4
 * ButterKnife will be used later when i figure out a workaround
 * For now its boilerplate code
 */

public class DetailsActivity extends AppCompatActivity implements
        TrailersAdapter.OnTrailerClickedHandler {
    private ImageView imageView;
    private ProgressBar progressBar;
    SingleMovie singleMovie;
    private TextView retryTextView;
    private ImageButton favButton;
    private boolean isFavourite;
    private TextView favouriteTextView;
    private List<Object> trailersAndReviewsList;
    private TrailersAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        imageView =findViewById(R.id.movie_poster_iv);
        TextView titleTextView = findViewById(R.id.orig_title_tv);
        TextView releaseTextView = findViewById(R.id.release_date_tv);
        TextView ratingTextView = findViewById(R.id.rating_tv);
        TextView plotTextView = findViewById(R.id.plot_tv);
        RecyclerView allTypeRecyclerView = findViewById(R.id.reviews_trailers_recyclerview);
       LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        favouriteTextView=findViewById(R.id.image_button_text);
        favButton=findViewById(R.id.fav_btn);
        retryTextView = findViewById(R.id.retry_tv);
        progressBar = findViewById(R.id.poster_download_pgbar);
        progressBar.setVisibility(View.VISIBLE);
        trailersAndReviewsList=new ArrayList<>();


        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(getString(R.string.singleMovie))){
                singleMovie = savedInstanceState.getParcelable(getString(R.string.singleMovie));
            }
            isFavourite=savedInstanceState.getBoolean(getString(R.string.isFavourite));
        }

        if(getIntent() != null) {
            if(getIntent().hasExtra(getString(R.string.singleMovie)))
            singleMovie = getIntent().getParcelableExtra(getString(R.string.singleMovie));
        }
        allTypeRecyclerView.setLayoutManager(manager);
        allTypeRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new TrailersAdapter(this,trailersAndReviewsList,singleMovie.getPosterPath());
allTypeRecyclerView.setAdapter(mAdapter);

        ReviewsViewModel reviewsViewModel = ViewModelProviders.of(this).get(ReviewsViewModel.class);
        reviewsViewModel.loadReviews(String.valueOf(singleMovie.getId()));
        reviewsViewModel.getReviewsList().observe(this, this::onReviewsReceived);

       TrailersViewModel viewModel = ViewModelProviders.of(this).get(TrailersViewModel.class);
        viewModel.loadTrailers(String.valueOf(singleMovie.getId()));
        viewModel.getTrailersList().observe(this, this::onTrailersReceived);

        PopularMoviesApplication
                .getApp()
                .getPicassoWithCache()
                .load(getString(R.string.base_image_url)+singleMovie.getPosterPath())
                .priority(Picasso.Priority.HIGH)
                .error(R.color.error_color)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        retryTextView.setVisibility(View.INVISIBLE);
                       progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        PopularMoviesApplication
                                .getApp()
                                .getPicassoWithCache()
                                .load(getString(R.string.base_image_url)+singleMovie.getPosterPath())
                                .priority(Picasso.Priority.HIGH)
                                .error(R.color.error_color)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        retryTextView.setVisibility(View.INVISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        retryTextView.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });

                    }
                });
        //setting up original title
        titleTextView.setText(String.format("%s%s", getString(R.string.original_movie_title), singleMovie.getOriginalTitle()));
        //setting up user rating
        ratingTextView.setText(String.format("%s%s", getString(R.string.user_rating), singleMovie.getVoteAverage()));
        //setting up release date
        releaseTextView.setText(String.format("%s%s", getString(R.string.release_date), singleMovie.getReleaseDate()));
        //setting up synopsis
        plotTextView.setText(String.format("%s%s", getString(R.string.plot_synopsis), singleMovie.getOverview()));
//check if movie is favourite
        PopularMoviesAppExecutors
                .getInstance()
                .getDiskIO()
                .execute(()->{
                    Database db = Database.getInstance(getApplicationContext());
                    if(db.favouriteStatusDao()
                            .isLikeStatus(String.valueOf(singleMovie.getId()))){
                        favButton.setImageResource(R.drawable.ic_tapped);
                        isFavourite=true;
                        favouriteTextView.setText(R.string.favourite_text);
                    }
                });
    }


    private void onReviewsReceived(List<SingleReview> results) {
        if (results != null) {
            if (results.size() > 0) {
               // trailersSwipeLayout.setRefreshing(false);
                trailersAndReviewsList.addAll(results);
                mAdapter.notifyDataSetChanged();
            } else {
                //trailersSwipeLayout.setRefreshing(false);
                Toast.makeText(this, "Reviews unavailable!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void onTrailersReceived(List<SingleTrailer> results) {
        if (results != null) {
            if (results.size() > 0) {
                trailersAndReviewsList.addAll(results);
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Trailers unavailable!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(singleMovie != null)
        outState.putParcelable(getString(R.string.singleMovie),singleMovie);
        if(isFavourite)outState.putBoolean(getString(R.string.isFavourite),isFavourite);
    }

    public void retryPosterDownload(View view) {
        progressBar.setVisibility(View.VISIBLE);
       PopularMoviesApplication
               .getApp()
               .getPicassoWithCache()
                .load(getString(R.string.base_image_url)+singleMovie.getPosterPath())
                .priority(Picasso.Priority.HIGH)
                .error(R.color.error_color)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        retryTextView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.INVISIBLE);
                        retryTextView.setVisibility(View.VISIBLE);

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back to the Main movies page
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onTrailersClicked(View view) {
        /*Intent intent = new Intent(this,TrailersActivity.class);
        intent.putExtra("movieId",String.valueOf(singleMovie.getId()));
        intent.putExtra("posterPath",singleMovie.getPosterPath());
        startActivity(intent);*/
        Toast.makeText(this, R.string.scroll_trailer_prompt, Toast.LENGTH_SHORT).show();
    }

    public void onReviewsButtonClicked(View view) {
        /*Intent intent = new Intent(this,ReviewsActivity.class);
        intent.putExtra("movieId",String.valueOf(singleMovie.getId()));
        intent.putExtra("posterPath",singleMovie.getPosterPath());
        startActivity(intent);*/
        Toast.makeText(this, R.string.scroll_reviews_text, Toast.LENGTH_SHORT).show();
    }

    public void onFavButtonClicked(View view) {
        DetailsViewModel viewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);
      if(!isFavourite) {
          viewModel.saveFavouriteMovie(singleMovie);
          FavouriteStatus status = new FavouriteStatus(
                  String.valueOf(singleMovie.getId()),true);
          viewModel.saveStatus(status);
          favButton.setImageResource(R.drawable.ic_tapped);
          favouriteTextView.setText(R.string.favourite_text);
      }else{
          viewModel.deleteFromFavourites(String.valueOf(singleMovie.getId()));
          viewModel.deleteStatus(String.valueOf(singleMovie.getId()));
          favButton.setImageResource(R.drawable.ic_untapped);
          favouriteTextView.setText(R.string.fav_btn_text);


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
}
