package com.example.feedmypet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.feedmypet.DBconnector.DatabaseHelper;

public class ViewDataActivity extends AppCompatActivity {

    private ListView listViewData;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        listViewData = findViewById(R.id.listViewData);
        dbHelper = new DatabaseHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM products", null);

        CustomAdapter adapter = new CustomAdapter(this, cursor);
        listViewData.setAdapter(adapter);
    }
}
