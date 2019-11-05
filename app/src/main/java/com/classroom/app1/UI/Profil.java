package com.classroom.app1.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.classroom.app1.Helpers.BaseActivity;
import com.classroom.app1.Helpers.DataStatusImage;
import com.classroom.app1.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profil extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "Profile";
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    private Uri filePath;
    StorageReference storageReference;
    FirebaseStorage storage;
    private CircleImageView profilImg;
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViewById(R.id.action_settings).setOnClickListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        profilImg = findViewById(R.id.ImgUserV);
        profilImg.setOnClickListener(this);
        getProfile();
    }


    protected void getProfile() {
        showProgressDialog();

        db.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        setProfil(document.getString("Nom"), document.getString("Prenom"), document.getString("Telephone"), document.getString("Email"), document.getString("Ville"));
                        if (!document.getString("image").isEmpty()){
                            bindModel(document.getString("image"));
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                    hideProgressDialog();
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.action_settings) {
            findViewById(R.id.facebook).setEnabled(true);
            findViewById(R.id.phone).setEnabled(true);
            findViewById(R.id.email).setEnabled(true);
        } else if (i == R.id.ImgUserV) {
            pickFromGallery();
        }
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
                    Glide.with(Profil.this).load(uri).into(profilImg);
                    Map<String, Object> data = new HashMap<>();
                    data.put("image", uri.toString());

                    db.collection("Users").document(mAuth.getUid())
                            .set(data, SetOptions.merge());
                    Toast.makeText(Profil.this, "uploaded", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String e) {

                }
            });
        }


    }

    void bindModel(String url) {
        CircleImageView img = findViewById(R.id.ImgUserV);
        Glide
                .with(this.getApplicationContext())
                .load(url)
                .into(img);
    }

    private void uploadImage(final DataStatusImage statusImageCallback) {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/" + mAuth.getUid() + "/" + UUID.randomUUID().toString());


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
                                        Toast.makeText(Profil.this, "Error", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Profil.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    public void setProfil(String nom, String prenom, String tel, String mail, String ville) {
        TextView Fullname, Fullprename;
        EditText name, prename, phone, email, city;
        Fullname = findViewById(R.id.tv_prename);
        Fullname.setText(prenom);
        Fullprename = findViewById(R.id.tv_name);
        Fullprename.setText(nom);
        name = findViewById(R.id.nom);
        name.setText(nom);
        prename = findViewById(R.id.prenom);
        prename.setText(prenom);
        phone = findViewById(R.id.phone);
        phone.setText(tel);
        email = findViewById(R.id.email);
        email.setText(mail);
        city = findViewById(R.id.tv_address);
        city.setText(ville);
    }

    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(this, Login.class));
                return true;
            case R.id.orders:
                startActivity(new Intent(this, UsersOrders.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
