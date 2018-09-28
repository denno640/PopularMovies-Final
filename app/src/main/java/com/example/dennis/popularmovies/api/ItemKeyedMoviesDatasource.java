package com.example.dennis.popularmovies.api;

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

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.example.dennis.popularmovies.PopularMoviesApplication;
import com.example.dennis.popularmovies.R;
import com.example.dennis.popularmovies.pojos.SingleMovie;
import com.example.dennis.popularmovies.pojos.TopRated;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemKeyedMoviesDatasource extends ItemKeyedDataSource<Long, SingleMovie> {
    /**
     * This class contains logic for populating our movies list
     * as well paginating data for continuous scrolling
     */

    private MutableLiveData<NetworkState> networkState;
    private MutableLiveData<NetworkState> initialLoading;
    private String sortCriteria;
    private long page_index = 1L;

    private Context context = PopularMoviesApplication
            .getApp()
            .getApplicationContext();

    public ItemKeyedMoviesDatasource(String sortCriteria) {
        networkState = new MutableLiveData<>();
        initialLoading = new MutableLiveData<>();
        this.sortCriteria = sortCriteria;

    }


    public MutableLiveData getNetworkState() {
        return networkState;
    }


    @Override
    public void loadInitial(@NonNull ItemKeyedDataSource.LoadInitialParams<Long> params,
                            @NonNull ItemKeyedDataSource.LoadInitialCallback<SingleMovie> callback) {
        networkState.postValue(NetworkState.LOADING);
        SharedPreferences pfs = PreferenceManager.getDefaultSharedPreferences(context);
        sortCriteria = pfs.getString(context.getString(R.string.sort_list_key), "");

        if (sortCriteria.equals(context.getString(R.string.most_popular))) {

            ApiMerchant
                    .getInstance()
                    .getMyApiService()
                    .getTopMostPopularMovies(ApiMerchant.provideApikey(), ApiMerchant.provideLanguage(), page_index)
                    .enqueue(new Callback<TopRated>() {
                        @Override
                        public void onResponse(@NonNull Call<TopRated> call, @NonNull Response<TopRated> response) {
                            if (response.isSuccessful()) {
                                List<SingleMovie> movieList = ApiMerchant.provideMovieList(response);
                                if (movieList != null) {
                                    callback.onResult(movieList);
                                }
                                initialLoading.postValue(NetworkState.LOADED);
                                networkState.postValue(NetworkState.LOADED);
                                //increment page on successful load. important for pagination
                                page_index++;

                            } else {
                                initialLoading.postValue(new NetworkState("FAILED", response.message()));
                                networkState.postValue(new NetworkState("FAILED", response.message()));

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<TopRated> call, @NonNull Throwable t) {
                            String errorMessage;
                            errorMessage = t.getMessage();

                            networkState.postValue(new NetworkState("FAILED", errorMessage));

                        }
                    });
        } else if (sortCriteria.equals(context.getString(R.string.top_rated))) {
            ApiMerchant
                    .getInstance()
                    .getMyApiService()
                    .getTopRatedMovies(ApiMerchant.provideApikey(), ApiMerchant.provideLanguage(), page_index)
                    .enqueue(new Callback<TopRated>() {
                        @Override
                        public void onResponse(@NonNull Call<TopRated> call, @NonNull Response<TopRated> response) {
                            if (response.isSuccessful()) {


                                //shouldFetchData = true;
                                // Log.d(TAG, "rated page is: " + response.body().getPage());

                                List<SingleMovie> movieList = ApiMerchant.provideMovieList(response);
                                if (movieList != null) {
                                    callback.onResult(movieList);
                                }
                                initialLoading.postValue(NetworkState.LOADED);
                                networkState.postValue(NetworkState.LOADED);
                                page_index++;
                                // Log.d(TAG, "data loaded successfully");

                            } else {
                                // Log.d(TAG, response.message());
                                initialLoading.postValue(new NetworkState("FAILED", response.message()));
                                networkState.postValue(new NetworkState("FAILED", response.message()));

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<TopRated> call, @NonNull Throwable t) {
                            String errorMessage;
                            errorMessage = t.getMessage();
                            networkState.postValue(new NetworkState("FAILED", errorMessage));
                            // Log.d(TAG, "Encountered an error while fetching movies");
                        }
                    });
        }

    }

    @Override
    public void loadAfter(@NonNull ItemKeyedDataSource.LoadParams<Long> params,
                          @NonNull ItemKeyedDataSource.LoadCallback<SingleMovie> callback) {

        networkState.postValue(NetworkState.LOADING);

        if (sortCriteria.equals(context.getString(R.string.most_popular))) {

            ApiMerchant
                    .getInstance()
                    .getMyApiService()
                    .getTopMostPopularMovies(ApiMerchant.provideApikey(), ApiMerchant.provideLanguage(), page_index)
                    .enqueue(new Callback<TopRated>() {
                        @Override
                        public void onResponse(@NonNull Call<TopRated> call, @NonNull Response<TopRated> response) {
                            if (response.isSuccessful()) {

                                // Log.d(TAG, "onloadmore pop page is: " + response.body().getPage());

                                //shouldFetchData = true;
                                List<SingleMovie> movieList = ApiMerchant.provideMovieList(response);
                                if (movieList != null) {
                                    callback.onResult(movieList);
                                }
                                initialLoading.postValue(NetworkState.LOADED);
                                networkState.postValue(NetworkState.LOADED);
                                page_index++;
                                // Log.d(TAG, "data loaded successfully");

                            } else {
                                //Log.d(TAG, response.message());
                                initialLoading.postValue(new NetworkState("FAILED", response.message()));
                                networkState.postValue(new NetworkState("FAILED", response.message()));

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<TopRated> call, @NonNull Throwable t) {
                            String errorMessage;
                            errorMessage = t.getMessage();
                            networkState.postValue(new NetworkState("FAILED", errorMessage));
                            // Log.d(TAG, "Encountered an error while fetching movies");
                        }
                    });
        } else if (sortCriteria.equals(context.getString(R.string.top_rated))) {
            ApiMerchant
                    .getInstance()
                    .getMyApiService()
                    .getTopRatedMovies(ApiMerchant.provideApikey(), ApiMerchant.provideLanguage(), page_index)
                    .enqueue(new Callback<TopRated>() {
                        @Override
                        public void onResponse(@NonNull Call<TopRated> call, @NonNull Response<TopRated> response) {
                            if (response.isSuccessful()) {

                                // Log.d(TAG, "onloadmore rated page is: " + response.body().getPage());


                                List<SingleMovie> movieList = ApiMerchant.provideMovieList(response);
                                if (movieList != null) {
                                    callback.onResult(movieList);
                                }
                                initialLoading.postValue(NetworkState.LOADED);
                                networkState.postValue(NetworkState.LOADED);
                                //increment page number. basis of pagination
                                page_index++;
                                //Log.d(TAG, "data loaded successfully");

                            } else {
                                // Log.d(TAG, response.message());
                                initialLoading.postValue(new NetworkState("FAILED", response.message()));
                                networkState.postValue(new NetworkState("FAILED", response.message()));

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<TopRated> call, @NonNull Throwable t) {
                            String errorMessage;
                            errorMessage = t.getMessage();
                            networkState.postValue(new NetworkState("FAILED", errorMessage));
                            // Log.d(TAG, "Encountered an error while fetching movies");
                        }
                    });
        }


    }

    @Override
    public void loadBefore(@NonNull ItemKeyedDataSource.LoadParams<Long> params,
                           @NonNull ItemKeyedDataSource.LoadCallback<SingleMovie> callback) {

    }

    @NonNull
    @Override
    public Long getKey(@NonNull SingleMovie singleMovie) {
        return page_index;
    }
}
