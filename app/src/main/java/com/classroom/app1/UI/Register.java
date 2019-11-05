package com.classroom.app1.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.classroom.app1.Helpers.BaseActivity;
import com.classroom.app1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class Register extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText mNomField;
    private EditText mPrenomField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mRepasswordField;
    private CheckBox sellerCheck;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Views

        mNomField = findViewById(R.id.fieldNom);
        mPrenomField = findViewById(R.id.fieldPrenom);
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        mRepasswordField = findViewById(R.id.fieldrePassword);
        sellerCheck = findViewById(R.id.checkSeller);
        // Buttons
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.signedInButton).setOnClickListener(this);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            putData(user.getUid(),user.getEmail(),mNomField.getText().toString(),mPrenomField.getText().toString());
                            updateUI(user);
                            getIdS();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "createUser failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }



    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        String repass = mRepasswordField.getText().toString();
        if(password.equals(repass))
        {
            mPasswordField.setError(null);

        } else {
            mPasswordField.setError("Erreur");
            mRepasswordField.setError("Erreur");
            valid = false;
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            user.getEmail();
            user.isEmailVerified();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
        if(i == R.id.signedInButton){
            Intent my = new Intent(this,Login.class);
            startActivity(my);
            finish();
        }

    }

    public void putData(String id , String email , String nom , String prenom  ){
        Map<String, Object> user = new HashMap<>();
        user.put("Email", email);
        user.put("Nom", nom);
        user.put("Prenom", prenom);
        user.put("Telephone","");
        user.put("image", "");

        if(sellerCheck.isChecked()){
            user.put("seller","true");
        }else{ user.put("seller","false"); }

// Add a new document with a generated ID
        db.collection("Users").document(id)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
    protected void getIdS() {
        showProgressDialog();
        db.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getString("seller").equals("true")) {
                            startActivity(new Intent(Register.this, Profil.class));
                            finish();
                        } else {
                            startActivity(new Intent(Register.this, HomeActivity.class));
                            finish();
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


}