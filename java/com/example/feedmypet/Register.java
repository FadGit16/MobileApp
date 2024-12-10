package com.example.feedmypet;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.feedmypet.DBconnector.DatabaseHelper;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private Button btnRegister;
    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Spinner spUsertype;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btnRegister);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spUsertype = findViewById(R.id.spUsertype);

        databaseHelper = new DatabaseHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.usertypes, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spUsertype.setAdapter(adapter);

        spUsertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected item
                String selectedUserType = parent.getItemAtPosition(position).toString();
                // Display a Toast message with the selected item
                Toast.makeText(parent.getContext(), "Selected: " + selectedUserType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = etUsername.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmpassword = etConfirmPassword.getText().toString();
                String usertype = spUsertype.getSelectedItem().toString();

                if (username.isEmpty() || password.isEmpty() || confirmpassword.isEmpty() || email.isEmpty() || usertype.isEmpty()) {
                    Toast.makeText(Register.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmpassword)) { //Checking if the user entered values in Password input field & Confirm Password input filed match
                    Toast.makeText(Register.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(email)) {//Cheking if the user has entered a proper email, to check this Email format checking regular expression is used
                    Toast.makeText(Register.this, "Invalid email address.", Toast.LENGTH_SHORT).show();
                } else {
                    // Open the database for writing
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();

                    // Prepare user data for insertion
                    ContentValues values = new ContentValues();
                    values.put("username", username);
                    values.put("password", password); // You should hash the password in practice
                    values.put("email", email);
                    values.put("usertype", usertype);

                    // Insert user data into the database
                    long newRowId = db.insert("users", null, values);

                    // dbHelper.close();

                    if (newRowId != -1) {
                        Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity and return to the previous screen
                    } else {
                        Toast.makeText(Register.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+$";
        return Pattern.matches(emailPattern, email);
    }
}