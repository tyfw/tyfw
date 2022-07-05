package com.example.tyfw.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.tyfw.R;


public class ProfileActivity extends AppCompatActivity {
    private TextView usernameTextView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameTextView = findViewById(R.id.profile_username);

        usernameTextView.setText(getIntent().getStringExtra("username"));
        
    }
}