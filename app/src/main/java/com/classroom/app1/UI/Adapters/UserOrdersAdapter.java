package com.classroom.app1.UI.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.classroom.app1.Model.Order;
import com.classroom.app1.Model.Product;
import com.classroom.app1.R;
import com.classroom.app1.UI.Cart;
import com.classroom.app1.UI.ClickListeners.RecyclerViewClickListenerProduct;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserOrdersAdapter extends RecyclerView.Adapter<UserOrdersAdapter.ViewHolder> {

    private ArrayList<Order> orders;
    private Context context;
    View v;
    private FirebaseFirestore db;
    //private RecyclerViewClickListenerProduct mClickListener;

    public UserOrdersAdapter(ArrayList<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
        //this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        db = FirebaseFirestore.getInstance();
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.produit_userorders,
                viewGroup, false);

        return new ViewHolder(v);
    }

    public ArrayList<Order> getItems() {
        return orders;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bindModel(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Order mOrder;
        TextView orderNom, produitPrice, orderID;
        RecyclerView rc_products;


        ViewHolder(View v) {
            super(v);
            orderNom = itemView.findViewById(R.id.product_title_cart);
            produitPrice = itemView.findViewById(R.id.product_price_cart);
            orderID = itemView.findViewById(R.id.id_order);
            rc_products = itemView.findViewById(R.id.rc_products);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false);
            rc_products.setLayoutManager(layoutManager);
            rc_products.setHasFixedSize(true);

        }

        @SuppressLint("SetTextI18n")
        void bindModel(Order order) {

            this.mOrder = order;
            ArrayList mOrderProducts  = mOrder.getProducts();
            orderNom.setText(mOrder.getId_order());

            if (mOrderProducts.size() != 0){
                ProductsOrdersAdapter productAdapter = new ProductsOrdersAdapter(mOrderProducts, context);
                rc_products.setAdapter(productAdapter);
                produitPrice.setText("number of Products: "+mOrderProducts.size());
            }

        }

    }


}
