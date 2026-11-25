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

public class ActividadesActivity extends AppCompatActivity {

    private static final String CATEGORY_ALL = "all";
    private static final String CATEGORY_SPORTS = "sports";
    private static final String CATEGORY_AQUATIC = "aquatic";

    private EditText etSearch;
    private TextView tvEmptySearch;

    private View cardActivity1;
    private View cardActivity2;

    private Button btnFilterAll;
    private Button btnFilterSports;
    private Button btnFilterAquatic;

    private String currentFilter = CATEGORY_ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividades);

        etSearch = findViewById(R.id.et_search_activity);
        tvEmptySearch = findViewById(R.id.tv_empty_search);

        cardActivity1 = findViewById(R.id.card_activity_1);
        cardActivity1.setTag(CATEGORY_SPORTS);

        cardActivity2 = findViewById(R.id.card_activity_2);
        cardActivity2.setTag(CATEGORY_AQUATIC);

        btnFilterAll = findViewById(R.id.btn_filter_all);
        btnFilterSports = findViewById(R.id.btn_filter_sports);
        btnFilterAquatic = findViewById(R.id.btn_filter_aquatic);

        btnFilterAll.setOnClickListener(v -> onFilterSelected(CATEGORY_ALL));
        btnFilterSports.setOnClickListener(v -> onFilterSelected(CATEGORY_SPORTS));
        btnFilterAquatic.setOnClickListener(v -> onFilterSelected(CATEGORY_AQUATIC));

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

        setupReserveButton(R.id.btn_view_activity_1);
        setupReserveButton(R.id.btn_view_activity_2);

        findViewById(R.id.nav_home).setOnClickListener(v -> finish());
        findViewById(R.id.nav_events).setOnClickListener(v ->
                startActivity(new android.content.Intent(this, EventosActivity.class))
        );
        findViewById(R.id.nav_profile).setOnClickListener(v ->
                Toast.makeText(this, R.string.nav_coming_soon, Toast.LENGTH_SHORT).show()
        );
    }

    private void setupReserveButton(int buttonId) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            button.setEnabled(false);
            button.setText(R.string.btn_reserved);
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
        setFilterButtonActive(btnFilterAquatic, CATEGORY_AQUATIC.equals(currentFilter));
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

        anyVisible |= applyFilterToCard(cardActivity1, query);
        anyVisible |= applyFilterToCard(cardActivity2, query);

        tvEmptySearch.setVisibility(anyVisible ? View.GONE : View.VISIBLE);
    }

    private boolean applyFilterToCard(View card, String query) {
        String category = (String) card.getTag();
        TextView nameTextView = findNameTextView(card);
        String name = nameTextView != null ? nameTextView.getText().toString().toLowerCase() : "";

        boolean matchesCategory = CATEGORY_ALL.equals(currentFilter) || category.equals(currentFilter);
        boolean matchesSearch = query.isEmpty() || name.contains(query);
        boolean visible = matchesCategory && matchesSearch;

        card.setVisibility(visible ? View.VISIBLE : View.GONE);
        return visible;
    }

    private TextView findNameTextView(View card) {
        if (card == cardActivity1) {
            return findViewById(R.id.tv_activity_1_name);
        }
        return findViewById(R.id.tv_activity_2_name);
    }
}