package com.ligaoqi.simplechatautoreplyapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class VoiceCallActivity extends Activity {

    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ICON = "icon";

    private TextView towardsName;
    private ImageView towardsicon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_call);

        towardsName = findViewById(R.id.name);
        towardsicon = findViewById(R.id.icon);

        String name = getIntent().getStringExtra(EXTRA_NAME);
        String icon = getIntent().getStringExtra(EXTRA_ICON);

        if(name == null || icon == null){
            finish();
        }

        towardsName.setText(name);
        Glide.with(this).load(icon).into(towardsicon);
    }
}
