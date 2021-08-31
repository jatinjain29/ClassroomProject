package com.example.classroom.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classroom.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginUser extends AppCompatActivity {

    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    EditText et_email, et_password;
    TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        msg = findViewById(R.id.msg);


        if (mauth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginUser.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    public void Login(View v) {
        String User_email = "", User_password = "";
        User_email = et_email.getText().toString();
        User_password = et_password.getText().toString();
        if (TextUtils.isEmpty(User_email))
            et_email.setError("Email Required");
        else if (TextUtils.isEmpty(User_password))
            et_password.setError("Password Required");

        else {

            mauth.signInWithEmailAndPassword(User_email, User_password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if (authResult.getUser().isEmailVerified()) {
                                Intent intent = new Intent(LoginUser.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                msg.setText("** Please Verify your Email first");
                                msg.setVisibility(View.VISIBLE);
                                return;
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    if (e instanceof NetworkErrorException || e instanceof FirebaseNetworkException)
                        Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    else if (e instanceof FirebaseAuthInvalidCredentialsException || e instanceof FirebaseAuthInvalidUserException)
                        Toast.makeText(getApplicationContext(), "Invalid User", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Try Again" + e.toString(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    public void RegisterNewUser(View v) {
        try {
            Intent intent = new Intent(LoginUser.this, RegisterUser.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void ResetPassword(View v) {
        startActivity(new Intent(LoginUser.this, ResetUser.class));
    }


}