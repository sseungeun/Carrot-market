package com.example.carrot.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrot.R;
import com.example.carrot.adapter.PostAdapter;
import com.example.carrot.model.Product;
import com.example.carrot.network.ApiService;
import com.example.carrot.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadPosts();
    }

    private void loadPosts() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Product>> call = apiService.getProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    List<Product> productList = response.body();
                    postAdapter = new PostAdapter(MainActivity.this, productList);
                    recyclerView.setAdapter(postAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "리스트 로드 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "서버 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
