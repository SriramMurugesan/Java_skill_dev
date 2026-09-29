<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="c"
           uri="jakarta.tags.core" %>

<!DOCTYPE html>

<html>

<head>

    <title>Student List</title>

</head>

<body>

<h1>Campus Student Management</h1>

<h2>Student List</h2>

<br>

<a href="student.html">
    Add New Student
</a>

<br><br>

<table border="1" cellpadding="10">

    <tr>

        <th>ID</th>

        <th>Name</th>

        <th>Department</th>

        <th>Age</th>

        <th>Actions</th>

    </tr>


    <c:forEach
            var="student"
            items="${students}">

        <tr>

            <td>
                ${student.id}
            </td>

            <td>
                ${student.name}
            </td>

            <td>
                ${student.department}
            </td>

            <td>
                ${student.age}
            </td>

            <td>

                <a href="students?action=edit&id=${student.id}">
                    Edit
                </a>

                &nbsp; | &nbsp;

                <a href="students?action=delete&id=${student.id}"
                   onclick="return confirm('Delete this student?');">
                    Delete
                </a>

            </td>

        </tr>

    </c:forEach>

</table>

<br>

<a href="index.html">
    Home
</a>

</body>

</html>