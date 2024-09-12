package com.ligaoqi.simplechatautoreplyapp.data;

import android.net.Uri;

public class Message {

    private final long id;
    private final long sender;
    private final String text;
    private final Uri photoUri;
    private final String photoMimeType;
    private final long timestamp;

    public Message(long id, long sender, String text, Uri photoUri, String photoMimeType, long timestamp) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.photoUri = photoUri;
        this.photoMimeType = photoMimeType;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public long getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public String getPhotoMimeType() {
        return photoMimeType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isIncoming() {
        return sender != 0L;
    }

    public static class Builder {
        private Long id;
        private Long sender;
        private String text;
        private Uri photoUri;
        private String photoMimeType;
        private Long timestamp;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setSender(Long sender) {
            this.sender = sender;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setPhotoUri(Uri photoUri) {
            this.photoUri = photoUri;
            return this;
        }

        public Builder setPhotoMimeType(String photoMimeType) {
            this.photoMimeType = photoMimeType;
            return this;
        }

        public Builder setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Message build() {
            if (id == null || sender == null || text == null || timestamp == null) {
                throw new IllegalStateException("Cannot create Message, some fields are missing");
            }
            return new Message(id, sender, text, photoUri, photoMimeType, timestamp);
        }
    }
}
