package com.example.feedmypet;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.feedmypet.DBconnector.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView;
    private EditText etUsername, etPassword;
    private Button btnUploadProfilePic, btnSaveChanges;

    private DatabaseHelper databaseHelper;
    private Uri profilePicUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profileImageView = findViewById(R.id.profileImageView);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnUploadProfilePic = findViewById(R.id.btnUploadProfilePic);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        databaseHelper = new DatabaseHelper(this);

        btnUploadProfilePic.setOnClickListener(v -> openImageChooser());
        btnSaveChanges.setOnClickListener(v -> updateProfile());

        // Load existing user details
        loadUserProfile();
    }


    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profilePicUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profilePicUri);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadUserProfile() {
        // Load existing user details from the database
        // This part needs to be implemented based on your app logic
    }

    private void updateProfile() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        Log.d("UserProfileActivity", "Updating profile with username: " + username + ", password: " + password);

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);

        if (profilePicUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profilePicUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] profilePicBytes = baos.toByteArray();
                values.put("profile_pic", profilePicBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Make sure to use the correct identifier to find the user to update
        String selection = "email = ?"; // This should be a unique identifier like user ID or email
        String[] selectionArgs = { "user@example.com" }; // Replace with actual email or user ID

        int rowsUpdated = db.update("users", values, selection, selectionArgs);
        Log.d("UserProfileActivity", "Rows updated: " + rowsUpdated);

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Profile update failed.", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}
