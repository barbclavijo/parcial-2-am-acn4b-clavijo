package com.example.clubapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class EventosActivity extends AppCompatActivity {

    private static final String CATEGORY_ALL = "all";
    private static final String CATEGORY_SPORTS = "sports";
    private static final String CATEGORY_SOCIAL = "social";

    private EditText etSearch;
    private TextView tvEmptySearch;
    private TextView tvStatus;
    private Button btnRetry;
    private LinearLayout containerEvents;

    private Button btnFilterAll;
    private Button btnFilterSports;
    private Button btnFilterSocial;

    private final List<View> eventCards = new ArrayList<>();
    private String currentFilter = CATEGORY_ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        etSearch = findViewById(R.id.et_search_event);
        tvEmptySearch = findViewById(R.id.tv_empty_search_events);
        tvStatus = findViewById(R.id.tv_events_status);
        btnRetry = findViewById(R.id.btn_retry);
        containerEvents = findViewById(R.id.container_events_list);

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

        btnRetry.setOnClickListener(v -> loadEvents());

        findViewById(R.id.nav_home).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.nav_activities).setOnClickListener(v ->
                startActivity(new Intent(this, ActividadesActivity.class))
        );
        findViewById(R.id.nav_profile).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class))
        );

        loadEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (View card : eventCards) {
            Event event = (Event) card.getTag();
            syncButtonState(card.findViewById(R.id.btn_join_event), event.getId());
        }
    }

    private void loadEvents() {
        showStatus(getString(R.string.loading_events), false);
        EventsApi.fetch(getString(R.string.url_events), new EventsApi.Listener() {
            @Override
            public void onSuccess(List<Event> events) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    hideStatus();
                    showEvents(events);
                });
            }

            @Override
            public void onError() {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    loadFallbackEvents();
                });
            }
        });
    }

    // sin conexión: se usa la copia local guardada en res/raw
    private void loadFallbackEvents() {
        try {
            String json = EventsApi.readRaw(this, R.raw.events_fallback);
            showEvents(EventsApi.parseEvents(json));
            showStatus(getString(R.string.events_offline), true);
        } catch (Exception e) {
            showStatus(getString(R.string.error_loading_events), true);
        }
    }

    private void showStatus(String message, boolean showRetry) {
        tvStatus.setText(message);
        tvStatus.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(showRetry ? View.VISIBLE : View.GONE);
    }

    private void hideStatus() {
        tvStatus.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);
    }

    private void showEvents(List<Event> events) {
        containerEvents.removeAllViews();
        eventCards.clear();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Event event : events) {
            View card = inflater.inflate(R.layout.item_event, containerEvents, false);
            bindEventCard(card, event);
            card.setTag(event);
            eventCards.add(card);
            containerEvents.addView(card);
        }
        applyFilters();
    }

    private void bindEventCard(View card, Event event) {
        ((TextView) card.findViewById(R.id.tv_event_name)).setText(event.getName());
        ((TextView) card.findViewById(R.id.tv_event_description)).setText(event.getDescription());
        ((TextView) card.findViewById(R.id.tv_event_date)).setText(event.getDate());
        ((TextView) card.findViewById(R.id.tv_event_location)).setText(event.getLocation());
        ((TextView) card.findViewById(R.id.tv_event_price_label)).setText(event.getPriceLabel());
        ((TextView) card.findViewById(R.id.tv_event_price)).setText(event.getPrice());

        ImageView image = card.findViewById(R.id.iv_event_image);
        Glide.with(this)
                .load(event.getImageUrl())
                .placeholder(R.color.icon_bg)
                .error(R.color.icon_bg)
                .into(image);

        Button joinButton = card.findViewById(R.id.btn_join_event);
        String detail = event.getDate() + " · " + event.getLocation();
        joinButton.setOnClickListener(v -> {
            AppState.getInstance().addRegistration(event.getId(), event.getName(), detail);
            markButtonAsJoined(joinButton);
        });
        syncButtonState(joinButton, event.getId());
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

        for (View card : eventCards) {
            Event event = (Event) card.getTag();
            boolean matchesCategory = CATEGORY_ALL.equals(currentFilter)
                    || currentFilter.equals(event.getCategory());
            boolean matchesSearch = query.isEmpty()
                    || event.getName().toLowerCase().contains(query);
            boolean visible = matchesCategory && matchesSearch;

            card.setVisibility(visible ? View.VISIBLE : View.GONE);
            anyVisible |= visible;
        }

        tvEmptySearch.setVisibility(!eventCards.isEmpty() && !anyVisible ? View.VISIBLE : View.GONE);
    }
}