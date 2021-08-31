package com.example.classroom.Repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.classroom.ObjectClasses.UserAssingment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserRepository {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://classroom-2859b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users");

    MutableLiveData<ArrayList<String>> classKeys = new MutableLiveData<>();
    MutableLiveData<ArrayList<UserAssingment>> sbmtdAssingment = new MutableLiveData<>();
    MutableLiveData<ArrayList<UserAssingment>> pndngAssingment = new MutableLiveData<>();

    public void updateDisplayName(FirebaseUser user, String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Profile Change", "User profile updated.");
                        }
                    }
                });

        databaseReference.child(user.getUid()).child("Name").setValue(name);
    }


    public void getClasses(String uid) {
        databaseReference.child(uid).child("classes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> class_keys = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String key = snapshot1.getKey();
                    class_keys.add(key);
                }
                classKeys.setValue(class_keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    public void RetrievePendingAssingment(String uid, String subjectId) {
        databaseReference.child(uid).child("classes").child(subjectId).child("Assingments").child("Pending").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<UserAssingment> updated_list = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    UserAssingment nAssingment = new UserAssingment(snapshot1.getKey(), (String) snapshot1.getValue());
                    updated_list.add(nAssingment);
                }
                pndngAssingment.setValue(updated_list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void RetrieveSubmittedAssingment(String uid, String subjectId) {
        databaseReference.child(uid).child("classes").child(subjectId).child("Assingments").child("Submitted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<UserAssingment> submittedAssingment = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    UserAssingment nAssingment = new UserAssingment(snapshot1.getKey(), (String) snapshot1.getValue());
                    submittedAssingment.add(nAssingment);
                }
                sbmtdAssingment.setValue(submittedAssingment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
