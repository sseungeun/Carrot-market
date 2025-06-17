package com.example.carrot.activity;

import android.content.pm.PackageManager;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.carrot.R;
import com.example.carrot.model.Product;
import com.example.carrot.model.ProductRequest;
import com.example.carrot.network.ApiService;
import com.example.carrot.network.RetrofitClient;
import com.example.carrot.utils.SharedPrefManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etPrice;
    private Button btnUpload, btnAddLocation, btnAddImage;
    private ImageView ivProductImage;
    private double latitude, longitude;
    private String imagePath = "";  // 이미지 경로 (파일 경로)

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int LOCATION_REQUEST_CODE = 3;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // 뷰 연결
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_content);
        etPrice = findViewById(R.id.et_price);
        btnUpload = findViewById(R.id.btn_upload);
        btnAddLocation = findViewById(R.id.btn_set_location);
        btnAddImage = findViewById(R.id.btn_add_image);
        ivProductImage = findViewById(R.id.iv_upload_image);

        // 위치 API 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 위치 버튼 클릭 시 위치 가져오기
        btnAddLocation.setOnClickListener(view -> getLocation());

        // 이미지 촬영 버튼 클릭 시 카메라 실행
        btnAddImage.setOnClickListener(view -> takePicture());

        // 업로드 버튼 클릭 시 상품 업로드
        btnUpload.setOnClickListener(view -> uploadProduct());
    }

    // 위치 정보 가져오기
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Toast.makeText(this, "위치 정보 가져오기 성공", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "위치 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 승인됨
                getLocation();
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 카메라로 이미지 촬영
    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "카메라 앱이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 카메라 촬영 후 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // 이미지가 성공적으로 촬영된 후
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            ivProductImage.setImageBitmap(imageBitmap);

            // 파일 경로로 저장 (추후 서버로 전송)
            imagePath = saveImageToInternalStorage(imageBitmap);
        }
    }

    // 이미지를 내부 저장소에 저장하고 경로 반환
    private String saveImageToInternalStorage(Bitmap bitmap) {
        FileOutputStream fos = null;
        File directory = getApplicationContext().getDir("images", Context.MODE_PRIVATE);
        File imageFile = new File(directory, "product_image.jpeg");

        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 확인: 저장된 파일 경로를 출력
        Log.d("UploadActivity", "Saved image path: " + imageFile.getAbsolutePath());
        return imageFile.getAbsolutePath();
    }

    // 상품 업로드
    private void uploadProduct() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceString = etPrice.getText().toString().trim();

        // 필수 값들 확인
        if (title.isEmpty() || description.isEmpty() || priceString.isEmpty()) {
            Toast.makeText(this, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = Integer.parseInt(priceString);

        if (price < 0) {
            Toast.makeText(this, "가격은 0 이상이어야 합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        // 위치 정보가 비어있을 경우 기본값 설정
        if (latitude == 0.0 && longitude == 0.0) {
            latitude = 37.5665;  // 서울 기본 좌표
            longitude = 126.9780;
        }

        // 이미지 경로가 비어있는 경우 기본 이미지로 처리
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "product_image.jpeg";  // 기본 이미지 경로 (서버에서 기본 이미지를 처리하는 경우)
        }

        // 판매자 ID 값 가져오기
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        int sellerId = sharedPrefManager.getUserId();

        // 상품 업로드 객체 생성
        ProductRequest productRequest = new ProductRequest(title, description, price, sellerId, latitude, longitude, "서울", imagePath);

        // 로그로 필드 값 확인
        Log.d("UploadActivity", "Title: " + title);
        Log.d("UploadActivity", "Description: " + description);
        Log.d("UploadActivity", "Price: " + price);
        Log.d("UploadActivity", "Seller ID: " + sellerId);
        Log.d("UploadActivity", "Latitude: " + latitude);
        Log.d("UploadActivity", "Longitude: " + longitude);
        Log.d("UploadActivity", "Image Path: " + imagePath);

        // ProductRequest를 JSON으로 변환
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(productRequest));

        // 이미지 파일 멀티파트로 처리
        File imageFile = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            imageFile = new File(imagePath);  // 이미지 경로가 존재하는 경우
        }

        MultipartBody.Part imagePart = null;
        if (imageFile != null) {
            RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
            imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageRequestBody);
        }

        // API 호출
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.d("UploadActivity", "Calling API...");
        Call<Product> call = apiService.createProduct(requestBody, imagePart);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Product product = response.body();
                    Log.d("UploadActivity", "Product registered successfully: " + product.toString());
                    Toast.makeText(UploadActivity.this, "상품 등록 성공!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                    intent.putExtra("newProduct", product);  // 등록된 상품을 MainActivity로 전달
                    startActivity(intent);
                    finish();
                } else {
                    // 서버 응답 실패
                    Log.e("UploadActivity", "Response failed: " + response.code() + " " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            String errorResponse = response.errorBody().string();
                            Log.e("UploadActivity", "Error Response: " + errorResponse);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(UploadActivity.this, "등록 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                // 서버 오류 발생 시
                Log.e("UploadActivity", "Error: " + t.getMessage(), t);
                Toast.makeText(UploadActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

