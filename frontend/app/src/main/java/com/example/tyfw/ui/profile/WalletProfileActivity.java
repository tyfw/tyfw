package com.example.tyfw.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tyfw.R;


public class WalletProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView profilePic = findViewById(R.id.wallet_default_pic);;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_profile);

        TextView walletAddr = findViewById(R.id.wallet_profile);

        walletAddr.setText(getIntent().getStringExtra("walletAddress"));
    }
}