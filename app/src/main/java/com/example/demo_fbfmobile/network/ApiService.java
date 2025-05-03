package com.example.demo_fbfmobile.network;

import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.AuthenticationRequest;
import com.example.demo_fbfmobile.model.AuthenticationResponse;
import com.example.demo_fbfmobile.model.RegisterRequest;
import com.example.demo_fbfmobile.model.ResetPasswordRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/v1/auth/send-otp")
    Call<ApiResponse<String>> sendOtp(@Query("email") String email);

    @POST("/api/v1/auth/register")
    Call<AuthenticationResponse> register(@Body RegisterRequest request);

    @POST("/api/v1/auth/authenticate")
    Call<AuthenticationResponse> authenticate(@Body AuthenticationRequest request);

    @GET("/api/v1/secured-request")
    Call<ApiResponse<String>> getSecuredData(@Header("Authorization") String token);

    @POST("/api/v1/auth/reset-password-request")
    Call<ApiResponse<String>> requestPasswordReset(@Query("email") String email);

    @POST("/api/v1/auth/reset-password")
    Call<ApiResponse<String>> resetPassword(@Body ResetPasswordRequest request);
}

