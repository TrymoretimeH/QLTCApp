package com.example.qltc.views.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qltc.R;
import com.example.qltc.adapters.AccountsAdapter;
import com.example.qltc.adapters.CategoryAdapter;
import com.example.qltc.models.Account;
import com.example.qltc.models.Category;
import com.example.qltc.models.Transaction;
import com.example.qltc.utils.Constants;
import com.example.qltc.utils.Helper;
import com.example.qltc.utils.Utility;
import com.example.qltc.views.activites.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class ViewTransactionDetailFragment extends BottomSheetDialogFragment {
    public interface OnDeleteTransactionListener {
        void onDeleteTransaction();
    }
    private OnDeleteTransactionListener listener;
    Transaction transaction;
    Calendar calendar;
    String docId;
    private Context context;
    private TextView title;
    private TextInputEditText date, amount, category, account, note;
    private Button saveTransactionBtn, deleteTransactionBtn;
    public ViewTransactionDetailFragment(Transaction transaction, String docId, Context context) {
        this.transaction = transaction;
        this.docId = docId;
        this.context = context;
    }
    public ViewTransactionDetailFragment(Transaction transaction, String docId, Context context, OnDeleteTransactionListener listener) {
        this.transaction = transaction;
        this.docId = docId;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        calendar = Calendar.getInstance();
        return inflater.inflate(R.layout.fragment_view_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        if (transaction.getType().equals(Constants.EXPENSE)) {
            String eTitle = "Chi tiết chi tiêu";
            title.setText(eTitle);
        } else {
            String iTitle = "Chi tiết thu nhập";
            title.setText(iTitle);
        }

        date.setText(Helper.formatDate(transaction.getDate()));
        amount.setText(String.valueOf(transaction.getAmount()));
        category.setText(transaction.getCategory());
        account.setText(transaction.getAccount());
        note.setText(transaction.getNote());

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext());
                datePickerDialog.setOnDateSetListener((datePicker, i, i1, i2) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                    calendar.set(Calendar.MONTH, datePicker.getMonth());
                    calendar.set(Calendar.YEAR, datePicker.getYear());

                    //SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
                    String dateToShow = Helper.formatDate(calendar.getTime());

                    date.setText(dateToShow);

                    transaction.setDate(calendar.getTime());
                });
                datePickerDialog.show();
            }
        });

        category.setOnClickListener(c-> {
            Utility.hideSoftKeyboard(getActivity(), view);
            AlertDialog categoryDialog = new AlertDialog.Builder(getContext()).create();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View v = inflater.inflate(R.layout.list_dialog, null);
            RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
            categoryDialog.setView(v);
            CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), Constants.categories, new CategoryAdapter.CategoryClickListener() {
                @Override
                public void onCategoryClicked(Category ct) {
                    category.setText(ct.getCategoryName());
                    transaction.setCategory(ct.getCategoryName());
                    categoryDialog.dismiss();
                }
            });
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
            recyclerView.setAdapter(categoryAdapter);
            categoryDialog.setTitle("Chọn danh mục");
            categoryDialog.show();
        });

        account.setOnClickListener(c-> {
            Utility.hideSoftKeyboard(getActivity(), view);
            AlertDialog accountsDialog = new AlertDialog.Builder(getContext()).create();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View v = inflater.inflate(R.layout.list_dialog, null);
            accountsDialog.setView(v);

            RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
            ArrayList<Account> accounts = new ArrayList<>();
            accounts.add(new Account(0, "Tiền mặt", R.drawable.cash));
            accounts.add(new Account(0, "Bank", R.drawable.bank));
            accounts.add(new Account(0, "Tín dụng", R.drawable.credit));

            AccountsAdapter adapter = new AccountsAdapter(getContext(), accounts, new AccountsAdapter.AccountsClickListener() {
                @Override
                public void onAccountSelected(Account ac) {
                    account.setText(ac.getAccountName());
                    transaction.setAccount(ac.getAccountName());
                    accountsDialog.dismiss();
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            accountsDialog.setTitle("Chọn phương thức thanh toán");
            accountsDialog.show();

        });

        saveTransactionBtn.setOnClickListener(c-> {
            if (transaction.getType() == null) {
                Utility.showToast(getContext(), "Loại giao dịch không được để trống");
                return;
            }
            if (amount.getText().toString().equals("")) {
                Utility.showToast(getContext(), "Số tiền không được để trống");
                return;
            }

            if (date.getText().toString().equals("")) {
                Utility.showToast(getContext(), "Ngày không được để trống");
                return;
            }

            if (category.getText().toString().equals("")) {
                Utility.showToast(getContext(), "Danh mục không được để trống");
                return;
            }

            if (account.getText().toString().equals("")) {
                Utility.showToast(getContext(), "Phương thức thanh toán không được để trống");
                return;
            }

            if (note.getText().toString().equals("")) {
                Utility.showToast(getContext(), "Ghi chú không được để trống");
                return;
            }
            int am = Integer.parseInt(Objects.requireNonNull(amount.getText()).toString());
            String nt = Objects.requireNonNull(note.getText()).toString();

            if(transaction.getType().equals(Constants.EXPENSE)) {
                transaction.setAmount(am*-1);
            } else {
                transaction.setAmount(am);
            }

            transaction.setNote(nt);
            Utility.updateTransaction(transaction, docId, context);
            dismiss();
        });

        deleteTransactionBtn.setOnClickListener(c-> {
            AlertDialog deleteDialog = new AlertDialog.Builder(getContext()).create();
            deleteDialog.setTitle("Xóa chi tiêu/thu nhập");
            deleteDialog.setMessage("Bạn có chắc chắn muốn xóa chi tiêu/thu nhập này?");
            deleteDialog.setButton(DialogInterface.BUTTON_POSITIVE, "CÓ", (dialogInterface, i) -> {
                Utility.deleteTransaction(transaction, docId, context);
                dismiss();
//               viewModel.getTransactions(calendar);
            });
            deleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "KHÔNG", (dialogInterface, i) -> {
                deleteDialog.dismiss();
            });
            deleteDialog.show();

        });
    }

    private void initView(View view) {
        title = view.findViewById(R.id.title);
        date = view.findViewById(R.id.date);
        amount = view.findViewById(R.id.amount);
        category = view.findViewById(R.id.category);
        account = view.findViewById(R.id.account);
        note = view.findViewById(R.id.note);
        saveTransactionBtn = view.findViewById(R.id.saveTransactionBtn);
        deleteTransactionBtn = view.findViewById(R.id.deleteTransactionBtn);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (listener != null) {
            listener.onDeleteTransaction();
        }
    }
}