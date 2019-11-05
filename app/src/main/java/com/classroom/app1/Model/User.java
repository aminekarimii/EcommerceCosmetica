package com.classroom.app1.Model;

import android.support.annotation.NonNull;

import com.classroom.app1.Helpers.DataStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class User {
    String email;
    String nom;
    String prenom;
    String password;
    String image;
    String tel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    protected  void  getProfile(final DataStatus callback) {
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Map profile = null;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                profile = document.getData();
                            }
                        } else {
                           // Toast.makeText(Profile.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
