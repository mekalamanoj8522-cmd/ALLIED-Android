package com.allied.music;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class YouTubeSearchActivity extends Activity {

    private EditText searchBox;
    private LinearLayout resultsLayout;

    private static final String API_KEY =
            "AIzaSyCbJKHTyKjOVTBQnPghTa1iUUqEjx_7Jl0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createScreen();
    }

    private void createScreen() {

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(30, 30, 30, 30);

        TextView title = new TextView(this);
        title.setText("ALLIED YouTube");
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 20, 0, 30);

        searchBox = new EditText(this);
        searchBox.setHint("Search YouTube...");
        searchBox.setSingleLine(true);

        Button searchButton = new Button(this);
        searchButton.setText("🔍 Search");

        resultsLayout = new LinearLayout(this);
        resultsLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        main.addView(title);
        main.addView(searchBox);
        main.addView(searchButton);
        main.addView(resultsLayout);

        setContentView(main);

        searchButton.setOnClickListener(v -> {

            String query =
                    searchBox.getText().toString().trim();

            if (query.isEmpty()) {

                Toast.makeText(
                        this,
                        "Enter something to search",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            searchYouTube(query);
        });
    }

    private void searchYouTube(String query) {

        resultsLayout.removeAllViews();

        TextView loading = new TextView(this);
        loading.setText("Searching YouTube...");
        loading.setTextSize(18);
        loading.setPadding(0, 30, 0, 30);

        resultsLayout.addView(loading);

        new Thread(() -> {

            try {

                String encodedQuery =
                        URLEncoder.encode(query, "UTF-8");

                String urlString =
                        "https://www.googleapis.com/youtube/v3/search"
                                + "?part=snippet"
                                + "&q=" + encodedQuery
                                + "&type=video"
                                + "&maxResults=10"
                                + "&key=" + API_KEY;

                URL url = new URL(urlString);

                HttpURLConnection connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream()
                                )
                        );

                StringBuilder response =
                        new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                JSONObject json =
                        new JSONObject(
                                response.toString()
                        );

                JSONArray items =
                        json.getJSONArray("items");

                runOnUiThread(() -> {

                    resultsLayout.removeAllViews();

                    try {

                        for (int i = 0;
                             i < items.length();
                             i++) {

                            JSONObject item =
                                    items.getJSONObject(i);

                            JSONObject snippet =
                                    item.getJSONObject(
                                            "snippet"
                                    );

                            String videoId =
                                    item.getJSONObject("id")
                                            .getString(
                                                    "videoId"
                                            );

                            String videoTitle =
                                    snippet.getString(
                                            "title"
                                    );

                            String channel =
                                    snippet.getString(
                                            "channelTitle"
                                    );

                            TextView result =
                                    new TextView(this);

                            result.setText(
                                    "▶ " + videoTitle
                                            + "\n"
                                            + channel
                                            + "\n"
                                            + "https://youtube.com/watch?v="
                                            + videoId
                            );

                            result.setTextSize(16);
                            result.setPadding(
                                    10,
                                    20,
                                    10,
                                    20
                            );

                            resultsLayout.addView(result);
                        }

                    } catch (Exception e) {

                        Toast.makeText(
                                this,
                                "Error reading results",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    resultsLayout.removeAllViews();

                    TextView error =
                            new TextView(this);

                    error.setText(
                            "Search failed:\n\n"
                                    + e.getMessage()
                    );

                    error.setTextSize(16);
                    error.setPadding(
                            0,
                            30,
                            0,
                            30
                    );

                    resultsLayout.addView(error);
                });
            }

        }).start();
    }
}