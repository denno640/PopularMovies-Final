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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dennis.popularmovies.PopularMoviesApplication;
import com.example.dennis.popularmovies.R;
import com.example.dennis.popularmovies.local_data.Database;
import com.example.dennis.popularmovies.pojos.FavouriteStatus;
import com.example.dennis.popularmovies.pojos.SingleMovie;
import com.example.dennis.popularmovies.utils.PopularMoviesAppExecutors;
import com.example.dennis.popularmovies.viewmodels.DetailsViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * could not set ButterKnife with gradle 3.1.4
 * ButterKnife will be used later when i figure out a workaround
 * For now its boilerplate code
 */

public class DetailsActivity extends AppCompatActivity {
    private ImageView imageView;
    private ProgressBar progressBar;
    SingleMovie singleMovie;
    private TextView retryTextView;
    private ImageButton favButton;
    private boolean isFavourite;
    private TextView favouriteTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        imageView =findViewById(R.id.movie_poster_iv);
        TextView titleTextView = findViewById(R.id.orig_title_tv);
        TextView releaseTextView = findViewById(R.id.release_date_tv);
        TextView ratingTextView = findViewById(R.id.rating_tv);
        TextView plotTextView = findViewById(R.id.plot_tv);
        favouriteTextView=findViewById(R.id.image_button_text);
        favButton=findViewById(R.id.fav_btn);
        retryTextView = findViewById(R.id.retry_tv);
        progressBar = findViewById(R.id.poster_download_pgbar);
        progressBar.setVisibility(View.VISIBLE);


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
        Intent intent = new Intent(this,TrailersActivity.class);
        intent.putExtra("movieId",String.valueOf(singleMovie.getId()));
        intent.putExtra("posterPath",singleMovie.getPosterPath());
        startActivity(intent);
    }

    public void onReviewsButtonClicked(View view) {
        Intent intent = new Intent(this,ReviewsActivity.class);
        intent.putExtra("movieId",String.valueOf(singleMovie.getId()));
        intent.putExtra("posterPath",singleMovie.getPosterPath());
        startActivity(intent);
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
          viewModel.deleteFromFavourites(singleMovie);
          viewModel.deleteStatus(String.valueOf(singleMovie.getId()));
          favButton.setImageResource(R.drawable.ic_untapped);
          favouriteTextView.setText(R.string.fav_btn_text);


      }

    }
}
