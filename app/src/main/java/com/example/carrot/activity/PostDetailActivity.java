package com.example.carrot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.carrot.R;
import com.example.carrot.model.Product;
import com.example.carrot.model.ProductStatusUpdateRequest;
import com.example.carrot.network.ApiService;
import com.example.carrot.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvLocation, tvPrice, tvDescription, tvStatus;
    private ImageView ivProductImage;
    private Button btnChat, btnDelete, btnMarkSold;
    private boolean isMyPost;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 전달받은 데이터
        product = (Product) getIntent().getSerializableExtra("product");
        isMyPost = getIntent().getBooleanExtra("isMyPost", false);

        // 예외 처리: product가 null이면 종료
        if (product == null) {
            Toast.makeText(this, "상품 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 레이아웃 설정
        setContentView(isMyPost ? R.layout.activity_post_detail_my : R.layout.activity_post_detail);

        if (isMyPost) {
            ivProductImage = findViewById(R.id.iv_detail_image);
            tvTitle = findViewById(R.id.tv_detail_title);
            tvDescription = findViewById(R.id.tv_detail_description);
            tvStatus = findViewById(R.id.tv_detail_status);
            tvLocation = null;
            tvPrice = null;
        } else {
            ivProductImage = findViewById(R.id.iv_product_image);
            tvTitle = findViewById(R.id.tv_title);
            tvDescription = findViewById(R.id.tv_content);
            tvStatus = null;
            tvLocation = findViewById(R.id.tv_location);
            tvPrice = findViewById(R.id.tv_price);
        }

        // 데이터 설정
        if (tvTitle != null) {
            tvTitle.setText(product.getTitle() != null ? product.getTitle() : "제목 없음");
        }

        if (tvLocation != null) {
            tvLocation.setText(product.getLocation_name() != null ? product.getLocation_name() : "위치 정보 없음");
        }

        if (tvPrice != null) {
            tvPrice.setText(product.getPrice() + "원");
        }

        if (tvDescription != null) {
            tvDescription.setText(product.getDescription() != null ? product.getDescription() : "내용 없음");
        }

        if (tvStatus != null) {
            tvStatus.setText(product.getStatus() != null ? product.getStatus() : "상태 없음");
        }

        Glide.with(this)
                .load(product.getImage() != null ? product.getImage() : R.drawable.ic_launcher_foreground)
                .into(ivProductImage);

        // 버튼 처리
        if (isMyPost) {
            btnDelete = findViewById(R.id.btn_delete);
            btnMarkSold = findViewById(R.id.btn_mark_as_sold);

            btnDelete.setOnClickListener(v -> deletePost(product.getId()));
            btnMarkSold.setOnClickListener(v -> markAsSold(product.getId()));
        } else {
            btnChat = findViewById(R.id.btn_chat);
            btnChat.setOnClickListener(v -> openChat(product));
        }
    }

    private void deletePost(int productId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Void> call = apiService.updateProductStatus(productId, new ProductStatusUpdateRequest("deleted"));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PostDetailActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("deletedProductId", productId);
                    setResult(RESULT_OK, resultIntent);

                    finish();
                } else {
                    Toast.makeText(PostDetailActivity.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PostDetail", "deletePost failed", t);
                Toast.makeText(PostDetailActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void markAsSold(int productId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Void> call = apiService.updateProductStatus(productId, new ProductStatusUpdateRequest("sold"));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PostDetailActivity.this, "판매 완료로 변경됨", Toast.LENGTH_SHORT).show();
                    if (tvStatus != null) {
                        tvStatus.setText("sold");
                    }

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedProductId", productId);
                    setResult(RESULT_OK, resultIntent);

                    finish();
                } else {
                    Toast.makeText(PostDetailActivity.this, "변경 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PostDetail", "markAsSold failed", t);
                Toast.makeText(PostDetailActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openChat(Product product) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("product_id", product.getId());
        intent.putExtra("other_id", product.getSeller_id());
        intent.putExtra("other_nickname", product.getSeller_nickname());
        startActivity(intent);
    }

}
