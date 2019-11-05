package com.classroom.app1.UI.Fragements;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.classroom.app1.Helpers.DataStatus;
import com.classroom.app1.Helpers.Singleton;
import com.classroom.app1.Model.Order;
import com.classroom.app1.Model.Product;
import com.classroom.app1.R;
import com.classroom.app1.UI.Adapters.SellerOrdersAdapter;
import com.classroom.app1.UI.ClickListeners.RecyclerViewClickListenerOrder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InProcessSeller extends Fragment {

    RecyclerView recyclerView;
    private static final String TAG = "awach a 3mi";
    private FirebaseFirestore db = Singleton.getDb();
    private SellerOrdersAdapter adapter;

    private ArrayList<Order> ordersList = new ArrayList<>();
    private FirebaseUser user = Singleton.getUser();
    private Product product;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private RecyclerViewClickListenerOrder mclickListener;

    public InProcessSeller() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getProducts(new DataStatus() {
            @Override
            public void onSuccess(ArrayList list) {
                if (adapter == null) {
                    adapter = new SellerOrdersAdapter(list, getContext(), mclickListener);
                    recyclerView.setAdapter(adapter);
                    //hideProgressDialog();
                } else {
                    adapter.getItems().clear();
                    adapter.getItems().addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String e) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inprocess__seller, container, false);

        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.users_orders_rc_inprocess);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        return view;
    }


    private void getProducts(final DataStatus callback) {
        db.collection("orders").whereEqualTo("status", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<Map> productsinorder = (ArrayList<Map>) document.get("products");
                                ArrayList<Product> productArrayList = new ArrayList<>();

                                Map myMap;
                                Set<String> ids = new TreeSet<>();

                                if (productsinorder != null) {
                                    for (int i = 0; i < productsinorder.size(); i++) {
                                        product = new Product();
                                        myMap = productsinorder.get(i);
                                        product.setPrice((Double) myMap.get("price"));
                                        product.setImg_product(myMap.get("img_product").toString());
                                        product.setNom(myMap.get("nom").toString());
                                        product.setId_seller(myMap.get("id_seller").toString());
                                        product.setDescription(myMap.get("description").toString());
                                        product.setId_cat(myMap.get("id_cat").toString());

                                        if (user.getUid().equals(product.getId_seller())) {
                                            productArrayList.add(product);
                                        }
                                        ids.add(product.getId_seller());
                                    }
                                }

                                // List of categories here
                                for (String s : ids) {
                                    if (user.getUid().equals(s)) {
                                        Order order = new Order(
                                                document.getId(),
                                                productArrayList,
                                                (String) document.get("id_user"),
                                                (Long) document.get("status"));
                                        ordersList.add(order);
                                    }
                                }


                            }
                            Log.v("apres2", "" + ordersList);
                            callback.onSuccess(ordersList);

                        } else {
                            //callback.onError("Error in data");
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });

    }

}