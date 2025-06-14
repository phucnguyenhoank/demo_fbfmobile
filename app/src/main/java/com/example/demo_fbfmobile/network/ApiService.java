package com.example.demo_fbfmobile.network;

import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.AuthenticationRequest;
import com.example.demo_fbfmobile.model.AuthenticationResponse;
import com.example.demo_fbfmobile.model.CartItemDisplay;
import com.example.demo_fbfmobile.model.CartItemDto;
import com.example.demo_fbfmobile.model.CartItemRequest;
import com.example.demo_fbfmobile.model.CartItemUpdateRequest;
import com.example.demo_fbfmobile.model.FbfOrderDto;
import com.example.demo_fbfmobile.model.FbfUserDto;
import com.example.demo_fbfmobile.model.FoodDto;
import com.example.demo_fbfmobile.model.OrderItemDetailDto;
import com.example.demo_fbfmobile.model.OrderRequest;
import com.example.demo_fbfmobile.model.PageResponse;
import com.example.demo_fbfmobile.model.RegisterRequest;
import com.example.demo_fbfmobile.model.ResetPasswordRequest;
import com.example.demo_fbfmobile.model.UpdateFbfUserRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
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

    @GET("/api/v1/cart-items/display")
    Call<ApiResponse<List<CartItemDisplay>>> getCartItemsDisplay(@Header("Authorization") String authToken);

    @PUT("/api/v1/cart-items/update")
    Call<ApiResponse<CartItemDto>> updateCartItem(@Header("Authorization") String token, @Body CartItemUpdateRequest req);

    @DELETE("/api/v1/cart-items/{cartItemId}")
    Call<ApiResponse<String>> deleteCartItem(@Header("Authorization") String token, @Path("cartItemId") Long cartItemId);

    @POST("/api/v1/fbf-orders/create-undo")
    Call<ApiResponse<FbfOrderDto>> createUndoOrder(@Header("Authorization") String authToken, @Body OrderRequest request);

    @POST("/api/v1/fbf-orders/{orderId}/confirm")
    Call<ApiResponse<String>> confirmOrder(@Header("Authorization") String authToken, @Path("orderId") Long orderId);

    @POST("/api/v1/fbf-orders/{orderId}/undo")
    Call<ApiResponse<String>> undoOrder(@Header("Authorization") String authToken, @Path("orderId") Long orderId);

    @DELETE("/api/v1/fbf-orders/{orderId}/delete-canceled")
    Call<ApiResponse<String>> deleteCanceledOrder(@Header("Authorization") String authToken, @Path("orderId") Long orderId);

    @GET("api/v1/users/me")
    Call<ApiResponse<FbfUserDto>> getCurrentUser(@Header("Authorization") String authToken);

    @PUT("api/v1/users/me")
    Call<ApiResponse<FbfUserDto>> updateCurrentUser(@Header("Authorization") String authToken, @Body UpdateFbfUserRequest request);

    @GET("/api/v1/fbf-orders/get-mine")
    Call<PageResponse<FbfOrderDto>> getOrderHistory(
            @Header("Authorization") String bearerToken,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort   // e.g. "createdAt,desc"
    );

    @POST("/api/v1/cart-items/add")
    Call<ApiResponse<CartItemDto>> addCartItem(
            @Header("Authorization") String bearerToken,
            @Body CartItemRequest request
    );
    @GET("/api/v1/food/all")
    Call<PageResponse<FoodDto>> getAllFoods(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort);

    @GET("/api/v1/food/{id}")
    Call<ApiResponse<FoodDto>> getFoodDetailsById(@Path("id") Long id);

    @GET("/api/v1/order-item/get")
    Call<ApiResponse<List<OrderItemDetailDto>>> getOrderItemByOrderId(@Header("Authorization") String token, @Query("id") Long id);
    @GET("/api/v1/food/search/category-id")
    Call<PageResponse<FoodDto>> getFoodsByCategoryId(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort,
            @Query("id") Long categoryId
    );

    @GET("/api/v1/food/search/full")
    Call<PageResponse<FoodDto>> getFoodByFullFilter(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort,
            @Query("min") double min,
            @Query("max") double max,
            @Query("name") String name,
            @Query("categoryId") Long categoryId
    );

    @GET("/api/v1/food/search/by-price")
    Call<PageResponse<FoodDto>> getFoodByPriceBetween(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort,
            @Query("min") double min,
            @Query("max") double max
    );

    @GET("/api/v1/fbf-orders/get")
    Call<ApiResponse<FbfOrderDto>> getOrderByOrderId(@Header("Authorization") String token, @Query("orderId") Long orderId);

}

