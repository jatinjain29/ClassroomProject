package com.example.classroom.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.classroom.R;
import com.example.classroom.Repositories.UserViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterUser extends AppCompatActivity {
    EditText et_email, et_passw, cnfrm_passw, fullName;
    TextView txt_msg;
    Button btn;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    UserViewModel userViewModel;
    RelativeLayout prgrsRegisteration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        et_email = findViewById(R.id.et_email);
        et_passw = findViewById(R.id.et_password);
        cnfrm_passw = findViewById(R.id.et_cnfrmpassword);
        txt_msg = findViewById(R.id.txt_msg);
        fullName = findViewById(R.id.et_fullName);
        btn = findViewById(R.id.btn);
        userViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(UserViewModel.class);
        prgrsRegisteration = findViewById(R.id.prgrs_registeration);
    }


    public void RegisterUser(View v) {
        txt_msg.setVisibility(View.GONE);
        String name = fullName.getText().toString();
        String email = et_email.getText().toString();
        String password = et_passw.getText().toString();
        String cnfrm_password = cnfrm_passw.getText().toString();
        if (TextUtils.isEmpty(name)) {
            fullName.setError("Name Required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            et_email.setError("Email Required");
            return;
        } else if (TextUtils.isEmpty(password)) {
            et_passw.setError("Password Required");
            return;
        } else if (TextUtils.isEmpty(cnfrm_password)) {
            cnfrm_passw.setError("Enter password to confirm");
            return;
        } else if (!password.equals(cnfrm_password)) {
            cnfrm_passw.setError("Password doesn't match");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Not a valid email");
            return;
        } else {
            prgrsRegisteration.setVisibility(View.VISIBLE);

            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    verifyemail(authResult);
                    userViewModel.updateDisplayName(name, authResult.getUser());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    prgrsRegisteration.setVisibility(View.GONE);
                    if (e instanceof FirebaseAuthWeakPasswordException) {
                        txt_msg.setVisibility(View.VISIBLE);
                        txt_msg.setText("Please enter a strong Password , atleast 6 character long");
                    } else if (e instanceof FirebaseAuthUserCollisionException) {

                        txt_msg.setVisibility(View.VISIBLE);
                        txt_msg.setText("Email already registered, please enter another email");
                    } else if (e instanceof NetworkErrorException) {
                        txt_msg.setVisibility(View.VISIBLE);
                        txt_msg.setText("Please check your internet connection");

                    } else {
                        txt_msg.setVisibility(View.VISIBLE);
                        txt_msg.setText("Error creating user: Please Check your credentials");
                    }
                }
            });
        }
    }


    public void verifyemail(AuthResult task) {


        try {
            task.getUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    prgrsRegisteration.setVisibility(View.GONE);
                    txt_msg.setText("A verification link has been sent to your email please verify your account");
                    txt_msg.setVisibility(View.VISIBLE);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    txt_msg.setText("Please Try Again");
                    txt_msg.setVisibility(View.VISIBLE);
                    prgrsRegisteration.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            txt_msg.setVisibility(View.VISIBLE);
            txt_msg.setText(e.toString());
            prgrsRegisteration.setVisibility(View.GONE);
        }
    }

    public void login(View v) {
        startActivity(new Intent(RegisterUser.this, LoginUser.class));
    }

}
