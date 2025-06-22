package com.example.carrot.activity;

import android.os.Bundle;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import android.location.Address;
import android.location.Geocoder;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;


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
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 위치 서비스 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }


        // 뷰 초기화
        recyclerView = findViewById(R.id.recycler_view_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        btnLocation = findViewById(R.id.btn_location);
        TextView tvTitle = findViewById(R.id.tv_chat_title);
        ImageView ivBack = findViewById(R.id.iv_back);

        ivBack.setOnClickListener(v -> finish());  // x 누르면 뒤로가기

        // 인텐트로 데이터 수신
        productId = getIntent().getIntExtra("product_id", -1);
        otherId = getIntent().getIntExtra("other_id", -1);
        otherNickname = getIntent().getStringExtra("other_nickname");
        myId = new SharedPrefManager(this).getUserId();

        tvTitle.setText(otherNickname != null ? otherNickname : "판매자");

        // 채팅 메시지 목록 초기화
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList, myId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Firebase DB 참조
        chatRef = FirebaseDatabase.getInstance().getReference("chat_messages").child(String.valueOf(productId));

        // 메시지 전송 버튼 이벤트
        btnSend.setOnClickListener(v -> sendTextMessage());

        // 위치 전송 버튼 이벤트
        btnLocation.setOnClickListener(v -> sendLocationMessage());

        // 메시지 로딩
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        // 🧭 주소 변환
                        String locationName = "위치 불명";
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                locationName = address.getAddressLine(0); // 전체 주소
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String key = chatRef.push().getKey();
                        if (key == null) return;

                        Map<String, Object> messageMap = new HashMap<>();
                        messageMap.put("sender_id", myId);
                        messageMap.put("receiver_id", otherId);
                        messageMap.put("content", "");
                        messageMap.put("latitude", lat);
                        messageMap.put("longitude", lng);
                        messageMap.put("location_name", locationName); // 실제 주소로 대체
                        messageMap.put("timestamp", ServerValue.TIMESTAMP);

                        chatRef.child(key).setValue(messageMap);
                    } else {
                        Toast.makeText(this, "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMessages() {
        messageList.clear();
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

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "메시지 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
