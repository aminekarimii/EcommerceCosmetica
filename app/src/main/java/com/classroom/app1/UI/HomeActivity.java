package com.classroom.app1.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.classroom.app1.Helpers.DataStatus;
import com.classroom.app1.Helpers.Singleton;
import com.classroom.app1.Model.Categories;
import com.classroom.app1.Model.Product;
import com.classroom.app1.R;
import com.classroom.app1.UI.Adapters.CategoriesAdapter;
import com.classroom.app1.UI.Adapters.ProductAdapter;
import com.classroom.app1.UI.ClickListeners.RecyclerViewClickListener;
import com.classroom.app1.UI.ClickListeners.RecyclerViewClickListenerProduct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "data";
    private RecyclerView recyclerView, recyclerView_products;
    private CategoriesAdapter adapter;
    private ProductAdapter products_adapter;
    private ArrayList<Categories> catList2 = new ArrayList<>();
    private ArrayList<Product> productsList = new ArrayList<>();
    private FirebaseFirestore db = Singleton.getDb();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);


        FirebaseApp.initializeApp(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.categories_rc);
        recyclerView_products = findViewById(R.id.products_rc);

        BottomNavigationView mBottomNav = findViewById(R.id.navigation_home);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_button:
                        Intent home = new Intent(getApplicationContext(), UsersOrders.class);
                        startActivity(home);
                        break;
                    case R.id.navigation_cart:
                        Intent cart = new Intent(getApplicationContext(), Cart.class);
                        startActivity(cart);
                        break;
                    case R.id.profil_button:
                        Intent profil = new Intent(getApplicationContext(), Profil.class);
                        startActivity(profil);
                        break;
                }
                return true;
            }
        });

        init();
        fetchData();
        fetchProducts();


    }

    private void fetchProducts() {
        getProducts(new DataStatus() {
            @Override
            public void onSuccess(ArrayList products) {
                if (products_adapter == null) {
                    products_adapter = new ProductAdapter(productsList, HomeActivity.this, new RecyclerViewClickListenerProduct() {
                                @Override
                                public void onClick(View view, Product product) {
                                    Intent productPage = new Intent(HomeActivity.this, ProductActivity.class);
                                    productPage.putExtra("nomItem", product.getNom());
                                    productPage.putExtra("descItem", product.getDescription());
                                    productPage.putExtra("idItem", product.getId_product());
                                    productPage.putExtra("imgItem", product.getImg());
                                    productPage.putExtra("priceItem", product.getPrice());
                                    productPage.putExtra("idSeller", product.getId_seller());
                                    productPage.putExtra("idCat", product.getId_cat());
                                    startActivity(productPage);
                                }
                            });
                    recyclerView_products.setAdapter(products_adapter);
                } else {
                    products_adapter.getItems().clear();
                    products_adapter.getItems().addAll(productsList);
                    products_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String e) {
                Toast.makeText(HomeActivity.this, e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchData() {
        getCategories(new DataStatus() {
            @Override
            public void onSuccess(ArrayList categories) {
                if (adapter == null) {
                    adapter = new CategoriesAdapter(categories, HomeActivity.this,
                            new RecyclerViewClickListener() {
                                @Override
                                public void onClick(View view, Categories categories) {
                                    Intent productPage = new Intent(HomeActivity.this, ProductsListActivity.class);
                                    productPage.putExtra("catName", categories.getId_cat());
                                    productPage.putExtra("isSeller", false);
                                    startActivity(productPage);
                                    //overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);

                                }
                            });
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.getItems().clear();
                    adapter.getItems().addAll(categories);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String e) {
                Toast.makeText(HomeActivity.this, e, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getCategories(final DataStatus callback) {

        //final ArrayList<Categories> catList = new ArrayList<>();

        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map myMap = document.getData();
                                Log.v("dataFire", (String) myMap.toString());

                                // List of categories here
                                Categories categorie = new Categories(document.getId(), (String) myMap.get("name"), (String) myMap.get("icon"), (String) myMap.get("color"));
                                catList2.add(categorie);
                            }
                            callback.onSuccess(catList2);

                        } else {
                            callback.onError("Error in data");
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });
    }

    private void getProducts(final DataStatus callback) {

        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.v("dataFireSec",document.getId()+" "+document.get("img"));

                                ArrayList<String> imgs = new ArrayList<>();
                                Map<String, Object> myMap = document.getData();
                                for (Map.Entry<String, Object> entry : myMap.entrySet()) {
                                    if (entry.getKey().equals("img")) {
                                        for (Object s : (ArrayList) entry.getValue()) {
                                            imgs.add((String) s);
                                        }
                                        Log.v("TagImg", entry.getValue().toString());

                                    }
                                }

                                Product product = new Product(document.getId(),
                                        (String) myMap.get("id_seller"),
                                        (String) myMap.get("id_cat"),
                                        (String) myMap.get("nom"),
                                        (String) myMap.get("description"),
                                        imgs,
                                        (Double) myMap.get("price"));
                                productsList.add(product);
                            }
                            callback.onSuccess(productsList);

                        } else {
                            callback.onError("Error in data");
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });
    }


    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);

        recyclerView_products.setLayoutManager(gridLayoutManager);
        recyclerView_products.setNestedScrollingEnabled(false);
    }


}
