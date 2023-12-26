package com.example.spotbuddy;

import java.util.List;

public interface IFirebaseChatLoadDone {
    void onFirebaseLoadSuccess(List<ChatModel> chatList);

    void onFirebaseLoadFailed(String message);
}