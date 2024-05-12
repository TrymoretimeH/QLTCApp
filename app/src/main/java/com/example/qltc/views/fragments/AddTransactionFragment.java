package com.example.qltc.views.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

public class AddTransactionFragment extends BottomSheetDialogFragment {
    private TextView incomeBtn, expenseBtn;
    private TextInputEditText date, amount, category, account, note;
    private Button saveTransactionBtn;
    public interface OnAddedTransactionListener {
        void onAddedTransaction();
    }

    private OnAddedTransactionListener listener;


    public AddTransactionFragment() {
        // Required empty public constructor
    }

    public AddTransactionFragment(OnAddedTransactionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Transaction transaction = new Transaction();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        incomeBtn.setOnClickListener(v -> {
            incomeBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.income_selector));
            expenseBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.default_selector));
            expenseBtn.setTextColor(requireContext().getColor(R.color.textColor));
            incomeBtn.setTextColor(requireContext().getColor(R.color.greenColor));

            transaction.setType(Constants.INCOME);
        });

        expenseBtn.setOnClickListener(v -> {
            incomeBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.default_selector));
            expenseBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.expense_selector));
            incomeBtn.setTextColor(requireContext().getColor(R.color.textColor));
            expenseBtn.setTextColor(requireContext().getColor(R.color.redColor));

            transaction.setType(Constants.EXPENSE);
        });

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
            //dialogBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(adapter);
            accountsDialog.setTitle("Chọn phương thức thanh toán");
            accountsDialog.show();

        });

        saveTransactionBtn.setOnClickListener(c-> {
            Utility.hideSoftKeyboard(getActivity(), view);
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

            Utility.addTransaction(transaction, getContext());

            dismiss();
        });
    }

    private void initView(View view) {
        incomeBtn = view.findViewById(R.id.incomeBtn);
        expenseBtn = view.findViewById(R.id.expenseBtn);

        date = view.findViewById(R.id.date);
        amount = view.findViewById(R.id.amount);

        category = view.findViewById(R.id.category);
        account = view.findViewById(R.id.account);
        note = view.findViewById(R.id.note);
        saveTransactionBtn = view.findViewById(R.id.saveTransactionBtn);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (listener != null) {
            listener.onAddedTransaction();
        }
    }

}