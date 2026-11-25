package com.example.clubapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        TextView editData = findViewById(R.id.tv_edit_data);
        editData.setOnClickListener(v ->
                Toast.makeText(this, R.string.nav_coming_soon, Toast.LENGTH_SHORT).show()
        );

        Button logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> onLogoutClick());

        findViewById(R.id.nav_home).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });        findViewById(R.id.nav_activities).setOnClickListener(v ->
                startActivity(new Intent(this, ActividadesActivity.class))
        );
        findViewById(R.id.nav_events).setOnClickListener(v ->
                startActivity(new Intent(this, EventosActivity.class))
        );
    }

    private void onLogoutClick() {
        Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}