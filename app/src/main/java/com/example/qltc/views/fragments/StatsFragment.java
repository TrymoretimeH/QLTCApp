package com.example.qltc.views.fragments;

import static com.example.qltc.utils.Constants.SELECTED_STATS_TYPE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.qltc.R;
import com.example.qltc.adapters.TransactionsAdapter;
import com.example.qltc.models.Transaction;
import com.example.qltc.utils.Constants;
import com.example.qltc.utils.Helper;
import com.example.qltc.utils.Utility;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class StatsFragment extends Fragment {
    private TransactionsAdapter transactionsAdapter;
    private Query query;
    private FirestoreRecyclerOptions<Transaction> options;
    private List<Transaction> newCategoriesTransactions = new ArrayList<>();

    private Calendar calendar;
    private Calendar currentCal;

    private TextView incomeBtn, expenseBtn, currentDate;
    private ImageView emptyState, previousDateBtn, nextDateBtn;
    private TabLayout tabLayout;
    private AnyChartView anyChart;
    private RelativeLayout loadingPanel;
    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendar = Calendar.getInstance();
        currentCal = Calendar.getInstance();
        initView(view);
        Pie pie = AnyChart.pie();
        updateDate(pie);
        transactionsAdapter = new TransactionsAdapter(options, getContext());

        incomeBtn.setOnClickListener(v -> {
            incomeBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.income_selector));
            expenseBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.default_selector));
            expenseBtn.setTextColor(requireContext().getColor(R.color.textColor));
            incomeBtn.setTextColor(requireContext().getColor(R.color.greenColor));

            SELECTED_STATS_TYPE = Constants.INCOME;
            updateDate(pie);
        });

        expenseBtn.setOnClickListener(v -> {
            incomeBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.default_selector));
            expenseBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.expense_selector));
            incomeBtn.setTextColor(requireContext().getColor(R.color.textColor));
            expenseBtn.setTextColor(requireContext().getColor(R.color.redColor));

            SELECTED_STATS_TYPE = Constants.EXPENSE;
            updateDate(pie);
        });

        nextDateBtn.setOnClickListener(c-> {
            if(Constants.SELECTED_TAB_STATS == Constants.DAILY) {
                calendar.add(Calendar.DATE, 1);
            } else if(Constants.SELECTED_TAB_STATS == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, 1);
            } else if(Constants.SELECTED_TAB_STATS == Constants.YEARLY) {
                calendar.add(Calendar.YEAR, 1);
            }
            updateDate(pie);
        });

        previousDateBtn.setOnClickListener(c-> {
            if(Constants.SELECTED_TAB_STATS == Constants.DAILY) {
                calendar.add(Calendar.DATE, -1);
            } else if(Constants.SELECTED_TAB_STATS == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, -1);
            } else if(Constants.SELECTED_TAB_STATS == Constants.YEARLY) {
                calendar.add(Calendar.YEAR, -1);
            }
            updateDate(pie);
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(Objects.equals(tab.getText(), "Ngày")) {
                    Constants.SELECTED_TAB_STATS = 0;
                    calendar.setTime(currentCal.getTime());
                    updateDate(pie);
                } else if(Objects.equals(tab.getText(), "Tháng")) {
                    Constants.SELECTED_TAB_STATS = 1;
                    calendar.setTime(currentCal.getTime());
                    updateDate(pie);
                } else if(Objects.equals(tab.getText(), "Năm")) {
                    Constants.SELECTED_TAB_STATS = 2;
                    calendar.setTime(currentCal.getTime());
                    updateDate(pie);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        pie.labels().position("outside");

//        pie.legend().title().enabled(true);
//        pie.legend().title()
//                .text("Retail channels")
//                .padding(0d, 0d, 10d, 0d);

//        pie.legend()
//                .position("center-bottom")
//                .itemsLayout(LegendLayout.HORIZONTAL)
//                .align(Align.CENTER);

        updateChart(pie);
        anyChart.setChart(pie);
    }

    private void initView(View view) {
        anyChart = view.findViewById(R.id.anyChart);
        incomeBtn = view.findViewById(R.id.incomeBtn);
        expenseBtn = view.findViewById(R.id.expenseBtn);
        emptyState = view.findViewById(R.id.emptyState);
        previousDateBtn = view.findViewById(R.id.previousDateBtn);
        currentDate = view.findViewById(R.id.currentDate);
        nextDateBtn = view.findViewById(R.id.nextDateBtn);
        tabLayout = view.findViewById(R.id.tabLayout);
        loadingPanel = view.findViewById(R.id.loadingPanel);
    }

    void updateDate(Pie pie) {
        if(Constants.SELECTED_TAB_STATS == Constants.DAILY) {
            currentDate.setText(Helper.formatDate(calendar.getTime()));
        } else if(Constants.SELECTED_TAB_STATS == Constants.MONTHLY) {
            currentDate.setText(Helper.formatDateByMonth(calendar.getTime()));
        } else if(Constants.SELECTED_TAB_STATS == Constants.YEARLY) {
            currentDate.setText(Helper.formatDateByYear(calendar.getTime()));
        }
        getTransactions(calendar, SELECTED_STATS_TYPE, pie);
    }

    private void getTransactions(Calendar calendar, String type, Pie pie) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        loadingPanel.setVisibility(View.VISIBLE);
        anyChart.setVisibility(View.GONE);
        if (Objects.equals(type, Constants.INCOME)) {
            pie.title("Biểu đồ tổng thể các khoản thu nhập");
        } else if (Objects.equals(type, Constants.EXPENSE)) {
            pie.title("Biểu đồ tổng thể các khoản chi tiêu");
        }

        newCategoriesTransactions = new ArrayList<>();

        if (Constants.SELECTED_TAB_STATS == Constants.DAILY) {
            query = Utility.getTransactionsFromCurrentUser()
                    .whereEqualTo("type", type)
                    .whereGreaterThanOrEqualTo("date", calendar.getTime())
                    .whereLessThan("date",
                            new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .orderBy("date", Query.Direction.DESCENDING);
            options = new FirestoreRecyclerOptions.Builder<Transaction>()
                    .setQuery(query, Transaction.class)
                    .build();
            query.get().addOnCompleteListener(
                    task -> {
                        loadingPanel.setVisibility(View.GONE);
                        anyChart.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().isEmpty()) {
                                emptyState.setVisibility(View.VISIBLE);
                                anyChart.setVisibility(View.GONE);
                            } else {
                                emptyState.setVisibility(View.GONE);
                                anyChart.setVisibility(View.VISIBLE);
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                newCategoriesTransactions.add(document.toObject(Transaction.class));
                            }
                            transactionsAdapter.setCategoriesList(newCategoriesTransactions);
                            updateChart(pie);
                        } else {
                            Exception e = task.getException();
                            e.printStackTrace();
                        }
                    }
            );


        }
        else if (Constants.SELECTED_TAB_STATS == Constants.MONTHLY) {
            calendar.set(Calendar.DAY_OF_MONTH, 0);

            Date startTime = calendar.getTime();


            calendar.add(Calendar.MONTH, 1);
            Date endTime = calendar.getTime();

            query = Utility.getTransactionsFromCurrentUser()
                    .whereEqualTo("type", type)
                    .whereGreaterThanOrEqualTo("date", startTime)
                    .whereLessThan("date", endTime)
                    .orderBy("date", Query.Direction.DESCENDING);
            options = new FirestoreRecyclerOptions.Builder<Transaction>()
                    .setQuery(query, Transaction.class)
                    .build();
            query.get().addOnCompleteListener(
                    task -> {
                        loadingPanel.setVisibility(View.GONE);
                        anyChart.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().isEmpty()) {
                                emptyState.setVisibility(View.VISIBLE);
                                anyChart.setVisibility(View.GONE);
                            } else {
                                emptyState.setVisibility(View.GONE);
                                anyChart.setVisibility(View.VISIBLE);
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                newCategoriesTransactions.add(document.toObject(Transaction.class));
                            }
                            transactionsAdapter.setCategoriesList(newCategoriesTransactions);
                            updateChart(pie);

                        } else {
                            Exception e = task.getException();
                            e.printStackTrace();
                        }
                    }
            );

        }
        else if (Constants.SELECTED_TAB == Constants.YEARLY) {
            calendar.set(Calendar.DAY_OF_YEAR, 0);

            Date startTime = calendar.getTime();

            calendar.add(Calendar.YEAR, 1);
            Date endTime = calendar.getTime();

            query = Utility.getTransactionsFromCurrentUser()
                    .whereEqualTo("type", type)
                    .whereGreaterThanOrEqualTo("date", startTime)
                    .whereLessThan("date", endTime)
                    .orderBy("date", Query.Direction.DESCENDING);

            options = new FirestoreRecyclerOptions.Builder<Transaction>()
                    .setQuery(query, Transaction.class)
                    .build();

            query.get().addOnCompleteListener(
                    task -> {
                        loadingPanel.setVisibility(View.GONE);
                        anyChart.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().isEmpty()) {
                                anyChart.setVisibility(View.GONE);
                                emptyState.setVisibility(View.VISIBLE);
                            } else {
                                emptyState.setVisibility(View.GONE);
                                anyChart.setVisibility(View.VISIBLE);
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                newCategoriesTransactions.add(document.toObject(Transaction.class));
                            }
                            transactionsAdapter.setCategoriesList(newCategoriesTransactions);
                            updateChart(pie);

                        } else {
                            Exception e = task.getException();
                            e.printStackTrace();
                        }
                    }
            );

        }
    }

    private void updateChart(Pie pie) {
        if(!transactionsAdapter.categories.isEmpty()) {
            emptyState.setVisibility(View.GONE);
            anyChart.setVisibility(View.VISIBLE);

            List<DataEntry> data = new ArrayList<>();

            Map<String, Integer> categoryMap = new HashMap<>();

            for(Transaction transaction : transactionsAdapter.categories) {
                String category = transaction.getCategory();
                int amount = transaction.getAmount();

                if(categoryMap.containsKey(category)) {
                    int currentTotal = categoryMap.get(category);
                    currentTotal += Math.abs(amount);
                    categoryMap.put(category, currentTotal);
                } else {
                    categoryMap.put(category, Math.abs(amount));
                }
            }

            for(Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
                data.add(new ValueDataEntry(entry.getKey(),entry.getValue()));
            }
            pie.data(data);

        } else {
            emptyState.setVisibility(View.VISIBLE);
            anyChart.setVisibility(View.GONE);
        }
    }


}