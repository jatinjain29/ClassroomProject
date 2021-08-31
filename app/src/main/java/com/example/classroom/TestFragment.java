package com.example.classroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.classroom.Activity.CreateTest;
import com.example.classroom.Activity.TestList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class TestFragment extends Fragment {

    public TestFragment() {
        super(R.layout.test_fragment);
    }

    boolean isTeacher = false;
    String subjectId;
    FloatingActionButton addTest;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        subjectId = getArguments().getString("SubjectId");
        BottomNavigationView nav_view = view.findViewById(R.id.btm_nav2);
        addTest = view.findViewById(R.id.fltn_addTest);
        checkifTeacher();

        nav_view.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                clickListener(item.getItemId());

                return true;
            }
        });
        addTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateTest.class);
                intent.putExtra("SubjectId", subjectId);
                startActivity(intent);
            }
        });
    }

    public void clickListener(int id) {
        Bundle data = new Bundle();
        data.putString("SubjectId", subjectId);
        data.putBoolean("isTeacher", isTeacher);
        switch (id) {
            case R.id.previous_test:
                data.putBoolean("displayUpcoming", false);
                break;
            case R.id.upcoming_test:
                data.putBoolean("displayUpcoming", true);
                break;
        }

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.frag_cntnr2, TestList.class, data)
                .setReorderingAllowed(true)
                .commit();

    }

    public void checkifTeacher() {
        FirebaseDatabase.getInstance(getString(R.string.database_url)).getReference().child("Classrooms").child(subjectId).child("teacher_id").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String teacherId = (String) dataSnapshot.getValue();
                if (teacherId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    isTeacher = true;
                    addTest.setVisibility(View.VISIBLE);
                } else {
                    isTeacher = false;
                    addTest.setVisibility(View.GONE);
                }

                clickListener(R.id.previous_test);
                //Toast.makeText(getContext(), " "+isTeacher, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
