package com.ligaoqi.simplechatautoreplyapp.ui.chat;

import android.content.ClipData;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.ligaoqi.simplechatautoreplyapp.R;

import java.util.HashSet;
import java.util.Set;

public class ChatEditText extends AppCompatEditText {

    public interface OnImageAddedListener {
        void onImageAdded(Uri contentUri, String mimeType, String label);
    }

    private static final Set<String> SUPPORTED_MIME_TYPES = new HashSet<String>() {{
        add("image/jpeg");
        add("image/jpg");
        add("image/png");
        add("image/gif");
    }};

    private OnImageAddedListener onImageAddedListener;

    public ChatEditText(Context context) {
        this(context, null);
    }

    public ChatEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public ChatEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        // Handle paste event
        if (id == android.R.id.paste) {
            ClipData clipData = getClipboardData();
            if (clipData != null) {
                handleClipData(clipData);
                return true;  // We've handled the paste event
            }
        }
        return super.onTextContextMenuItem(id);  // Default handling
    }

    private ClipData getClipboardData() {
        // Get the clipboard manager and current clip data
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            return clipboard.getPrimaryClip();
        }
        return null;
    }

    private void handleClipData(ClipData clipData) {
        String mimeType = findSupportedMimeType(clipData);
        if (mimeType != null && clipData.getItemCount() > 0) {
            if (onImageAddedListener != null) {
                onImageAddedListener.onImageAdded(
                        clipData.getItemAt(0).getUri(),
                        mimeType,
                        clipData.getDescription().getLabel().toString()
                );
            }
        } else {
            // Handle text or other data types here if needed
        }
    }

    private String findSupportedMimeType(ClipData clipData) {
        for (String mimeType : SUPPORTED_MIME_TYPES) {
            if (clipData.getDescription().hasMimeType(mimeType)) {
                return mimeType;
            }
        }
        return null;
    }

    public void setOnImageAddedListener(OnImageAddedListener listener) {
        this.onImageAddedListener = listener;
    }

}
