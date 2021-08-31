package com.example.classroom.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.classroom.Adapters.SubmissionsAdapter;
import com.example.classroom.ObjectClasses.Submissions;
import com.example.classroom.R;
import com.example.classroom.Repositories.ClassroomViewModel;

import java.util.ArrayList;

public class ViewSubmissions extends AppCompatActivity {

    ClassroomViewModel viewModel;
    String subjectId = "", assignmentId = "";
    SubmissionsAdapter adapter;
    String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_submissions);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ClassroomViewModel.class);
        Intent intent = getIntent();
        subjectId = intent.getStringExtra("SubjectId");
        assignmentId = intent.getStringExtra("AssignmentId");
        setUpRecycelrView();
    }


    public void setUpRecycelrView() {
        RecyclerView rv = findViewById(R.id.rv_sbmsns);
        adapter = new SubmissionsAdapter(new ArrayList<Submissions>(), ViewSubmissions.this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        setUpClickListener();
    }

    protected void setUpClickListener() {
        adapter.OnItemClicked(new SubmissionsAdapter.ClickListener() {
            @Override
            public void OnAssignedClick(String id) {
                switchViews();
                studentId = id;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getAllSubmissions(subjectId, assignmentId);
        viewModel.allSubmissions.observe(this, new Observer<ArrayList<Submissions>>() {
            @Override
            public void onChanged(ArrayList<Submissions> submissions) {
                if (submissions != null && submissions.size() > 0)
                    adapter.setList(submissions);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //detach event listener
    }

    public void Assign(View v) {
        EditText et = findViewById(R.id.et_assignedMarks);
        if (TextUtils.isEmpty(et.getText().toString())) {
            et.setError("Marks can't be empty");
            return;
        }
        //update marks
        String marks = et.getText().toString();
        viewModel.AssignMarks(subjectId, assignmentId, studentId, marks);
        switchViews();

    }

    public void switchViews() {

        LinearLayout ll = findViewById(R.id.ll_marksAssign);
        LinearLayout ll2 = findViewById(R.id.ll_sbmsnView);
        if (ll2.getVisibility() == View.VISIBLE) {
            ll2.setVisibility(View.GONE);
            ll.setVisibility(View.VISIBLE);
        } else {
            ll.setVisibility(View.GONE);
            ll2.setVisibility(View.VISIBLE);
        }
    }
}