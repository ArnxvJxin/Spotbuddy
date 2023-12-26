package com.example.spotbuddy;

import java.util.List;

public interface IFirebaseSwipeLoadDone {
    void onFirebaseLoadSuccess(List<ItemModel> userList);
    void onFirebaseLoadFailed(String message);
}