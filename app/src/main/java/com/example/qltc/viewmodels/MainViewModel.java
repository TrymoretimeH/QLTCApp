package com.example.qltc.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.qltc.migrations.QLTCMigration;
import com.example.qltc.models.Transaction;
import com.example.qltc.utils.Constants;
import com.example.qltc.utils.Utility;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.internal.Util;

public class MainViewModel extends AndroidViewModel {

    public MutableLiveData<RealmResults<Transaction>> transactions = new MutableLiveData<>();
    public MutableLiveData<RealmResults<Transaction>> categoriesTransactions = new MutableLiveData<>();

    public MutableLiveData<Integer> totalIncome = new MutableLiveData<>();
    public MutableLiveData<Integer> totalExpense = new MutableLiveData<>();
    public MutableLiveData<Integer> totalAmount = new MutableLiveData<>();

//    public Query query;
//    public FirestoreRecyclerOptions<Transaction> options;
    private RealmResults<Transaction> newTransactions = null;
    private int income = 0, expense = 0, total = 0;
    Realm realm;
    Calendar calendar;

    public MainViewModel(@NonNull Application application) {
        super(application);
        Realm.init(application);
        setupDatabase();
    }

    public void getTransactions(Calendar calendar, String type) {
        this.calendar = calendar;
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if(Constants.SELECTED_TAB_STATS == Constants.DAILY) {
//            query = Utility.getTransactions()
//                    .whereEqualTo("type", type)
//                    .whereGreaterThanOrEqualTo("date", calendar.getTime())
//                    .whereLessThan("date",
//                            new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)));
//            options = new FirestoreRecyclerOptions.Builder<Transaction>()
//                    .setQuery(query, Transaction.class)
//                    .build();
//            query.get().addOnCompleteListener(
//                    task -> {
//                        if (task.isSuccessful()) {
//                            newTransactions = task.getResult().getDocuments();
//                        } else {
//                            Exception e = task.getException();
//                            e.printStackTrace();
//                        }
//                    }
//            );

            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .equalTo("type", type)
                    .findAll();

        } else if(Constants.SELECTED_TAB_STATS == Constants.MONTHLY) {
            calendar.set(Calendar.DAY_OF_MONTH,0);

            Date startTime = calendar.getTime();


            calendar.add(Calendar.MONTH,1);
            Date endTime = calendar.getTime();

//            query = Utility.getTransactions()
//                    .whereEqualTo("type", type)
//                    .whereGreaterThanOrEqualTo("date", startTime)
//                    .whereLessThan("date", endTime);
//            options = new FirestoreRecyclerOptions.Builder<Transaction>()
//                    .setQuery(query, Transaction.class)
//                    .build();
//            query.get().addOnCompleteListener(
//                    task -> {
//                        if (task.isSuccessful()) {
//                            newTransactions = task.getResult().getDocuments();
//                        } else {
//                            Exception e = task.getException();
//                            e.printStackTrace();
//                        }
//                    }
//            );
            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", type)
                    .findAll();
        } else if(Constants.SELECTED_TAB == Constants.YEARLY) {
            calendar.set(Calendar.DAY_OF_YEAR, 0);

            Date startTime = calendar.getTime();

            calendar.add(Calendar.YEAR, 1);
            Date endTime = calendar.getTime();

            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .findAll();
        }
        categoriesTransactions.setValue(newTransactions);
    }

    public void getTransactions(Calendar calendar) {
        this.calendar = calendar;
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if(Constants.SELECTED_TAB == Constants.DAILY) {
//            query = Utility.getTransactions()
//                    .whereGreaterThanOrEqualTo("date", calendar.getTime())
//                    .whereLessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)));
//            options = new FirestoreRecyclerOptions.Builder<Transaction>()
//                    .setQuery(query, Transaction.class)
//                    .build();
//            query.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    System.out.println("QUERY CHECKER DAILY: " + task.getResult().getDocuments().size());
//                    newTransactions = task.getResult().getDocuments();
//
//                    for (DocumentSnapshot document : newTransactions) {
//                        Transaction transaction = document.toObject(Transaction.class);
//                        assert transaction != null;
//                        if (transaction.getType().equals(Constants.INCOME)) {
//                            income += transaction.getAmount();
//                        } else {
//                            expense += transaction.getAmount();
//                        }
//                        total+= transaction.getAmount();
//                    }
//                } else {
//                    Exception e = task.getException();
//                    e.printStackTrace();
//                }
//            });
            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .findAll();

            income = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .equalTo("type", Constants.INCOME)
                    .sum("amount")
                    .intValue();

            expense = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .equalTo("type", Constants.EXPENSE)
                    .sum("amount")
                    .intValue();

            total = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .sum("amount")
                    .intValue();
        } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {
            calendar.set(Calendar.DAY_OF_MONTH,0);

            Date startTime = calendar.getTime();

            calendar.add(Calendar.MONTH,1);
            Date endTime = calendar.getTime();

//            query = Utility.getTransactions()
//                    .whereGreaterThanOrEqualTo("date", startTime)
//                    .whereLessThan("date", endTime);
//            options = new FirestoreRecyclerOptions.Builder<Transaction>()
//                    .setQuery(query, Transaction.class)
//                    .build();
//            query.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    newTransactions = task.getResult().getDocuments();
//                    System.out.println("QUERY CHECKER MONTH: " + task.getResult().getDocuments().size());
//                    for (DocumentSnapshot document : newTransactions) {
//                        Transaction transaction = document.toObject(Transaction.class);
//                        assert transaction != null;
//                        if (transaction.getType().equals(Constants.INCOME)) {
//                            income += transaction.getAmount();
//                        } else {
//                            expense += transaction.getAmount();
//                        }
//                        total += transaction.getAmount();
//                    }
//                } else {
//                    Exception e = task.getException();
//                    e.printStackTrace();
//                }
//            });
            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .findAll();

            income = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", Constants.INCOME)
                    .sum("amount")
                    .intValue();

            expense = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", Constants.EXPENSE)
                    .sum("amount")
                    .intValue();

            total = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .sum("amount")
                    .intValue();
        } else if(Constants.SELECTED_TAB == Constants.YEARLY) {
            calendar.set(Calendar.DAY_OF_YEAR,0);

            Date startTime = calendar.getTime();

            calendar.add(Calendar.YEAR,1);
            Date endTime = calendar.getTime();

            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .findAll();

            income = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", Constants.INCOME)
                    .sum("amount")
                    .intValue();

            expense = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", Constants.EXPENSE)
                    .sum("amount")
                    .intValue();

            total = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .sum("amount")
                    .intValue();
        }

        totalIncome.setValue(income);
        totalExpense.setValue(expense);
        totalAmount.setValue(total);
        transactions.setValue(newTransactions);

        income = 0;
        expense = 0;
        total = 0;
    }

    public void addTransaction(Transaction transaction) {
//        DocumentReference documentReference = Utility.getTransactions().document();
//        documentReference.set(transaction).addOnCompleteListener(
//                task -> {
//                    if (task.isSuccessful()) {
//                        Utility.showToast(getApplication().getApplicationContext(),
//                                "Thêm chi tiêu/thu nhập thành công");
//                        getTransactions(calendar);
//                    } else {
//                        Utility.showToast(getApplication().getApplicationContext(),
//                                "Thêm chi tiêu/thu nhập thất bại");
//                    }
//                }
//        );
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(transaction);
        // some code here
        realm.commitTransaction();
        Utility.showToast(getApplication().getApplicationContext(), "Thêm chi tiêu/thu nhập thành công");
    }

    public void updateTransaction(Transaction transaction, String docId) {
//        DocumentReference documentReference = Utility.getTransactions().document(docId);
//        documentReference.set(transaction).addOnCompleteListener(
//                task -> {
//                    if (task.isSuccessful()) {
//                        Utility.showToast(getApplication().getApplicationContext(),
//                                "Sửa chi tiêu/thu nhập thành công");
//                        getTransactions(calendar);
//                    } else {
//                        Utility.showToast(getApplication().getApplicationContext(),
//                                "Sửa chi tiêu/thu nhập thất bại");
//                    }
//                }
//
//        );
//        getTransactions(calendar);
        Utility.showToast(getApplication().getApplicationContext(), "Sửa chi tiêu/thu nhập thành công");
    }

    public void deleteTransaction(Transaction transaction) {
//        DocumentReference documentReference = Utility.getTransactions().document(docId);
//        documentReference.delete().addOnCompleteListener(
//                task -> {
//                    if (task.isSuccessful()) {
//                        Utility.showToast(getApplication().getApplicationContext(),
//                                "Xóa chi tiêu/thu nhập thành công");
//                        getTransactions(calendar);
//                    } else {
//                        Utility.showToast(getApplication().getApplicationContext(),
//                                "Xóa chi tiêu/thu nhập thất bại");
//                    }
//                }
//
//        );
        realm.beginTransaction();
        transaction.deleteFromRealm();
        realm.commitTransaction();
        Utility.showToast(getApplication().getApplicationContext(), "Xóa chi tiêu/thu nhập thành công");
        getTransactions(calendar);
    }

    void setupDatabase() {
//        Realm config if somethings changed
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(new QLTCMigration())
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }

}
