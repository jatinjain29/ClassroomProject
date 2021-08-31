package com.example.classroom.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.classroom.Adapters.StudentListAdapter;
import com.example.classroom.ObjectClasses.StudentTest;
import com.example.classroom.R;
import com.example.classroom.Repositories.ClassroomViewModel;

import java.util.ArrayList;

public class AssignMarks extends AppCompatActivity {
    StudentListAdapter adapter;
    String cstudentId = "", testId = "", subjectId = "";
    ClassroomViewModel viewModel;

    RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_marks);
        rl = findViewById(R.id.rl_assign);
        testId = getIntent().getStringExtra("TestId");
        subjectId = getIntent().getStringExtra("SubjectId");
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ClassroomViewModel.class);
        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        RecyclerView rv = findViewById(R.id.rv_students);
        adapter = new StudentListAdapter(new ArrayList<>());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        attachObserver();
        adapter.ItemClicked(new StudentListAdapter.clickListener() {
            @Override
            public void assignClicked(String studentId) {
                rl.setVisibility(View.VISIBLE);
                cstudentId = studentId;
            }
        });
    }

    private void attachObserver() {
        viewModel.getStudents(subjectId, testId);
        viewModel.students.observe(this, new Observer<ArrayList<StudentTest>>() {
            @Override
            public void onChanged(ArrayList<StudentTest> studentTests) {
                Log.i("list size", studentTests.size() + "");
                if (studentTests != null && studentTests.size() > 0)
                    adapter.setList(studentTests);
            }
        });
    }

    public void AssignTestMarks(View view) {
        EditText et = findViewById(R.id.et_Testmarks);
        if (TextUtils.isEmpty(et.getText().toString())) {
            et.setError("Marks Required");
            return;
        }
        updateStudentMarks(cstudentId, et.getText().toString());
    }

    private void updateStudentMarks(String cstudentId, String marks) {
        viewModel.updateStudentMarks(subjectId, testId, marks, cstudentId);
        rl.setVisibility(View.GONE);
    }
}