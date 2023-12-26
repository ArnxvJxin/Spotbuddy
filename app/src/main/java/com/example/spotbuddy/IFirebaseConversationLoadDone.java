package com.example.spotbuddy;

import java.util.List;

public interface IFirebaseConversationLoadDone {
    void onFirebaseLoadSuccess(List<ConversationModel> conversationList);

    void onFirebaseLoadFailed(String message);
}