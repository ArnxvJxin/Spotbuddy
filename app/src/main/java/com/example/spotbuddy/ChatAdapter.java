package com.example.spotbuddy;

import static com.example.spotbuddy.ChatModel.LAYOUT_ANNOUNCEMENT;
import static com.example.spotbuddy.ChatModel.LAYOUT_RECEIVER;
import static com.example.spotbuddy.ChatModel.LAYOUT_SENDER;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    List<ChatModel> chatList;
    LayoutInflater inflater;

    public ChatAdapter(Context context, List<ChatModel> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (chatList.get(position).getViewType())
        {
            case 1:
                return LAYOUT_SENDER;

            case 2:
                return LAYOUT_RECEIVER;

            case 3:
                return LAYOUT_ANNOUNCEMENT;

            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType)
        {
            case LAYOUT_SENDER:
                View layout_sender = LayoutInflater.from(context).inflate(R.layout.item_sender, parent, false);
                return new SenderMessageViewHolder(layout_sender);

            case LAYOUT_RECEIVER:
                View layout_receiver = LayoutInflater.from(context).inflate(R.layout.item_receiver, parent, false);
                return new ReceiverMessageViewHolder(layout_receiver);

            case LAYOUT_ANNOUNCEMENT:
                View layout_anouncement = LayoutInflater.from(context).inflate(R.layout.item_announcement, parent, false);
                return new AnnouncementViewHolder(layout_anouncement);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel chat = chatList.get(position);
        switch (chat.getViewType()) {
            case LAYOUT_SENDER:
                SenderMessageViewHolder senderHolder = (SenderMessageViewHolder) holder;
                senderHolder.message_sender.setText(chat.getMessage());
                senderHolder.timestamp_sender.setText(chat.getTimeStamp());
                break;

            case LAYOUT_RECEIVER:
                ReceiverMessageViewHolder receiverHolder = (ReceiverMessageViewHolder) holder;
                receiverHolder.message_receiver.setText(chat.getMessage());
                receiverHolder.timestamp_receiver.setText(chat.getTimeStamp());
                break;

            case LAYOUT_ANNOUNCEMENT:
                AnnouncementViewHolder announcementHolder = (AnnouncementViewHolder) holder;
                announcementHolder.announcement.setText(chat.getMessage());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    private static class SenderMessageViewHolder extends RecyclerView.ViewHolder {
        TextView message_sender, timestamp_sender;
        public SenderMessageViewHolder(View senderView) {
            super(senderView);
            message_sender = senderView.findViewById(R.id.sender_txt_message);
            timestamp_sender = senderView.findViewById(R.id.sender_txt_timestamp);
        }
    }

    private static class ReceiverMessageViewHolder extends RecyclerView.ViewHolder {
        TextView message_receiver, timestamp_receiver;
        public ReceiverMessageViewHolder(View receiverView) {
            super(receiverView);
            message_receiver = receiverView.findViewById(R.id.receiver_txt_message);
            timestamp_receiver = receiverView.findViewById(R.id.receiver_txt_timestamp);
        }
    }

    private static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView announcement;
        public AnnouncementViewHolder(View announcementView) {
            super(announcementView);
            announcement = announcementView.findViewById(R.id.announcement_txt_message);
        }
    }
}
