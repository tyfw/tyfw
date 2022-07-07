package com.example.tyfw.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tyfw.App;
import com.example.tyfw.R;


public class ProfileActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private TextView walletAddr;

    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameTextView = findViewById(R.id.profile_username);

        usernameTextView.setText(getIntent().getStringExtra("username"));

        walletAddr = findViewById(R.id.wallet_address);

        walletAddr.setText(getIntent().getStringExtra("walletAddress"));

        profilePic = (ImageView) findViewById(R.id.profile_default_pic);

        profilePic.setImageResource(R.drawable.ic_baseline_people_24);
        profilePic.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
    }
}