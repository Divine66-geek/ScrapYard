package com.scrapyard.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scrapyard.app.R;
import com.scrapyard.app.database.entities.Transaction;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void updateList(List<Transaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction tx = transactions.get(position);
        holder.bind(tx, position + 1);
    }

    @Override
    public int getItemCount() { return transactions.size(); }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvDate, tvWeight, tvAmount, tvNotes;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvTxNumber);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvNotes = itemView.findViewById(R.id.tvNotes);
        }

        void bind(Transaction tx, int number) {
            tvNumber.setText("#" + number);
            tvDate.setText(tx.getDate());
            tvWeight.setText(String.format(Locale.getDefault(), "%.2f kg", tx.getWeight()));
            tvAmount.setText(String.format(Locale.getDefault(), "R %.2f", tx.getAmount()));
            if (tx.getNotes() != null && !tx.getNotes().isEmpty()) {
                tvNotes.setVisibility(View.VISIBLE);
                tvNotes.setText(tx.getNotes());
            } else {
                tvNotes.setVisibility(View.GONE);
            }
        }
    }
}
