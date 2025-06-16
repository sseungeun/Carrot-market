package com.example.carrot.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrot.R;
import com.example.carrot.adapter.ChatAdapter;
import com.example.carrot.model.Message;
import com.example.carrot.network.ApiService;
import com.example.carrot.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private EditText etMessage;
    private Button btnSend;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;

    private int productId;
    private int senderId;
    private int receiverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.recycler_view_chat);
        ImageView ivBack = findViewById(R.id.iv_back);

        productId = getIntent().getIntExtra("product_id", 1);
        senderId = getIntent().getIntExtra("buyer_id", 2);
        receiverId = getIntent().getIntExtra("seller_id", 3);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList, senderId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);


        btnSend.setOnClickListener(view -> sendMessage());
        ivBack.setOnClickListener(v -> finish());
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(ChatActivity.this, "메시지를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        double latitude = 0.0;
        double longitude = 0.0;
        String locationName = "";

        Message message = new Message(productId, senderId, receiverId, content, latitude, longitude, locationName);

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Message> call = apiService.sendMessage(message);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Message newMessage = response.body();
                    messageList.add(newMessage);
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                    etMessage.setText("");
                } else {
                    Toast.makeText(ChatActivity.this, "전송 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
