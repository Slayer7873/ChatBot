package com.example.chatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatsModel> messagesList;
    private Context context;
    private static final int USER_VIEW_TYPE = 0; // Type for user messages
    private static final int BOT_VIEW_TYPE = 1; // Type for bot messages

    // Constructor for the Adapter
    public Adapter(Context context, List<ChatsModel> messagesList) {
        this.context = context;
        this.messagesList = messagesList;
    }

    @Override
    public int getItemViewType(int position) {
        // Return the view type based on the sender (user or bot)
       switch (messagesList.get(position).getSender()){
           case "user":
               return 0;
           case "bot":
               return 1;
           default:
               return -1;
       }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the correct layout based on the message type (user or bot)
        if (viewType == USER_VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_msg, parent, false);
            return new UserViewHolder(view); // Return UserViewHolder for user message
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_message, parent, false);
            return new BotViewHolder(view); // Return BotViewHolder for bot message
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Bind the correct data to the view based on the message type
        ChatsModel chatsModel = messagesList.get(position);
        if (getItemViewType(position) == USER_VIEW_TYPE) {
            ((UserViewHolder) holder).usermsg.setText(chatsModel.getMessage()); // Set user message
        } else {
            ((BotViewHolder) holder).botmsg.setText(chatsModel.getMessage()); // Set bot message
        }
    }

    @Override
    public int getItemCount() {
        // Return the total number of messages
        return messagesList.size();
    }

    // ViewHolder for user messages
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usermsg; // TextView for user message

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usermsg = itemView.findViewById(R.id.user_message); // Find the user message TextView
        }
    }

    // ViewHolder for bot messages
    public static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView botmsg; // TextView for bot message

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            botmsg = itemView.findViewById(R.id.idTVBot); // Find the bot message TextView
        }
    }
}