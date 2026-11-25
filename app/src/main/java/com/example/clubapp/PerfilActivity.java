package com.example.clubapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    private ImageView ivProfilePhoto;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImagePicked);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        ivProfilePhoto = findViewById(R.id.iv_profile_photo);
        ProfilePhoto.applyTo(this, ivProfilePhoto);

        ivProfilePhoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        findViewById(R.id.tv_change_photo).setOnClickListener(v -> pickImageLauncher.launch("image/*"));

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
        });
        findViewById(R.id.nav_activities).setOnClickListener(v ->
                startActivity(new Intent(this, ActividadesActivity.class))
        );
        findViewById(R.id.nav_events).setOnClickListener(v ->
                startActivity(new Intent(this, EventosActivity.class))
        );
    }

    private void onImagePicked(Uri uri) {
        if (uri == null) {
            return; // canceló la selección
        }
        if (ProfilePhoto.save(this, uri)) {
            ProfilePhoto.applyTo(this, ivProfilePhoto);
            Toast.makeText(this, R.string.photo_updated, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void onLogoutClick() {
        Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}