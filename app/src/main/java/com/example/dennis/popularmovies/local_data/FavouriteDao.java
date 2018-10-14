package com.example.dennis.popularmovies.local_data;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.dennis.popularmovies.pojos.SingleMovie;

@Dao
public interface FavouriteDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavouriteMovie(SingleMovie movie);

    @Update
    void update(SingleMovie movie);

    @Delete
    void delete(SingleMovie movie);

    @Query("DELETE  FROM favourites WHERE id =:id")
    void deleteStatus(String id);

    @Query("SELECT * FROM favourites")
    DataSource.Factory<Integer, SingleMovie> getAllFavouriteMovies();
}
