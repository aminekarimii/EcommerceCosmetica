package com.classroom.app1.UI;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.classroom.app1.Helpers.Singleton;
import com.classroom.app1.Helpers.TinyDB;
import com.classroom.app1.Model.Product;
import com.classroom.app1.R;
import com.classroom.app1.UI.Adapters.CartProductAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class Cart extends AppCompatActivity implements View.OnClickListener {

    private CartProductAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Product> listItems = new ArrayList<>();
    private FirebaseFirestore db = Singleton.getDb();
    private TinyDB tinydb;
    FancyButton rate;
    private Dialog myDialog;
    private FirebaseUser user = Singleton.getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_rc);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Cart.this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        fetchRemoteData();

    }

    private void fetchRemoteData() {
        tinydb = new TinyDB(Cart.this);
        ArrayList<Product> productsOnCart = tinydb.getListObject(user.getUid(), Product.class);
        for (int i = 0; i < productsOnCart.size(); i++) {
            Product item = new Product(productsOnCart.get(i).getId_product(),
                    productsOnCart.get(i).getId_seller(),
                    productsOnCart.get(i).getId_cat(),
                    productsOnCart.get(i).getNom(),
                    productsOnCart.get(i).getDescription(),
                    productsOnCart.get(i).getImg_product(),
                    productsOnCart.get(i).getPrice()
            );
            listItems.add(item);
        }
        if (listItems.size() != 0) {
            //favtext.setVisibility(View.GONE);
        }

        findViewById(R.id.checkout_button).setOnClickListener(this);
        Collections.reverse(listItems);
        onSuccess(listItems);

    }

    public void onSuccess(ArrayList<Product> posts) {
        TextView total_price = findViewById(R.id.total_price);
        if (adapter == null) {
            adapter = new CartProductAdapter(posts, Cart.this);
            recyclerView.setAdapter(adapter);
            if (!posts.isEmpty()) {
                int totalPrice = 0;
                for (int i = 0; i < posts.size(); i++) {
                    totalPrice += posts.get(i).getPrice();
                }
                total_price.setText("Your Total Price: " + totalPrice + "MAD");
            } else {
                total_price.setVisibility(View.GONE);
            }
        } else {
            adapter.getItems().clear();
            adapter.getItems().addAll(posts);
            adapter.notifyDataSetChanged();
        }
    }

    public void addOrder() {

        Map<String, Object> productOrder = new HashMap<>();
        productOrder.put("products", listItems);
        productOrder.put("id_user", user.getUid());
        productOrder.put("status", 0);

        db.collection("orders")
                .add(productOrder)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.v("dataAdded", documentReference.getId());
                        tinydb.remove(user.getUid());

                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

                Log.v("dataAdded", "failed");
            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkout_button) {
            myDialog = new Dialog(Cart.this);
            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            assert myDialog.getWindow() != null;
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.setCancelable(false);
            myDialog.setContentView(R.layout.rate_us_dialog);
            rate = myDialog.findViewById(R.id.rateButton);

            myDialog.show();

            myDialog.findViewById(R.id.close_rate_dialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });
            rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addOrder();
                    startActivity(new Intent(Cart.this, UsersOrders.class));
                    myDialog.dismiss();
                }
            });

        }
    }
}
