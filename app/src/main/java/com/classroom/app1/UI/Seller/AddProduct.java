package com.classroom.app1.UI.Seller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.classroom.app1.Helpers.DataStatus;
import com.classroom.app1.Helpers.DataStatusImage;
import com.classroom.app1.Helpers.Singleton;
import com.classroom.app1.Model.Product;
import com.classroom.app1.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mehdi.sakout.fancybuttons.FancyButton;

public class AddProduct extends AppCompatActivity {

    private EditText fieldNom, fieldDesc, fieldPrice;
    private FancyButton add_product_button;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String text;
    private FirebaseFirestore db = Singleton.getDb();
    private FirebaseUser user = Singleton.getUser();
    private static Product product;
    private Uri filePath;
    private ArrayList<String> list = new ArrayList<>();
    private Spinner spinner;
    private int time;
    private ArrayList urls_pictures;
    private ImageButton picture_added, upload_imgs;
    StorageReference storageReference;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Views
        fieldNom = findViewById(R.id.fieldAddNom);
        fieldDesc = findViewById(R.id.fieldAddDesc);
        fieldPrice = findViewById(R.id.fieldAddPrice);
        picture_added = findViewById(R.id.picture_added);
        //buttons
        add_product_button = findViewById(R.id.add_product_button);

        picture_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        product = new Product();

        getCategories(new DataStatus() {
            @Override
            public void onSuccess(ArrayList list) {
                spinner = findViewById(R.id.spinner);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddProduct.this,
                        R.layout.spinner_item, (ArrayList<String>) list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
            }

            @Override
            public void onError(String e) {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();

            uploadImage(new DataStatusImage() {
                @Override
                public void onSuccess(final Uri uri) {

                    Glide.with(AddProduct.this).load(uri).into(picture_added);
                    add_product_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList img = new ArrayList();
                            img.add(uri.toString());
                            if (checkContent(fieldNom) && checkContent(fieldDesc) && checkContent(fieldPrice)) {
                                product.setDescription(fieldDesc.getText().toString());
                                product.setNom(fieldNom.getText().toString());
                                product.setId_seller(user.getUid());
                                product.setId_cat(String.valueOf(spinner.getSelectedItem()));
                                product.setPrice(Double.valueOf(fieldPrice.getText().toString()));
                                product.setImg_product(uri.toString());
                                product.setImg(img);
                                addOrder(product);
                                Toast.makeText(AddProduct.this, "Produit Ajouté", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddProduct.this, "Compléter les champs nécessaires", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Toast.makeText(AddProduct.this, "uploaded", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String e) {

                }
            });
        }


    }

    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    private void uploadImage(final DataStatusImage statusImageCallback) {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/" + user.getUid() + "/" + UUID.randomUUID().toString());


            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            //statusImageCallback.onSuccess("images/"+user.getUid()+"/" + UUID.randomUUID().toString());
                            Log.v("getDownloadUrl", ref.getDownloadUrl().toString());

                            //final StorageReference refs = ref.child("images/mountains.jpg");
                            UploadTask uploadTask = ref.putFile(filePath);

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    Log.v("getDownloadUrl", ref.getDownloadUrl().toString());
                                    return ref.getDownloadUrl();

                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        statusImageCallback.onSuccess(downloadUri);
                                    } else {
                                        Toast.makeText(AddProduct.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                            //Toast.makeText(AddProduct.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            statusImageCallback.onError("Error");
                            Toast.makeText(AddProduct.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    public boolean checkContent(EditText editText) {
        text = editText.getText().toString();
        return !TextUtils.isEmpty(text);
    }

    public void addOrder(Product mProduct) {

        Map<String, Object> product = new HashMap<>();
        product.put("description", mProduct.getDescription());
        product.put("nom", mProduct.getNom());
        product.put("price", mProduct.getPrice());
        product.put("id_cat", mProduct.getId_cat());
        product.put("id_seller", mProduct.getId_seller());
        product.put("img_product", mProduct.getImg_product());
        product.put("img", mProduct.getImg());

        db.collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.v("dataAdded", documentReference.getId());

                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

                Log.v("dataAdded", "failed");
            }
        });

    }

    public void getCategories(final DataStatus callback) {

        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //if (document.getString("name") != null)
                                list.add(document.getId());
                            }
                            callback.onSuccess(list);

                        } else {
                            callback.onError("Error in data");
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });

    }
}
