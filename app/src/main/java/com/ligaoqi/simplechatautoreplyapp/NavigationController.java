package com.ligaoqi.simplechatautoreplyapp;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

public interface NavigationController {

    public void openChat(Long id, String prepopulateText);

    public void openPhoto(Uri photo);

    public void updateAppBar(Boolean showContact, Boolean hidden, AppBarUpdater body);

    interface AppBarUpdater {
        void update(TextView name, ImageView icon);
    }

}
