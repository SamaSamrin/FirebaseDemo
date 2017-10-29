package com.example.user.firebasedemo.Messaging;

import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.zzj;

/**
 * Created by ASUS on 23-Oct-17.
 */

public class InstanceIdService extends FirebaseInstanceIdService {

    public final String TAG = getClass().getSimpleName();

    public InstanceIdService(){

    }

    public InstanceIdService(FirebaseApp firebaseApp, zzj zzj) {
        //super(firebaseApp, zzj);
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }

}
