package com.example.classroom.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classroom.ObjectClasses.Assingment;
import com.example.classroom.R;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PreviousAssinngmentView extends AppCompatActivity {
    Assingment cAssignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_assinngment_view);
        cAssignment = new Gson().fromJson(getIntent().getStringExtra("Assignment"), Assingment.class);
        TextView asngName, dueDate, maxMarks;
        asngName = findViewById(R.id.tv_p_asng_name);
        dueDate = findViewById(R.id.tv_p_ddate);
        maxMarks = findViewById(R.id.tv_p_marks);
        asngName.setText(cAssignment.assingment_name);
        dueDate.setText("Due Time: " + getDate(cAssignment.dueTime));
        maxMarks.setText("Max Marks: " + cAssignment.max_marks);
    }

    public void downloadFile(View v) {
        if (cAssignment.url.isEmpty()) {
            Toast.makeText(PreviousAssinngmentView.this, "No files attahed", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(cAssignment.url));
        startActivity(intent);
    }

    public void checkSubmissions(View v) {
        Intent intent = new Intent(PreviousAssinngmentView.this, ViewSubmissions.class);
        intent.putExtra("AssignmentId", cAssignment.assignmentId);
        intent.putExtra("SubjectId", getIntent().getStringExtra("SubjectId"));
        if(cAssignment.assignmentId!=null)
        startActivity(intent);
        else
            Log.i("still null",cAssignment.toString());
    }

    public String getDate(long timeinmillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeinmillis);
        return formatter.format(calendar.getTime());
    }

}