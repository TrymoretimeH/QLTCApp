package com.example.qltc.views.activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.qltc.R;
import com.example.qltc.utils.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText eEmail, ePassword;
    private Button btLogin;
    private ProgressBar pbLogin;
    private TextView tvSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        initView();

        btLogin.setOnClickListener(v -> loginUser());
        tvSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,
                CreateAccountActivity.class)));

    }

    private void loginUser() {
        String email = eEmail.getText().toString();
        String pass = ePassword.getText().toString();

        boolean isValidated = validateData(email, pass);
        if (!isValidated) {
            return;
        }

        loginAccountInFireBase(email,pass);
    }

    private void loginAccountInFireBase(String email, String pass) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()) {
//                    login successful
                    if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
//                        go to main activity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Utility.showToast(LoginActivity.this, "Email chưa được xác minh!");
                    }

                } else {
//                    login failed
                    Utility.showToast(LoginActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
                }
            }
        });
    }

    private void changeInProgress(boolean inProgress) {
        if (inProgress) {
            pbLogin.setVisibility(View.VISIBLE);
            btLogin.setVisibility(View.GONE);
        } else {
            btLogin.setVisibility(View.VISIBLE);
            pbLogin.setVisibility(View.GONE);
        }
    }

    private boolean validateData(String email, String pass) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eEmail.setError("Email không hợp lệ!");
            return false;
        }

        if (pass.length()<6) {
            ePassword.setError("Mật khẩu chưa đủ dài!");
            return false;
        }

        return true;
    }

    private void initView() {
        eEmail = findViewById(R.id.eEmail);
        ePassword = findViewById(R.id.ePassword);
        btLogin = findViewById(R.id.btLogin);
        pbLogin = findViewById(R.id.pbLogin);
        tvSignup = findViewById(R.id.tvSignup);
    }
}