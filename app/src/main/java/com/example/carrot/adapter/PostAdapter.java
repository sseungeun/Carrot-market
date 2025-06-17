package com.example.carrot.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carrot.R;
import com.example.carrot.activity.PostDetailActivity;
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

        // 이미지 경로가 null이거나 비어있으면 기본 이미지를 사용하도록 처리
        String imagePath = product.getImage();
        Glide.with(context)
                .load(imagePath != null && !imagePath.isEmpty() ? imagePath : R.drawable.ic_launcher_foreground) // null일 경우 기본 이미지
                .into(holder.ivProductImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // 제품 리스트 업데이트 메서드
    public void updateProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged(); // 리스트 갱신 후 RecyclerView 갱신
    }
    public void addNewProduct(Product product) {
        productList.add(0, product);  // 첫 번째 위치에 새 상품 추가
        notifyItemInserted(0);  // RecyclerView 갱신
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
