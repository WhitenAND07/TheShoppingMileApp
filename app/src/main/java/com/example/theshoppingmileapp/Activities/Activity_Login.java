package com.example.theshoppingmileapp.Activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.example.theshoppingmileapp.R;
import com.example.theshoppingmileapp.dominio.PlacesShopping;

import butterknife.BindView;
import butterknife.ButterKnife;
import android.app.ProgressDialog;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;

public class Activity_Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    public static final String PREFERENCE= "preference";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_PASSWD = "passwd";
    public static final String PREF_SKIP_LOGIN = "skip_login";
    public static  final String EMAILU = Activity_Signup.PREF_Email;
    public static  final String PASSWLU = Activity_Signup.PREF_PASSWD;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;

    @BindView(R.id.input_email) EditText emailText;
    @BindView(R.id.input_password) EditText passwordText;
    @BindView(R.id.buttonloginNew) Button buttonLoginNew;
    @BindView(R.id.buttnSignupRegisterd) Button buttonSigUp;
    RelativeLayout rellay1, rellay2;

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

        rellay1 =  findViewById(R.id.rellay1);
        rellay2 =  findViewById(R.id.rellay2);
        ButterKnife.bind(this);
        handler.postDelayed(runnable, 2000); //2000 is the timeout for the splash

        mSharedPreferences = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        if(mSharedPreferences.contains(PREF_SKIP_LOGIN)){
            startToMainActivity();

        }else{
            buttonLoginNew.setOnClickListener(new  View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    login();
                }
            });
        }
        buttonSigUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Activity_Signup.class));
                finish();
            }
        });
    }

    public void login() {

        Log.d(TAG, "Login");
        if (!validate()) {
            onLoginFailed();
            return;
        }
        String emaliU = mSharedPreferences.getString(EMAILU, "userEmail");
        String pasUser = mSharedPreferences.getString(PASSWLU, "userPass");
        if(mSharedPreferences.contains(PREF_EMAIL)&& mSharedPreferences.contains(PREF_PASSWD)){
            if(loginEmailPassValid(emaliU, pasUser)){
                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                mEditor.putString(PREF_SKIP_LOGIN, "skip");
                mEditor.apply();
                startToMainActivity();
            }else{
                Toast.makeText(getApplicationContext(), "Username or password invalid!", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            Toast.makeText(getApplicationContext(),"Your user not exist.",Toast.LENGTH_SHORT).show();
            buttonLoginNew.setEnabled(false);
            return;
        }
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                    }
                }, 5000);
    }

    private void startToMainActivity(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void onLoginSuccess() {
        buttonLoginNew.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        buttonLoginNew.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Enter a valid email address");
            valid = false;
        }else {
            emailText.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        }else {
            passwordText.setError(null);
        }
        return valid;
    }

    private boolean loginEmailPassValid (String email, String pass) {
        return emailText.getText().toString().equals(email) &&
                passwordText.getText().toString().equals(pass);
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
