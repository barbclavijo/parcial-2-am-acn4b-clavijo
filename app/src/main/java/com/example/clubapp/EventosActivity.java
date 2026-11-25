package com.example.clubapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EventosActivity extends AppCompatActivity {

    private static final String CATEGORY_ALL = "all";
    private static final String CATEGORY_SPORTS = "sports";
    private static final String CATEGORY_SOCIAL = "social";

    private EditText etSearch;
    private TextView tvEmptySearch;

    private View cardEvent1;
    private View cardEvent2;

    private Button btnFilterAll;
    private Button btnFilterSports;
    private Button btnFilterSocial;

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

        setupJoinButton(R.id.btn_join_event_1);
        setupJoinButton(R.id.btn_join_event_2);

        findViewById(R.id.nav_home).setOnClickListener(v -> finish());
        findViewById(R.id.nav_activities).setOnClickListener(v ->
                startActivity(new android.content.Intent(this, ActividadesActivity.class))
        );
        findViewById(R.id.nav_profile).setOnClickListener(v ->
                Toast.makeText(this, R.string.nav_coming_soon, Toast.LENGTH_SHORT).show()
        );
    }

    private void setupJoinButton(int buttonId) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            button.setEnabled(false);
            button.setText(R.string.btn_joined);
            button.setBackgroundTintList(getResources().getColorStateList(R.color.accent_darker));
        });
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