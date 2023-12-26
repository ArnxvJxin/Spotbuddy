package com.example.spotbuddy;
public class ChatModel {
    public static final int LAYOUT_SENDER = 1;
    public static final int LAYOUT_RECEIVER = 2;
    public static final int LAYOUT_ANNOUNCEMENT = 3;
    private final int viewType;
    private String Message, TimeStamp, Sender, ChatId;

    public ChatModel(int viewType, String message, String timeStamp, String sender, String chatId) {
        this.viewType = viewType;
        this.Message = message;
        this.TimeStamp = timeStamp;
        this.Sender = sender;
        this.ChatId = chatId;
    }

    public int getViewType() {
        return viewType;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getChatId() {
        return ChatId;
    }

    public void setChatId(String chatId) {
        ChatId = chatId;
    }
}
