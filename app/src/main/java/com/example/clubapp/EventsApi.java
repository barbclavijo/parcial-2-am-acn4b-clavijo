package com.example.clubapp;

import android.content.Context;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EventsApi {

    public interface Listener {
        void onSuccess(List<Event> events);
        void onError();
    }

    private static final OkHttpClient client = new OkHttpClient();

    // los callbacks se ejecutan en un hilo de fondo: la Activity debe pasar a la UI con runOnUiThread
    public static void fetch(String url, Listener listener) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError();
            }

            @Override
            public void onResponse(Call call, Response response) {
                List<Event> events = null;
                try (Response r = response) {
                    if (r.isSuccessful() && r.body() != null) {
                        events = parseEvents(r.body().string());
                    }
                } catch (Exception e) {
                    events = null;
                }
                if (events == null) {
                    listener.onError();
                } else {
                    listener.onSuccess(events);
                }
            }
        });
    }

    public static List<Event> parseEvents(String json) {
        EventsResponse response = new Gson().fromJson(json, EventsResponse.class);
        if (response == null || response.getEvents() == null) {
            return new ArrayList<>();
        }
        return response.getEvents();
    }

    public static String readRaw(Context context, int resId) throws IOException {
        try (InputStream in = context.getResources().openRawResource(resId);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return out.toString(StandardCharsets.UTF_8.name());
        }
    }
}