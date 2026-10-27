package com.scrapyard.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scrapyard.app.R;
import com.scrapyard.app.database.entities.Seller;

import java.util.List;
import java.util.Locale;

public class ReportSellerAdapter extends RecyclerView.Adapter<ReportSellerAdapter.ViewHolder> {

    private List<Seller> sellers;

    public ReportSellerAdapter(List<Seller> sellers) {
        this.sellers = sellers;
    }

    public void updateList(List<Seller> newSellers) {
        this.sellers = newSellers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report_seller, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Seller seller = sellers.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvName.setText(seller.getName());
        holder.tvNationalId.setText(seller.getNationalId());
        holder.tvWeight.setText(String.format(Locale.getDefault(), "%.2f kg", seller.getTotalWeight()));
        holder.tvAmount.setText(String.format(Locale.getDefault(), "R %.2f", seller.getTotalAmount()));
        holder.tvTxCount.setText(seller.getTransactionCount() + " txn");
    }

    @Override
    public int getItemCount() { return sellers.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvNationalId, tvWeight, tvAmount, tvTxCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvSellerName);
            tvNationalId = itemView.findViewById(R.id.tvNationalId);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvTxCount = itemView.findViewById(R.id.tvTxCount);
        }
    }
}
