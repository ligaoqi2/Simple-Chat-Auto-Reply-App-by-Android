package com.ligaoqi.simplechatautoreplyapp.data;

import java.util.List;

public interface ChatThreadListener {
    void onThreadUpdated(List<Message> messages);
}
