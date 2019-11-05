package com.classroom.app1.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.classroom.app1.Helpers.DataStatus;
import com.classroom.app1.Helpers.Singleton;
import com.classroom.app1.Model.Product;
import com.classroom.app1.R;
import com.classroom.app1.UI.Adapters.ProductAdapter;
import com.classroom.app1.UI.ClickListeners.RecyclerViewClickListenerProduct;
import com.classroom.app1.UI.Seller.AddProduct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class ProductsListActivity extends AppCompatActivity {

    private FirebaseFirestore db = Singleton.getDb();
    private ArrayList<Product> productsList = new ArrayList<>();
    private String categorieId;
    private ProductAdapter adapter;
    private Button add_product;
    private boolean isSeller;
    private FirebaseUser user = Singleton.getUser();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);

        isSeller = (boolean) getIntent().getSerializableExtra("isSeller");
        String categorieIdUser = (String) getIntent().getSerializableExtra("catName");

        FirebaseApp.initializeApp(this);

        add_product = findViewById(R.id.add_product_button_listprodcuts);

        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent products_seller = new Intent(ProductsListActivity.this, AddProduct.class);
                startActivity(products_seller);
            }
        });

        recyclerView = findViewById(R.id.products_list_rc);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        if (isSeller) {
            add_product.setVisibility(View.VISIBLE);
            String idSel = (String) getIntent().getSerializableExtra("sellerID");
            Log.v("dataFireSecsel", idSel);
            categorieId = idSel;
            fetchData();
        } else {
            add_product.setVisibility(View.GONE);
            categorieId = categorieIdUser;
            fetchData();
        }


    }

    private void fetchData() {
        if (isSeller) {
            getDataBySeller(new DataStatus() {
                @Override
                public void onSuccess(ArrayList products) {
                    if (adapter == null) {
                        adapter = new ProductAdapter(products, ProductsListActivity.this,
                                new RecyclerViewClickListenerProduct() {
                                    @Override
                                    public void onClick(View view, Product product) {
                                        Intent productPage = new Intent(ProductsListActivity.this, ProductActivity.class);
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
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.getItems().clear();
                        adapter.getItems().addAll(products);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onError(String e) {
                    Toast.makeText(ProductsListActivity.this, e, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            getData(new DataStatus() {
                @Override
                public void onSuccess(ArrayList products) {
                    if (adapter == null) {
                        adapter = new ProductAdapter(products, ProductsListActivity.this,
                                new RecyclerViewClickListenerProduct() {
                                    @Override
                                    public void onClick(View view, Product product) {
                                        Intent productPage = new Intent(ProductsListActivity.this, ProductActivity.class);
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
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.getItems().clear();
                        adapter.getItems().addAll(products);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onError(String e) {
                    Toast.makeText(ProductsListActivity.this, e, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void getData(final DataStatus callback) {

        db.collection("products").whereEqualTo("id_cat", categorieId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.v("dataFireSec", document.getData().toString());

                                ArrayList<String> imgs = new ArrayList<>();
                                Map<String, Object> myMap = document.getData();
                                for (Map.Entry<String, Object> entry : myMap.entrySet()) {
                                    if (entry.getKey().equals("img")) {
                                        for (Object s : (ArrayList) entry.getValue()) {
                                            imgs.add((String) s);
                                        }
                                        //Log.v("TagImg", entry.getValue().toString());

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
                            Log.w("error", "Error getting documents.", task.getException());
                        }

                    }
                });
    }

    private void getDataBySeller(final DataStatus callback) {

        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (user.getUid().matches(document.get("id_seller").toString())) {

                                    Log.v("dataFireSec", document.getData().toString());

                                    ArrayList<String> imgs = new ArrayList<>();
                                    Map<String, Object> myMap = document.getData();
                                    for (Map.Entry<String, Object> entry : myMap.entrySet()) {
                                        if (entry.getKey().equals("img")) {
                                            for (Object s : (ArrayList) entry.getValue()) {
                                                imgs.add((String) s);
                                            }
                                            //Log.v("TagImg", entry.getValue().toString());

                                        }
                                    }


                                    Product product = new Product(document.getId(),
                                            (String) myMap.get("id_seller"),
                                            (String) myMap.get("id_cat"),
                                            (String) myMap.get("nom"),
                                            (String) myMap.get("description"),
                                            imgs,
                                            (Double)myMap.get("price"));
                                    productsList.add(product);
                                }
                            }
                            callback.onSuccess(productsList);

                        } else {
                            callback.onError("Error in data");
                            Log.w("error", "Error getting documents.", task.getException());
                        }

                    }
                });
    }


}
