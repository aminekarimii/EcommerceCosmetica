package com.classroom.app1.UI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.classroom.app1.Helpers.Singleton;
import com.classroom.app1.Helpers.TinyDB;
import com.classroom.app1.Model.Product;
import com.classroom.app1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class ProductActivity extends AppCompatActivity {
    private static final String TAG = "wach";
    String nomItem, descItem, idItem, idSeller, idCat;
    ArrayList imgItem;
    Double priceItem;
    TextView title_item, price_item, description_item;
    CircleImageView profile;
    private FancyButton buy;
    private TinyDB tinydb;
    private boolean clicked;
    TextView seller_name;
    String emails, tels;
    Product productHolder;
    SliderLayout sliderLayout;
    private FirebaseUser user = Singleton.getUser();

    private FirebaseFirestore db = Singleton.getDb();


    //BottomSheetBehavior sheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);

        nomItem = (String) getIntent().getSerializableExtra("nomItem");
        descItem = (String) getIntent().getSerializableExtra("descItem");
        imgItem = (ArrayList) getIntent().getSerializableExtra("imgItem");
        idItem = (String) getIntent().getSerializableExtra("idItem");
        priceItem = (Double) getIntent().getSerializableExtra("priceItem");

        idSeller = (String) getIntent().getSerializableExtra("idSeller");
        idCat = (String) getIntent().getSerializableExtra("idCat");

        tinydb = new TinyDB(this);

        Log.v("imgsArray", imgItem.get(0).toString());

        //Setting items
        title_item = findViewById(R.id.title_item);
        description_item = findViewById(R.id.description_item);
        description_item = findViewById(R.id.description_item);
        price_item = findViewById(R.id.price_item);
        //image = findViewById(R.id.image);

        seller_name = findViewById(R.id.seller_name);
        profile = findViewById(R.id.profile_image);


        buy = findViewById(R.id.buy_now);


        if (alreadyExist(idItem, tinydb)) {
            buy.setBackgroundColor(getResources().getColor(R.color.md_red_400));
            clicked = true;
        } else {
            buy.setBackgroundColor(getResources().getColor(R.color.md_black_1000));
            clicked = false;
        }

        sliderLayout = findViewById(R.id.image);
        //sliderLayout.setIndicatorAnimation(SliderLayout.; //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(3); //set scroll delay in seconds :

        title_item.setText(nomItem);
        description_item.setText(descItem);
        price_item.setText(priceItem + "MAD");

        //Log.v("imgfromArray", imgItem.get(1).toString());

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked) {
                    productHolder = new Product(idItem, idSeller, idCat, nomItem, descItem, (String) imgItem.get(0), priceItem);
                    WriteInShared(productHolder, tinydb);
                    Toast.makeText(ProductActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    buy.setBackgroundColor(getResources().getColor(R.color.md_red_400));
                    clicked = true;
                } else {
                    removeFromArrayList(idItem, tinydb);
                    Toast.makeText(ProductActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                    buy.setBackgroundColor(getResources().getColor(R.color.md_black_1000));
                    clicked = false;
                }
            }
        });


        db.collection("Users").document(idSeller)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Document found in the offline cache
                                    DocumentSnapshot document = task.getResult();
                                    seller_name.setText(document.getString("Nom"));
                                    tels = document.getString("Telephone");
                                    emails = document.getString("Email");
                                    Glide.with(ProductActivity.this)
                                            .load(document.getString("image"))
                                            .into(profile);
                                    Log.d(TAG, "Cached document data: " + document.getData());
                                } else {
                                    Log.d(TAG, "Cached get failed: ", task.getException());
                                }
                            }
                        });

        setSliderViews();


        ButterKnife.bind(this);


    }

    public void WriteInShared(Product product, TinyDB tinydb) {
        tinydb = new TinyDB(getApplicationContext());

        ArrayList<Product> postObjects = tinydb.getListObject(user.getUid(), Product.class);

        ArrayList<Product> myfav = new ArrayList<>();

        for (Object objs : postObjects) {
            myfav.add((Product) objs);
        }

        myfav.add(product);
        tinydb.putListObject(user.getUid(), myfav);
    }

    public void removeFromArrayList(String id_product, TinyDB tinydb) {
        ArrayList<Product> myfavSaved;
        myfavSaved = tinydb.getListObject(user.getUid(), Product.class);
        for (int i = 0; i < myfavSaved.size(); i++) {
            if (myfavSaved.get(i).getId_product().equalsIgnoreCase(id_product)) {
                myfavSaved.remove(i);
                saveFav(myfavSaved, tinydb);
            }
        }
        Toast.makeText(getApplicationContext(), "Removed from Cart", Toast.LENGTH_SHORT).show();
    }

    public void saveFav(ArrayList<Product> arrayList, TinyDB tinydb) {
        tinydb.putListObject(user.getUid(), arrayList);
    }

    public boolean alreadyExist(String id_product, TinyDB tinydb) {
        ArrayList<Product> myfavSaved;
        myfavSaved = tinydb.getListObject(user.getUid(), Product.class);
        for (int i = 0; i < myfavSaved.size(); i++) {
            if (myfavSaved.get(i).getId_product().equalsIgnoreCase(id_product)) {
                return true;
            }
        }
        return false;
    }


    private void setSliderViews() {

        for (int i = 0; i < imgItem.size(); i++) {

            DefaultSliderView sliderView = new DefaultSliderView(this);

            sliderView.setImageUrl(imgItem.get(i).toString());

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            //sliderView.setDescription("setDescription " + (i + 1));
            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    //Toast.makeText(ProductActivity.this, "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show();
                }
            });

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }
    }


    /**
     * showing bottom sheet dialog
     */
    @OnClick(R.id.btn_bottom_sheet_dialog)
    public void showBottomSheetDialog() {

        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet, null);
        TextView email = view.findViewById(R.id.emailSeller);
        TextView tel = view.findViewById(R.id.TelSeller);
        email.setText(emails);
        tel.setText(tels);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
    }


}
