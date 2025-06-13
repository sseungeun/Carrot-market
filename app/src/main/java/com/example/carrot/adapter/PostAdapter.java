package com.example.carrot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carrot.R;
import com.example.carrot.model.Product;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;

    public PostAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvTitle.setText(product.getTitle());
        holder.tvLocation.setText("위치 미지정");
        holder.tvPrice.setText(product.getPrice() + "원");

        // 이미지 로드 (Glide 사용, 없으면 기본 이미지)
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.ivProductImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvTitle, tvLocation, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
