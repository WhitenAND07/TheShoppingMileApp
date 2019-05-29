package com.example.shoppingmileapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.content.Intent;
import android.util.Log;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.example.shoppingmileapp.R;
import com.example.shoppingmileapp.ui.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.example.shoppingmileapp.dominio.User;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_Signup extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private ProgressDialog progressDialog;
    private String email ;
    private String password;
    private String name;
    private static String token;

    @BindView(R.id.input_nom)
    EditText inputEditextNom;
    @BindView(R.id.input_email)
    EditText inputEditextEmail;
    @BindView(R.id.input_password)
    EditText inputEditextPass;
    @BindView(R.id.buttonNewSigNup)
    Button buttonRegiterdSigNup;
    @BindView(R.id.estoyRegistrado)
    Button buttonEstoyRegistrado;

    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        //Inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        setButtonListeners();
    }

    private void setButtonListeners() {
        //login button
        buttonRegiterdSigNup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRegistrer();
            }
        });

        // Resgistration button
        buttonEstoyRegistrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startToLoginActivity();
            }
        });
    }
    private void handleRegistrer() {
       email = inputEditextEmail.getText().toString();
       password = inputEditextPass.getText().toString();
       name = inputEditextNom.getText().toString();

        if (!validateEmailPass(name, email, password)) {
            return;
        }
        performAccountCreation(name, email, password);
    }

    private boolean validateEmailPass(String name, String email, String password) {

        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEditextEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            inputEditextEmail.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputEditextPass.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputEditextPass.setError(null);
        }
        if (name.isEmpty()) {
            inputEditextNom.setError("Obligatory field");
            valid = false;
        } else {
            inputEditextNom.setError(null);
        }
        return valid;
    }

    private void performAccountCreation(final String name, final String email, final String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "account created");
                            User user = new User(name, email, password);
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name).build();
                                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);
                                        showMessage("User Registration OK");
                                    }else{
                                        showMessage("User Registration Failde");
                                    }
                                }
                            });
                            showProgressDialog();
                            logToken();
                            startToMainActivity();
                            showMessage("Vienbenidos a Shopping Mile");

                        }else {
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                showMessage("you are already registered");
                            }else{
                                showMessage(task.getException().getMessage());
                            }
                        }
                    }

                });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setMessage("Creating Account...");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private void startToMainActivity() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }
    private void startToLoginActivity() {
        startActivity(new Intent(getApplicationContext(), Activity_Login.class));
        finish();
    }

    public void logToken() {
        token = FirebaseInstanceId.getInstance().getToken();
        Log.d("SendToken", "logToken");
        sendRegistrationTokenToServer(token);
    }

    public void sendRegistrationTokenToServer(String token) {
        DatabaseReference fcmRef = FirebaseDatabase.getInstance().getReference("Tokens");
        fcmRef.child(token).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("SendToken", "Token updated");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            startToMainActivity();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }


}
