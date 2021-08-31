package com.example.classroom.ObjectClasses;

import androidx.annotation.Nullable;

public class Submissions {
    public String studentId, submission_url, studentName;
    public long submitted_time;
    public String marks_Assigned = "NA";

    public Submissions(String studentId, String submission_url, long submitted_time, String marks_Assigned) {
        this.studentId = studentId;
        this.submission_url = submission_url;
        this.submitted_time = submitted_time;
        this.marks_Assigned = marks_Assigned;
    }

    public Submissions() {

    }
}
