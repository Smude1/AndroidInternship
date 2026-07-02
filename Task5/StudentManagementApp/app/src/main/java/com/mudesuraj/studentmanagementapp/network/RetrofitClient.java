package com.mudesuraj.studentmanagementapp.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit Client for API communication.
 * Singleton pattern to ensure only one instance is used throughout the app.
 */
public class RetrofitClient {

    // Base URL for the API
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    private static Retrofit retrofit;

    /**
     * Get singleton instance of Retrofit.
     */
    public static Retrofit getInstance() {
        if (retrofit == null) {
            // Create HTTP client with logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // Build Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    /**
     * Get the API service interface.
     */
    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }
}

