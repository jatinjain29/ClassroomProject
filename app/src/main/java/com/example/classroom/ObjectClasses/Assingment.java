package com.example.classroom.ObjectClasses;

import java.util.ArrayList;

public class Assingment {
    public long dueTime;
    public String assingment_name, url;
    public String max_marks;
    public String assignmentId="";

    public Assingment(long dueTime, String assingment_name, String url, String max_marks) {
        this.dueTime = dueTime;
        this.assingment_name = assingment_name;
        this.url = url;
        this.max_marks = max_marks;

    }

    public Assingment() {

    }
}
