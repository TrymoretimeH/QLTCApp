package com.example.qltc.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.qltc.models.Transaction;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class Utility {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static CollectionReference getTransactionsFromCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        return FirebaseFirestore.getInstance().collection("qltc")
                .document(user.getUid()).collection("transactions");
    }

    public static void deleteTransaction(Transaction transaction, String docId, Context context) {
        DocumentReference documentReference = Utility.getTransactionsFromCurrentUser().document(docId);
        documentReference.delete().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        Utility.showToast(context.getApplicationContext(),
                                "Xóa chi tiêu/thu nhập thành công");
                    } else {
                        Utility.showToast(context.getApplicationContext(),
                                "Xóa chi tiêu/thu nhập thất bại");
                    }
                }

        );
    }

    public static void addTransaction(Transaction transaction, Context context) {
        DocumentReference documentReference = getTransactionsFromCurrentUser().document();
        transaction.setId(documentReference.getId());
        documentReference.set(transaction).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        Utility.showToast(context.getApplicationContext(),
                                "Thêm chi tiêu/thu nhập thành công");
                    } else {
                        Utility.showToast(context.getApplicationContext(),
                                "Thêm chi tiêu/thu nhập thất bại");
                    }
                }
        );
    }

    public static void updateTransaction(Transaction transaction, String docId, Context context) {
        DocumentReference documentReference = getTransactionsFromCurrentUser().document(docId);
        documentReference.set(transaction).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        Utility.showToast(context.getApplicationContext(),
                                "Cập nhật chi tiêu/thu nhập thành công");
                    } else {
                        Utility.showToast(context.getApplicationContext(),
                                "Cập nhật chi tiêu/thu nhập thất bại");
                    }
                }
        );
    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("dd/MM/yyyy").format(timestamp.toDate());
    }

    public static void signOut(Context context) {
        FirebaseAuth.getInstance().signOut();
        showToast(context, "Đã đăng xuất");
    }
}
