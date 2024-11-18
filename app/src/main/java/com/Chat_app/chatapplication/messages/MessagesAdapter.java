package com.learnoset.chatapplication.messages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learnoset.chatapplication.R;
import com.learnoset.chatapplication.chat.Chat;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<MessagesList> messagesLists;
    private final Context context;

    public MessagesAdapter(List<MessagesList> messagesLists, Context context) {
        this.messagesLists = messagesLists;
        this.context = context;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public MessagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_adapter_layout, null));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MyViewHolder holder, int position) {

        MessagesList list2 = messagesLists.get(position);

        holder.name.setText(list2.getFullName());
        holder.lastMessage.setText(list2.getLastMessage());

        if (list2.getUnseenMessages() == 0) {
            holder.unseenMessages.setVisibility(View.GONE);
            holder.lastMessage.setTextColor(Color.parseColor("#959595"));
        } else {
            holder.unseenMessages.setVisibility(View.VISIBLE);
            holder.unseenMessages.setText(list2.getUnseenMessages() + "");
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.theme_color_80));
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // setting unseen messages as 0
                list2.setUnseenMessages(0);

                notifyItemChanged(holder.getAdapterPosition());

                // create intent to open Chat activity
                Intent intent = new Intent(context, Chat.class);

                // append data along with the intent
                intent.putExtra("mobile", list2.getMobile());
                intent.putExtra("full_name", list2.getFullName());
                intent.putExtra("chat_key", list2.getChatKey());

                // launch Chat activity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messagesLists.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateMessages(List<MessagesList> userMessagesList) {
        messagesLists = userMessagesList;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        final private TextView name;
        final private TextView lastMessage;
        final private TextView unseenMessages;
        final private LinearLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            unseenMessages = itemView.findViewById(R.id.unseenMessages);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }
}
