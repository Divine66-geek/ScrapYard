package com.scrapyard.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scrapyard.app.R;
import com.scrapyard.app.database.entities.Seller;
import com.scrapyard.app.utils.ImageUtils;

import java.util.List;
import java.util.Locale;

public class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.SellerViewHolder> {

    private List<Seller> sellers;
    private final OnSellerClickListener listener;

    public interface OnSellerClickListener {
        void onSellerClick(Seller seller);
        void onNewTransactionClick(Seller seller);
    }

    public SellerAdapter(List<Seller> sellers, OnSellerClickListener listener) {
        this.sellers = sellers;
        this.listener = listener;
    }

    public void updateList(List<Seller> newSellers) {
        this.sellers = newSellers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_card, parent, false);
        return new SellerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerViewHolder holder, int position) {
        holder.bind(sellers.get(position), listener);
    }

    @Override
    public int getItemCount() { return sellers.size(); }

    static class SellerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvInitial, tvName, tvNationalId, tvTransactions, tvWeight, tvAmount;
        Button btnNewTransaction;

        SellerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto          = itemView.findViewById(R.id.ivSellerPhoto);
            tvInitial        = itemView.findViewById(R.id.tvInitial);
            tvName           = itemView.findViewById(R.id.tvSellerName);
            tvNationalId     = itemView.findViewById(R.id.tvNationalId);
            tvTransactions   = itemView.findViewById(R.id.tvTransactions);
            tvWeight         = itemView.findViewById(R.id.tvWeight);
            tvAmount         = itemView.findViewById(R.id.tvAmount);
            btnNewTransaction = itemView.findViewById(R.id.btnNewTransaction);
        }

        void bind(Seller seller, OnSellerClickListener listener) {
            tvName.setText(seller.getName());
            tvNationalId.setText("ID: " + seller.getNationalId());
            tvTransactions.setText(seller.getTransactionCount() + " transactions");
            tvWeight.setText(String.format(Locale.getDefault(), "%.2f kg", seller.getTotalWeight()));
            tvAmount.setText(String.format(Locale.getDefault(), "R %.2f", seller.getTotalAmount()));

            Context ctx = itemView.getContext();
            if (seller.hasPhoto()) {
                ivPhoto.setVisibility(View.VISIBLE);
                tvInitial.setVisibility(View.GONE);
                ImageUtils.loadSellerPhoto(ctx, seller.getPhotoPath(), ivPhoto);
            } else {
                ivPhoto.setVisibility(View.GONE);
                tvInitial.setVisibility(View.VISIBLE);
                String initial = seller.getName().isEmpty() ? "?" : seller.getName().substring(0, 1).toUpperCase();
                tvInitial.setText(initial);
            }

            itemView.setOnClickListener(v -> listener.onSellerClick(seller));
            btnNewTransaction.setOnClickListener(v -> listener.onNewTransactionClick(seller));
        }
    }
}
