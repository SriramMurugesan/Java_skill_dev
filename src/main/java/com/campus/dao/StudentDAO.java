package com.campus.dao;

import com.campus.model.Department;
import com.campus.model.Student;
import com.campus.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // READ ALL
    public List<Student> getAllStudents() {

        List<Student> students =
                new ArrayList<>();

        String sql =
                "SELECT * FROM students ORDER BY id";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Student student =
                        new Student();

                student.setId(
                        resultSet.getInt("id")
                );

                student.setName(
                        resultSet.getString("name")
                );

                String deptName =
                        resultSet.getString("department");
                student.setDepartment(
                        deptName != null ? new Department(deptName) : null
                );

                student.setAge(
                        resultSet.getInt("age")
                );

                students.add(student);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return students;
    }


    // CREATE
    public void addStudent(Student student) {

        String sql =
                "INSERT INTO students " +
                "(name, department, age) " +
                "VALUES (?, ?, ?)";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    student.getName()
            );

            statement.setString(
                    2,
                    student.getDepartment() != null ? student.getDepartment().getName() : null
            );

            statement.setInt(
                    3,
                    student.getAge()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }


    // FIND BY ID
    public Student getStudentById(int id) {

        String sql =
                "SELECT * FROM students WHERE id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    Student student =
                            new Student();

                    student.setId(
                            resultSet.getInt("id")
                    );

                    student.setName(
                            resultSet.getString("name")
                    );

                    String deptName =
                            resultSet.getString("department");
                    student.setDepartment(
                            deptName != null ? new Department(deptName) : null
                    );

                    student.setAge(
                            resultSet.getInt("age")
                    );

                    return student;
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    // UPDATE
    public void updateStudent(Student student) {

        String sql =
                "UPDATE students " +
                "SET name = ?, department = ?, age = ? " +
                "WHERE id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    student.getName()
            );

            statement.setString(
                    2,
                    student.getDepartment() != null ? student.getDepartment().getName() : null
            );

            statement.setInt(
                    3,
                    student.getAge()
            );

            statement.setInt(
                    4,
                    student.getId()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }


    // DELETE
    public void deleteStudent(int id) {

        String sql =
                "DELETE FROM students WHERE id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}