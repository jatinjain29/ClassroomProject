package com.example.classroom.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.example.classroom.ObjectClasses.Assingment;
import com.example.classroom.Repositories.ClassroomViewModel;
import com.example.classroom.R;
import com.example.classroom.ObjectClasses.Submissions;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SubmittedAssingmentView extends AppCompatActivity {
    ClassroomViewModel viewModel;
    String subjectID, userId, AsngId;
    String submsnUrl, asngmntUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitted_assingment_view);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ClassroomViewModel.class);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent receivedIntent = getIntent();
        subjectID = receivedIntent.getStringExtra("SubjectId");
        AsngId = receivedIntent.getStringExtra("AssingmentId");
    }

    @Override
    protected void onResume() {
        super.onResume();
        RetrieveData();

    }

    public void RetrieveData() {
        viewModel.RetireveAssingment_Subission(subjectID, userId, AsngId);
        viewModel.asngSubmsn.observe(this, new Observer<Pair<Submissions, Assingment>>() {
            @Override
            public void onChanged(Pair<Submissions, Assingment> stringPairPair) {
                Assingment updatedAssingment = stringPairPair.second;
                Submissions updatedSubmission = stringPairPair.first;
                if (updatedAssingment != null && updatedSubmission != null) {

                    TextView asngName, dueDate, marksAssigned, maxMarks, submsnTime;
                    asngName = findViewById(R.id.tv_s_asng_name);
                    dueDate = findViewById(R.id.tv_s_ddate);
                    marksAssigned = findViewById(R.id.tv_s_marks_assigned);
                    maxMarks = findViewById(R.id.tv_s_marks);
                    submsnTime = findViewById(R.id.tv_s_sdate);
                    submsnUrl = updatedSubmission.submission_url;
                    asngmntUrl = updatedAssingment.url;
                    asngName.setText(updatedAssingment.assingment_name);
                    dueDate.setText("Due Time: " + timefromMillis(updatedAssingment.dueTime));
                    maxMarks.setText("Max Marks: " + updatedAssingment.max_marks);
                    marksAssigned.setText("Marks Assigned: " + updatedSubmission.marks_Assigned);
                    submsnTime.setText("Submission Time: " + timefromMillis(updatedSubmission.submitted_time));

                }

            }
        });
    }

    public String timefromMillis(long timeinmillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeinmillis);
        return formatter.format(calendar.getTime());
    }

    public void downloadFile(View v) {
        openUrl(asngmntUrl);
    }
    public void downloadSubmission(View v) {
        openUrl(submsnUrl);
    }
    public void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


}