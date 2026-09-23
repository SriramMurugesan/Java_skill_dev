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

        StudentService.displayResult(student);

        System.out.println("Student Type: " + student.getStudentType());

        StudentOperations operations = (StudentOperations) student;
        operations.generateReport();
        System.out.println("Is Eligible for Scholarship: " + operations.isEligibleForScholarship());

        // Member Inner Classes
        Student.StudentCard card = student.new StudentCard();
        card.displayStudentCard();

        Student.ExamResult examResult = student.new ExamResult();
        examResult.displayExamResult();

        // Anonymous Inner Class implementing StudentOperations (Special Quota)
        StudentOperations specialOperations = new StudentOperations() {
            @Override
            public void generateReport() {
                System.out.println("Special Quota Criteria: Minimum 75% average marks");
            }

            @Override
            public boolean isEligibleForScholarship() {
                return StudentService.calculateAverageMarks(student) >= 75;
            }
        };
        specialOperations.generateReport();
        System.out.println("Is Eligible for Scholarship (Special Quota): " + specialOperations.isEligibleForScholarship());

        System.out.println("Students Created: " + Student.getTotalStudents());

        sc.close();
    }
}