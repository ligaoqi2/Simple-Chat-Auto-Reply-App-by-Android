package com.ligaoqi.simplechatautoreplyapp.data;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private final Contact contact;
    private final List<ChatThreadListener> listeners = new ArrayList<>();
    private final List<Message> _messages = new ArrayList<>();

    public Contact getContact() {
        return contact;
    }

    public Chat(Contact contact) {
        this.contact = contact;

        // Initialize the chat with some messages
        _messages.add(new Message(1L, contact.getId(), "Send me a message", null, null, System.currentTimeMillis()));
        _messages.add(new Message(2L, contact.getId(), "I will reply in 5 seconds", null, null, System.currentTimeMillis()));
    }

    // Provide an unmodifiable view of the messages list
    public List<Message> getMessages() {
        return _messages;
    }

    // Add a listener for chat thread updates
    public void addListener(ChatThreadListener listener) {
        listeners.add(listener);
    }

    // Remove a listener for chat thread updates
    public void removeListener(ChatThreadListener listener) {
        listeners.remove(listener);
    }

    // Add a new message to the chat thread
    public void addMessage(Message.Builder builder) {
        // Set the message ID to be one greater than the last message's ID
        builder.setId(_messages.get(_messages.size() - 1).getId() + 1);
        _messages.add(builder.build());

        // Notify all listeners of the updated message list
        for (ChatThreadListener listener : listeners) {
            listener.onThreadUpdated(_messages);
        }
    }
}
