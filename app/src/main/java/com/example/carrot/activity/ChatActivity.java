package com.example.carrot.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrot.R;
import com.example.carrot.adapter.ChatAdapter;
import com.example.carrot.model.Message;
import com.example.carrot.utils.SharedPrefManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText etMessage;
    private Button btnSend;
    private ImageButton btnLocation;
    private DatabaseReference chatRef;

    private int productId, myId, otherId;
    private String otherNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_view_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        btnLocation = findViewById(R.id.btn_location);
        TextView tvTitle = findViewById(R.id.tv_chat_title);

        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> finish());


        productId = getIntent().getIntExtra("product_id", -1);
        otherId = getIntent().getIntExtra("other_id", -1);
        otherNickname = getIntent().getStringExtra("other_nickname");

        myId = new SharedPrefManager(this).getUserId();
        tvTitle.setText(otherNickname != null ? otherNickname : "판매자");

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList, myId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        chatRef = FirebaseDatabase.getInstance().getReference("chat_messages").child(String.valueOf(productId));

        btnSend.setOnClickListener(v -> sendTextMessage());

        btnLocation.setOnClickListener(v -> sendLocationMessage());

        loadMessages();
    }

    private void sendTextMessage() {
        String content = etMessage.getText().toString().trim();
        if (content.isEmpty()) return;

        String key = chatRef.push().getKey();
        if (key == null) return;

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("sender_id", myId);
        messageMap.put("receiver_id", otherId);
        messageMap.put("content", content);
        messageMap.put("latitude", 0.0);
        messageMap.put("longitude", 0.0);
        messageMap.put("location_name", "");
        messageMap.put("timestamp", ServerValue.TIMESTAMP);

        chatRef.child(key).setValue(messageMap);
        etMessage.setText("");
    }

    private void sendLocationMessage() {
        // 예시: 고정 위치 전송
        double lat = 37.5665;
        double lng = 126.9780;
        String locationName = "서울 시청";

        String key = chatRef.push().getKey();
        if (key == null) return;

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("sender_id", myId);
        messageMap.put("receiver_id", otherId);
        messageMap.put("content", "");
        messageMap.put("latitude", lat);
        messageMap.put("longitude", lng);
        messageMap.put("location_name", locationName);
        messageMap.put("timestamp", ServerValue.TIMESTAMP);

        chatRef.child(key).setValue(messageMap);
    }

    private void loadMessages() {
        messageList.clear(); // 다른 채팅방 진입 시 초기화
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    messageList.add(message);
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "메시지 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
