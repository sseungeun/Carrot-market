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

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private List<Message> messageList;
    private int myId;  // 내 사용자 ID

    public ChatAdapter(Context context, List<Message> messageList, int myId) {
        this.context = context;
        this.messageList = messageList;
        this.myId = myId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);

        // 내가 보낸 메시지인지 확인
        if (message.getSender_id() == myId) {
            holder.layoutMyMessage.setVisibility(View.VISIBLE);
            holder.layoutOtherMessage.setVisibility(View.GONE);

            holder.tvMyMessage.setText(message.getContent());
            // holder.tvMyTime.setText("시간 추가 가능");
        } else {
            holder.layoutMyMessage.setVisibility(View.GONE);
            holder.layoutOtherMessage.setVisibility(View.VISIBLE);

            holder.tvOtherMessage.setText(message.getContent());
            // holder.tvOtherTime.setText("시간 추가 가능");
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutMyMessage, layoutOtherMessage;
        TextView tvMyMessage, tvOtherMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutMyMessage = itemView.findViewById(R.id.layout_my_message);
            layoutOtherMessage = itemView.findViewById(R.id.layout_other_message);
            tvMyMessage = itemView.findViewById(R.id.tv_my_message);
            tvOtherMessage = itemView.findViewById(R.id.tv_other_message);
        }
    }
}
