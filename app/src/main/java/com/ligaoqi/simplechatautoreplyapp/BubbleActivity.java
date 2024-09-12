package com.ligaoqi.simplechatautoreplyapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ligaoqi.simplechatautoreplyapp.ui.chat.ChatFragment;
import com.ligaoqi.simplechatautoreplyapp.ui.photo.PhotoFragment;

public class BubbleActivity extends AppCompatActivity implements NavigationController, NavigationController.AppBarUpdater{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);

        Uri data = getIntent().getData();
        if (data == null || data.getLastPathSegment() == null) {
            return;
        }

        long id;
        try {
            id = Long.parseLong(data.getLastPathSegment());
        } catch (NumberFormatException e) {
            return;
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ChatFragment.newInstance(id, false, null))
                    .commitNow();
        }
    }

    @Override
    public void openChat(Long id, String prepopulateText) {
        throw new UnsupportedOperationException("BubbleActivity always shows a single chat thread.");
    }

    @Override
    public void openPhoto(Uri photo) {
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, PhotoFragment.newInstance(photo))
                .commit();
    }

    @Override
    public void updateAppBar(Boolean showContact, Boolean hidden, AppBarUpdater body) {

    }

    @Override
    public void update(TextView name, ImageView icon) {

    }
}
