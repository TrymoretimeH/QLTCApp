package com.example.qltc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qltc.R;
import com.example.qltc.models.Category;
import com.example.qltc.models.Transaction;
import com.example.qltc.utils.Constants;
import com.example.qltc.utils.Helper;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionsAdapter  extends  FirestoreRecyclerAdapter<Transaction,
        TransactionsAdapter.TransactionViewHolder> {
    private Context context;
    private List<Transaction> transactions;
    public List<Transaction> categories;
    private final DecimalFormat df = Constants.getVNFormat();
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    private OnItemClickListener mListener;

    public TransactionsAdapter(@NonNull FirestoreRecyclerOptions<Transaction> options, Context context) {
        super(options);
        this.context = context;
        this.transactions = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    public TransactionsAdapter(@NonNull FirestoreRecyclerOptions<Transaction> options, Context context, OnItemClickListener listener) {
        super(options);
        this.context = context;
        this.transactions = new ArrayList<>();
        this.mListener = listener;
    }

    public void setList(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public void setCategoriesList(List<Transaction> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    public Transaction getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position, @NonNull Transaction transaction) {
        String docId = transaction.getId();

        holder.transactionAmount.setText(String.format("%s đ", df.format(transaction.getAmount())));
        holder.accountLbl.setText(transaction.getAccount());

        holder.transactionDate.setText(Helper.formatDate(transaction.getDate()));
        holder.transactionCategory.setText(transaction.getCategory());

        Category transactionCategory = Constants.getCategoryDetails(transaction.getCategory());

        holder.categoryIcon.setImageResource(transactionCategory.getCategoryImage());
//        holder.binding.categoryIcon.setBackgroundTintList(context.getColorStateList(transactionCategory.getCategoryColor()));

        holder.accountLbl.setBackgroundTintList(context.getColorStateList(Constants.getAccountsColor(transaction.getAccount())));

        if(transaction.getType().equals(Constants.INCOME)) {
            holder.transactionAmount.setTextColor(context.getColor(R.color.greenColor));
        } else if(transaction.getType().equals(Constants.EXPENSE)) {
            holder.transactionAmount.setTextColor(context.getColor(R.color.redColor));
        }

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(v, position);
            }
        });

    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.row_transaction, parent, false));
    }


    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private ImageView categoryIcon;
        private TextView transactionCategory, transactionAmount, accountLbl, transactionDate;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
            transactionCategory = itemView.findViewById(R.id.transactionCategory);
            transactionAmount = itemView.findViewById(R.id.transactionAmount);
            accountLbl = itemView.findViewById(R.id.accountLbl);
            transactionDate = itemView.findViewById(R.id.transactionDate);
        }
    }
}
