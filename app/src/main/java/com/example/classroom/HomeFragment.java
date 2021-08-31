package com.example.classroom;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroom.Adapters.messageAdapter;
import com.example.classroom.ObjectClasses.Classroom;
import com.example.classroom.Repositories.ClassroomViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    ExtendedFloatingActionButton createMeet;
    Button enterMeet, addMessage;
    boolean isTeacher = false;
    RelativeLayout createlayout, homeView;
    EditText meetCode, messg;
    String subjectID;
    TextInputLayout lyt;
    messageAdapter adapter;
    ClassroomViewModel viewModel;


    public HomeFragment() {
        super(R.layout.home_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        subjectID = getArguments().getString("SubjectId");
        meetCode = view.findViewById(R.id.et_meetCode);
        homeView = view.findViewById(R.id.homView);
        messg = view.findViewById(R.id.et_mesage);
        lyt = view.findViewById(R.id.etmsg);
        enterMeet = view.findViewById(R.id.btn_enter);
        createlayout = view.findViewById(R.id.rlCreateMeet);
        createMeet = view.findViewById(R.id.btnCreateMeet);
        addMessage = view.findViewById(R.id.btnAddMessage);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(ClassroomViewModel.class);
        checkIfTeacher();
        setUpRecyclerView(view);

        createMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (createlayout.getVisibility() == View.GONE) {
                    createlayout.setVisibility(View.VISIBLE);
                    homeView.setVisibility(View.GONE);
                }

            }
        });
        Button gback = view.findViewById(R.id.btn_back);
        gback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createlayout.setVisibility(View.GONE);
                homeView.setVisibility(View.VISIBLE);
            }
        });

        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(messg.getText().toString())) {
                    messg.setError("Required Field");
                    return;
                }
                String message = messg.getText().toString();
                messg.setText("");
                viewModel.AddMessage(message, subjectID);
            }
        });


    }

    private void setUpRecyclerView(View v) {
        RecyclerView rv = v.findViewById(R.id.rvUpdates);
        adapter = new messageAdapter(new ArrayList<>());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewModel.RetrieveMessage(subjectID);
        viewModel.allMEssages.observe(getActivity(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                if (strings != null)
                    adapter.setList(strings);
            }
        });
    }


    private void checkIfTeacher() {
        FirebaseDatabase.getInstance(getString(R.string.database_url)).getReference().child("Classrooms").child(subjectID).child("teacher_id").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String teacherId = (String) dataSnapshot.getValue();
                if (teacherId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    isTeacher = true;
                    addMessage.setVisibility(View.VISIBLE);
                    messg.setVisibility(View.VISIBLE);
                    lyt.setVisibility(View.VISIBLE);
                    enterMeet.setText("CREATE");
                    createMeet.setText("CREATE MEET");
                    enterMeet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            enterTeacher();
                        }
                    });

                } else {
                    isTeacher = false;
                    enterMeet.setText("JOIN");
                    createMeet.setText("JOIN MEET");
                    enterMeet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            enterStudent();
                        }
                    });
                }

            }
        });

    }


    private void enterTeacher() {
        if (TextUtils.isEmpty(meetCode.getText().toString())) {
            meetCode.setError("Room Code required");
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String roomCode = (uid).substring(1, 2) + meetCode.getText().toString() + uid.substring(3, 6);
        URL url;
        try {
            url = new URL(("https://meet.jit.si"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(url)
                .setRoom(roomCode)
                .setAudioMuted(false)
                .setVideoMuted(false)
                .setAudioOnly(false)
                .setWelcomePageEnabled(false)
                .build();
        String msg = "Please enter this room code to join " + "\n" + "Room code: " + roomCode;
        viewModel.AddMessage(msg, subjectID);
        JitsiMeetActivity.launch(getActivity(), options);


    }

    private void enterStudent() {
        if (TextUtils.isEmpty(meetCode.getText().toString())) {
            meetCode.setError("Room Code required");
            return;
        }
        String roomCode = meetCode.getText().toString();
        try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(roomCode)
                    .setAudioMuted(true)
                    .setVideoMuted(true)
                    .setAudioOnly(false)
                    .setWelcomePageEnabled(false)
                    .build();
            JitsiMeetActivity.launch(getActivity(), options);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Unable to join", Toast.LENGTH_SHORT).show();
        }
    }


}
