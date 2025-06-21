package com.example.carrot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrot.R;
import com.example.carrot.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    private List<Message> messageList;
    private int myId;

    public ChatAdapter(Context context, List<Message> messageList, int myId) {
        this.context = context;
        this.messageList = messageList;
        this.myId = myId;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        holder.layoutMyMessage.setVisibility(View.GONE);
        holder.layoutOtherMessage.setVisibility(View.GONE);
        holder.layoutLocationMessage.setVisibility(View.GONE);

        String timeFormatted = formatTimestamp(message.getTimestamp());

        if (message.getLocation_name() != null && !message.getLocation_name().isEmpty()) {
            holder.layoutLocationMessage.setVisibility(View.VISIBLE);
            holder.tvLocation.setText(message.getLocation_name());
        } else if (message.getSender_id() == myId) {
            holder.layoutMyMessage.setVisibility(View.VISIBLE);
            holder.tvMyMessage.setText(message.getContent());
            holder.tvMyTime.setText(timeFormatted);
        } else {
            holder.layoutOtherMessage.setVisibility(View.VISIBLE);
            holder.tvOtherMessage.setText(message.getContent());
            holder.tvOtherTime.setText(timeFormatted);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private String formatTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("a h:mm", Locale.KOREA);
        return sdf.format(date);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutMyMessage, layoutOtherMessage, layoutLocationMessage;
        TextView tvMyMessage, tvMyTime, tvOtherMessage, tvOtherTime, tvLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutMyMessage = itemView.findViewById(R.id.layout_my_message);
            layoutOtherMessage = itemView.findViewById(R.id.layout_other_message);
            layoutLocationMessage = itemView.findViewById(R.id.layout_location_message);
            tvMyMessage = itemView.findViewById(R.id.tv_my_message);
            tvMyTime = itemView.findViewById(R.id.tv_my_time);
            tvOtherMessage = itemView.findViewById(R.id.tv_other_message);
            tvOtherTime = itemView.findViewById(R.id.tv_other_time);
            tvLocation = itemView.findViewById(R.id.tv_location);
        }
    }
}
