package com.example.tyfw.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tyfw.R;


public class WalletProfileActivity extends AppCompatActivity {
    private TextView walletAddr;

    private ImageView profilePic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_profile);

        walletAddr = findViewById(R.id.profile_wallet_addr);

        walletAddr.setText(getIntent().getStringExtra("walletAddr"));

        profilePic = findViewById(R.id.profile_pic_wallet);
        profilePic.setVisibility(1);
    }
}