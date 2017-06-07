package com.example.user.firebasedemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "Sign In";
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    DatabaseReference usersReference;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference imageReference;

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
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        usersReference = databaseReference.child("Users");
        setDatabaseListener();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        imageReference = storageReference.child("images");

        emailInput = (EditText) findViewById(R.id.email_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        emailGoogleInput = (EditText) findViewById(R.id.email_input_google);
        passwordGoogleInput = (EditText) findViewById(R.id.password_input_google);
        tabHostHandling();

        Bundle intentInfo = getIntent().getExtras();
        if (intentInfo!=null){
            if (intentInfo.get("source").equals("SignUp")) {
                name = intentInfo.getString("fullname");
                email = intentInfo.getString("email");
                password = intentInfo.getString("password");
                phoneNumber = intentInfo.getString("number");
                boolean passwordsMatch = intentInfo.getBoolean("passwords match");
                if (!passwordsMatch)
                    Log.e(TAG, "passwords dont match");
                Log.e(TAG, "received intent info");
                createAccount();
            }
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
                email = emailInput.getText().toString().trim();
            if (email.length()==0){
                emailInput.setError("email cannot be empty");
                emailInput.requestFocus();
            }
            if (passwordInput.getText() != null)
                password = passwordInput.getText().toString().trim();
            if (password.length()==0){
                passwordInput.setError("password cannot be empty");
                passwordInput.requestFocus();
            }
        }
    }
    public void goToSignUp(View view){
        Intent intent = new Intent(SignIn.this, SignUp.class);
        startActivity(intent);
    }

    public void onSignIn(View view){
        getValues();
        signIn();
        Intent intent = new Intent(SignIn.this, NavigationDrawer.class);
        intent.putExtra("email", email);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    void whenSignedIn(String name, String email){
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
                    whenSignedIn("Username", user_email);
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
                            if (task.isSuccessful()) {
                                Log.e(TAG, "created account successfully");
                                User user = new User(email, password);
                                    DatabaseReference newUserLocation = usersReference.push();
                                    newUserLocation.setValue(user, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError!=null)
                                                Log.e(TAG, "error exists? - " + databaseError.getMessage());
                                            else
                                                Log.e(TAG, "new user pushed to database successfully");
                                        }
                                    });
                                }
                            else {
                                Log.e(TAG, "create account not successful"+task.getException().getMessage());
                            }
                        }
                    });
        }else
            Log.e(TAG, "signIn() : email or password is null");
    }

    void showSignUpDialogue(){
        Log.e(TAG, "show signUp dialog");
        AlertDialog.Builder signUpDialogBuilder = new AlertDialog.Builder(this);
        signUpDialogBuilder.setMessage("Not a member?");
        signUpDialogBuilder.setPositiveButton("Sign Up!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });
        signUpDialogBuilder.create();
        signUpDialogBuilder.show();
    }

    void signIn(){
        if (email!=null && password!=null ) {
            if (email.length()!=0 && password.length()!=0) {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.e(TAG, "sign in : on complete ");
                                if (task.isSuccessful())
                                    Log.e(TAG, "task successful");
                                else {
                                    if (task.getException().getMessage().contains("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                        showSignUpDialogue();
                                        createAccount();
                                        signIn();
                                    }
                                    Log.e(TAG, "task not successful " + task.getException().getMessage());
                                }
                            }
                        });
            }else
                Log.e(TAG, "signIn() : email or password is empty");
        }else
            Log.e(TAG, "signIn() : email or password is null");
    }

    void addFileToStorage(Uri fileUrl){
        storageReference.putFile(fileUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.e(TAG, "on Success");
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "on failure");
                    }
                });
    }

    void setDatabaseListener(){
        usersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "on child added - "+s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "on child changed - "+s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e(TAG, "on child removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "on child moved - "+s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "on data change, data snapshot - "+dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "on cancelled");
                if (databaseError!=null){
                    Log.e(TAG, "onCancelled error - "+databaseError.getMessage());
                }
            }
        });
    }
}

