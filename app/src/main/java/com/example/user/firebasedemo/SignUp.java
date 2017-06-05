package com.example.user.firebasedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    EditText nameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText passwordConfirmationInput;
    EditText phoneNumberInput;

    String name;
    String email;
    String password;
    String reEnteredPassword;
    String number;
    boolean passwordsMatch = false;

    private static final String TAG = "Sign Up";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameInput = (EditText) findViewById(R.id.name_input_signup);
        emailInput = (EditText) findViewById(R.id.email_input_signup);
        passwordInput = (EditText) findViewById(R.id.password_first_input);
        passwordConfirmationInput = (EditText) findViewById(R.id.reenter_password);
        phoneNumberInput = (EditText) findViewById(R.id.phone_number_input);
    }

    void getAllValues(){
        if (nameInput.getText() != null)
            name = nameInput.getText().toString();
        else
            Log.e(TAG, "name input is null");
        if (emailInput.getText() != null)
            email = emailInput.getText().toString();
        else
            Log.e(TAG, "email input is null");

        gettingPasswords();

        if (password!=null && reEnteredPassword!=null){
            if (password.equals(reEnteredPassword))
                passwordsMatch = true;
            else {
                Toast.makeText(SignUp.this, "Re enter password", Toast.LENGTH_SHORT).show();
                gettingPasswords();
            }
        }

        if (phoneNumberInput.getText() != null)
            number = phoneNumberInput.getText().toString();
        else
            Log.e(TAG, "phone number input is null");
    }

    void gettingPasswords(){
        if (passwordInput.getText() != null)
            password = passwordInput.getText().toString();
        else
            Log.e(TAG, "password input is null");
        if (passwordConfirmationInput.getText() != null)
            reEnteredPassword = passwordConfirmationInput.getText().toString();
        else
            Log.e(TAG, "re entered password is null");
    }

    public void goBackToSignIn(View view){
        getAllValues();
        Intent intent = new Intent(SignUp.this, SignIn.class);
        intent.putExtra("fullname", name);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("passwords match", passwordsMatch);
        intent.putExtra("phone number", number);
        intent.putExtra("source", "SignUp");
        startActivity(intent);
    }
}
