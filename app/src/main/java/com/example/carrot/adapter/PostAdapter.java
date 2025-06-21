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
import com.example.carrot.utils.SharedPrefManager;

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
        holder.tvLocation.setText(product.getLocation_name() != null ? product.getLocation_name() : "위치 미지정");
        holder.tvPrice.setText(product.getPrice() + "원");

        // 이미지 처리
        String imagePath = product.getImage();
        Glide.with(context)
                .load(imagePath != null && !imagePath.isEmpty() ? imagePath : R.drawable.ic_launcher_foreground)
                .into(holder.ivProductImage);

        // 게시글 클릭 시 상세화면 이동
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("product", product);

            // 🟡 내가 올린 게시물인지 확인해서 함께 전달
            SharedPrefManager pref = new SharedPrefManager(context);
            boolean isMyPost = (product.getSeller_id() == pref.getUserId());
            intent.putExtra("isMyPost", isMyPost);

            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).startActivityForResult(intent, 1001);
            }

        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // 리스트 갱신
    public void updateProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    // 새 게시글 추가
    public void addNewProduct(Product product) {
        productList.add(0, product);
        notifyItemInserted(0);
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
