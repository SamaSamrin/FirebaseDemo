package com.example.user.firebasedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "Sign In";

    String name;
    String email;
    String password;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

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
        tabHostHandling();
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
    }

    public void goToSignUp(View view){
        Intent intent = new Intent(SignIn.this, SignUp.class);
        startActivity(intent);
    }
}
