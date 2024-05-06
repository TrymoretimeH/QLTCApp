package com.example.qltc.views.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qltc.R;
import com.example.qltc.utils.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText eEmail, ePassword, eConfirmPassword;
    private Button btCreateAccount;
    private ProgressBar pbCreateAccount;
    private TextView tvLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initView();
        btCreateAccount.setOnClickListener(v -> createAccount());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void createAccount() {
        String email = eEmail.getText().toString();
        String pass = ePassword.getText().toString();
        String cPass = eConfirmPassword.getText().toString();

        boolean isValidated = validateData(email, pass, cPass);
        if (!isValidated) {
            return;
        }

        createAccountInFireBase(email,pass);
    }

    private void createAccountInFireBase(String email, String pass) {
        changeInProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                          account created
                            Utility.showToast(CreateAccountActivity.this,
                                    "Tạo tài khoản thành công, kiểm tra email để xác thực!");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();

                            finish();
                        } else {
//                          failure
                            Utility.showToast(CreateAccountActivity.this,
                                    task.getException().getLocalizedMessage());
                        }
                    }
                });

    }

    private void changeInProgress(boolean inProgress) {
        if (inProgress) {
            pbCreateAccount.setVisibility(View.VISIBLE);
            btCreateAccount.setVisibility(View.GONE);
        } else {
            btCreateAccount.setVisibility(View.VISIBLE);
            pbCreateAccount.setVisibility(View.GONE);
        }
    }

    private boolean validateData(String email, String pass, String cPass) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eEmail.setError("Email không hợp lệ!");
            return false;
        }

        if (pass.length()<6) {
            ePassword.setError("Mật khẩu chưa đủ dài!");
            return false;
        }

        if (!pass.equals(cPass)) {
            eConfirmPassword.setError("Mật khẩu nhập lại chưa chính xác!");
            return false;
        }
        return true;
    }

    private void initView() {
        eEmail = findViewById(R.id.eEmail);
        ePassword = findViewById(R.id.ePassword);
        eConfirmPassword = findViewById(R.id.eConfirmPassword);
        btCreateAccount = findViewById(R.id.btCreateAccount);
        pbCreateAccount = findViewById(R.id.pbCreateAccount);
        tvLogin = findViewById(R.id.tvLogin);

    }
}