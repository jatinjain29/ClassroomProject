package com.example.classroom.Repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.classroom.ObjectClasses.UserAssingment;
import com.example.classroom.Repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class UserViewModel extends AndroidViewModel {
    UserRepository repo;
    public MutableLiveData<ArrayList<String>> ClassKeys;
    public MutableLiveData<ArrayList<UserAssingment>> pendingAssingment;
    public MutableLiveData<ArrayList<UserAssingment>> sbmtdAssingment;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repo = new UserRepository();
        ClassKeys = repo.classKeys;
        pendingAssingment = repo.pndngAssingment;
        sbmtdAssingment = repo.sbmtdAssingment;
    }

    public void updateDisplayName(String name, FirebaseUser user) {
        repo.updateDisplayName(user, name);
    }

    public void getClasses(String uid) {
        repo.getClasses(uid);
    }

    public void getPendingAssingment(String uid, String subjectId) {
        repo.RetrievePendingAssingment(uid, subjectId);
    }

    public void getSubmittedAssingment(String uid, String subjectId) {
        repo.RetrieveSubmittedAssingment(uid, subjectId);
    }
}
