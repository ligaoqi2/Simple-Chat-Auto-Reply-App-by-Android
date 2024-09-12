package com.ligaoqi.simplechatautoreplyapp.data;

import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;

import com.ligaoqi.simplechatautoreplyapp.adapter.MessageAdapter;

import java.io.IOException;
import java.util.List;

public interface ChatRepository {
    public List<Contact> getContacts();
    public Contact findContact(Long id);
    public List<Message> findMessages(Long id, MessageAdapter adapter, List<Message> messagesList, LinearLayoutManager linearLayoutManager);
    public void sendMessage(Long id, String text, Uri photoUrl, String photoMineType);
    public void updateNotification(Long id) throws IOException;
    public void activateChat(Long id);
    public void deactivateChat(Long id);
    public void showAsBubble(Long id);
    public Boolean canBubble(Long id);
}
