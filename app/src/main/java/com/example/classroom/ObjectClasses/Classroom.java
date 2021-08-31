package com.example.classroom.ObjectClasses;

public  class Classroom {
   public String unique_code;
    public String subject, name;
    public String teacher_name,teacher_id;

public Classroom(){}
    public Classroom(String uq, String subject, String name,String teacher_name,String teaher_id) {
        unique_code = uq;
        this.subject = subject;
        this.name = name;
        this.teacher_name=teacher_name;
        this.teacher_id=teaher_id;

    }
}
