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

import com.example.dennis.popularmovies.pojos.Review;
import com.example.dennis.popularmovies.pojos.TopRated;
import com.example.dennis.popularmovies.pojos.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface TMDbApiService {

    /**
     * Api call to get the top rated  movies
     * and before we go far, you need to head over to https://www.themoviedb.org
     * sign up and request(warning:involves filling a long form) for your OWN api key.
     * you will use that key to send requests to api
     *
     * @param apiKey
     * @param language
     * @param pageIndex
     *
     */
    @GET("top_rated")
    Call<TopRated> getTopRatedMovies(
            @Query("api_key") String apiKey,//get your own key from TMDb Api
            @Query("language") String language,
            @Query("page") long pageIndex
    );

    /**
     * Api call to get the most popular movies
     * @param apiKey
     * @param language
     * @param pageIndex
     * @return
     */

    @GET("popular")
    Call<TopRated> getTopMostPopularMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") long pageIndex
    );
    @GET
    Call<Trailers> getTrailers(
            @Url String url,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );
    @GET
    Call<Review> getReviews(
            @Url String url,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );
}
