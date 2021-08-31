package com.example.classroom.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classroom.ObjectClasses.Assingment;
import com.example.classroom.R;
import com.example.classroom.ObjectClasses.Submissions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AssingmentSubmission extends AppCompatActivity {
    String assingmentId = "", subjectId = "";
    TextView asngn_name, ddate, max_marks;
    DatabaseReference reference = FirebaseDatabase.getInstance("https://classroom-2859b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Classrooms");
    Assingment assingment;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    Uri file_uri = null;
    ProgressBar progressBar;
    String submsn_url;
    FirebaseUser cuser = FirebaseAuth.getInstance().getCurrentUser();
    String uid = cuser.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assingment_submiision);
        assingmentId = getIntent().getStringExtra("Assingmnet Id");
        subjectId = getIntent().getStringExtra("Subject Id");
        asngn_name = findViewById(R.id.tv_asing_name);
        ddate = findViewById(R.id.tv_ddate);
        max_marks = findViewById(R.id.tv_marks);
        progressBar = findViewById(R.id.prgrs_bar);
        retrieveAssingment();

    }

    public void retrieveAssingment() {
        assingment = new Assingment();
        reference.child(subjectId).child("Assingments").child(assingmentId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot snapshot1 : task.getResult().getChildren()) {
                    switch (snapshot1.getKey()) {
                        case "url":
                            assingment.url = (String) snapshot1.getValue();

                            break;
                        case "assingment_name":
                            assingment.assingment_name = (String) snapshot1.getValue();
                            break;
                        case "dueTime":
                            long dtime = (long) snapshot1.getValue();
                            assingment.dueTime = dtime;
                            break;
                        case "max_marks":
                            assingment.max_marks = (String) snapshot1.getValue();
                            break;
                    }
                }

                asngn_name.setText(assingment.assingment_name);
                max_marks.setText("Max Marks: " + assingment.max_marks);
                ddate.setText("Due Time: " + getDate(assingment.dueTime));
            }
        });
    }

    public void Submit(View v) {
        if (file_uri == null) {
            Toast.makeText(this, "No files attached", Toast.LENGTH_SHORT).show();
            return;
        }
        //upload to firebase storage
        progressBar.setVisibility(View.VISIBLE);

        uploadFile();


    }

    public void uploadFile() {

        long ctime = System.currentTimeMillis();
        String fileName = ctime + ".pdf";
        final StorageReference ref = storageReference.child("Submissions").child(fileName);


        ref.putFile(file_uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
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
                    submsn_url = downloadUri.toString();
                    progressBar.setVisibility(View.GONE);
                    Button attach_btn = findViewById(R.id.btn_attach);
                    Button submt_btn = findViewById(R.id.btn_submit);
                    attach_btn.setClickable(false);
                    submt_btn.setClickable(false);
                    submt_btn.setText("Already Submitted");
                    TextView msg = findViewById(R.id.tv_sb_msg);
                    msg.setVisibility(View.VISIBLE);
                    msg.setText("Assingment Successfully Submitted");
                    addSubmission(ctime);
                    MarkSubmitted();
                } else {
                    Toast.makeText(AssingmentSubmission.this, "Unable to submit", Toast.LENGTH_SHORT).show();
                    Log.e("submission Error", "Error");

                }
            }
        });

    }


    private void MarkSubmitted() {
        Map<String, Object> submsn = new HashMap<>();
        submsn.put(assingmentId, assingment.assingment_name);
        DatabaseReference nreference = reference.getParent().child("Users").child(uid).child("classes").child(subjectId).child("Assingments");
        nreference.child("Submitted").updateChildren(submsn).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        nreference.child("Pending").child(assingmentId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void addSubmission(long time) {
        Submissions newSubmission = new Submissions(uid, submsn_url, time, "NA");
        newSubmission.studentName = cuser.getDisplayName();
        Map<String, Submissions> mmap = new HashMap<>();
        mmap.put(uid, newSubmission);
        reference.child(subjectId).child("Assingments").child(assingmentId).child("Submissions").setValue(mmap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Submission added", "Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e("Submission added", "Failure " + e.toString());
            }
        });
    }

    public void attach(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            file_uri = data.getData();
            Button btn = findViewById(R.id.btn_attach);
            btn.setText("Attached");
        }
    }

    public void downloadFile(View v) {
        String url = assingment.url;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

    }

    public String getDate(long timeinmillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeinmillis);
        return formatter.format(calendar.getTime());

    }
}