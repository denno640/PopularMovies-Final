package com.example.dennis.popularmovies.local_data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.dennis.popularmovies.pojos.FavouriteStatus;

@Dao
public interface FavouriteStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavouriteMovie(FavouriteStatus status);

    @Update
    void update(FavouriteStatus status);

    @Delete
    void delete(FavouriteStatus status);

    @Query("SELECT isFavourite FROM statuses WHERE movieId =:movieId")
    boolean isLikeStatus(String movieId);

    @Query("DELETE  FROM statuses WHERE movieId =:movieId")
    void deleteStatus(String movieId);
}
