package com.example.classroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroom.Activity.AssingmentSubmission;
import com.example.classroom.Activity.CreateAssingment;
import com.example.classroom.Activity.PreviousAssinngmentView;
import com.example.classroom.Activity.SubjectScreen;
import com.example.classroom.Activity.SubmittedAssingmentView;
import com.example.classroom.Adapters.TeacherAssingmentAdapter;
import com.example.classroom.Adapters.UserAssingmentAdapter;
import com.example.classroom.ObjectClasses.Assingment;
import com.example.classroom.ObjectClasses.UserAssingment;
import com.example.classroom.Repositories.ClassroomViewModel;
import com.example.classroom.Repositories.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;

public class AssingmentFragment extends Fragment {

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String subjectId;
    UserViewModel viewModel;
    ClassroomViewModel viewModel2;
    boolean isTeacher = false;
    RecyclerView rv_pndng, rv_submtd;
    UserAssingmentAdapter pndng_adapter, sbmtd_adapter;
    TeacherAssingmentAdapter tasng_adapter;
    RecyclerView rvteacher;
    LinearLayout studentView;
    FrameLayout teacherView;

    public AssingmentFragment() {
        super(R.layout.asngmnt_frgmnt);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        subjectId = requireArguments().getString("SubjectId");
        studentView = view.findViewById(R.id.ll_stdnt);
        teacherView = view.findViewById(R.id.fl_teacher);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(UserViewModel.class);
        viewModel2 = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(ClassroomViewModel.class);

        updateView(view);


    }


    public void updateView(View view) {
        FirebaseDatabase.getInstance(getString(R.string.database_url)).getReference().child("Classrooms").child(subjectId).child("teacher_id").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task != null) {
                    String teaherid = task.getResult().getValue().toString();
                    if (teaherid.equals(uid)) {
                        isTeacher = true;
                        setUpForTeacher(view);
                    } else {
                        setUpForStudent(view);
                    }
                }
            }
        });
    }

    public void setUpForTeacher(View v) {
        teacherView.setVisibility(View.VISIBLE);
        studentView.setVisibility(View.GONE);

        FloatingActionButton btn = getActivity().findViewById(R.id.fltn_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CAssng();
            }
        });

        setUpRvTeacher(v);

    }

    private void RetrieveAssingmentforTeacher() {
        viewModel2.RetrieveAllAssingment(subjectId);
        viewModel2.allAssingment.observe(getActivity(), new Observer<ArrayList<Assingment>>() {
            @Override
            public void onChanged(ArrayList<Assingment> userAssingments) {
                if (tasng_adapter == null) {
                    tasng_adapter = new TeacherAssingmentAdapter(new ArrayList<>());
                }

                if (userAssingments != null && userAssingments.size() > 0)
                    tasng_adapter.setList(userAssingments);
            }
        });
    }

    private void setUpRvTeacher(View view) {
        rvteacher = view.findViewById(R.id.rv_t_pvasng);
        tasng_adapter = new TeacherAssingmentAdapter(new ArrayList<Assingment>());
        rvteacher.setAdapter(tasng_adapter);
        rvteacher.setLayoutManager(new LinearLayoutManager(view.getContext()));
        tasng_adapter.ItemClicked(new TeacherAssingmentAdapter.ClickListener() {
            @Override
            public void onItemClicked(Assingment asngmnt) {
                Intent intent = new Intent(getActivity(), PreviousAssinngmentView.class);
                String assignment = new Gson().toJson(asngmnt, Assingment.class);
                intent.putExtra("Assignment", assignment);
                intent.putExtra("SubjectId", subjectId);
                startActivity(intent);

            }
        });

        RetrieveAssingmentforTeacher();
    }

    public void setUpForStudent(View view) {
        studentView.setVisibility(View.VISIBLE);
        teacherView.setVisibility(View.GONE);
        setUpRvStudent(view);
    }

    private void setUpRvStudent(View view) {

        rv_pndng = view.findViewById(R.id.rv_pndng);
        rv_submtd = view.findViewById(R.id.rv_submitted);
        pndng_adapter = new UserAssingmentAdapter(new ArrayList<UserAssingment>());
        sbmtd_adapter = new UserAssingmentAdapter(new ArrayList<UserAssingment>());
        setUpClickListeners();
        rv_pndng.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rv_pndng.setAdapter(pndng_adapter);
        rv_submtd.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rv_submtd.setAdapter(sbmtd_adapter);
        RetrievePendingAssingment();
        RetrieveSubmittedAssingment();
    }

    public void setUpClickListeners() {
        pndng_adapter.ItemClicked(new UserAssingmentAdapter.ClickListener() {
            @Override
            public void onItemClicked(UserAssingment asngmnt) {
                Intent intent = new Intent(getContext(), AssingmentSubmission.class);
                intent.putExtra("Assingmnet Id", asngmnt.assingmentId);
                intent.putExtra("Subject Id", subjectId);
                startActivity(intent);
            }
        });

        sbmtd_adapter.ItemClicked(new UserAssingmentAdapter.ClickListener() {
            @Override
            public void onItemClicked(UserAssingment asngmnt) {
                Intent intent = new Intent(getActivity(), SubmittedAssingmentView.class);
                intent.putExtra("SubjectId", subjectId);
                intent.putExtra("AssingmentId", asngmnt.assingmentId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!isTeacher) {
//            RetrieveSubmittedAssingment();
//            RetrievePendingAssingment();
//
//        }
//        else
//            RetrieveAssingmentforTeacher();
    }

    public void RetrievePendingAssingment() {
        viewModel.getPendingAssingment(uid, subjectId);
        viewModel.pendingAssingment.observe(getActivity(), new Observer<ArrayList<UserAssingment>>() {
            @Override
            public void onChanged(ArrayList<UserAssingment> userAssingments) {
//                Toast.makeText(getActivity(), "pedng" + userAssingments.size(), Toast.LENGTH_SHORT).show();

                if (pndng_adapter == null) {
                    pndng_adapter = new UserAssingmentAdapter(new ArrayList<>());

                }

                if (userAssingments != null && userAssingments.size() > 0)
                    pndng_adapter.setList(userAssingments);
            }
        });
    }

    public void RetrieveSubmittedAssingment() {
        viewModel.getSubmittedAssingment(uid, subjectId);
        viewModel.sbmtdAssingment.observe(getActivity(), new Observer<ArrayList<UserAssingment>>() {
            @Override
            public void onChanged(ArrayList<UserAssingment> userAssingments) {
                // Toast.makeText(getActivity(), "submtd" + userAssingments.size(), Toast.LENGTH_SHORT).show();
                if (sbmtd_adapter == null)
                    sbmtd_adapter = new UserAssingmentAdapter(new ArrayList<>());
                if (userAssingments != null && userAssingments.size() > 0)
                    sbmtd_adapter.setList(userAssingments);
            }
        });
    }

    public void CAssng() {
        Intent intent = new Intent(getActivity(), CreateAssingment.class);
        intent.putExtra("Subject Id", subjectId);
        ((SubjectScreen) getActivity()).startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        //detachEventListeners
    }
}
