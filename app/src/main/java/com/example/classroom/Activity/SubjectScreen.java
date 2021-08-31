package com.example.classroom.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.classroom.AssingmentFragment;
import com.example.classroom.HomeFragment;
import com.example.classroom.R;
import com.example.classroom.TestFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SubjectScreen extends AppCompatActivity {
    BottomNavigationView nav_view;
    FragmentContainerView fragmentContainer;
    String subjectId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_screen);
        nav_view = findViewById(R.id.btm_nav);
        fragmentContainer = findViewById(R.id.frag_cntnr);
        subjectId = getIntent().getStringExtra("SubjectId");
        Bundle data = new Bundle();
        data.putString("SubjectId", subjectId);
        loadFragment(HomeFragment.class, data);
        nav_view.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.asng:
                        loadFragment(AssingmentFragment.class, data);
                        break;
                    case R.id.test:
                        loadFragment(TestFragment.class, data);
                        break;

                    case R.id.home:
                        loadFragment(HomeFragment.class, data);
                        break;

                }
                return true;
            }

        });
    }


    public void loadFragment(Class fragmentClass, Bundle data) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frag_cntnr, fragmentClass, data)
                .setReorderingAllowed(true)
                .commit();

    }


}