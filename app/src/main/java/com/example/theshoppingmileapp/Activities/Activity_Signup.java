package com.example.theshoppingmileapp.Activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.content.Intent;
import android.util.Log;
import android.app.ProgressDialog;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;


import com.example.theshoppingmileapp.R;
import com.example.theshoppingmileapp.dominio.PlacesShopping;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_Signup extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    public static final String PREFERENCE= "preference";
    public static final String PREF_Email = "email";
    public static final String PREF_PASSWD = "passwd";
    private String email, password;
    private SharedPreferences mSharedPreferences;
    private  ProgressDialog progressDialog;

    @BindView(R.id.input_nom) EditText inputEditextNom;
    @BindView(R.id.input_email) EditText inputEditextEmail;
    @BindView(R.id.input_password)EditText inputEditextPass;
    @BindView(R.id.buttonNewSigNup) Button buttonRegiterdSigNup;
    @BindView(R.id.estoyRegistrado) Button buttonEstoyRegistrado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        mSharedPreferences = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);

        buttonRegiterdSigNup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        buttonEstoyRegistrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Activity_Login.class));

            }
        });
    }
        public void signup() {
            Log.d(TAG, "Signup");
            if (!validate()) {
                onSignupFailed();
                return;
            }
            buttonRegiterdSigNup.setEnabled(false);
            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.putString(PREF_Email,email);
            mEditor.putString(PREF_PASSWD,password);
            mEditor.apply();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();



            progressDialog = new ProgressDialog(Activity_Signup.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();





            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onSignupSuccess or onSignupFailed
                            // depending on success
                            onSignupSuccess();

                            //progressDialog.dismiss();
                        }
                    }, 6000);

        }
        public void onSignupSuccess() {
            buttonRegiterdSigNup.setEnabled(true);
             finish();
        }
        public void onSignupFailed() {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
            buttonRegiterdSigNup.setEnabled(true);
        }

        public boolean validate(){
            boolean valid = true;
            String nom = inputEditextNom.getText().toString();
            email = inputEditextEmail.getText().toString();
            password = inputEditextPass.getText().toString();
            if (nom.isEmpty() || nom.length() < 3) {
                inputEditextNom.setError("at least 3 characters");
                valid = false;
            } else {
                inputEditextNom.setError(null);
            }

            if (email.isEmpty()) {
                inputEditextEmail.setError("Enter Valid Address");
                valid = false;
            } else {
                inputEditextEmail.setError(null);
            }


            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputEditextEmail.setError("enter a valid email address");
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


            return valid;
        }
        @Override
        public void onDestroy (){
            super.onDestroy();

            if(progressDialog != null)
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            progressDialog= null;
        }


}
