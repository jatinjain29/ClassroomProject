package com.example.classroom.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.classroom.ObjectClasses.Assingment;
import com.example.classroom.ObjectClasses.StudentTest;
import com.example.classroom.ObjectClasses.Test;
import com.example.classroom.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateTest extends AppCompatActivity {

    int cyear, cmonth, cday, chour, cminute, csecond;
    EditText testName, ddate, dtime, instruction;
    DatePickerDialog.OnDateSetListener datecallback;
    TimePickerDialog.OnTimeSetListener timecallback;
    Calendar c = Calendar.getInstance();
    Uri fileUri;
    TextView msg;
    Button fileAttach;
    RelativeLayout progressBar;
    String fileUrl, subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);
        testName = findViewById(R.id.et_testName);
        ddate = findViewById(R.id.et_test_ddate);
        dtime = findViewById(R.id.et_test_dTime);
        instruction = findViewById(R.id.et_instructions);
        fileAttach = findViewById(R.id.btn_fileattach);
        datecallback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int fmonth = month + 1;
                ddate.setText(year + "-" + fmonth + "-" + dayOfMonth);
                c.set(year, month, dayOfMonth);

            }

        };
        msg = findViewById(R.id.infoMsgtest);
        timecallback = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                dtime.setText(hourOfDay + ":" + minute + ":" + "00");
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
            }
        };
        progressBar = findViewById(R.id.prgrs_test);
        subjectId = getIntent().getStringExtra("SubjectId");
    }

    public void PickDate(View v) {
        cyear = c.get(Calendar.YEAR);
        cmonth = c.get(Calendar.MONTH);
        cday = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTest.this, datecallback, cyear, cmonth, cday);
        datePickerDialog.show();

    }

    public void PickTime(View v) {
        chour = c.get(Calendar.HOUR_OF_DAY);
        cminute = c.get(Calendar.MINUTE);
        csecond = c.get(Calendar.SECOND);
        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTest.this, timecallback, chour, cminute, true);
        timePickerDialog.show();

    }

    public void attach(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, 2);

    }


    public void SaveTest(View v) {


        if (TextUtils.isEmpty(testName.getText().toString())) {
            testName.setError("Name required");
            return;
        }


        if (TextUtils.isEmpty(ddate.getText().toString()) || TextUtils.isEmpty(dtime.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Due date and time required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(instruction.getText().toString())) {
            testName.setError("Instructions required");
            return;
        }
        if (fileUri == null) {

            progressBar.setVisibility(View.VISIBLE);
            UpdateDatabase();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        uploadFile();


    }

    public void uploadFile() {
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        String name = testName.getText().toString() + ("" + System.currentTimeMillis()).substring(4, 7) + ".pdf";


        final StorageReference ref = reference.child("Tests").child(name);

        ref.putFile(fileUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    notifyFailure();
                    Log.i("got error", task.getException().getLocalizedMessage());
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    fileUrl = downloadUri.toString();
                    Log.i("Created", "created");
                    UpdateDatabase();
                } else {
                    // Handle failures
                    // ...not
                    notifyFailure();
                }
            }
        });

    }

    public void UpdateDatabase() {
        Map<String, Object> nTest = new HashMap<>();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://classroom-2859b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Classrooms").child(subjectId).child("Tests");
        String key = reference.push().getKey();
        Test ntest = new Test(testName.getText().toString(), instruction.getText().toString(), c.getTimeInMillis(), fileUrl,key);

        nTest.put(key, ntest);
        reference.child(key).setValue(ntest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("success", "Success");
                addUsers(reference.child(key));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                notifyFailure();
            }
        });
    }

    private void addUsers(DatabaseReference reference) {
        DatabaseReference memRefernce = reference.getParent().getParent().child("Members");

        memRefernce.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Map<String, String> allMembers = new HashMap<>();
                allMembers = (Map<String, String>) dataSnapshot.getValue();
                Map<String, StudentTest> testStudents = new HashMap<>();
                for (Map.Entry<String, String> mems : allMembers.entrySet()) {
                    String key = mems.getKey();
                    StudentTest nw = new StudentTest(mems.getValue(), "NA", key);
                    testStudents.put(key, nw);
                }
                reference.child("Students").setValue(testStudents).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        notifySuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        notifyFailure();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                notifyFailure();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && data != null) {
            fileUri = data.getData();
            fileAttach.setText("Change");
        }
    }

    protected void notifyFailure() {
        msg.setText("** Error creating test, please Try Again");
        progressBar.setVisibility(View.GONE);
    }

    protected void notifySuccess() {
        testName.setText("");
        ddate.setText("");
        dtime.setText("");
        instruction.setText("");
        msg.setText("** Test Added Successfully");
        progressBar.setVisibility(View.GONE);
    }

}