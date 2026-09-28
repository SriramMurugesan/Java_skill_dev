package com.campus.controller;

import com.campus.service.StudentService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    // Handles GET /students
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException,
            ServletException {

        var students=studentService.getAllStudents();

        request.setAttribute("students", students);

        RequestDispatcher dispatcher=request.getRequestDispatcher("/students.jsp");
        dispatcher.forward(request, response);
    }

    // Handles POST /students
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");

        String name =
                request.getParameter("name");

        String department =
                request.getParameter("department");

        studentService.addStudent(
                name,
                department
        );

        response.sendRedirect("students");
    }
}