package com.example.qltc.views.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qltc.R;
import com.example.qltc.adapters.TransactionsAdapter;
import com.example.qltc.models.Transaction;
import com.example.qltc.utils.Constants;
import com.example.qltc.utils.Helper;
import com.example.qltc.utils.Utility;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

//import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class TransactionsFragment extends Fragment implements AddTransactionFragment.OnAddedTransactionListener,
        TransactionsAdapter.OnItemClickListener, ViewTransactionDetailFragment.OnDeleteTransactionListener {
    private final DecimalFormat df = Constants.getVNFormat();
    private ImageView previousDateBtn, nextDateBtn, emptyState;
    private TextView currentDate, incomeLbl, expenseLbl, totalLbl;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private RelativeLayout loadingPanel;
    private TransactionsAdapter transactionsAdapter;
    private Calendar calendar = Calendar.getInstance();
    private Calendar currentCal = Calendar.getInstance();
    private Query query;
    private FirestoreRecyclerOptions<Transaction> options;
    private List<Transaction> newTransactions = new ArrayList<>();
    private long income = 0, expense = 0, total = 0;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    public TransactionsFragment(TransactionsAdapter adapter) {
        this.transactionsAdapter = adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.transactionsList);
        initView(view);

        nextDateBtn.setOnClickListener(c-> {
            if(Constants.SELECTED_TAB == Constants.DAILY) {
                calendar.add(Calendar.DATE, 1);
            } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, 1);
            } else if(Constants.SELECTED_TAB == Constants.YEARLY) {
                calendar.add(Calendar.YEAR, 1);
            }
            updateDate();
        });

        previousDateBtn.setOnClickListener(c-> {
            if(Constants.SELECTED_TAB == Constants.DAILY) {
                calendar.add(Calendar.DATE, -1);
            } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, -1);
            } else if(Constants.SELECTED_TAB == Constants.YEARLY) {
                calendar.add(Calendar.YEAR, -1);
            }
            updateDate();
        });

        fab.setOnClickListener(c -> {
            new AddTransactionFragment(this).show(getParentFragmentManager(), null);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateDate();
        transactionsAdapter = new TransactionsAdapter(options, getContext(), this);
        recyclerView.setAdapter(transactionsAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
                } else if(Objects.equals(tab.getText(), "Năm")) {
                    Constants.SELECTED_TAB = 2;
                    calendar.setTime(currentCal.getTime());
                    updateDate();
                } else {
                    Constants.SELECTED_TAB = 1;
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

    }

    private void initView(View view) {
        previousDateBtn = view.findViewById(R.id.previousDateBtn);
        nextDateBtn = view.findViewById(R.id.nextDateBtn);
        currentDate = view.findViewById(R.id.currentDate);
        incomeLbl = view.findViewById(R.id.incomeLbl);
        expenseLbl = view.findViewById(R.id.expenseLbl);
        totalLbl = view.findViewById(R.id.totalLbl);
        tabLayout = view.findViewById(R.id.tabLayout);
        emptyState = view.findViewById(R.id.emptyState);
        fab = view.findViewById(R.id.floatingActionButton);
        loadingPanel = view.findViewById(R.id.loadingPanel);
    }

    void updateDate() {
        if(Constants.SELECTED_TAB == Constants.DAILY) {
            currentDate.setText(Helper.formatDate(calendar.getTime()));
        } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {
            currentDate.setText(Helper.formatDateByMonth(calendar.getTime()));
        } else if(Constants.SELECTED_TAB == Constants.YEARLY) {
            currentDate.setText(Helper.formatDateByYear(calendar.getTime()));
        }
        getTransactions();
    }

    public void getTransactions() {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        newTransactions = new ArrayList<>();
        loadingPanel.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        if(Constants.SELECTED_TAB == Constants.DAILY) {
            query = Utility.getTransactionsFromCurrentUser()
                    .whereGreaterThanOrEqualTo("date", calendar.getTime())
                    .whereLessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .orderBy("date", Query.Direction.DESCENDING);
            options = new FirestoreRecyclerOptions.Builder<Transaction>()
                    .setQuery(query, Transaction.class)
                    .build();
            query.get().addOnCompleteListener(task -> {
                loadingPanel.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (task.isSuccessful()) {

                    if (task.getResult().getDocuments().isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Transaction transaction = document.toObject(Transaction.class);
                        newTransactions.add(transaction);
                        if (transaction.getType().equals(Constants.INCOME)) {
                            income += transaction.getAmount();
                        } else {
                            expense += transaction.getAmount();
                        }
                        total+= transaction.getAmount();
                    }
                    transactionsAdapter.setList(newTransactions);
                    transactionsAdapter.setCategoriesList(newTransactions);
                    incomeLbl.setText(String.format("%s đ", df.format(income)));
                    expenseLbl.setText(String.format("%s đ", df.format(expense)));

                    if (total > 0) {
                        totalLbl.setTextColor(getResources().getColor(R.color.ok, null));
                        totalLbl.setText(String.format("%s đ", df.format(total)));
                    } else {
                        totalLbl.setTextColor(getResources().getColor(R.color.not_ok, null));
                        totalLbl.setText(String.format("%s đ", df.format(total)));
                    }
                } else {
                    Exception e = task.getException();
                    e.printStackTrace();

                }
            });
        } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {
            calendar.set(Calendar.DAY_OF_MONTH,0);

            Date startTime = calendar.getTime();

            calendar.add(Calendar.MONTH,1);
            Date endTime = calendar.getTime();

            query = Utility.getTransactionsFromCurrentUser()
                    .whereGreaterThanOrEqualTo("date", startTime)
                    .whereLessThan("date", endTime)
                    .orderBy("date", Query.Direction.DESCENDING);
            options = new FirestoreRecyclerOptions.Builder<Transaction>()
                    .setQuery(query, Transaction.class)
                    .build();
            query.get().addOnCompleteListener(task -> {
                loadingPanel.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (task.isSuccessful()) {

                    if (task.getResult().getDocuments().isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                    }
                    for (DocumentSnapshot document : task.getResult()) {
                        Transaction transaction = document.toObject(Transaction.class);
                        newTransactions.add(transaction);
                        assert transaction != null;
                        if (transaction.getType().equals(Constants.INCOME)) {
                            income += transaction.getAmount();
                        } else {
                            expense += transaction.getAmount();
                        }
                        total+= transaction.getAmount();
                    }
                    transactionsAdapter.setList(newTransactions);
                    transactionsAdapter.setCategoriesList(newTransactions);

                    incomeLbl.setText(String.format("%s đ", df.format(income)));
                    expenseLbl.setText(String.format("%s đ", df.format(expense)));

                    if (total > 0) {
                        totalLbl.setTextColor(getResources().getColor(R.color.ok, null));
                        totalLbl.setText(String.format("%s đ", df.format(total)));
                    } else {
                        totalLbl.setTextColor(getResources().getColor(R.color.not_ok, null));
                        totalLbl.setText(String.format("%s đ", df.format(total)));
                    }
                } else {

                    Exception e = task.getException();
                    e.printStackTrace();
                }
            });
        } else if(Constants.SELECTED_TAB == Constants.YEARLY) {
            calendar.set(Calendar.DAY_OF_YEAR,0);

            Date startTime = calendar.getTime();

            calendar.add(Calendar.YEAR,1);
            Date endTime = calendar.getTime();

            query = Utility.getTransactionsFromCurrentUser()
                    .whereGreaterThanOrEqualTo("date", startTime)
                    .whereLessThan("date", endTime)
                    .orderBy("date", Query.Direction.DESCENDING);
            options = new FirestoreRecyclerOptions.Builder<Transaction>()
                    .setQuery(query, Transaction.class)
                    .build();
            query.get().addOnCompleteListener(task -> {
                loadingPanel.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (task.isSuccessful()) {

                    if (task.getResult().getDocuments().isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                    }
                    for (DocumentSnapshot document : task.getResult()) {
                        Transaction transaction = document.toObject(Transaction.class);
                        newTransactions.add(transaction);
                        assert transaction != null;
                        if (transaction.getType().equals(Constants.INCOME)) {
                            income += transaction.getAmount();
                        } else {
                            expense += transaction.getAmount();
                        }
                        total+= transaction.getAmount();
                    }
                    transactionsAdapter.setList(newTransactions);
                    transactionsAdapter.setCategoriesList(newTransactions);

                    incomeLbl.setText(String.format("%s đ", df.format(income)));
                    expenseLbl.setText(String.format("%s đ", df.format(expense)));

                    if (total > 0) {
                        totalLbl.setTextColor(getResources().getColor(R.color.ok, null));
                        totalLbl.setText(String.format("%s đ", df.format(total)));
                    } else {
                        totalLbl.setTextColor(getResources().getColor(R.color.not_ok, null));
                        totalLbl.setText(String.format("%s đ", df.format(total)));
                    }
                } else {
                    Exception e = task.getException();
                    e.printStackTrace();
                }
            });
        }
        this.income = 0; this.expense = 0; this.total = 0;
    }

    @Override
    public void onAddedTransaction() {
        getTransactions();
    }

    @Override
    public void onItemClick(View view, int position) {
        Transaction transaction = transactionsAdapter.getItem(position);
        new ViewTransactionDetailFragment(transaction, transaction.getId(), getContext(), this).show(getParentFragmentManager(), null);
    }

    @Override
    public void onDeleteTransaction() {
        getTransactions();
    }
}