package com.campus.controller;

import com.campus.service.StudentService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    // Handles GET /students
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        PrintWriter out =
                response.getWriter();

        out.println("<!DOCTYPE html>");

        out.println("<html>");

        out.println("<head>");

        out.println(
                "<title>Student Management</title>"
        );

        out.println("</head>");

        out.println("<body>");

        out.println(
                "<h1>Campus Student Management System</h1>"
        );

        out.println("<h2>Student List</h2>");

        out.println("<ul>");

        for (String student :
                studentService.getAllStudents()) {

            out.println(
                    "<li>"
                    + student
                    + "</li>"
            );
        }

        out.println("</ul>");

        out.println("<br>");

        out.println(
                "<a href='student.html'>"
                + "Add Student"
                + "</a>"
        );

        out.println("</body>");

        out.println("</html>");
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