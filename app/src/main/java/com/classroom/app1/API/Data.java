package com.classroom.app1.API;

import android.support.annotation.NonNull;

import com.classroom.app1.Helpers.DataStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class Data {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    private void processData(Map myMap) {
        Map myMap1 = myMap;
    }

    private void getCategories(final DataStatus callback) {

        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Map list = null;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list = document.getData();
                            }
                            processData(list);

                        } else {
                            callback.onError("Error in data");
                        }

                    }
                });
    }
}
