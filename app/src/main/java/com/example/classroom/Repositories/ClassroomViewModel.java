package com.example.classroom.Repositories;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.classroom.ObjectClasses.Assingment;
import com.example.classroom.ObjectClasses.Classroom;
import com.example.classroom.ObjectClasses.StudentTest;
import com.example.classroom.ObjectClasses.Submissions;
import com.example.classroom.ObjectClasses.Test;
import com.example.classroom.ObjectClasses.UserAssingment;
import com.example.classroom.Repositories.ClassroomRepo;

import java.util.ArrayList;

public class ClassroomViewModel extends AndroidViewModel {
    ClassroomRepo repo;
    public MutableLiveData<Classroom> joinedClasses;

    public MutableLiveData<ArrayList<Assingment>> allAssingment;
    public MutableLiveData<Pair<Submissions, Assingment>> asngSubmsn;

    public MutableLiveData<ArrayList<Submissions>> allSubmissions;

    public MutableLiveData<ArrayList<Test>> previous, upcoming;
    public MutableLiveData<ArrayList<StudentTest>> students;
    public MutableLiveData<ArrayList<String>> allMEssages;

    public ClassroomViewModel(@NonNull Application application) {
        super(application);
        repo = new ClassroomRepo();
        joinedClasses = repo.joined_class;
        allMEssages = repo.allMessages;
        allAssingment = repo.allAssingment;
        asngSubmsn = repo.asngSubmsn;
        allSubmissions = repo.allSubmission;
        previous = repo.previousTests;
        upcoming = repo.upcomingTests;
        students = repo.students;
    }

    public void getClassroom(String key) {
        repo.getClassroom(key);
    }

    public void RetrieveAllAssingment(String subjectId) {
        repo.getAllAssingments(subjectId);
    }

    public void RetireveAssingment_Subission(String subjectID, String userId, String AsngID) {
        repo.RetrieveAssignment_Submission(subjectID, userId, AsngID);
    }

    public void getAllSubmissions(String subjecctId, String AsngID) {
        repo.getAllSubmsiions(subjecctId, AsngID);
    }

    public void AssignMarks(String subjectId, String asngId, String userId, String marks) {
        repo.AssignMarks(subjectId, asngId, userId, marks);
    }

    public void UpcomingTests(String subjectId, long currentTime) {
        repo.RetrieveUpcomingTest(subjectId, currentTime);
    }

    public void PreviousTests(String subjectId, long currentTime, String userId, boolean isTeacher) {
        repo.RetrievePreviousTest(subjectId, currentTime, userId, isTeacher);
    }

    public void updateStudentMarks(String subjectId, String testId, String marks, String studentId) {
        repo.updateStudentMarks(subjectId, testId, marks, studentId);
    }

    public void getStudents(String subjectId, String testId) {
        repo.getStudentList(subjectId, testId);
    }

    public void AddMessage(String messgae, String sujectID) {
        repo.AddMessage(messgae, sujectID);
    }

    public void RetrieveMessage(String subjectID) {
        repo.retrieveMessage(subjectID);
    }

}
