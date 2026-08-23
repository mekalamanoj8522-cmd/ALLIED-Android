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
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 30, 30, 30);

        TextView title = new TextView(this);
        title.setText("ALLIED YouTube");
        title.setTextSize(28);
        title.setPadding(0, 20, 0, 30);

        searchBox = new EditText(this);
        searchBox.setHint("Search YouTube music...");

        searchButton = new Button(this);
        searchButton.setText("🔍 Search");

        resultText = new TextView(this);
        resultText.setTextSize(18);
        resultText.setPadding(0, 30, 0, 0);

        layout.addView(title);
        layout.addView(searchBox);
        layout.addView(searchButton);
        layout.addView(resultText);

        setContentView(layout);

        searchButton.setOnClickListener(v -> {

            String query = searchBox.getText().toString().trim();

            if (query.isEmpty()) {
                resultText.setText("Please enter something to search.");
            } else {
                resultText.setText(
                        "Searching YouTube for:\n\n" + query
                );
            }
        });
    }
}