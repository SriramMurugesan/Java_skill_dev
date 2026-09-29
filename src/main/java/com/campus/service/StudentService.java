package com.campus.service;

import com.campus.dao.StudentDAO;
import com.campus.model.Student;

import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO =
            new StudentDAO();


    public List<Student> getAllStudents() {

        return studentDAO.getAllStudents();
    }


    public Student getStudentById(int id) {

        return studentDAO.getStudentById(id);
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
}