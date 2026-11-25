package com.example.clubapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_MEMBER_NAME = "extra_member_name";
    public static final String EXTRA_MEMBER_EMAIL = "extra_member_email";

    public static final String PREFS_NAME = "club_prefs";
    public static final String PREF_REMEMBER_SESSION = "remember_session";

    private static final String DEFAULT_MEMBER_NAME = "Mariano Benítez";

    private FirebaseAuth mAuth;

    private EditText etEmail;
    private EditText etPassword;
    private CheckBox cbRememberSession;
    private Button loginButton;
    private LinearLayout containerLoginError;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // si ya hay sesión y el socio pidió recordarla, entra directo a Inicio
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            boolean remember = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .getBoolean(PREF_REMEMBER_SESSION, false);
            if (remember) {
                goToHome(currentUser);
                return;
            }
            mAuth.signOut();
        }

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        cbRememberSession = findViewById(R.id.cb_remember_session);
        loginButton = findViewById(R.id.btn_login);
        containerLoginError = findViewById(R.id.container_login_error);

        TextView togglePassword = findViewById(R.id.tv_toggle_password);
        togglePassword.setOnClickListener(v -> onTogglePasswordClick(togglePassword));

        loginButton.setOnClickListener(v -> onLoginClick());

        findViewById(R.id.tv_forgot_password).setOnClickListener(v -> onForgotPasswordClick());

        findViewById(R.id.tv_new_member).setOnClickListener(v ->
                Toast.makeText(this, R.string.nav_coming_soon, Toast.LENGTH_SHORT).show()
        );
    }

    private void onTogglePasswordClick(TextView toggle) {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggle.setText(R.string.btn_toggle_password_hide);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggle.setText(R.string.btn_toggle_password_show);
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void onLoginClick() {
        containerLoginError.removeAllViews();

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.error_empty_fields));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(getString(R.string.error_invalid_email));
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText(R.string.logging_in);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loginButton.setEnabled(true);
                    loginButton.setText(R.string.btn_login);

                    if (task.isSuccessful()) {
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                                .edit()
                                .putBoolean(PREF_REMEMBER_SESSION, cbRememberSession.isChecked())
                                .apply();
                        goToHome(mAuth.getCurrentUser());
                    } else if (task.getException() instanceof FirebaseNetworkException) {
                        showError(getString(R.string.error_network));
                    } else {
                        showError(getString(R.string.error_invalid_credentials));
                    }
                });
    }

    private void onForgotPasswordClick() {
        containerLoginError.removeAllViews();
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(getString(R.string.error_enter_email_first));
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, R.string.reset_email_sent, Toast.LENGTH_LONG).show();
            } else {
                showError(getString(R.string.error_network));
            }
        });
    }

    private void goToHome(FirebaseUser user) {
        String name = user.getDisplayName();
        if (name == null || name.isEmpty()) {
            name = DEFAULT_MEMBER_NAME;
        }

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(EXTRA_MEMBER_NAME, name);
        intent.putExtra(EXTRA_MEMBER_EMAIL, user.getEmail());
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        TextView errorTextView = new TextView(this);
        errorTextView.setText(message);
        errorTextView.setTextColor(getResources().getColor(R.color.error));
        errorTextView.setTextSize(13);
        containerLoginError.addView(errorTextView);
    }
}