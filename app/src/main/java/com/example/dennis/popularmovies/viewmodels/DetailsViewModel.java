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


import android.arch.lifecycle.ViewModel;

import com.example.dennis.popularmovies.MoviesRepository;
import com.example.dennis.popularmovies.pojos.FavouriteStatus;
import com.example.dennis.popularmovies.pojos.SingleMovie;

public class DetailsViewModel extends ViewModel {
    public DetailsViewModel() {

    }

    public void saveFavouriteMovie(SingleMovie movie){
        MoviesRepository.saveFavouriteMovie(movie);
    }

    public void deleteFromFavourites(String id) {
        MoviesRepository.deleteFromFavourites(id);
    }

    public void saveStatus(FavouriteStatus status) {
        MoviesRepository.saveStatus(status);
    }

    public void deleteStatus(String movieId) {
        MoviesRepository.deleteStatus(movieId);
    }
}
