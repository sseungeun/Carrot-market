package com.example.carrot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.carrot.R;
import com.example.carrot.model.Product;

public class PostDetailActivity extends AppCompatActivity {

    private ImageView ivProductImage;
    private TextView tvTitle, tvDescription, tvPrice;
    private Button btnChat;

    private int productId;
    private int sellerId;  // 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        ivProductImage = findViewById(R.id.iv_product_image);
        tvTitle = findViewById(R.id.tv_title);
        tvDescription = findViewById(R.id.tv_content);
        tvPrice = findViewById(R.id.tv_price);
        btnChat = findViewById(R.id.btn_chat);

        Product product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            productId = product.getId();
            sellerId = product.getSeller_id();

            tvTitle.setText(product.getTitle());
            tvDescription.setText(product.getDescription());
            tvPrice.setText(product.getPrice() + "원");

            Glide.with(this)
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(ivProductImage);
        }

        btnChat.setOnClickListener(view -> {
            Intent intent = new Intent(PostDetailActivity.this, ChatActivity.class);
            intent.putExtra("product_id", productId);
            intent.putExtra("seller_id", sellerId);
            intent.putExtra("buyer_id", 2);  // 👉 여기서는 임시로 로그인 유저 ID = 2 넣어둠
            startActivity(intent);
        });
    }
}
