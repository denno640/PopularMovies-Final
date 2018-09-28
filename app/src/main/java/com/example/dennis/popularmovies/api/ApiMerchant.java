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

import com.example.dennis.popularmovies.PopularMoviesApplication;
import com.example.dennis.popularmovies.R;
import com.example.dennis.popularmovies.pojos.Review;
import com.example.dennis.popularmovies.pojos.SingleMovie;
import com.example.dennis.popularmovies.pojos.SingleReview;
import com.example.dennis.popularmovies.pojos.SingleTrailer;
import com.example.dennis.popularmovies.pojos.TopRated;
import com.example.dennis.popularmovies.pojos.Trailers;

import java.util.List;

import retrofit2.Response;

public class ApiMerchant {
    /**
     * This class provides the API service
     */
    private TMDbApiService myApiService;
    private static ApiMerchant sInstance;

    public TMDbApiService getMyApiService() {
        return myApiService;
    }


    public ApiMerchant() {
        myApiService = APIUtils.getAPIService();

    }
    public static synchronized ApiMerchant getInstance() {
        if (sInstance == null) {
            sInstance = new ApiMerchant();
        }
        return sInstance;
    }

    /**
     * obtain your own api key from TMDb the movie database api
     * @return api_key
     */
    public static String provideApikey(){
        return PopularMoviesApplication
                .getApp()
                .getApplicationContext()
                .getString(R.string.api_key);
    }

    /**
     * set up language
     * @return en_US
     */
    public static String provideLanguage(){
        return PopularMoviesApplication
                .getApp()
                .getApplicationContext()
                .getString(R.string.language);
    }

    /**
     * utility method to extract list of movies from the network response
     * @param response
     * @return
     */
    public static List<SingleMovie> provideMovieList(Response<TopRated> response) {
        TopRated topRatedMovies = response.body();
        if (topRatedMovies != null) {
            return topRatedMovies.getResults();
        }
        return null;
    }
    public static List<SingleTrailer> provideTrailerList(Response<Trailers> response){
        List<SingleTrailer> results = response.body().getResults();
        return results;
    }
    public static List<SingleReview> provideReviewsList(Response<Review> response){
        List<SingleReview> results = response.body().getResults();
        return results;
    }
}
