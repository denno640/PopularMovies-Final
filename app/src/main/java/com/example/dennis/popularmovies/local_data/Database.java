package com.example.dennis.popularmovies.local_data;

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

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.dennis.popularmovies.pojos.FavouriteStatus;
import com.example.dennis.popularmovies.pojos.SingleMovie;

@android.arch.persistence.room.Database(entities = {SingleMovie.class, FavouriteStatus.class}, version = 1, exportSchema = false)
public abstract class Database  extends RoomDatabase{
    private static final String DATABASE_NAME = "popular_movies";
    private static final Object LOCK = new Object();
    private static volatile Database sInstance;
    public abstract FavouriteDao favouriteDao();
    public abstract FavouriteStatusDao favouriteStatusDao();

    public static Database getInstance(Context context) {
        if (sInstance == null) {

            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), Database.class,
                            Database.DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }

            }
        }
        return sInstance;
    }
}
