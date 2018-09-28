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

import android.app.Application;
import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class PopularMoviesApplication extends Application {
    private static PopularMoviesApplication sInstance;
    private Picasso picassoWithCache;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
        File httpCacheDirectory = new File(getCacheDir(), "picasso-cache");
        Cache cache = new Cache(httpCacheDirectory, 15 * 1024 * 1024);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().cache(cache);
        picassoWithCache = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(okHttpClientBuilder.build()))
                .build();

    }
    public Picasso getPicassoWithCache(){
        return picassoWithCache;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static PopularMoviesApplication getApp(){
        return sInstance;
    }

}
