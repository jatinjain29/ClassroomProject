
package com.example.classroom.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.classroom.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ResetUser extends AppCompatActivity {
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_user);
        email = findViewById(R.id.user_email);
    }


    public void ResetPassword(View v) {
        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Email Required");
            return;
        }
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        TextView msg = findViewById(R.id.tvmsg);
        mauth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                msg.setText("A Password reset link has been sent. Please check your email");
                msg.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                msg.setText("Please check your email and Try Again");
                msg.setVisibility(View.VISIBLE);
            }
        });


    }
}