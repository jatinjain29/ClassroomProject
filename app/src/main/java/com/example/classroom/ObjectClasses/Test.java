package com.example.classroom.ObjectClasses;

public class Test {
    public String name, instructions, testId;
    public long dueTime;
    public String refereneMaterial;
    public String umarks="NA";

    public Test(String name, String instructions, long dueTime, String refereneMaterial,String testId) {
        this.name = name;
        this.instructions = instructions;
        this.dueTime = dueTime;
        this.refereneMaterial = refereneMaterial;
        this.testId=testId;
    }

    public Test() {
    }
}
