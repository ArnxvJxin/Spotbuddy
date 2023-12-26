package com.example.spotbuddy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    Context context;
    List<ConversationModel> conversationList;
    LayoutInflater inflater;

    public ConversationAdapter(Context context, List<ConversationModel> conversationList) {
        this.context = context;
        this.conversationList = conversationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ConversationModel conversation = conversationList.get(position);
        Picasso.get().load(conversation.getPhoto1()).into(holder.profile);
        holder.name.setText(conversation.getName());
        holder.message.setText(conversation.getMessage());
        holder.timestamp.setText(conversation.getTimestamp());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ChatActivity.class);
                i.putExtra("name", conversation.getName());
                i.putExtra("photo", conversation.getPhoto1());
                i.putExtra("uid", conversation.getUID());
                i.putExtra("serverstamp", conversation.getServerstamp());
                view.getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile;
        TextView name, message, timestamp;

        public ViewHolder(View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.conversations_img_match);
            name = itemView.findViewById(R.id.conversations_txt_name);
            message = itemView.findViewById(R.id.conversations_txt_message);
            timestamp = itemView.findViewById(R.id.conversations_txt_time);
        }
    }
}
