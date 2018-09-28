package com.example.dennis.popularmovies.pojos;

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

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "statuses", indices = {@Index(value = {"movieId"}, unique = true)})
public class FavouriteStatus {
   private String movieId;
    @PrimaryKey(autoGenerate = true)
   private long id;
   private boolean isFavourite;

   @Ignore
    public FavouriteStatus(String movieId, boolean isFavourite) {
        this.movieId = movieId;
        this.isFavourite = isFavourite;
    }

    public FavouriteStatus(String movieId, long id, boolean isFavourite) {
        this.movieId = movieId;
        this.id = id;
        this.isFavourite = isFavourite;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
}
