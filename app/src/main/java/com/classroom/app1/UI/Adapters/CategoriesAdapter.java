package com.classroom.app1.UI.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.classroom.app1.Model.Categories;
import com.classroom.app1.R;
import com.classroom.app1.UI.ClickListeners.RecyclerViewClickListener;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private ArrayList<Categories> categories;
    private Context context;
    private RecyclerViewClickListener mClickListener;

    public CategoriesAdapter(ArrayList<Categories> categories, Context context, RecyclerViewClickListener mClickListener) {
        this.categories = categories;
        this.context = context;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cat_cardview,
                viewGroup, false);
        return new ViewHolder(v);
    }

    public ArrayList<Categories> getItems() {
        return categories;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bindModel(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Categories mCats;
        ImageView icon;
        TextView title;

        ViewHolder(View v) {
            super(v);
            icon = itemView.findViewById(R.id.cat_icon);
            title = itemView.findViewById(R.id.cat_title);

            v.setOnClickListener(this);
        }

        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
        void bindModel(Categories categories) {

            this.mCats = categories;

            //title.setText(mCats.getName());

            Glide
                    .with(context)
                    .load(mCats.getIcon())
                    //.placeholder(new ColorDrawable(R.color.colorPrimaryDark))
                    .into(icon);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onClick(view, mCats);
        }
    }
}
