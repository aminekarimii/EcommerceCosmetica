package com.classroom.app1.Helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Singleton {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static FirebaseFirestore getDb() {
        return db;
    }
    public static FirebaseUser getUser() {
        return user;
    }

}
