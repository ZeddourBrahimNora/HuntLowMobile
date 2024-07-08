package com.example.huntlow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private ArrayList<String> productNames;
    private ArrayList<String> nutriScores;
    private ArrayList<String> productImages;

    public ProductAdapter(ArrayList<String> productNames, ArrayList<String> nutriScores, ArrayList<String> productImages) {
        this.productNames = productNames;
        this.nutriScores = nutriScores;
        this.productImages = productImages;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        String productName = productNames.get(position);
        String nutriScore = nutriScores.get(position);
        String productImage = productImages.get(position);

        holder.productNameTextView.setText(productName);
        holder.nutriScoreTextView.setText("Nutri-Score: " + nutriScore);
        Glide.with(holder.productImageView.getContext())
                .load(productImage)
                .into(holder.productImageView);
    }

    @Override
    public int getItemCount() {
        return productNames.size();
    }

    public void updateData(ArrayList<String> newProductNames, ArrayList<String> newNutriScores, ArrayList<String> newProductImages) {
        this.productNames = newProductNames;
        this.nutriScores = newNutriScores;
        this.productImages = newProductImages;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productNameTextView;
        TextView nutriScoreTextView;
        ImageView productImageView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            nutriScoreTextView = itemView.findViewById(R.id.nutriScoreTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
        }
    }
}
