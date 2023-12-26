package com.example.spotbuddy;
public class ConversationModel {

    private String UID, Photo1, Name, Message, Timestamp, Serverstamp;

    // Empty constructor
    public ConversationModel() {
    }

    public ConversationModel(String uid, String name, String photo1, String message, String timestamp, String serverstamp) {
        this.UID = uid;
        this.Name = name;
        this.Photo1 = photo1;
        this.Message = message;
        this.Timestamp = timestamp;
        this.Serverstamp = serverstamp;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoto1() {
        return Photo1;
    }

    public void setPhoto1(String photo1) {
        Photo1 = photo1;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getServerstamp() {
        return Serverstamp;
    }

    public void setServerstamp(String serverstamp) {
        Serverstamp = serverstamp;
    }
}
