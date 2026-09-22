package com.campus.model;

public class Student {

    // Instance variables
    private int studentId;
    private String name;
    private String department;
    private int age;
    private int[] marks;

    // Static variable
    public static int totalStudents = 0;

    // Default Constructor
    public Student() {
        totalStudents++;
    }

    // Parameterized Constructor
    public Student(int studentId, String name, String department, int age, int[] marks) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.age = age;
        this.marks = marks;
        totalStudents++;
    }

    // Getter and Setter methods

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int[] getMarks() {
        return marks;
    }

    public void setMarks(int[] marks) {
        this.marks = marks;
    }

    // Instance method
    public void displayStudentInfo() {

        System.out.println("Student ID: " + studentId);
        System.out.println("Name: " + name);
        System.out.println("Department: " + department);
        System.out.println("Age: " + age);
    }

    // Method Overloading
    public void displayStudentInfo(boolean showMarks) {

        displayStudentInfo();

        if (showMarks) {
            displayMarks();
        }
    }

    // Display marks
    public void displayMarks() {

        System.out.println("Marks: ");

        if (marks != null && marks.length > 0) {

            for (int mark : marks) {
                System.out.print(mark + " ");
            }

            System.out.println();

        } else {
            System.out.println("No marks available");
        }
    }

    // Static method
    public static int getTotalStudents() {
        return totalStudents;
    }
}