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
import android.arch.paging.PagedList;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dennis.popularmovies.R;
import com.example.dennis.popularmovies.adapters.PopularMoviesAdapter;
import com.example.dennis.popularmovies.pojos.SingleMovie;
import com.example.dennis.popularmovies.utils.ColumnCalculator;
import com.example.dennis.popularmovies.viewmodels.PopularMoviesViewModel;

/**
 * could not set ButterKnife with gradle 3.1.4
 * ButterKnife will be used later when i figure out a workaround
 * For now its boilerplate code
 */

public class PopularMovies extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        PopularMoviesAdapter.OnMoviePosterClicked {
    private Toast mToast;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PopularMoviesAdapter mAdapter;
    private TextView sortCriteriumTextView;
    private PopularMoviesViewModel viewModel;
    private RecyclerView moviesRvItem;
    private String sortCriteria;
    private PagedList<SingleMovie> singleMoviePagedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies);
        swipeRefreshLayout = findViewById(R.id.swipe);
        moviesRvItem = findViewById(R.id.moviesRvItem);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark, android.R.color.holo_blue_dark, android.R.color.holo_orange_dark,
                android.R.color.holo_purple);
        swipeRefreshLayout.setRefreshing(true);
        moviesRvItem.setHasFixedSize(true);
        int columns = ColumnCalculator.calculateNoOfColumns(this);
        GridLayoutManager manager = new GridLayoutManager(this, columns, GridLayoutManager.VERTICAL, false);
        moviesRvItem.setLayoutManager(manager);
        mAdapter = new PopularMoviesAdapter(this);
        moviesRvItem.setAdapter(mAdapter);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sortCriteria = preferences.getString(getString(R.string.sort_list_key), "");
        if (sortCriteria.equals(""))
            sortCriteria = getString(R.string.most_popular);
        sortCriteriumTextView = findViewById(R.id.sort_criteria_tv);
        sortCriteriumTextView.setText(sortCriteria);

        if (sortCriteria.equals(getString(R.string.my_favourites))) {
            viewModel = ViewModelProviders.of(this).get(PopularMoviesViewModel.class);
            viewModel.getFavouriteList().observe(this, this::onFavouriteMoviesReceived);

        } else {
            viewModel = ViewModelProviders.of(this).get(PopularMoviesViewModel.class);
            viewModel.getMovieList().observe(this, singleMovies -> {
                mAdapter.submitList(singleMovies);
                if(singleMovies!= null)singleMoviePagedList=singleMovies;
            });

            viewModel.getNetworkState().observe(this, networkState -> {
                if (networkState != null) {
                    //some feedback to the user to know what causes the problem
                    //in production we cannot tell the user the actual cause of
                    //loading failure especially if it relates to our server
                    switch (networkState.getStatus()) {
                        case "RUNNING":
                            // Toast.makeText(this, "loading...", Toast.LENGTH_SHORT).show();
                            break;
                        case "FAILED":
                             //Toast.makeText(this, "failed...", Toast.LENGTH_SHORT).show();
                            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                            if (mToast != null) {
                                mToast.cancel();
                            }
                            String toastMessage = getString(R.string.could_not_load_movies) + networkState.getMsg();
                            mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
                            mToast.show();
                            break;
                        default:
                            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                            break;
                    }
                    mAdapter.setNetworkState(networkState);
                }

            });

        }

        ///swipeRefreshLayout to refresh implementation
        swipeRefreshLayout.setOnRefreshListener(() -> {

            if (mAdapter.getCurrentList() != null) {
                mAdapter.getCurrentList().getDataSource().invalidate();
            }

        });
        // registering preferenceChangeListener
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

    }

    /**
     * Methods for setting up the menu
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popular_movies_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //start settings activity
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called each time the preference changes
     *
     * @param sharedPreferences
     * @param key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
String previousSortingCriteria = sortCriteria;
        sortCriteria = sharedPreferences.getString(key, "");

        sortCriteriumTextView.setText(sortCriteria);
        //as soon as the user chooses a new setting
        swipeRefreshLayout.setRefreshing(true);
        if (sortCriteria.equals(getString(R.string.my_favourites))) {

            // mAdapter.getCurrentList().clear();
            mAdapter = null;
            mAdapter = new PopularMoviesAdapter(this);
            moviesRvItem.setAdapter(mAdapter);
            viewModel.getFavouriteList().observe(this, PopularMovies.this::onFavouriteMoviesReceived);

        } else {
            if(!previousSortingCriteria.equals(getString(R.string.my_favourites))){
                swipeRefreshLayout.setRefreshing(true);
                //if(mAdapter == null) Toast.makeText(this, "adapter is null", Toast.LENGTH_SHORT).show();
                if(mAdapter.getCurrentList()!= null)
                    mAdapter.getCurrentList().getDataSource().invalidate();
            }else {

                mAdapter = null;
                mAdapter = new PopularMoviesAdapter(this);
                moviesRvItem.setAdapter(mAdapter);
                swipeRefreshLayout.setRefreshing(true);
                if(singleMoviePagedList != null) {
                    singleMoviePagedList.getDataSource().invalidate();
                }else{
                    viewModel = ViewModelProviders.of(this).get(PopularMoviesViewModel.class);
                    viewModel.getMovieList().observe(this, singleMovies -> {
                        mAdapter.submitList(singleMovies);
                        if(singleMovies!= null)singleMoviePagedList=singleMovies;
                    });

                    viewModel.getNetworkState().observe(this, networkState -> {
                        if (networkState != null) {
                            //some feedback to the user to know what causes the problem
                            //in production we cannot tell the user the actual cause of
                            //loading failure especially if it relates to our server
                            switch (networkState.getStatus()) {
                                case "RUNNING":
                                    // Toast.makeText(this, "loading...", Toast.LENGTH_SHORT).show();
                                    break;
                                case "FAILED":
                                    //Toast.makeText(this, "failed...", Toast.LENGTH_SHORT).show();
                                    if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                                    if (mToast != null) {
                                        mToast.cancel();
                                    }
                                    String toastMessage = getString(R.string.could_not_load_movies) + networkState.getMsg();
                                    mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
                                    mToast.show();
                                    break;
                                default:
                                    if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                                    break;
                            }
                            mAdapter.setNetworkState(networkState);
                        }

                    });
                }

            }

        }

    }

    private void onFavouriteMoviesReceived(PagedList<SingleMovie> singleMovies) {
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
       if(sortCriteria.equals(getString(R.string.my_favourites))){
            mAdapter.submitList(singleMovies);
        }
    }

    //we unregister the preferenceChangeLister when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister PopularMovies as an OnPreferenceChangedListener
        // to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    //interface method to open details activity
    @Override
    public void onMoviePosterClicked(SingleMovie singleMovie) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("singleMovie", singleMovie);
        startActivity(intent);

    }
}
