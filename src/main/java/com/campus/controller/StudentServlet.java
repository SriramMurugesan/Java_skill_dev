package com.campus.controller;

import com.campus.model.Department;
import com.campus.model.Student;
import com.campus.service.StudentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    private StudentService service;


    @Override
    public void init() {
        service = new StudentService();
    }


    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action =
                request.getParameter("action");


        if (action == null ||
                action.equals("list")) {

            List<Student> students =
                    service.getAllStudents();

            List<Department> departments =
                    service.getAllDepartments();

            request.setAttribute(
                    "students",
                    students
            );

            request.setAttribute(
                    "departments",
                    departments
            );

            request.getRequestDispatcher(
                    "/students.jsp"
            ).forward(
                    request,
                    response
            );

            return;
        }


        if (action.equals("edit")) {

            int id = Integer.parseInt(
                    request.getParameter("id")
            );

            Student student =
                    service.getStudentById(id);

            List<Department> departments =
                    service.getAllDepartments();

            request.setAttribute(
                    "student",
                    student
            );

            request.setAttribute(
                    "departments",
                    departments
            );

            request.getRequestDispatcher(
                    "/edit-student.jsp"
            ).forward(
                    request,
                    response
            );

            return;
        }


        if (action.equals("delete")) {

            int id = Integer.parseInt(
                    request.getParameter("id")
            );

            service.deleteStudent(id);

            response.sendRedirect(
                    request.getContextPath()
                    + "/students"
            );

            return;
        }


        if (action.equals("department")) {

            String department =
                    request.getParameter(
                            "department"
                    );

            List<Student> students =
                    service.findStudentsByDepartment(
                            department
                    );

            List<Department> departments =
                    service.getAllDepartments();

            request.setAttribute(
                    "students",
                    students
            );

            request.setAttribute(
                    "departments",
                    departments
            );

            request.setAttribute(
                    "selectedDepartment",
                    department
            );

            request.getRequestDispatcher(
                    "/students.jsp"
            ).forward(
                    request,
                    response
            );
        }
    }


    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action =
                request.getParameter("action");


        if ("update".equals(action)) {

            int id =
                    Integer.parseInt(
                            request.getParameter("id")
                    );

            String name =
                    request.getParameter("name");

            int age =
                    Integer.parseInt(
                            request.getParameter("age")
                    );

            int departmentId =
                    Integer.parseInt(
                            request.getParameter(
                                    "departmentId"
                            )
                    );

            Department department =
                    service.getDepartmentById(
                            departmentId
                    );

            Student student =
                    new Student(
                            name,
                            age,
                            department
                    );

            student.setId(id);

            service.updateStudent(student);

            response.sendRedirect(
                    request.getContextPath()
                    + "/students"
            );

            return;
        }


        String name =
                request.getParameter("name");

        int age =
                Integer.parseInt(
                        request.getParameter("age")
                );

        int departmentId =
                Integer.parseInt(
                        request.getParameter(
                                "departmentId"
                        )
                );

        Department department =
                service.getDepartmentById(
                        departmentId
                );

        Student student =
                new Student(
                        name,
                        age,
                        department
                );

        service.addStudent(student);

        response.sendRedirect(
                request.getContextPath()
                + "/students"
        );
    }
}