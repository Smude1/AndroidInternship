package com.mudesuraj.studentmanagementapp.network;

import com.mudesuraj.studentmanagementapp.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Retrofit API Service Interface.
 * Defines all API endpoints used in the application.
 */
public interface ApiService {

    /**
     * Fetch list of users from the API.
     * Using JSONPlaceholder API as dummy data source.
     */
    @GET("users")
    Call<List<User>> getUsers();
}

