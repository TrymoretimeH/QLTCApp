package com.example.qltc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qltc.R;
import com.example.qltc.databinding.RowTransactionBinding;
import com.example.qltc.models.Category;
import com.example.qltc.models.Transaction;
import com.example.qltc.utils.Constants;
import com.example.qltc.utils.Helper;
import com.example.qltc.views.activites.MainActivity;
import com.example.qltc.views.fragments.ViewTransactionDetailFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DecimalFormat;

import io.realm.RealmResults;


public class TransactionsAdapter  extends  RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder> {
    private Context context;
    private RealmResults<Transaction> transactions;
    private final DecimalFormat df = Constants.getVNFormat();
    public TransactionsAdapter(Context context, RealmResults<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.binding.transactionAmount.setText(String.format("%s đ", df.format(transaction.getAmount())));
        holder.binding.accountLbl.setText(transaction.getAccount());

        holder.binding.transactionDate.setText(Helper.formatDate(transaction.getDate()));
        holder.binding.transactionCategory.setText(transaction.getCategory());

        Category transactionCategory = Constants.getCategoryDetails(transaction.getCategory());

        holder.binding.categoryIcon.setImageResource(transactionCategory.getCategoryImage());
//        holder.binding.categoryIcon.setBackgroundTintList(context.getColorStateList(transactionCategory.getCategoryColor()));

        holder.binding.accountLbl.setBackgroundTintList(context.getColorStateList(Constants.getAccountsColor(transaction.getAccount())));

        if(transaction.getType().equals(Constants.INCOME)) {
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.greenColor));
        } else if(transaction.getType().equals(Constants.EXPENSE)) {
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.redColor));
        }

//        Will change into click listener for item detail view
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                AlertDialog deleteDialog = new AlertDialog.Builder(context).create();
//                deleteDialog.setTitle("Xóa chi tiêu/thu nhập");
//                deleteDialog.setMessage("Bạn có chắc chắn muốn xóa chi tiêu/thu nhập?");
//                deleteDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Có", (dialogInterface, i) -> {
//                    ((MainActivity)context).viewModel.deleteTransaction(transaction);
//                });
//                deleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Không", (dialogInterface, i) -> {
//                    deleteDialog.dismiss();
//                });
//                deleteDialog.show();
//                return false;
//            }
//        });
        holder.itemView.setOnClickListener(v -> {
            new ViewTransactionDetailFragment(transaction).show(((MainActivity) context).getSupportFragmentManager(), null);
        });

    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.row_transaction, parent, false));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private RowTransactionBinding binding;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowTransactionBinding.bind(itemView);
        }
    }
}
