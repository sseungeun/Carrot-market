package com.example.carrot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Product> productList;
    private PostAdapter postAdapter;
    private Button btnAll, btnMyProducts;
    private int sellerId;  // int로 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        sellerId = sharedPrefManager.getUserId();  // int 로 받아옴

        recyclerView = findViewById(R.id.recycler_view_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab_add_post);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intent);
        });

        btnAll = findViewById(R.id.btn_all);
        btnMyProducts = findViewById(R.id.btn_my_products);

        btnAll.setOnClickListener(v -> loadAllProducts());
        btnMyProducts.setOnClickListener(v -> loadMyProducts());

        loadAllProducts();  // 최초 전체 상품 조회

        // UploadActivity에서 넘긴 새 상품을 받기
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("newProduct")) {
            Product newProduct = (Product) intent.getSerializableExtra("newProduct");
            if (newProduct != null) {
                // 새로운 상품을 목록에 추가하고 "내 판매물건" 탭으로 활성화
                loadMyProducts();  // 내 판매물건 리스트 다시 불러오기
            }
        }
    }

    private void loadAllProducts() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Product>> call = apiService.getProducts(0, 20, "for_sale");

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postAdapter = new PostAdapter(MainActivity.this, response.body());
                    recyclerView.setAdapter(postAdapter);
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
                    postAdapter = new PostAdapter(MainActivity.this, response.body());
                    recyclerView.setAdapter(postAdapter);
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
    protected void onResume() {
        super.onResume();

        // 데이터를 받아 RecyclerView 갱신
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("newProduct")) {
            Product newProduct = (Product) intent.getSerializableExtra("newProduct");
            if (newProduct != null) {
                // 새로운 상품을 목록에 추가하고 "내 판매물건" 탭으로 활성화
                addNewProductToList(newProduct);
            }
        }
    }

    private void addNewProductToList(Product newProduct) {
        if (postAdapter != null) {
            postAdapter.addNewProduct(newProduct); // PostAdapter에 새 상품 추가
        }
    }

}

