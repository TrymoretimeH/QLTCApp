package com.example.qltc.views.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.qltc.R;
import com.example.qltc.databinding.FragmentViewTransactionBinding;
import com.example.qltc.databinding.ListDialogBinding;
import com.example.qltc.models.Transaction;
import com.example.qltc.utils.Constants;
import com.example.qltc.utils.Helper;
import com.example.qltc.viewmodels.MainViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

public class ViewTransactionDetailFragment extends BottomSheetDialogFragment {
    Transaction transaction;
    MainViewModel viewModel;
    Calendar calendar;
    public ViewTransactionDetailFragment(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentViewTransactionBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        calendar = Calendar.getInstance();

        binding = FragmentViewTransactionBinding.inflate(inflater);

        if (transaction.getType().equals(Constants.EXPENSE)) {
            String eTitle = "Chi tiết chi tiêu";
            binding.title.setText(eTitle);
        } else {
            String iTitle = "Chi tiết thu nhập";
            binding.title.setText(iTitle);
        }

        binding.date.setText(Helper.formatDate(transaction.getDate()));
        binding.amount.setText(String.valueOf(transaction.getAmount()));
        binding.category.setText(transaction.getCategory());
        binding.account.setText(transaction.getAccount());
        binding.note.setText(transaction.getNote());
//        binding.date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext());
//                datePickerDialog.setOnDateSetListener((datePicker, i, i1, i2) -> {
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
//                    calendar.set(Calendar.MONTH, datePicker.getMonth());
//                    calendar.set(Calendar.YEAR, datePicker.getYear());
//
//                    //SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
//                    String dateToShow = Helper.formatDate(calendar.getTime());
//
//                    binding.date.setText(dateToShow);
//
//                    transaction.setDate(calendar.getTime());
//                    transaction.setId(calendar.getTime().getTime());
//                });
//                datePickerDialog.show();
//            }
//        });
//
//        binding.category.setOnClickListener(c-> {
//            ListDialogBinding dialogBinding = ListDialogBinding.inflate(inflater);
//            AlertDialog categoryDialog = new AlertDialog.Builder(getContext()).create();
//            categoryDialog.setView(dialogBinding.getRoot());
//            CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), Constants.categories, new CategoryAdapter.CategoryClickListener() {
//                @Override
//                public void onCategoryClicked(Category category) {
//                    binding.category.setText(category.getCategoryName());
//                    transaction.setCategory(category.getCategoryName());
//                    categoryDialog.dismiss();
//                }
//            });
//            dialogBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
//            dialogBinding.recyclerView.setAdapter(categoryAdapter);
//
//            categoryDialog.show();
//        });
//
//        binding.account.setOnClickListener(c-> {
//            ListDialogBinding dialogBinding = ListDialogBinding.inflate(inflater);
//            AlertDialog accountsDialog = new AlertDialog.Builder(getContext()).create();
//            accountsDialog.setView(dialogBinding.getRoot());
//
//            ArrayList<Account> accounts = new ArrayList<>();
//            accounts.add(new Account(0, "Tiền mặt"));
//            accounts.add(new Account(0, "Bank"));
//            accounts.add(new Account(0, "Tín dụng"));
//
//            AccountsAdapter adapter = new AccountsAdapter(getContext(), accounts, new AccountsAdapter.AccountsClickListener() {
//                @Override
//                public void onAccountSelected(Account account) {
//                    binding.account.setText(account.getAccountName());
//                    transaction.setAccount(account.getAccountName());
//                    accountsDialog.dismiss();
//                }
//            });
//            dialogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            //dialogBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
//            dialogBinding.recyclerView.setAdapter(adapter);
//
//            accountsDialog.show();
//
//        });
//
//        binding.saveTransactionBtn.setOnClickListener(c-> {
//            int amount = Integer.parseInt(Objects.requireNonNull(binding.amount.getText()).toString());
//            String note = Objects.requireNonNull(binding.note.getText()).toString();
//
//            if(transaction.getType().equals(Constants.EXPENSE)) {
//                transaction.setAmount(amount*-1);
//            } else {
//                transaction.setAmount(amount);
//            }
//
//            transaction.setNote(note);
//
//            ((MainActivity) requireActivity()).viewModel.addTransaction(transaction);
//            ((MainActivity) requireActivity()).getTransactions();
//            dismiss();
//        });

        binding.deleteTransactionBtn.setOnClickListener(c-> {
            AlertDialog deleteDialog = new AlertDialog.Builder(getContext()).create();
            deleteDialog.setTitle("Xóa chi tiêu/thu nhập");
            deleteDialog.setMessage("Bạn có chắc chắn muốn xóa chi tiêu/thu nhập này?");
            deleteDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Có", (dialogInterface, i) -> {
                    if (viewModel != null) {
                        viewModel.deleteTransaction(transaction);
                        viewModel.getTransactions(calendar);
                    }
            });
            deleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Không", (dialogInterface, i) -> {
                deleteDialog.dismiss();
            });
            deleteDialog.show();
            dismiss();
        });

        return binding.getRoot();
    }
}