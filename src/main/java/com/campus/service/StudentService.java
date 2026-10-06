package com.campus.service;

import com.campus.dao.DepartmentJPADAO;
import com.campus.dao.StudentJPADAO;
import com.campus.model.Department;
import com.campus.model.Student;

import java.util.List;

public class StudentService {

    private final StudentJPADAO studentDAO =
            new StudentJPADAO();

    private final DepartmentJPADAO departmentDAO =
            new DepartmentJPADAO();


    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }


    public Student getStudentById(int id) {
        return studentDAO.getStudentById(id);
    }


    public List<Department> getAllDepartments() {
        return departmentDAO.getAllDepartments();
    }


    public Department getDepartmentById(int id) {
        return departmentDAO.getDepartmentById(id);
    }


    public void addStudent(Student student) {
        studentDAO.addStudent(student);
    }


    public void updateStudent(Student student) {
        studentDAO.updateStudent(student);
    }


    public void deleteStudent(int id) {
        studentDAO.deleteStudent(id);
    }


    public List<Student> findStudentsByDepartment(
            String departmentName) {

        return studentDAO.findStudentsByDepartment(
                departmentName
        );
    }


    public List<Student> searchByName(
            String name) {

        return studentDAO.searchByName(name);
    }


    public long getStudentCount() {
        return studentDAO.getStudentCount();
    }
}