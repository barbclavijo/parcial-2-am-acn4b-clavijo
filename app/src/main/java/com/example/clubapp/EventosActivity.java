package com.example.clubapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventosActivity extends AppCompatActivity {

    private static final String CATEGORY_ALL = "all";
    private static final String CATEGORY_SPORTS = "sports";
    private static final String CATEGORY_SOCIAL = "social";

    private static final String SOURCE_EVENT_1 = "event_padel";
    private static final String SOURCE_EVENT_2 = "event_asado";

    private EditText etSearch;
    private TextView tvEmptySearch;

    private View cardEvent1;
    private View cardEvent2;

    private Button btnFilterAll;
    private Button btnFilterSports;
    private Button btnFilterSocial;

    private Button joinButton1;
    private Button joinButton2;

    private String currentFilter = CATEGORY_ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        etSearch = findViewById(R.id.et_search_event);
        tvEmptySearch = findViewById(R.id.tv_empty_search_events);

        cardEvent1 = findViewById(R.id.card_event_1);
        cardEvent1.setTag(CATEGORY_SPORTS);

        cardEvent2 = findViewById(R.id.card_event_2);
        cardEvent2.setTag(CATEGORY_SOCIAL);

        btnFilterAll = findViewById(R.id.btn_filter_events_all);
        btnFilterSports = findViewById(R.id.btn_filter_events_sports);
        btnFilterSocial = findViewById(R.id.btn_filter_events_social);

        btnFilterAll.setOnClickListener(v -> onFilterSelected(CATEGORY_ALL));
        btnFilterSports.setOnClickListener(v -> onFilterSelected(CATEGORY_SPORTS));
        btnFilterSocial.setOnClickListener(v -> onFilterSelected(CATEGORY_SOCIAL));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        joinButton1 = setupJoinButton(
                R.id.btn_join_event_1,
                SOURCE_EVENT_1,
                getString(R.string.event_name_1),
                getString(R.string.event_date_1) + " · " + getString(R.string.event_location_1)
        );

        joinButton2 = setupJoinButton(
                R.id.btn_join_event_2,
                SOURCE_EVENT_2,
                getString(R.string.event_name_2),
                getString(R.string.event_date_2) + " · " + getString(R.string.event_location_2)
        );

        findViewById(R.id.nav_home).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });        findViewById(R.id.nav_activities).setOnClickListener(v ->
                startActivity(new Intent(this, ActividadesActivity.class))
        );
        findViewById(R.id.nav_profile).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncButtonState(joinButton1, SOURCE_EVENT_1);
        syncButtonState(joinButton2, SOURCE_EVENT_2);
    }

    private Button setupJoinButton(int buttonId, String sourceId, String name, String detail) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            AppState.getInstance().addRegistration(sourceId, name, detail);
            markButtonAsJoined(button);
        });
        return button;
    }

    private void syncButtonState(Button button, String sourceId) {
        if (AppState.getInstance().isRegistered(sourceId)) {
            markButtonAsJoined(button);
        } else {
            resetJoinButton(button);
        }
    }

    private void markButtonAsJoined(Button button) {
        button.setEnabled(false);
        button.setText(R.string.btn_joined);
        button.setBackgroundTintList(getResources().getColorStateList(R.color.accent_darker));
    }

    private void resetJoinButton(Button button) {
        button.setEnabled(true);
        button.setText(R.string.btn_join_event);
        button.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
    }

    private void onFilterSelected(String filter) {
        currentFilter = filter;
        updateFilterButtonStyles();
        applyFilters();
    }

    private void updateFilterButtonStyles() {
        setFilterButtonActive(btnFilterAll, CATEGORY_ALL.equals(currentFilter));
        setFilterButtonActive(btnFilterSports, CATEGORY_SPORTS.equals(currentFilter));
        setFilterButtonActive(btnFilterSocial, CATEGORY_SOCIAL.equals(currentFilter));
    }

    private void setFilterButtonActive(Button button, boolean active) {
        if (active) {
            button.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
            button.setTextColor(getResources().getColor(R.color.white));
        } else {
            button.setBackgroundTintList(getResources().getColorStateList(R.color.icon_bg));
            button.setTextColor(getResources().getColor(R.color.primary));
        }
    }

    private void applyFilters() {
        String query = etSearch.getText().toString().trim().toLowerCase();
        boolean anyVisible = false;

        anyVisible |= applyFilterToCard(cardEvent1, R.id.tv_event_1_name, query);
        anyVisible |= applyFilterToCard(cardEvent2, R.id.tv_event_2_name, query);

        tvEmptySearch.setVisibility(anyVisible ? View.GONE : View.VISIBLE);
    }

    private boolean applyFilterToCard(View card, int nameViewId, String query) {
        String category = (String) card.getTag();
        TextView nameTextView = findViewById(nameViewId);
        String name = nameTextView.getText().toString().toLowerCase();

        boolean matchesCategory = CATEGORY_ALL.equals(currentFilter) || category.equals(currentFilter);
        boolean matchesSearch = query.isEmpty() || name.contains(query);
        boolean visible = matchesCategory && matchesSearch;

        card.setVisibility(visible ? View.VISIBLE : View.GONE);
        return visible;
    }
}