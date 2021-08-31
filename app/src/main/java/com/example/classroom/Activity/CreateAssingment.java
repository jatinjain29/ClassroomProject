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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.classroom.ObjectClasses.Assingment;
import com.example.classroom.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateAssingment extends AppCompatActivity {

    EditText Ddate, AssngName, Dtime, MaxMarks;
    Assingment fin = new Assingment();
    Uri fileUri = null;
    String fileUrl = "";
    Button attachfile;
    int cyear, cmonth, cday, chour, cminute, csecond;
    RelativeLayout progressBar;
    TextView tv;
    DatePickerDialog.OnDateSetListener datecallback;
    TimePickerDialog.OnTimeSetListener timecallback;
    Calendar c = Calendar.getInstance();
    String subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_assingment);

        subjectId = getIntent().getStringExtra("Subject Id");

        Ddate = findViewById(R.id.et_ddate);
        attachfile = findViewById(R.id.btn_attach);
        Dtime = findViewById(R.id.et_dTime);
        AssngName = findViewById(R.id.et_asngName);
        MaxMarks = findViewById(R.id.et_marks);
        progressBar = findViewById(R.id.prgrs);
        tv = findViewById(R.id.infoMsg);
        datecallback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int fmonth = month + 1;
                Ddate.setText(year + "-" + fmonth + "-" + dayOfMonth);

            }
        };

        timecallback = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Dtime.setText(hourOfDay + ":" + minute + ":" + "00");
            }
        };

    }


    public void PickDate(View v) {
        cyear = c.get(Calendar.YEAR);
        cmonth = c.get(Calendar.MONTH);
        cday = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateAssingment.this, datecallback, cyear, cmonth, cday);
        datePickerDialog.show();

    }


    public void PickTime(View v) {
        chour = c.get(Calendar.HOUR_OF_DAY);
        cminute = c.get(Calendar.MINUTE);
        csecond = c.get(Calendar.SECOND);
        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateAssingment.this, timecallback, chour, cminute, true);
        timePickerDialog.show();

    }


    public void SaveAssingment(View v) {


        if (TextUtils.isEmpty(AssngName.getText().toString())) {
            AssngName.setError("Assingment Name required");
            return;
        }

        if (TextUtils.isEmpty(MaxMarks.getText().toString())) {
            MaxMarks.setError("Enter Maximum Marks");
            return;
        }


        if (TextUtils.isEmpty(Ddate.getText().toString()) || TextUtils.isEmpty(Dtime.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Due date and time required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fileUri == null) {
            attachfile.setError("Attachment Required");
            return;
        }
        fin.max_marks = MaxMarks.getText().toString();
        fin.assingment_name = AssngName.getText().toString();
        progressBar.setVisibility(View.VISIBLE);

        uploadFile();


    }

    public void uploadFile() {
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        String name = fin.assingment_name + ("" + System.currentTimeMillis()).substring(4, 6) + ".pdf";


        final StorageReference ref = reference.child("Assingments").child(name);

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
        Map<String, Object> nAssingment = new HashMap<>();
        fin.url = fileUrl;
        fin.dueTime = c.getTimeInMillis();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://classroom-2859b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Classrooms").child(subjectId).child("Assingments");
        String key = reference.push().getKey();
        Assingment nasng = new Assingment(fin.dueTime, fin.assingment_name, fin.url, fin.max_marks);
        nAssingment.put(key, nasng);
        reference.updateChildren(nAssingment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("success", "Success");
                notifyMembers(key, nasng.assingment_name);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                notifyFailure();
            }
        });
    }

    public void notifyMembers(String key, String asng_name) {
        FirebaseDatabase.getInstance("https://classroom-2859b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Classrooms").child(subjectId).child("Members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String userId = snapshot1.getKey();
                    addAssingmentInUser(userId, key, asng_name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                notifyFailure();
            }
        });
        notifySuccess();
    }

    private void addAssingmentInUser(String userId, String key, String asng_name) {
        Map<String, Object> nasng = new HashMap<>();
        nasng.put(key, asng_name);

        FirebaseDatabase.getInstance("https://classroom-2859b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users")
                .child(userId).child("classes").child(subjectId).child("Assingments").child("Pending").updateChildren(nasng).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public void attach(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, 2);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && data != null) {
            fileUri = data.getData();
            attachfile.setText("Change");
        }
    }

    protected void notifyFailure() {
        tv.setText("** Error creating assignment, please Try Again");
        progressBar.setVisibility(View.GONE);
    }

    protected void notifySuccess() {
        AssngName.setText("");
        MaxMarks.setText("");
        Ddate.setText("");
        Dtime.setText("");
        tv.setText("** Assignment Created Successfully");
        progressBar.setVisibility(View.GONE);
    }

}