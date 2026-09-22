package com.campus.service;

import com.campus.model.Student;

public class StudentService {

    // Calculate total marks
    public static int calculateTotalMarks(Student student) {
        int total = 0;
        int[] marks = student.getMarks();

        if (marks != null && marks.length > 0) {
            for (int mark : marks) {
                total += mark;
            }
        }
        return total;
    }

    // Calculate average marks
    public static double calculateAverageMarks(Student student) {
        int[] marks = student.getMarks();

        if (marks != null && marks.length > 0) {
            return (double) calculateTotalMarks(student) / marks.length;
        } else {
            return 0.0;
        }
    }

    // Find minimum marks
    public static int findMinMarks(Student student) {
        int[] marks = student.getMarks();

        if (marks != null && marks.length > 0) {
            int min = marks[0];
            for (int mark : marks) {
                if (mark < min) {
                    min = mark;
                }
            }
            return min;
        } else {
            return 0;
        }
    }

    // Find maximum marks
    public static int findMaxMarks(Student student) {
        int[] marks = student.getMarks();

        if (marks != null && marks.length > 0) {
            int max = marks[0];
            for (int mark : marks) {
                if (mark > max) {
                    max = mark;
                }
            }
            return max;
        } else {
            return 0;
        }
    }

    // Calculate grade
    public static String calculateGrade(Student student) {
        double average = calculateAverageMarks(student);

        if (average >= 90) {
            return "A";
        } else if (average >= 80) {
            return "B";
        } else if (average >= 70) {
            return "C";
        } else if (average >= 60) {
            return "D";
        } else {
            return "F";
        }
    }

    // Check pass/fail status
    public static boolean isPassed(Student student, int passingMark) {
        return isPassed(student.getMarks(), passingMark);
    }

    public static boolean isPassed(int[] marks, int passingMark) {
        if (marks != null && marks.length > 0) {
            for (int mark : marks) {
                if (mark < passingMark) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    // Display result summary
    public static void displayResult(Student student) {
        int total = calculateTotalMarks(student);
        double average = calculateAverageMarks(student);
        String grade = calculateGrade(student);
        boolean passed = isPassed(student, 40);
        int high = findMaxMarks(student);
        int low = findMinMarks(student);

        System.out.println("Name: " + student.getName());
        System.out.println("Total Marks: " + total);
        System.out.println("Average Marks: " + average);
        System.out.println("Grade: " + grade);
        System.out.println("Result: " + (passed ? "Pass" : "Fail"));
        System.out.println("High Marks: " + high);
        System.out.println("Low Marks: " + low);
    }
}