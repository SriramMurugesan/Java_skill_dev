package com.campus.app;

import java.util.Scanner;
import com.campus.model.Student;
import com.campus.service.StudentService;
import com.campus.model.ScholarshipStudent;
import com.campus.contract.StudentOperations;

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

        System.out.println("Enter the scholarship percentage: ");
        double scholarshipPercentage = sc.nextDouble();
        

        Student student = new ScholarshipStudent(studentId, name, department, age, marks, scholarshipPercentage);

        student.displayStudentInfo();

        student.displayStudentInfo(true);

        StudentService.displayResult(student);

        System.out.println("Student Type: " + student.getStudentType());

        StudentOperations operations = (StudentOperations) student;
        operations.generateReport();

        System.out.println("Is Eligible for Scholarship: " + operations.isEligibleForScholarship());

        
        Student.StudentCard card = student.new StudentCard();
        card.displayStudentCard();

        // Member Inner Class: ExamResult (previously unused)
        Student.ExamResult examResult = student.new ExamResult();
        examResult.displayExamResult();

        // Anonymous Inner Class implementing StudentOperations
        StudentOperations specialOperations = new StudentOperations() {
            @Override
            public void generateReport() {
                System.out.println("Special Operations Report (Anonymous Class)");
            }

            @Override
            public boolean isEligibleForScholarship() {
                return false;
            }
        };
        specialOperations.generateReport();
        System.out.println("Is Eligible for Scholarship (Special): " + specialOperations.isEligibleForScholarship());

        System.out.println("Students Created: " + Student.getTotalStudents());

        sc.close();
    }
}