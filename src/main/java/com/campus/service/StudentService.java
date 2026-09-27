package com.campus.service;

import java.util.ArrayList;
import java.util.List;

public class StudentService {

    private final List<String> students =
            new ArrayList<>();

    public StudentService() {

        students.add("101 - Arun - CSE");
        students.add("102 - Priya - ECE");
        students.add("103 - Karthik - IT");
    }

    // Get all students
    public List<String> getAllStudents() {

        return students;
    }

    // Add student
    public void addStudent(
            String name,
            String department) {

        String student =
                (students.size() + 101)
                + " - "
                + name
                + " - "
                + department;

        students.add(student);
    }
}