package com.example.carrot.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carrot.R;
import com.example.carrot.model.Product;
import com.example.carrot.model.ProductRequest;
import com.example.carrot.network.ApiService;
import com.example.carrot.network.RetrofitClient;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    private EditText etTitle, etContent, etPrice;
    private Button btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        etPrice = findViewById(R.id.et_price);
        btnUpload = findViewById(R.id.btn_upload);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProduct();
            }
        });
    }

    private void uploadProduct() {
        String title = etTitle.getText().toString().trim();
        String description = etContent.getText().toString().trim();
        int price = Integer.parseInt(etPrice.getText().toString().trim());
        int sellerId = 1;

        // nullable field에 null이 들어가야 하므로 null 넣어줌
        Double latitude = null;
        Double longitude = null;
        String locationName = null;
        String image = null;

        ProductRequest productRequest = new ProductRequest(
                title, description, price, sellerId,
                latitude, longitude, locationName, image
        );


        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Product> call = apiService.createProduct(productRequest);
        Log.d("DEBUG_REQUEST", new GsonBuilder().serializeNulls().create().toJson(productRequest));

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UploadActivity.this, "상품 등록 성공!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UploadActivity.this, "등록 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(UploadActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
