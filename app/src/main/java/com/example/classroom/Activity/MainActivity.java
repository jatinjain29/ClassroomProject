package com.example.classroom.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.classroom.Adapters.ClassListAdapter;
import com.example.classroom.ObjectClasses.Classroom;
import com.example.classroom.Repositories.ClassroomViewModel;
import com.example.classroom.R;
import com.example.classroom.Repositories.UserViewModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ArrayList<Classroom> class_list = new ArrayList<>();
    ClassListAdapter madapter;
    RecyclerView rv;
    ClassroomViewModel classModel;
    UserViewModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        setUpRecyclerView();
        classModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ClassroomViewModel.class);
        userModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(UserViewModel.class);
        getClassKeys();
        attachObserver();
    }


    private void setUpRecyclerView() {
        rv = findViewById(R.id.rv);
        madapter = new ClassListAdapter(class_list);
        rv.setAdapter(madapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        implementClickListener();

    }

    private void implementClickListener() {
        madapter.OnitemClicked(new ClassListAdapter.ClickListener() {
            @Override
            public void itemClick(Classroom current_class) {
                Intent intent = new Intent(MainActivity.this, SubjectScreen.class);
                intent.putExtra("SubjectId", current_class.unique_code);
                startActivity(intent);
            }
        });
    }

    private void attachObserver() {
        classModel.joinedClasses.observe(this, new Observer<Classroom>() {
            @Override
            public void onChanged(Classroom classroom) {

                class_list.add(classroom);
                madapter.setList(class_list);
            }
        });
    }

    private void getClassroom(String key) {
        classModel.getClassroom(key);

    }

    private void getClassKeys() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userModel.getClasses(uid);
        userModel.ClassKeys.observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                class_list.clear();
                for (String key : strings) {
                    getClassroom(key);
                }

            }
        });
    }

    public void AddJoinClass(View v) {
        Intent intent = new Intent(MainActivity.this, JoinClass.class);
        startActivity(intent);
    }


}