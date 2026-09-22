package com.campus.app;

import java.util.Scanner;
import com.campus.model.Student;
import com.campus.service.StudentService;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter the student ID: ");
        int studentId = sc.nextInt();
        sc.nextLine(); // Consume newline left by nextInt()

        System.out.println("Enter the student name: ");
        String name = sc.nextLine();

        System.out.println("Enter the student department: ");
        String department = sc.nextLine();

        System.out.println("Enter the student age: ");
        int age = sc.nextInt();

        System.out.println("Enter the number of subjects: ");
        int numSubjects = sc.nextInt();

        int[] marks = new int[numSubjects];

        for (int i = 0; i < numSubjects; i++) {

            System.out.println("Enter the marks for subject " + (i + 1) + ": ");
            marks[i] = sc.nextInt();
        }

        Student student = new Student(studentId, name, department, age, marks);

        student.displayStudentInfo(true);

        StudentService.displayResult(student);

        System.out.println("Students Created: " + Student.getTotalStudents());

        sc.close();
    }
}