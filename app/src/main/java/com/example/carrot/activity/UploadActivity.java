package com.example.carrot.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.carrot.R;
import com.example.carrot.model.Product;
import com.example.carrot.network.ApiService;
import com.example.carrot.network.RetrofitClient;
import com.example.carrot.utils.SharedPrefManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

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
    private double latitude = 0.0, longitude = 0.0;
    private String imagePath = "";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int LOCATION_REQUEST_CODE = 2;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_content);
        etPrice = findViewById(R.id.et_price);
        btnUpload = findViewById(R.id.btn_upload);
        btnAddLocation = findViewById(R.id.btn_set_location);
        btnAddImage = findViewById(R.id.btn_add_image);
        ivProductImage = findViewById(R.id.iv_upload_image);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnAddLocation.setOnClickListener(view -> getLocation());
        btnAddImage.setOnClickListener(view -> takePicture());
        btnUpload.setOnClickListener(view -> uploadProduct());
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(this, "위치 정보 설정됨", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            ivProductImage.setImageBitmap(imageBitmap);
            imagePath = saveImageToInternalStorage(imageBitmap);
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        File directory = getApplicationContext().getDir("images", Context.MODE_PRIVATE);
        File imageFile = new File(directory, "product_image.jpeg");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFile.getAbsolutePath();
    }

    private void uploadProduct() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = Integer.parseInt(priceStr);
        if (latitude == 0.0 && longitude == 0.0) {
            latitude = 37.5665;
            longitude = 126.9780;
        }

        if (imagePath == null || imagePath.isEmpty()) {
            Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_image);
            imagePath = saveImageToInternalStorage(defaultBitmap);
        }

        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        int sellerId = sharedPrefManager.getUserId();

        // RequestBody 변환
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(price));
        RequestBody sellerIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(sellerId));
        RequestBody latBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latitude));
        RequestBody lonBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(longitude));
        RequestBody locationNameBody = RequestBody.create(MediaType.parse("text/plain"), "서울");

        File imageFile = new File(imagePath);
        RequestBody imageReqBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageReqBody);

        // API 호출
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Product> call = apiService.createProduct(titleBody, descBody, priceBody, sellerIdBody, latBody, lonBody, locationNameBody, imagePart);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UploadActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UploadActivity.this, "서버 응답 오류: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(UploadActivity.this, "업로드 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
