package com.example.carrot.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.carrot.R;
import com.example.carrot.model.Product;

public class PostDetailActivity extends AppCompatActivity {

    private ImageView ivProductImage;
    private TextView tvTitle, tvDescription, tvPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        ivProductImage = findViewById(R.id.iv_product_image);
        tvTitle = findViewById(R.id.tv_title);
        tvDescription = findViewById(R.id.tv_content);
        tvPrice = findViewById(R.id.tv_price);

        Product product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            tvTitle.setText(product.getTitle());
            tvDescription.setText(product.getDescription());
            tvPrice.setText(product.getPrice() + "원");
            Glide.with(this)
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(ivProductImage);
        }
    }
}
