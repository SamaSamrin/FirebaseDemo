package com.example.user.firebasedemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "Sign In";
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;

    EditText emailInput;
    EditText passwordInput;
    EditText emailGoogleInput;
    EditText passwordGoogleInput;

    String name;
    String email;
    String password;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();
        setAuthStateListener();

        emailInput = (EditText) findViewById(R.id.email_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        emailGoogleInput = (EditText) findViewById(R.id.email_input_google);
        passwordGoogleInput = (EditText) findViewById(R.id.password_input_google);
        tabHostHandling();

        Bundle intentInfo = getIntent().getExtras();
        if (intentInfo!=null){
            name = intentInfo.getString("fullname");
            email = intentInfo.getString("email");
            password = intentInfo.getString("password");
            phoneNumber = intentInfo.getString("number");
            boolean passwordsMatch = intentInfo.getBoolean("passwords match");
            if (!passwordsMatch)
                Log.e(TAG, "passwords dont match");
        }else{
            Log.e(TAG, "received intent bundle is null");
        }
    }

    private void tabHostHandling(){
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost_login);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Email");
        spec.setContent(R.id.tab1_login);
        spec.setIndicator("Email");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Google");
        spec.setContent(R.id.tab2_login);
        spec.setIndicator("Google");
        tabHost.addTab(spec);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                Log.e(TAG, "on tab changed - "+s);
                if (s.equals("Email")){
                    Editable temp = emailInput.getText();
                    if (temp != null)
                        email = temp.toString();
                    else
                        Log.e(TAG, "normal email input is null");
                    temp = passwordInput.getText();
                    if (temp != null)
                        password = temp.toString();
                    else
                        Log.e(TAG, "normal password input is null");
                }else if (s.equals("Google")){
                    Editable temp = emailGoogleInput.getText();
                    if (temp != null)
                        email = temp.toString();
                    else
                        Log.e(TAG, "google email input is null");
                    temp = passwordGoogleInput.getText();
                    if (temp != null)
                        password = temp.toString();
                    else
                        Log.e(TAG, "google password input is null");
                }
            }
        });
    }

    void getValues(){
        if (email==null && password==null){
            if (emailInput.getText() != null)
                email = emailInput.getText().toString();
            if (passwordInput.getText() != null)
                password = passwordInput.getText().toString();
        }
    }
    public void goToSignUp(View view){
        Intent intent = new Intent(SignIn.this, SignUp.class);
        startActivity(intent);
    }

    public  void onSignIn(View view){
        getValues();
        signIn();
        Intent intent = new Intent(SignIn.this, NavigationDrawer.class);
        intent.putExtra("email", email);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    void setAuthStateListener(){
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    //user is signed in
                    Log.e(TAG, "user signed in");
                    String user_name = user.getDisplayName();
                    String user_email = user.getEmail();
                    Log.e(TAG, "on auth state changed: name = "+user_name+" , email = "+user_email);
                    String user_id = user.getUid();
                    Log.e(TAG, "on auth state changed: user id = "+user_id);
                }else{
                    //user is signed out
                    Log.e(TAG, "user signed out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null)
            auth.removeAuthStateListener(authStateListener);
    }

    void createAccount(){
        if (email!=null && password!=null) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.e(TAG, "create account : on complete");
                            if (task.isSuccessful())
                                Log.e(TAG, "task successful");
                            else
                                Log.e(TAG, "task not successful");
                        }
                    });
        }else
            Log.e(TAG, "signIn() : email or password is null");
    }

    void signIn(){
        if (email!=null && password!=null) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.e(TAG, "sign in : on complete ");
                            if (task.isSuccessful())
                                Log.e(TAG, "task successful");
                            else
                                Log.e(TAG, "task not successful");
                        }
                    });
        }else
            Log.e(TAG, "signIn() : email or password is null");
    }
}

