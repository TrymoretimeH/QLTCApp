package com.example.qltc.views.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qltc.R;
import com.example.qltc.adapters.TransactionsAdapter;
import com.example.qltc.databinding.FragmentTransactionsBinding;
import com.example.qltc.models.Transaction;
import com.example.qltc.utils.Constants;
import com.example.qltc.utils.Helper;
import com.example.qltc.utils.Utility;
import com.example.qltc.viewmodels.MainViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.realm.RealmResults;

public class TransactionsFragment extends Fragment {
    private final DecimalFormat df = Constants.getVNFormat();
    FragmentTransactionsBinding binding;
    public TransactionsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    Calendar calendar;
    Calendar currentCal;
    public MainViewModel viewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTransactionsBinding.inflate(inflater);
        calendar = Calendar.getInstance();
        currentCal = Calendar.getInstance();

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        updateDate();

        binding.nextDateBtn.setOnClickListener(c-> {
            if(Constants.SELECTED_TAB == Constants.DAILY) {
                calendar.add(Calendar.DATE, 1);
            } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, 1);
            }
            updateDate();
        });

        binding.previousDateBtn.setOnClickListener(c-> {
            if(Constants.SELECTED_TAB == Constants.DAILY) {
                calendar.add(Calendar.DATE, -1);
            } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, -1);
            }
            updateDate();
        });


        binding.floatingActionButton.setOnClickListener(c -> {
            new AddTransactionFragment().show(getParentFragmentManager(), null);
        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(Objects.equals(tab.getText(), "Tháng")) {
                    Constants.SELECTED_TAB = 1;
                    calendar.setTime(currentCal.getTime());
                    updateDate();
                } else if(Objects.equals(tab.getText(), "Ngày")) {
                    Constants.SELECTED_TAB = 0;
                    calendar.setTime(currentCal.getTime());
                    updateDate();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        binding.transactionsList.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.transactions.observe(getViewLifecycleOwner(), new Observer<RealmResults<Transaction>>() {
            @Override
            public void onChanged(RealmResults<Transaction> transactions) {
                TransactionsAdapter transactionsAdapter = new TransactionsAdapter(getActivity(), transactions);
                binding.transactionsList.setAdapter(transactionsAdapter);
                if(!transactions.isEmpty()) {
                    binding.emptyState.setVisibility(View.GONE);
                } else {
                    binding.emptyState.setVisibility(View.VISIBLE);
                }

            }
        });

        viewModel.totalIncome.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer aInt) {
                binding.incomeLbl.setText(String.format("%s đ", df.format(aInt)));
            }
        });

        viewModel.totalExpense.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer aInt) {
                binding.expenseLbl.setText(String.format("%s đ", df.format(aInt)));
            }
        });

        viewModel.totalAmount.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer aInt) {
                if (aInt > 0) {
                    binding.totalLbl.setTextColor(getResources().getColor(R.color.ok, null));
                } else {
                    binding.totalLbl.setTextColor(getResources().getColor(R.color.not_ok, null));
                }
                binding.totalLbl.setText(String.format("%s đ", df.format(aInt)));
            }
        });
        viewModel.getTransactions(calendar);

        return binding.getRoot();
    }

    void updateDate() {
        if(Constants.SELECTED_TAB == Constants.DAILY) {
            binding.currentDate.setText(Helper.formatDate(calendar.getTime()));
        } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {
            binding.currentDate.setText(Helper.formatDateByMonth(calendar.getTime()));
        }
        viewModel.getTransactions(calendar);
    }

}