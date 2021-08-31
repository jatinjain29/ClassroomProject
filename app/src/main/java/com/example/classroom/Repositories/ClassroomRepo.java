package com.example.classroom.Repositories;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.classroom.ObjectClasses.Classroom;
import com.example.classroom.ObjectClasses.Assingment;
import com.example.classroom.ObjectClasses.StudentTest;
import com.example.classroom.ObjectClasses.Submissions;
import com.example.classroom.ObjectClasses.Test;
import com.example.classroom.ObjectClasses.UserAssingment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ClassroomRepo {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://classroom-2859b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Classrooms");
    MutableLiveData<Classroom> joined_class = new MutableLiveData<>();
    MutableLiveData<Pair<Submissions, Assingment>> asngSubmsn = new MutableLiveData<>();
    MutableLiveData<ArrayList<Submissions>> allSubmission = new MutableLiveData<>();
    MutableLiveData<ArrayList<Assingment>> allAssingment = new MutableLiveData<>();
    MutableLiveData<ArrayList<Test>> previousTests = new MutableLiveData<>();
    MutableLiveData<ArrayList<Test>> upcomingTests = new MutableLiveData<>();
    MutableLiveData<ArrayList<StudentTest>> students = new MutableLiveData<>();
    MutableLiveData<ArrayList<String>> allMessages = new MutableLiveData<>();

    protected void getClassroom(String key) {
        databaseReference.child(key).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                Classroom nclass = snapshot.getValue(Classroom.class);
                joined_class.setValue(nclass);
            }
        });
    }

    protected void RetrieveAssignment_Submission(String subjectID, String userId, String assignmentId) {
        DatabaseReference reference = databaseReference.child(subjectID).child("Assingments").child(assignmentId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String assignmentName = (String) snapshot.child("assingment_name").getValue();
                long dueTime = (long) snapshot.child("dueTime").getValue();
                String maxMarks = (String) snapshot.child("max_marks").getValue();
                String url = (String) snapshot.child("url").getValue();
                String submission_url = (String) snapshot.child("Submissions").child(userId).child("submission_url").getValue();
                String marks_Assigned = (String) snapshot.child("Submissions").child(userId).child("marks_Assigned").getValue();
                long submissionTime = (long) snapshot.child("Submissions").child(userId).child("submitted_time").getValue();
                Assingment cAssignment = new Assingment(dueTime, assignmentName, url, maxMarks);
                Submissions mySubmission = new Submissions(userId, submission_url, submissionTime, marks_Assigned);
                Pair<Submissions, Assingment> pair1 = new Pair<>(mySubmission, cAssignment);
                asngSubmsn.setValue(pair1);
                reference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    protected void getAllSubmsiions(String subjectID, String AsngID) {

        DatabaseReference reference = databaseReference.child(subjectID).child("Assingments").child(AsngID).child("Submissions");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<Submissions> allSubmissions = new ArrayList();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Submissions subi = snapshot1.getValue(Submissions.class);
                    allSubmissions.add(subi);
                }
                allSubmission.setValue(allSubmissions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    protected void getAllAssingments(String subjectId) {
        DatabaseReference reference = databaseReference.child(subjectId).child("Assingments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Assingment> allAssingmets = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Assingment nwAssingment = snapshot1.getValue(Assingment.class);
                    Log.i("id added", "true" + snapshot1.getKey());
                    nwAssingment.assignmentId = snapshot1.getKey();
                    allAssingmets.add(nwAssingment);
                }
                allAssingment.setValue(allAssingmets);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void AssignMarks(String subjectId, String asngId, String userId, String marks) {
        DatabaseReference reference = databaseReference.child(subjectId).child("Assingments").child(asngId).child("Submissions").child(userId).child("marks_Assigned");
        reference.setValue(marks).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    public void RetrieveUpcomingTest(String subjectId, long currentTime) {
        DatabaseReference reference = databaseReference.child(subjectId).child("Tests");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<Test> upcomingTest = new ArrayList<Test>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Log.i("here", currentTime + " " + snapshot1.child("dueTime").getValue() + " " + (currentTime - (long) snapshot1.child("dueTime").getValue()));
                    if ((long) snapshot1.child("dueTime").getValue() > currentTime) {
                        Test upcoming = snapshot1.getValue(Test.class);
                        upcomingTest.add(upcoming);
                    }
                }
                Log.i("UPCOMING TEST", "change detected");
                upcomingTests.setValue(upcomingTest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void RetrievePreviousTest(String subjectId, long currentTime, String userId, boolean isTeacher) {
        DatabaseReference reference = databaseReference.child(subjectId).child("Tests");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<Test> previousTest = new ArrayList<Test>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if ((long) snapshot1.child("dueTime").getValue() <= currentTime) {
                        Test upcoming = snapshot1.getValue(Test.class);
                        if (!isTeacher) {
                            String marks = (String) snapshot1.child("Students").child(userId).child("marks").getValue();
                            upcoming.umarks = marks;
                        }
                        previousTest.add(upcoming);
                    }
                }
                previousTests.setValue(previousTest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateStudentMarks(String subjectId, String testId, String marks, String studentId) {
        databaseReference.child(subjectId).child("Tests").child(testId).child("Students").child(studentId).child("marks").setValue(marks);
    }

    public void getStudentList(String subjectId, String testId) {
        databaseReference.child(subjectId).child("Tests").child(testId).child("Students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<StudentTest> updated = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    StudentTest nw = snapshot1.getValue(StudentTest.class);
                    updated.add(nw);
                }
                students.setValue(updated);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void AddMessage(String messgae, String sujectID) {
        String key = databaseReference.child(sujectID).child("Messages").push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put(key, messgae);
        databaseReference.child(sujectID).child("Messages").updateChildren(map);
    }

    public void retrieveMessage(String sujectID) {
        databaseReference.child(sujectID).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> allmsg = new ArrayList<String>();
                for (DataSnapshot snapshots : snapshot.getChildren()) {
                    allmsg.add((String) snapshots.getValue());
                }
                allMessages.setValue(allmsg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
