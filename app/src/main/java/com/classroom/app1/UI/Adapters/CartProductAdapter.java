package com.classroom.app1.UI.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.classroom.app1.Model.Product;
import com.classroom.app1.R;
import com.classroom.app1.UI.ClickListeners.RecyclerViewClickListenerProduct;

import java.util.ArrayList;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.ViewHolder> {

    private ArrayList<Product> products;
    private Context context;
    //private RecyclerViewClickListenerProduct mClickListener;

    public CartProductAdapter(ArrayList<Product> products, Context context) {
        this.products = products;
        this.context = context;
        //this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.produit_card,
                viewGroup, false);
        return new ViewHolder(v);
    }

    public ArrayList<Product> getItems() {
        return products;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bindModel(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Product mProduct;
        ImageView productImg;
        TextView produitNom;
        TextView produitPrice;


        ViewHolder(View v) {
            super(v);
            productImg = itemView.findViewById(R.id.product_img_cart);
            produitNom = itemView.findViewById(R.id.product_title_cart);
            produitPrice = itemView.findViewById(R.id.product_price_cart);

            //v.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void bindModel(Product products) {

            this.mProduct = products;
            produitNom.setText(mProduct.getNom());
            produitPrice.setText(mProduct.getPrice().toString()+"MAD");


            Glide
                    .with(context)
                    .load(mProduct.getImg_product())
                    .into(productImg);
        }

        //@Override
        /*public void onClick(View view) {
            mClickListener.onClick(view, mProduct);
        }*/
    }
}
