package com.example.classroom.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroom.Adapters.TestTvAdapter;
import com.example.classroom.ObjectClasses.Test;
import com.example.classroom.R;
import com.example.classroom.Repositories.ClassroomViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class TestList extends Fragment {

    public TestList() {
        super(R.layout.test_list);
    }

    ClassroomViewModel viewModel;
    boolean isTeacher = false;
    TestTvAdapter adapter;
    String subjectId, uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean displayUpcoming = getArguments().getBoolean("displayUpcoming");
        isTeacher = getArguments().getBoolean("isTeacher");
        subjectId = getArguments().getString("SubjectId");

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(ClassroomViewModel.class);

        if (displayUpcoming) {
            observeUpcoming(subjectId);
        } else {
            observePrevious(subjectId);

        }
        setUpRecyclerView(view);
    }


    private void setUpRecyclerView(View v) {
        RecyclerView rv = v.findViewById(R.id.rv_Tests);
        adapter = new TestTvAdapter(getContext(), isTeacher, new ArrayList<>());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        adapter.OnItemClicked(new TestTvAdapter.ClickListener() {
            @Override
            public void SubmissionClicked(Test test) {
                String testId = test.testId;
                Intent intent = new Intent(getActivity(), AssignMarks.class);
                intent.putExtra("TestId", testId);
                intent.putExtra("SubjectId", subjectId);
                startActivity(intent);


            }
        });

    }

    private void observeUpcoming(String subjectId) {
        long ctime = System.currentTimeMillis();
        viewModel.UpcomingTests(subjectId, ctime);
        viewModel.upcoming.observe(getActivity(), new Observer<ArrayList<Test>>() {
            @Override
            public void onChanged(ArrayList<Test> tests) {
                Log.i("sizeupc", tests.size() + " ");
                if (tests != null && tests.size() > 0)
                    adapter.setList(tests);
            }
        });
    }

    ;

    private void observePrevious(String subjectId) {
        long ctime = System.currentTimeMillis();
        viewModel.PreviousTests(subjectId, ctime, uid, isTeacher);
        viewModel.previous.observe(getActivity(), new Observer<ArrayList<Test>>() {
            @Override
            public void onChanged(ArrayList<Test> tests) {

                Log.i("sizeupc", tests.size() + " ");
                if (tests != null && tests.size() > 0)
                    adapter.setList(tests);
            }
        });
    }
}
