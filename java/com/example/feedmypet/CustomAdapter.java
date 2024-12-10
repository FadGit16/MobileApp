package com.example.feedmypet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedmypet.DBconnector.DatabaseHelper;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;
    private LayoutInflater inflater;

    public CustomAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        cursor.moveToPosition(position);
        return cursor;
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(cursor.getColumnIndex("id"));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        cursor.moveToPosition(position);

        TextView nameTextView = convertView.findViewById(R.id.textViewName);
        TextView priceTextView = convertView.findViewById(R.id.textViewPrice);
        TextView brandTextView = convertView.findViewById(R.id.textViewBrand);
        TextView typeTextView = convertView.findViewById(R.id.textViewType);
        TextView ageTextView = convertView.findViewById(R.id.textViewAge);
        TextView descriptionTextView = convertView.findViewById(R.id.textViewDescription);
        ImageView imageView = convertView.findViewById(R.id.imageViewProduct);
        EditText quantityEditText = convertView.findViewById(R.id.editTextQuantity);
        Button addToCartButton = convertView.findViewById(R.id.buttonAddToCart);

        String name = cursor.getString(cursor.getColumnIndex("product_name"));
        String price = cursor.getString(cursor.getColumnIndex("price"));
        String brand = cursor.getString(cursor.getColumnIndex("brand"));
        String type = cursor.getString(cursor.getColumnIndex("type"));
        String age = cursor.getString(cursor.getColumnIndex("age"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex("image"));

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        nameTextView.setText(name);
        priceTextView.setText(price);
        brandTextView.setText(brand);
        typeTextView.setText(type);
        ageTextView.setText(age);
        descriptionTextView.setText(description);
        imageView.setImageBitmap(bitmap);

        addToCartButton.setOnClickListener(view -> {
            String quantityString = quantityEditText.getText().toString();
            if (!quantityString.isEmpty()) {
                int quantity = Integer.parseInt(quantityString);
                long productId = getItemId(position);
                addToCart(productId, name, quantity, Double.parseDouble(price));
            }
        });

        return convertView;
    }

    private void addToCart(long productId, String productName, int quantity, double price) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_id", productId);
        values.put("product_name", productName);
        values.put("product_price", price);
        values.put("quantity", quantity);

        long result = db.insert("cart", null, values);
        db.close();

        // Show a message based on the result of the insert operation
        if (result != -1) {
            Toast.makeText(context, "Added to cart successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Add to cart failed", Toast.LENGTH_SHORT).show();
        }
    }
}
