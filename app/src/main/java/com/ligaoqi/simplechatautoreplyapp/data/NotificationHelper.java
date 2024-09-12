package com.ligaoqi.simplechatautoreplyapp.data;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.app.RemoteInput;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.LocusId;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.WorkerThread;

import com.ligaoqi.simplechatautoreplyapp.BubbleActivity;
import com.ligaoqi.simplechatautoreplyapp.MainActivity;
import com.ligaoqi.simplechatautoreplyapp.R;
import com.ligaoqi.simplechatautoreplyapp.ReplyReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationHelper {
    private static final String CHANNEL_NEW_MESSAGES = "new_messages";
    private static final int REQUEST_CONTENT = 1;
    private static final int REQUEST_BUBBLE = 2;
    private final NotificationManager notificationManager;
    private final ShortcutManager shortcutManager;
    private Context mContext;
    private Icon icon;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public NotificationHelper(Context context) {
        mContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            throw new IllegalStateException("NotificationManager is not available");
        }

        shortcutManager = (ShortcutManager) context.getSystemService(Context.SHORTCUT_SERVICE);
        if (shortcutManager == null) {
            throw new IllegalStateException("ShortcutManager is not available");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setUpNotificationChannels() throws IOException {
        if (notificationManager.getNotificationChannel(CHANNEL_NEW_MESSAGES) == null) {
            notificationManager.createNotificationChannel(new NotificationChannel(
                    CHANNEL_NEW_MESSAGES,
                    mContext.getString(R.string.channel_new_messages),
                    NotificationManager.IMPORTANCE_HIGH)
            );
        }
        updateShortcuts(null);
    }

    @WorkerThread
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void updateShortcuts(Contact importantContact) throws IOException {

        List<ShortcutInfo> shortcuts = new ArrayList<>();

        for (Contact contact : Contact.CONTACTS) {
            icon = Icon.createWithAdaptiveBitmap(BitmapFactory.decodeStream(mContext.getResources().getAssets().open(contact.getIcon())));
            ShortcutInfo shortcut = null;
            shortcut = new ShortcutInfo.Builder(mContext, contact.getShortcutId())
                    .setLocusId(new LocusId(contact.getShortcutId()))
                    .setActivity(new ComponentName(mContext, MainActivity.class))
                    .setShortLabel(contact.getName())
                    .setIcon(icon)
                    .setLongLived(true)
                    .setCategories(Collections.singleton("com.example.android.bubbles.category.TEXT_SHARE_TARGET"))
                    .setIntent(
                            new Intent(mContext, MainActivity.class)
                                    .setAction(Intent.ACTION_VIEW)
                                    .setData(
                                            Uri.parse("https://android.example.com/chat/" + contact.getId())
                                    )
                    )
                    .setPerson(
                            new Person.Builder()
                                    .setName(contact.getName())
                                    .setIcon(icon)
                                    .build()
                    )
                    .build();
            shortcuts.add(shortcut);
        }

        if (importantContact != null) {
            shortcuts.sort((s1, s2) -> Boolean.compare(s2.getId().equals(importantContact.getShortcutId()), s1.getId().equals(importantContact.getShortcutId())));
        }

        int maxCount = 0;
        maxCount = shortcutManager.getMaxShortcutCountPerActivity();
        if (shortcuts.size() > maxCount) {
            shortcuts = shortcuts.subList(0, maxCount);
        }
        shortcutManager.addDynamicShortcuts(shortcuts);
    }

    @WorkerThread
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void showNotification(Chat chat, Boolean fromUser) throws IOException {
        updateShortcuts(chat.getContact());
        Icon iconShow = Icon.createWithAdaptiveBitmapContentUri(chat.getContact().getIconUri());
        Person user = new Person.Builder().setName(mContext.getString(R.string.sender_you)).build();
        Person person = new Person.Builder().setName(chat.getContact().getName()).setIcon(icon).build();
        Uri contentUri = Uri.parse("https://android.example.com/chat/"+chat.getContact().getId());

        Notification.Builder builder = new Notification.Builder(mContext, CHANNEL_NEW_MESSAGES);

        Notification.BubbleMetadata.Builder bubbleBuilder =
                new Notification.BubbleMetadata.Builder(
                        PendingIntent.getActivity(
                                mContext,
                                REQUEST_BUBBLE,
                                new Intent(mContext, BubbleActivity.class)
                                        .setAction(Intent.ACTION_VIEW)
                                        .setData(contentUri),
                                PendingIntent.FLAG_UPDATE_CURRENT
                        ),
                        iconShow
                )
                        .setDesiredHeightResId(R.dimen.bubble_height);

        if (fromUser) {
            bubbleBuilder.setAutoExpandBubble(true);
            bubbleBuilder.setSuppressNotification(true);
        }

        Notification.BubbleMetadata bubbleMetadata = bubbleBuilder.build();
        builder.setBubbleMetadata(bubbleMetadata);
        builder.setContentTitle(chat.getContact().getName());
        builder.setSmallIcon(R.drawable.ic_message);
        builder.setCategory(Notification.CATEGORY_MESSAGE);
        builder.setShortcutId(chat.getContact().getShortcutId());
        builder.setLocusId(new LocusId(chat.getContact().getShortcutId()));
        builder.addPerson(person);
        builder.setShowWhen(true);
        builder.setContentIntent(
                PendingIntent.getActivity(
                        mContext,
                        REQUEST_CONTENT,
                        new Intent(mContext, MainActivity.class)
                                .setAction(Intent.ACTION_VIEW)
                                .setData(contentUri), PendingIntent.FLAG_UPDATE_CURRENT));

        builder.addAction(new Notification.Action.Builder(
                Icon.createWithResource(mContext, R.drawable.ic_send),
                mContext.getString(R.string.label_reply),
                PendingIntent.getBroadcast(mContext, REQUEST_CONTENT, new Intent(mContext, ReplyReceiver.class).setData(contentUri), PendingIntent.FLAG_UPDATE_CURRENT)
        ).addRemoteInput(
                new RemoteInput.Builder(ReplyReceiver.KEY_TEXT_REPLY)
                        .setLabel(mContext.getString(R.string.hint_input))
                        .build()
                        )
                        .setAllowGeneratedReplies(true)
                        .build()
        );

        Notification.MessagingStyle messagingStyle = new Notification.MessagingStyle(user);
        long lastId = chat.getMessages().get(chat.getMessages().size() - 1).getId();

        for (Message message : chat.getMessages()) {
            Notification.MessagingStyle.Message m = new Notification.MessagingStyle.Message(
                    message.getText(),
                    message.getTimestamp(),
                    message.isIncoming() ? person : null
            );

            if (message.getPhotoUri() != null) {
                m.setData(message.getPhotoMimeType(), message.getPhotoUri());
            }

            if (message.getId() < lastId) {
                messagingStyle.addHistoricMessage(m);
            } else {
                messagingStyle.addMessage(m);
            }
        }

        messagingStyle.setGroupConversation(false);

        builder.setStyle(messagingStyle).setWhen(chat.getMessages().get(chat.getMessages().size() - 1).getTimestamp());

        notificationManager.notify((int) chat.getContact().getId(), builder.build());

    }

    public void dismissNotification(Long id) {
        notificationManager.cancel(id.intValue());
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Boolean canBubble(Contact contact) {
        NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_NEW_MESSAGES, contact.getShortcutId());
        return (notificationManager.areBubblesAllowed() || (channel != null && channel.canBubble()));
    }
}
