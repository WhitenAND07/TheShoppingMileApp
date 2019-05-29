package com.example.shoppingmileapp.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.example.shoppingmileapp.R;
import com.example.shoppingmileapp.ui.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.ProgressDialog;
import android.widget.Toast;


public class Activity_Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    public ProgressDialog progressDialog;

    @BindView(R.id.input_email)
    EditText emailText;
    @BindView(R.id.input_password)
    EditText passwordText;
    @BindView(R.id.buttonloginNew)
    Button buttonLoginNew;
    @BindView(R.id.buttnSignupRegisterd)
    Button buttonSigUp;
    RelativeLayout rellay1, rellay2;

    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        rellay1 = findViewById(R.id.rellay1);
        rellay2 = findViewById(R.id.rellay2);
        ButterKnife.bind(this);
        handler.postDelayed(runnable, 2000); //2000 is the timeout for the splash

        //Inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        setButtonListeners();
    }

    private void setButtonListeners() {

        //login button
        buttonLoginNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogin();
            }
        });

        // Resgistration button
        buttonSigUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startToSignupActivity();
            }
        });
    }

    private void handleLogin() {
        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();
        if (!validateEmailPass(email, password)) {
            return;
        }
        //perform login and account creation depending on existence of email in firebase
        performLogin(email, password);
    }

    private void performLogin(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "login success");
                            showProgressDialog();
                            startToMainActivity();
                        } else{
                            Log.e(TAG, "Login fail", task.getException());
                            showMessage(task.getException().getMessage());
                        }

                    }
                });
    }

    private boolean validateEmailPass(String email, String password) {

        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }
        return valid;
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("logging in..");
        }
        progressDialog.show();

    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();

        }
        progressDialog = null;
    }

    private void startToMainActivity() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }

    private void startToSignupActivity() {
        startActivity(new Intent(getApplicationContext(), Activity_Signup.class));
        finish();
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
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


