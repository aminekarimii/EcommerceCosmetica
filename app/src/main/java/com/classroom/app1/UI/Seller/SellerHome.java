package com.classroom.app1.UI.Seller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import com.classroom.app1.Helpers.DataStatus;
import com.classroom.app1.Helpers.Singleton;
import com.classroom.app1.Model.Order;
import com.classroom.app1.Model.Product;
import com.classroom.app1.R;
import com.classroom.app1.UI.Login;
import com.classroom.app1.UI.ProductsListActivity;
import com.classroom.app1.UI.Profil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SellerHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    FirebaseAuth mAuth;
    private FirebaseFirestore db = Singleton.getDb();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        findViewById(R.id.products_seller).setOnClickListener(this);
        findViewById(R.id.orders_seller).setOnClickListener(this);

        getProducts();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.seller_home, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profil) {
            startActivity(new Intent(this, Profil.class));
            finishAffinity();
        }
        else if (id == R.id.nav_logout) {
            mAuth.signOut();
            startActivity(new Intent(this, Login.class));
            finishAffinity();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getProducts() {

        db.collection("products").whereEqualTo("id_seller", mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                count++;
                            }
                            TextView pd = findViewById(R.id.productcounts);
                            pd.setText(String.valueOf(count));

                        }

                    }
                });

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.products_seller) {
            Intent products_seller = new Intent(this, ProductsListActivity.class);
            products_seller.putExtra("isSeller", true);
            products_seller.putExtra("sellerID", mAuth.getCurrentUser().getUid());
            startActivity(products_seller);
        }else if (v.getId() == R.id.orders_seller){
            Intent products_seller = new Intent(this, SellersOrders.class);
            startActivity(products_seller);
        }
    }
}
