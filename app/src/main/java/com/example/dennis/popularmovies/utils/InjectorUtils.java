package com.example.dennis.popularmovies.utils;

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

import com.example.dennis.popularmovies.MoviesRepository;
import com.example.dennis.popularmovies.api.MoviesDataSourceFactory;
import com.example.dennis.popularmovies.viewmodels.PopularMoviesViewModelFactory;

public class InjectorUtils {


    public static MoviesDataSourceFactory provideMoviesDataSourceFactory(String sortCriteria) {
        return new MoviesDataSourceFactory(sortCriteria);
    }

    public static PopularMoviesViewModelFactory providePopularMoviesViewModelFactory(String sortCriteria) {
        MoviesRepository mRepository = MoviesRepository.getInstance();
        return new PopularMoviesViewModelFactory(mRepository,sortCriteria);
    }
}
