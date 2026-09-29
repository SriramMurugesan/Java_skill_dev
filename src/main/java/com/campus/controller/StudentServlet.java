package com.campus.controller;

import com.campus.model.Student;
import com.campus.service.StudentService;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();


    // READ
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action =
                request.getParameter("action");

        if ("edit".equals(action)) {

            showEditForm(request, response);

        } else if ("delete".equals(action)) {

            deleteStudent(request, response);

        } else {

            listStudents(request, response);
        }
    }


    // CREATE / UPDATE
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");

        String action =
                request.getParameter("action");

        String name =
                request.getParameter("name");

        String department =
                request.getParameter("department");

        int age =
                Integer.parseInt(
                        request.getParameter("age")
                );


        if ("update".equals(action)) {

            int id =
                    Integer.parseInt(
                            request.getParameter("id")
                    );

            Student student =
                    new Student(
                            id,
                            name,
                            department,
                            age
                    );

            studentService.updateStudent(student);

        } else {

            Student student =
                    new Student(
                            name,
                            department,
                            age
                    );

            studentService.addStudent(student);
        }


        response.sendRedirect(
                request.getContextPath()
                        + "/students"
        );
    }


    private void listStudents(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        List<Student> students =
                studentService.getAllStudents();

        request.setAttribute(
                "students",
                students
        );

        RequestDispatcher dispatcher =
                request.getRequestDispatcher(
                        "/students.jsp"
                );

        dispatcher.forward(
                request,
                response
        );
    }


    private void showEditForm(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int id =
                Integer.parseInt(
                        request.getParameter("id")
                );

        Student student =
                studentService.getStudentById(id);

        request.setAttribute(
                "student",
                student
        );

        RequestDispatcher dispatcher =
                request.getRequestDispatcher(
                        "/edit-student.jsp"
                );

        dispatcher.forward(
                request,
                response
        );
    }


    private void deleteStudent(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        int id =
                Integer.parseInt(
                        request.getParameter("id")
                );

        studentService.deleteStudent(id);

        response.sendRedirect(
                request.getContextPath()
                        + "/students"
        );
    }
}