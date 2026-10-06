<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="c"
           uri="jakarta.tags.core" %>

<!DOCTYPE html>

<html>

<head>
    <title>Students</title>
</head>

<body>

<h1>Campus Students</h1>


<h2>Add Student</h2>

<form action="students" method="post">

    <label>Name:</label>

    <input
        type="text"
        name="name"
        required
    >

    <br><br>

    <label>Age:</label>

    <input
        type="number"
        name="age"
        required
    >

    <br><br>

    <label>Department:</label>

    <select name="departmentId" required>

        <option value="">
            Select Department
        </option>

        <c:forEach
            var="department"
            items="${departments}">

            <option value="${department.id}">
                ${department.name}
            </option>

        </c:forEach>

    </select>

    <br><br>

    <button type="submit">
        Add Student
    </button>

</form>


<hr>


<h2>Filter by Department</h2>

<form action="students" method="get">

    <input
        type="hidden"
        name="action"
        value="department"
    >

    <select name="department">

        <option value="">
            Select Department
        </option>

        <c:forEach
            var="department"
            items="${departments}">

            <option value="${department.name}">
                ${department.name}
            </option>

        </c:forEach>

    </select>

    <button type="submit">
        Filter
    </button>

</form>


<hr>


<h2>Student List</h2>

<table border="1" cellpadding="10">

    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Age</th>
        <th>Department</th>
        <th>Actions</th>
    </tr>


    <c:forEach
        var="student"
        items="${students}">

        <tr>

            <td>${student.id}</td>

            <td>${student.name}</td>

            <td>${student.age}</td>

            <td>
                ${student.department.name}
            </td>

            <td>

                <a href="students?action=edit&id=${student.id}">
                    Edit
                </a>

                |

                <a
                    href="students?action=delete&id=${student.id}"
                    onclick="return confirm('Delete this student?');"
                >
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