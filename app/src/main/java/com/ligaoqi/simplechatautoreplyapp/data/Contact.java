package com.ligaoqi.simplechatautoreplyapp.data;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public abstract class Contact {
    private final long id;
    private final String name;
    private final String icon;

    public Contact(long id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }
    public static final List<Contact> CONTACTS;

    static {
        CONTACTS = new ArrayList<>();
        CONTACTS.add(new Contact(1L, "Cat", "cat.jpg") {
            @Override
            public Message.Builder reply(String text) {
                return buildReply().setText("Meow");
            }
        });
        CONTACTS.add(new Contact(2L, "Dog", "dog.jpg") {
            @Override
            public Message.Builder reply(String text) {
                return buildReply().setText("Woof woof!!");
            }
        });
        CONTACTS.add(new Contact(3L, "Parrot", "parrot.jpg") {
            @Override
            public Message.Builder reply(String text) {
                return buildReply().setText(text);
            }
        });
        CONTACTS.add(new Contact(4L, "Sheep", "sheep.jpg") {
            @Override
            public Message.Builder reply(String text) {
                return buildReply().setText("Look at me!")
                        .setPhotoUri(Uri.parse("file:///android_asset/sheep_full.jpg"))
                        .setPhotoMimeType("image/jpeg");
            }
        });
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getIconUri() {
        return "file:///android_asset/" + icon;
    }

    public String getShortcutId() {
        return "contact_" + id;
    }

    public Message.Builder buildReply() {
        return new Message.Builder()
                .setSender(this.id)
                .setTimestamp(System.currentTimeMillis());
    }

    public abstract Message.Builder reply(String text);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (id != contact.id) return false;
        if (!name.equals(contact.name)) return false;
        return icon.equals(contact.icon);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + icon.hashCode();
        return result;
    }

}
