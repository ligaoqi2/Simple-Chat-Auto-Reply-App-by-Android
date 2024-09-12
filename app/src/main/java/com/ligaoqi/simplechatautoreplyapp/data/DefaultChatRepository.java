package com.ligaoqi.simplechatautoreplyapp.data;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;

import com.ligaoqi.simplechatautoreplyapp.adapter.MessageAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DefaultChatRepository implements ChatRepository{
    private static volatile DefaultChatRepository instance;
    private NotificationHelper notificationHelper;
    private Executor executor;
    private Long currentChat = 0L;
    private final Map<Long, Chat> chats;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public DefaultChatRepository(NotificationHelper notificationHelper, Executor executor) throws IOException {
        this.notificationHelper = notificationHelper;
        this.executor = executor;

        List<Contact> contactList = Contact.CONTACTS;
        Map<Long, Chat> tempChats = new HashMap<>();
        for (Contact contact : contactList) {
            tempChats.put(contact.getId(), new Chat(contact));
        }
        this.chats = tempChats;

        notificationHelper.setUpNotificationChannels();

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static DefaultChatRepository getInstance(Context context) throws IOException {
        if (instance == null) {
            synchronized (DefaultChatRepository.class) {
                if (instance == null) {
                    instance = new DefaultChatRepository(new NotificationHelper(context), Executors.newFixedThreadPool(4)
                    );
                }
            }
        }
        return instance;
    }

    @Override
    @MainThread
    public List<Contact> getContacts() {
        return Contact.CONTACTS;
    }

    @Override
    public Contact findContact(Long id) {
        Contact findContact = null;
        for(Contact contact: Contact.CONTACTS){
            if (id.equals(contact.getId())) {
                findContact = contact;
                break;
            }
        }
        return findContact;
    }

    @Override
    public List<Message> findMessages(Long id, MessageAdapter adapter, List<Message> messagesList, LinearLayoutManager linearLayoutManager) {
        Chat chat = chats.get(id);
        Handler handler = new Handler(Looper.getMainLooper());
        assert chat != null;
        chat.addListener(messages -> {
            // 更新视图
            handler.post(() -> {
                messagesList.clear();
                messagesList.addAll(messages);
                linearLayoutManager.scrollToPosition(messagesList.size() - 1);
                adapter.notifyDataSetChanged();
            });
        });
        return chat.getMessages();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void sendMessage(Long id, String text, Uri photoUri, String photoMimeType) {
        Chat chat = chats.get(id);
        assert chat != null;
        chat.addMessage(new Message.Builder().setSender(0L)
                .setText(text)
                .setTimestamp(System.currentTimeMillis())
                .setPhotoUri(photoUri)
                .setPhotoMimeType(photoMimeType));

        executor.execute(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            chat.addMessage(chat.getContact().reply(text));
            if (chat.getContact().getId() != currentChat) {
                try {
                    notificationHelper.showNotification(chat, false);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void updateNotification(Long id) throws IOException {
        Chat chat = chats.get(id);
        notificationHelper.showNotification(chat, false);
    }

    @Override
    public void activateChat(Long id) {
        currentChat = id;
        notificationHelper.dismissNotification(id);
    }

    @Override
    public void deactivateChat(Long id) {
        if(currentChat.equals(id)){
            currentChat = 0L;
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void showAsBubble(Long id) {
        Chat chat = chats.get(id);
        executor.execute(() -> {
            try {
                assert chat != null;
                notificationHelper.showNotification(chat, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.R)
    public Boolean canBubble(Long id) {
        Chat chat = chats.get(id);
        assert chat != null;
        return notificationHelper.canBubble(chat.getContact());
    }
}
