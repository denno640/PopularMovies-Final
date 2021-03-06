package com.example.dennis.popularmovies.viewmodels;

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

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;

import com.example.dennis.popularmovies.MoviesRepository;
import com.example.dennis.popularmovies.api.ItemKeyedMoviesDatasource;
import com.example.dennis.popularmovies.api.MoviesDataSourceFactory;
import com.example.dennis.popularmovies.api.NetworkState;
import com.example.dennis.popularmovies.pojos.SingleMovie;
import com.example.dennis.popularmovies.utils.InjectorUtils;

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

public class PopularMoviesViewModel extends ViewModel {
    private LiveData<PagedList<SingleMovie>> movieList;
    private LiveData<NetworkState> networkState;
    private MoviesDataSourceFactory factory;
    private final LiveData<PagedList<SingleMovie>> favouriteList;
    private MoviesRepository mRepository;

    public PopularMoviesViewModel() {
        mRepository = MoviesRepository.getInstance();
        factory = InjectorUtils.provideMoviesDataSourceFactory();
        networkState = Transformations.switchMap(factory.getMutableLiveData(),
                (Function<ItemKeyedMoviesDatasource, LiveData<NetworkState>>) ItemKeyedMoviesDatasource::getNetworkState);
        movieList = mRepository.provieMovieList(factory);
        favouriteList = mRepository.getFavouriteMovies();

    }


    public LiveData<PagedList<SingleMovie>> getFavouriteList() {
        return favouriteList;
    }

    public LiveData<PagedList<SingleMovie>> getMovieList() {
        return movieList;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public MoviesDataSourceFactory getFactory() {
        return factory;
    }
}
