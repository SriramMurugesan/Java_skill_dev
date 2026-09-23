package com.campus.model;

import com.campus.service.StudentService;
import com.campus.contract.StudentOperations;
public class ScholarshipStudent extends Student implements StudentOperations {
    
    private double scholarshipPercentage;

    public ScholarshipStudent(int studentId, String name, String department, int age, int[] marks, double scholarshipPercentage) {
        
        super(studentId, name, department, age, marks);
        this.scholarshipPercentage = scholarshipPercentage;
    }
    //getter and setter

    public double getScholarshipPercentage() {
        return scholarshipPercentage;
    }
    public void setScholarshipPercentage(double scholarshipPercentage) {
        this.scholarshipPercentage = scholarshipPercentage;
    }

    @Override 
    public void  displayStudentInfo() {
        super.displayStudentInfo();
        System.out.println("Scholarship Percentage: " + scholarshipPercentage);
    }

    @Override
    public void generateReport() {
        System.out.println("Scholarship Student Report");
        
    }

    @Override
    public boolean isEligibleForScholarship() {
        return StudentService.calculateAverageMarks(this) >= 80;
    }

    @Override
    public String getStudentType() {
        return "Scholarship Student";
    }   
    
}
