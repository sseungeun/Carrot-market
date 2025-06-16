package com.example.carrot.network;

import com.example.carrot.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("/users/register")
    Call<Void> registerUser(@Body UserRegisterRequest user);

    @POST("/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    // ✅ 여기 수정 (Product → ProductRequest)
    @Headers("Content-Type: application/json")
    @POST("/products")
    Call<Product> createProduct(@Body ProductRequest productRequest);

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

    @POST("/messages")
    Call<Message> sendMessage(@Body Message message);

    @GET("/messages/{product_id}/{user1_id}/{user2_id}")
    Call<List<Message>> getMessages(
            @Path("product_id") String productId,
            @Path("user1_id") String user1Id,
            @Path("user2_id") String user2Id);

    @GET("/messages/conversations/{user_id}")
    Call<List<Message>> getUserConversations(@Path("user_id") String userId);
}
