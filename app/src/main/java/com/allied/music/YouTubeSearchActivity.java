package com.allied.music;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

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
            "AIzaSyAYrOmSmLjS4iuRssfE1HzjIsAch9NAivE";

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

        searchButton.setText("🔍 SEARCH");

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
                    searchBox.getText()
                            .toString()
                            .trim();

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

            HttpURLConnection connection = null;

            try {

                String encodedQuery =
                        URLEncoder.encode(
                                query,
                                "UTF-8"
                        );

                String urlString =
                        "https://www.googleapis.com/youtube/v3/search"
                                + "?part=snippet"
                                + "&q=" + encodedQuery
                                + "&type=video"
                                + "&maxResults=10"
                                + "&key=" + API_KEY;

                URL url =
                        new URL(urlString);

                connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setRequestMethod("GET");

                connection.setConnectTimeout(10000);

                connection.setReadTimeout(10000);

                int responseCode =
                        connection.getResponseCode();

                if (responseCode != 200) {

                    throw new Exception(
                            "YouTube API error: "
                                    + responseCode
                    );
                }

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

                            JSONObject id =
                                    item.getJSONObject(
                                            "id"
                                    );

                            String videoId =
                                    id.getString(
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
                                    "▶ "
                                            + videoTitle
                                            + "\n\n"
                                            + channel
                                            + "\n\n"
                                            + "Tap to play"
                            );

                            result.setTextSize(17);

                            result.setPadding(
                                    20,
                                    25,
                                    20,
                                    25
                            );

                            result.setClickable(true);

                            result.setFocusable(true);

                            /*
                             * PLAY VIDEO
                             */
                            result.setOnClickListener(
                                    v -> {

                                        String youtubeUrl =
                                                "https://www.youtube.com/watch?v="
                                                        + videoId;

                                        Intent intent =
                                                new Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(
                                                                youtubeUrl
                                                        )
                                                );

                                        startActivity(intent);
                                    }
                            );

                            resultsLayout.addView(
                                    result
                            );
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

                    resultsLayout.addView(
                            error
                    );
                });

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }

        }).start();
    }
}