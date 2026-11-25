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

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_MEMBER_NAME = "extra_member_name";
    private static final String DEMO_EMAIL = "mariano@clubdeportivo.com";
    private static final String DEMO_PASSWORD = "club2026";
    private static final String DEMO_MEMBER_NAME = "Mariano Benítez";

    private EditText etEmail;
    private EditText etPassword;
    private LinearLayout containerLoginError;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        containerLoginError = findViewById(R.id.container_login_error);

        TextView togglePassword = findViewById(R.id.tv_toggle_password);
        togglePassword.setOnClickListener(v -> onTogglePasswordClick(togglePassword));

        Button loginButton = findViewById(R.id.btn_login);
        loginButton.setOnClickListener(v -> onLoginClick());

        TextView forgotPassword = findViewById(R.id.tv_forgot_password);
        forgotPassword.setOnClickListener(v ->
                Toast.makeText(this, R.string.nav_coming_soon, Toast.LENGTH_SHORT).show()
        );

        TextView newMember = findViewById(R.id.tv_new_member);
        newMember.setOnClickListener(v ->
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

        if (!email.equalsIgnoreCase(DEMO_EMAIL) || !password.equals(DEMO_PASSWORD)) {
            showError(getString(R.string.error_invalid_credentials));
            return;
        }

        CheckBox rememberSession = findViewById(R.id.cb_remember_session);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(EXTRA_MEMBER_NAME, DEMO_MEMBER_NAME);
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