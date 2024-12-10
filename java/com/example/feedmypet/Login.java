package com.example.feedmypet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.feedmypet.DBconnector.DatabaseHelper;

public class Login extends AppCompatActivity {

    private Button btnLogin1;
    private EditText etEmail1, etPassword1;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        btnLogin1 = findViewById(R.id.btnLogin1);
        etEmail1 = findViewById(R.id.etEmail1);
        etPassword1 = findViewById(R.id.etPassword1);

        databaseHelper = new DatabaseHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLogin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail1.getText().toString();
                String password = etPassword1.getText().toString();

                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(Login.this, "Fields cannot be empty please fill them!", Toast.LENGTH_SHORT).show();
                }
                else {
                    authenticateUser(email, password);
                }

            }
        });
    }
    private void authenticateUser(String email, String password) {

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {"usertype"};
        String selection = "email = ? AND password = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query("users", projection, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String usertype = cursor.getString(cursor.getColumnIndexOrThrow("usertype"));

            if ("Admin".equals(usertype)) {
                Intent intent = new Intent(Login.this, AdminPanel.class);
                startActivity(intent);
                finish();
            } else if ("User".equals(usertype)) {
                Intent intent = new Intent(Login.this, UserPanel.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Login.this, "Invalid user type", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
    }
}