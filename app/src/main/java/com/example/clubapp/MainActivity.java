package com.example.clubapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String SOURCE_EVENT_1 = "event_padel";
    private static final String SOURCE_EVENT_2 = "event_asado";
    private LinearLayout containerRegistrations;
    private TextView tvEmptyState;
    private Button eventButton1;
    private Button eventButton2;
    private ImageView ivMemberPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvMemberName = findViewById(R.id.tv_member_name);
        String memberNameFromLogin = getIntent().getStringExtra(LoginActivity.EXTRA_MEMBER_NAME);
        if (memberNameFromLogin != null) {
            tvMemberName.setText(memberNameFromLogin);
        }

        containerRegistrations = findViewById(R.id.container_registrations);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        ivMemberPhoto = findViewById(R.id.iv_member_photo);
        eventButton1 = findViewById(R.id.btn_join_event_1);
        eventButton1.setOnClickListener(v -> onJoinEventClick(
                SOURCE_EVENT_1,
                eventButton1,
                getString(R.string.event_name_1),
                getString(R.string.event_date_1) + " · " + getString(R.string.event_location_1)
        ));

        eventButton2 = findViewById(R.id.btn_join_event_2);
        eventButton2.setOnClickListener(v -> onJoinEventClick(
                SOURCE_EVENT_2,
                eventButton2,
                getString(R.string.event_name_2),
                getString(R.string.event_date_2) + " · " + getString(R.string.event_location_2)
        ));

        findViewById(R.id.nav_activities).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ActividadesActivity.class))
        );
        findViewById(R.id.nav_events).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EventosActivity.class))
        );
        findViewById(R.id.nav_profile).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, PerfilActivity.class))
        );
        findViewById(R.id.iv_profile_icon).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRegistrationsList();
        syncEventButtonState(eventButton1, SOURCE_EVENT_1);
        syncEventButtonState(eventButton2, SOURCE_EVENT_2);
        ProfilePhoto.applyTo(this, ivMemberPhoto);
    }

    private void onJoinEventClick(String sourceId, Button button, String name, String detail) {
        AppState.getInstance().addRegistration(sourceId, name, detail);
        refreshRegistrationsList();
        markButtonAsJoined(button);
    }

    private void refreshRegistrationsList() {
        containerRegistrations.removeAllViews();

        List<AppState.Registration> registrations = AppState.getInstance().getRegistrations();
        for (AppState.Registration registration : registrations) {
            containerRegistrations.addView(buildRegistrationCard(registration));
        }
        updateEmptyState();
    }

    private View buildRegistrationCard(AppState.Registration registration) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(32, 24, 32, 24);
        card.setBackgroundColor(getResources().getColor(R.color.white));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16);
        card.setLayoutParams(cardParams);

        TextView nameTextView = new TextView(this);
        nameTextView.setText(registration.name);
        nameTextView.setTextColor(getResources().getColor(R.color.text_primary));
        nameTextView.setTextSize(16);
        nameTextView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView detailTextView = new TextView(this);
        detailTextView.setText(registration.detail);
        detailTextView.setTextColor(getResources().getColor(R.color.text_secondary));
        detailTextView.setTextSize(14);

        TextView ticketTextView = new TextView(this);
        ticketTextView.setText("Ticket ID: " + registration.ticketId);
        ticketTextView.setTextColor(getResources().getColor(R.color.accent_darker));
        ticketTextView.setTextSize(12);

        Button cancelButton = new Button(this);
        cancelButton.setText(R.string.btn_cancel);
        cancelButton.setOnClickListener(v -> {
            AppState.getInstance().removeRegistration(registration.sourceId);
            refreshRegistrationsList();
            reactivateIfLocalButton(registration.sourceId);
        });

        card.addView(nameTextView);
        card.addView(detailTextView);
        card.addView(ticketTextView);
        card.addView(cancelButton);

        return card;
    }

    private void reactivateIfLocalButton(String sourceId) {
        if (SOURCE_EVENT_1.equals(sourceId)) {
            resetButton(eventButton1);
        } else if (SOURCE_EVENT_2.equals(sourceId)) {
            resetButton(eventButton2);
        }
    }

    private void syncEventButtonState(Button button, String sourceId) {
        if (AppState.getInstance().isRegistered(sourceId)) {
            markButtonAsJoined(button);
        } else {
            resetButton(button);
        }
    }

    private void markButtonAsJoined(Button button) {
        button.setEnabled(false);
        button.setText(R.string.btn_joined);
        button.setBackgroundTintList(getResources().getColorStateList(R.color.accent_darker));
    }

    private void resetButton(Button button) {
        button.setEnabled(true);
        button.setText(R.string.btn_join_event);
        button.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
    }

    private void updateEmptyState() {
        if (containerRegistrations.getChildCount() == 0) {
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
        }
    }
}