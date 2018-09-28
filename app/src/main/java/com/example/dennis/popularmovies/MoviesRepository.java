package com.example.dennis.popularmovies;

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

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.example.dennis.popularmovies.api.MoviesDataSourceFactory;
import com.example.dennis.popularmovies.local_data.Database;
import com.example.dennis.popularmovies.pojos.FavouriteStatus;
import com.example.dennis.popularmovies.pojos.SingleMovie;
import com.example.dennis.popularmovies.utils.PopularMoviesAppExecutors;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MoviesRepository {
    private Executor mExecutor;
    private static final Object LOCK = new Object();
    private static MoviesRepository sInstance;

    private MoviesRepository() {
        mExecutor= Executors.newFixedThreadPool(3);

    }


    public synchronized static MoviesRepository getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MoviesRepository();
            }
        }
        return sInstance;
    }


    public LiveData<PagedList<SingleMovie>> provieMovieList(MoviesDataSourceFactory factory) {
        //initial number of movies to download per request is set at 20
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder()).setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(20)
                        .setPageSize(20).build();
        return (new LivePagedListBuilder(factory, pagedListConfig))
                .setFetchExecutor(mExecutor)/*BackgroundThreadExecutor(executor)*/
                .build();
    }

    public LiveData<PagedList<SingleMovie>> getFavouriteMovies() {
        Database database = Database.getInstance(
                PopularMoviesApplication.getApp()
                .getApplicationContext()
        );
        LiveData<PagedList<SingleMovie>> singleMessages;
        PagedList.Config pagedListConfig = (new PagedList.Config.Builder()
                .setPrefetchDistance(5)
                .setPageSize(30)
                .setEnablePlaceholders(true)
                .build());
        singleMessages = new LivePagedListBuilder<>(database
                .favouriteDao()
                .getAllFavouriteMovies(), pagedListConfig)
                /* .setBoundaryCallback(boundaryCallback)*/
                .build();
        return singleMessages;
    }

    /**
     * saving favourite movie
     * accessing Room has to be in a background thread
     * @param movie
     */

    public static void saveFavouriteMovie(SingleMovie movie) {
        PopularMoviesAppExecutors
                .getInstance()
                .getDiskIO()
                .execute(()->{
                    Database db = Database.getInstance(
                            PopularMoviesApplication
                            .getApp()
                            .getApplicationContext());
                    db.favouriteDao().insertFavouriteMovie(movie);
                });
    }

    public static void deleteFromFavourites(SingleMovie singleMovie) {
        PopularMoviesAppExecutors
                .getInstance()
                .getDiskIO()
                .execute(()->{
                    Database db = Database.getInstance(
                            PopularMoviesApplication
                                    .getApp()
                                    .getApplicationContext());
                    db.favouriteDao().delete(singleMovie);
                });
    }

    public static void saveStatus(FavouriteStatus status) {
        PopularMoviesAppExecutors
                .getInstance()
                .getDiskIO()
                .execute(()->{
                    Database db = Database.getInstance(
                            PopularMoviesApplication
                                    .getApp()
                                    .getApplicationContext());
                    db.favouriteStatusDao().insertFavouriteMovie(status);
                });
    }

    public static void deleteStatus(String movieId) {
        PopularMoviesAppExecutors
                .getInstance()
                .getDiskIO()
                .execute(()->{
                    Database db = Database.getInstance(
                            PopularMoviesApplication
                                    .getApp()
                                    .getApplicationContext());
                    db.favouriteStatusDao().deleteStatus(movieId);
                });
    }
}
