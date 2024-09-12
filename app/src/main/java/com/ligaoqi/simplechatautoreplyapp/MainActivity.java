package com.ligaoqi.simplechatautoreplyapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ligaoqi.selfsdk.SelfUtil;
import com.ligaoqi.simplechatautoreplyapp.data.Contact;
import com.ligaoqi.simplechatautoreplyapp.ui.chat.ChatFragment;
import com.ligaoqi.simplechatautoreplyapp.ui.main.MainFragment;
import com.ligaoqi.simplechatautoreplyapp.ui.photo.PhotoFragment;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigationController, NavigationController.AppBarUpdater {
    private final String FRAGMENT_CHAT = "chat";
    private Transition transition;
    private Toolbar toolbar;
    private ConstraintLayout appBar;

    private TextView name;
    private ImageView icon;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBar = (ConstraintLayout) findViewById(R.id.app_bar);
        name = (TextView)findViewById(R.id.name);
        icon = (ImageView)findViewById(R.id.icon);

        setSupportActionBar(toolbar);

        transition = TransitionInflater.from(this).inflateTransition(R.transition.app_bar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new MainFragment(), null)
                    .commitNow();
            if (getIntent() != null) {
                handleIntent(getIntent());
            }
        }
        // SDK预留 测试
        SelfUtil.Introduce(this, "欢迎使用自动回复聊天助手App");
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            handleIntent(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void handleIntent(Intent intent) {
        switch (Objects.requireNonNull(intent.getAction())) {
            case Intent.ACTION_VIEW: {
                Uri data = getIntent().getData();
                String lastPathSegment = (data != null) ? data.getLastPathSegment() : null;
                Long id = null;
                if (lastPathSegment != null) {
                    id = Long.parseLong(lastPathSegment);
                }
                if (id != null) {
                    openChat(id, null);
                }
                break;
            }
            case Intent.ACTION_SEND: {
                String shortcutId = intent.getStringExtra(Intent.EXTRA_SHORTCUT_ID);
                String text = intent.getStringExtra(Intent.EXTRA_TEXT);

                Contact shortCutIdContact = null;

                for(Contact contact: Contact.CONTACTS){
                    assert shortcutId != null;
                    if (shortcutId.equals(contact.getShortcutId())) {
                        shortCutIdContact = contact;
                        break;
                    }
                }
                assert shortCutIdContact != null;

                openChat(shortCutIdContact.getId(), text);

                break;
            }
        }
    }

    @Override
    public void openChat(Long id, String prepopulateText) {
        getSupportFragmentManager().popBackStack(FRAGMENT_CHAT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, ChatFragment.newInstance(id, true, prepopulateText), null)
                .commit();
    }

    @Override
    public void openPhoto(Uri photo) {
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, PhotoFragment.newInstance(photo), null)
                .commit();
    }

    @Override
    public void updateAppBar(Boolean showContact, Boolean hidden, AppBarUpdater body) {
        if(hidden){
            appBar.setVisibility(View.GONE);
        }else{
            appBar.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition(appBar, transition);
            if(showContact){
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                }
                name.setVisibility(View.VISIBLE);
                icon.setVisibility(View.VISIBLE);
            }else{
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    getSupportActionBar().setTitle("自动回复聊天助手App");
                }
                name.setVisibility(View.GONE);
                icon.setVisibility(View.GONE);
            }
        }
        body.update(name, icon);
    }

    @Override
    public void update(TextView name, ImageView icon) {

    }
}