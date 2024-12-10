package com.example.feedmypet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.feedmypet.DBconnector.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ListView cartListView;
    private TextView totalTextView;
    private Button checkoutButton;
    private CartAdapter cartAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartListView = findViewById(R.id.cartListView);
        totalTextView = findViewById(R.id.totalTextView);
        checkoutButton = findViewById(R.id.checkoutButton);

        databaseHelper = new DatabaseHelper(this);

        loadCartItems();

        checkoutButton.setOnClickListener(view -> {
            // Implement checkout functionality
            Toast.makeText(CartActivity.this, "Checkout clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCartItems() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("cart", null, null, null, null, null, null);

        List<CartItem> cartItems = new ArrayList<>();
        double total = 0.0;

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("product_name"));
            double price = cursor.getDouble(cursor.getColumnIndex("product_price"));
            int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));

            total += price * quantity;
            cartItems.add(new CartItem(id, name, price, quantity));
        }

        cursor.close();
        db.close();

        totalTextView.setText("Total: $" + total);

        cartAdapter = new CartAdapter(this, cartItems);
        cartListView.setAdapter(cartAdapter);
    }

    private class CartAdapter extends BaseAdapter {

        private Context context;
        private List<CartItem> cartItems;

        public CartAdapter(Context context, List<CartItem> cartItems) {
            this.context = context;
            this.cartItems = cartItems;
        }

        @Override
        public int getCount() {
            return cartItems.size();
        }

        @Override
        public Object getItem(int position) {
            return cartItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return cartItems.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
            }

            TextView nameTextView = convertView.findViewById(R.id.cartItemName);
            TextView quantityTextView = convertView.findViewById(R.id.cartItemQuantity);
            TextView priceTextView = convertView.findViewById(R.id.cartItemPrice);
            Button deleteButton = convertView.findViewById(R.id.deleteButton);

            CartItem cartItem = cartItems.get(position);

            nameTextView.setText(cartItem.getName());
            quantityTextView.setText("Qty: " + cartItem.getQuantity());
            priceTextView.setText("$" + cartItem.getPrice());

            deleteButton.setOnClickListener(view -> {
                deleteCartItem(cartItem.getId());
                cartItems.remove(position);
                notifyDataSetChanged();
                updateTotal();
            });

            return convertView;
        }

        private void deleteCartItem(int id) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.delete("cart", "id = ?", new String[]{String.valueOf(id)});
            db.close();
        }

        private void updateTotal() {
            double total = 0.0;
            for (CartItem item : cartItems) {
                total += item.getPrice() * item.getQuantity();
            }
            totalTextView.setText("Total: $" + total);
        }
    }

    private static class CartItem {
        private int id;
        private String name;
        private double price;
        private int quantity;

        public CartItem(int id, String name, double price, int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
