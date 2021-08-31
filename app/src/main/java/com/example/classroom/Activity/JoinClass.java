package com.example.classroom.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classroom.ObjectClasses.Classroom;
import com.example.classroom.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class JoinClass extends AppCompatActivity {
    CheckBox jnClass, crtClass;
    LinearLayout jnClassLayout, crtClassLayout;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://classroom-2859b-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference classDatabase = database.getReference().child("Classrooms");
    EditText class_name, subject_name, class_code;

    FirebaseUser cuser = FirebaseAuth.getInstance().getCurrentUser();
    String uid = cuser.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);
        jnClass = findViewById(R.id.chck_joinClass);
        crtClass = findViewById(R.id.chck_createClass);
        jnClassLayout = findViewById(R.id.l_jclass);
        crtClassLayout = findViewById(R.id.l_cClass);
        class_name = findViewById(R.id.et_class_name);
        subject_name = findViewById(R.id.et_subject_name);
        class_code = findViewById(R.id.et_class_code);


    }

    public void Join(View v) {
        if (jnClass.isChecked()) {
            crtClass.setChecked(false);
            crtClassLayout.setVisibility(View.GONE);
            jnClassLayout.setVisibility(View.VISIBLE);
        } else {
            jnClassLayout.setVisibility(View.GONE);
        }

    }

    public void Create(View v) {
        if (crtClass.isChecked()) {
            jnClass.setChecked(false);
            crtClassLayout.setVisibility(View.VISIBLE);
            jnClassLayout.setVisibility(View.GONE);
        } else
            crtClassLayout.setVisibility(View.GONE);
    }

    public void CreateClass(View v) {

        if (TextUtils.isEmpty(class_name.getText().toString())) {
            class_name.setError("Required Field");
            return;
        }
        if (TextUtils.isEmpty(subject_name.getText().toString())) {
            subject_name.setError("Required Field");
            return;
        }

        String unique_code = classDatabase.push().getKey();
        String subject = subject_name.getText().toString();
        String Class = class_name.getText().toString();
        String teacher = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Classroom nclass = new Classroom(unique_code, subject, Class, teacher, uid);
        Map<String, Classroom> details = new HashMap<>();

        details.put(unique_code, nclass);

        classDatabase.child(unique_code).setValue(nclass).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Success", "Created Successfully");
                TextView tv = findViewById(R.id.tv_msg);
                tv.setText("Share this code with other user to Join :\n " + unique_code);
                subject_name.setText("");
                class_name.setText("");

                addClassInUser(unique_code, 1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Create Failure", e.toString());
            }
        });


    }

    public void addClassInUser(String class_code, int p) {
        String role = "";
        if (p == 1)
            role = "Teacher";
        else
            role = "Student";
        Map<String, String> details = new HashMap<>();
        details.put("Role", role);
        database.getReference().child("Users").child(uid).child("classes").child(class_code).setValue(details).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void JoinClass(View v) {
        if (TextUtils.isEmpty(class_code.getText().toString())) {
            class_code.setError("Required Field");
            return;
        }
        String code = class_code.getText().toString();
        classDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean added = false;
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getKey().equals(code)) {
                        addUserInClass(code);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    Toast.makeText(JoinClass.this, "Unable to join the class" + "\n" + "Please check the class code and try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void addUserInClass(String code) {
        Map<String, Object> nuser = new HashMap<>();
        nuser.put(uid, cuser.getDisplayName());
        classDatabase.child(code).child("Members").updateChildren(nuser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                addClassInUser(code, 0);
                TextView mesg = (TextView) findViewById(R.id.tv_msg);
                mesg.setText("Joined Successfully");
                class_code.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }
}