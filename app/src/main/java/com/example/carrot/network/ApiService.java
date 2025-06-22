package com.example.carrot.network;

import com.example.carrot.model.*;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("/users/register")
    Call<Void> registerUser(@Body UserRegisterRequest user);

    @POST("/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @Multipart
    @POST("/products")
    Call<Product> createProduct(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part("seller_id") RequestBody sellerId,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("location_name") RequestBody locationName,
            @Part MultipartBody.Part image
    );

    @GET("/products")
    Call<List<Product>> getProducts(
            @Query("skip") int skip,
            @Query("limit") int limit,
            @Query("status") String status
    );


    @GET("/products/{product_id}")
    Call<Product> getProductDetail(@Path("product_id") int productId);

    @GET("/products/seller/{seller_id}")
    Call<List<Product>> getSellerProducts(@Path("seller_id") int sellerId);

    @PATCH("/products/{product_id}/status")
    Call<Void> updateProductStatus(@Path("product_id") int productId, @Body ProductStatusUpdateRequest request);

}
