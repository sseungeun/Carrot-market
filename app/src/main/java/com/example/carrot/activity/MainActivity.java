package com.example.carrot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrot.R;
import com.example.carrot.adapter.PostAdapter;
import com.example.carrot.model.Product;
import com.example.carrot.network.ApiService;
import com.example.carrot.network.RetrofitClient;
import com.example.carrot.utils.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Product> productList = new ArrayList<>();
    private PostAdapter postAdapter;
    private Button btnAll, btnMyProducts;
    private int sellerId;
    private boolean viewingMyProducts = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        sellerId = sharedPrefManager.getUserId();

        recyclerView = findViewById(R.id.recycler_view_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postAdapter = new PostAdapter(this, productList);
        recyclerView.setAdapter(postAdapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_post);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intent);
        });

        btnAll = findViewById(R.id.btn_all);
        btnMyProducts = findViewById(R.id.btn_my_products);

        btnAll.setOnClickListener(v -> {
            viewingMyProducts = false;
            loadAllProducts();
        });

        btnMyProducts.setOnClickListener(v -> {
            viewingMyProducts = true;
            loadMyProducts();
        });

        loadAllProducts();
    }

    private void loadAllProducts() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Product>> call = apiService.getProducts(0, 20, "for_sale");

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    postAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "전체 상품 조회 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "서버 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMyProducts() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Product>> call = apiService.getSellerProducts(sellerId);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 🔽 삭제된 상품 제외하고 필터링
                    List<Product> filteredList = new ArrayList<>();
                    for (Product product : response.body()) {
                        if (!"deleted".equalsIgnoreCase(product.getStatus())) {
                            filteredList.add(product);
                        }
                    }

                    productList.clear();
                    productList.addAll(filteredList);
                    postAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "내 판매물건 조회 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "서버 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            int deletedProductId = data.getIntExtra("deletedProductId", -1);
            int updatedProductId = data.getIntExtra("updatedProductId", -1);

            if (deletedProductId != -1) {
                for (int i = 0; i < productList.size(); i++) {
                    if (productList.get(i).getId() == deletedProductId) {
                        productList.remove(i);
                        postAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            } else if (updatedProductId != -1) {
                for (int i = 0; i < productList.size(); i++) {
                    if (productList.get(i).getId() == updatedProductId) {
                        productList.get(i).setStatus("sold");
                        postAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
    }
}
