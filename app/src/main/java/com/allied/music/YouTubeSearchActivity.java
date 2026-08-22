package com.allied.music;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class YouTubeSearchActivity extends Activity {

    private EditText searchBox;
    private Button searchButton;
    private LinearLayout results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        searchBox = new EditText(this);
        searchBox.setHint("Search YouTube videos");

        searchButton = new Button(this);
        searchButton.setText("🔎 Search");

        results = new LinearLayout(this);
        results.setOrientation(LinearLayout.VERTICAL);

        layout.addView(searchBox);
        layout.addView(searchButton);
        layout.addView(results);

        setContentView(layout);

        searchButton.setOnClickListener(v -> searchYouTube());
    }

    private void searchYouTube() {

        String query = searchBox.getText().toString().trim();

        if (query.isEmpty()) {
            searchBox.setError("Enter a video name");
            return;
        }

        results.removeAllViews();

        TextView message = new TextView(this);
        message.setText(
                "Searching YouTube for:\n\n" + query
        );
        message.setTextSize(18);

        results.addView(message);
    }
}
