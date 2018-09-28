package com.example.dennis.popularmovies.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.dennis.popularmovies.api.ApiMerchant;
import com.example.dennis.popularmovies.pojos.SingleTrailer;
import com.example.dennis.popularmovies.pojos.Trailers;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrailersViewModel extends ViewModel {
    private static final String TAG = TrailersViewModel.class.getSimpleName();
    private final LiveData<List<SingleTrailer>> trailersList;
    private MutableLiveData<List<SingleTrailer>> trailers;

    public TrailersViewModel() {
        trailers = new MutableLiveData<>();
        trailersList = trailers;
    }

    public void refresh(String movieId) {
        loadTrailers(movieId);
    }

    public void loadTrailers(String movieId) {

        ApiMerchant
                .getInstance()
                .getMyApiService()
                .getTrailers(movieId + "/videos", ApiMerchant.provideApikey(), ApiMerchant.provideLanguage())
                .enqueue(new Callback<Trailers>() {
                    @Override
                    public void onResponse(@NonNull Call<Trailers> call, @NonNull Response<Trailers> response) {
                        if (response.isSuccessful()) {
                            List<SingleTrailer> trailerList = ApiMerchant.provideTrailerList(response);
                            if (trailerList != null) {
                                trailers.setValue(trailerList);
                            }


                        } else {
                            if (response.errorBody() != null)
                                Log.d(TAG, response.errorBody().toString());
                            // Log.d(TAG,"response is not successful");

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Trailers> call, @NonNull Throwable t) {
                        String errorMessage;
                        errorMessage = t.getMessage();
                        Log.e(TAG, errorMessage);
                        //networkState.postValue(new NetworkState(Status.FAILED, errorMessage));

                    }
                });
    }

    public LiveData<List<SingleTrailer>> getTrailersList() {
        return trailersList;
    }
}
