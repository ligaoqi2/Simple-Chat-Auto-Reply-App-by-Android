package com.ligaoqi.simplechatautoreplyapp;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import com.ligaoqi.simplechatautoreplyapp.data.ChatRepository;
import com.ligaoqi.simplechatautoreplyapp.data.DefaultChatRepository;

import java.io.IOException;

public class ReplyReceiver extends BroadcastReceiver {

    public static final String KEY_TEXT_REPLY = "reply";

    @Override
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void onReceive(Context context, Intent intent) {

        ChatRepository repository = null;
        try {
            repository = DefaultChatRepository.getInstance(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 从 Intent 中获取 RemoteInput 结果
        Bundle results = RemoteInput.getResultsFromIntent(intent);
        if (results == null) {
            return;
        }

        // 获取用户在通知回复中输入的消息
        CharSequence inputCharSequence = results.getCharSequence(KEY_TEXT_REPLY);
        String input = (inputCharSequence != null) ? inputCharSequence.toString() : null;

        // 获取 Intent 中的 URI
        Uri uri = intent.getData();
        if (uri == null) {
            return;
        }

        // 从 URI 中获取 chatId
        String lastPathSegment = uri.getLastPathSegment();
        long chatId = (lastPathSegment != null) ? Long.parseLong(lastPathSegment) : -1;

        // 如果 chatId 有效并且输入不为空，发送消息并更新通知
        if (chatId > 0 && input != null && !input.isEmpty()) {
            repository.sendMessage(chatId, input, null, null);
            // 更新通知，以便用户可以看到回复已发送
            try {
                repository.updateNotification(chatId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
