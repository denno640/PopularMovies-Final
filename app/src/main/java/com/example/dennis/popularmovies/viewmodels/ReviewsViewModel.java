package com.example.dennis.popularmovies.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.dennis.popularmovies.api.ApiMerchant;
import com.example.dennis.popularmovies.pojos.Review;
import com.example.dennis.popularmovies.pojos.SingleReview;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsViewModel extends ViewModel {
    private static final String TAG = TrailersViewModel.class.getSimpleName();
    private final LiveData<List<SingleReview>> reviewsList;
    private MutableLiveData<List<SingleReview>> reviews;
    public ReviewsViewModel() {
        reviews =new MutableLiveData<>();
        reviewsList = reviews;
    }
    public void refresh(String movieId){
        loadReviews(movieId);
    }
    public void loadReviews(String movieId){

        ApiMerchant
                .getInstance()
                .getMyApiService()
                .getReviews(movieId+"/reviews",ApiMerchant.provideApikey(), ApiMerchant.provideLanguage())
                .enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(@NonNull Call<Review> call, @NonNull Response<Review> response) {
                        if (response.isSuccessful()) {
                            List<SingleReview> trailerList = ApiMerchant.provideReviewsList(response);
                            if (trailerList != null) {
                                reviews.setValue(trailerList);
                            }


                        } else {
                        Log.d(TAG,response.errorBody().toString());
                       // Log.d(TAG,"response is not successful");

                    }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Review> call, @NonNull Throwable t) {
                        String errorMessage;
                        errorMessage = t.getMessage();
                        Log.e(TAG,errorMessage);

                        //networkState.postValue(new NetworkState(Status.FAILED, errorMessage));

                    }
                });
    }

    public LiveData<List<SingleReview>> getReviewsList() {
        return reviewsList;
    }
}
