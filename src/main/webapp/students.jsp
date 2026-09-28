<!DOCTYPE html>
<html>

<head>

    <title>Student List</title>

</head>

<body>

<h1>Campus Student Management System</h1>

<h2>Student List</h2>

<%
    java.util.List<String> students =
            (java.util.List<String>)
                    request.getAttribute("students");

    for (String student : students) {
%>

    <p>
        <%= student %>
    </p>

<%
    }
%>

<br>

<a href="student.html">
    Add New Student
</a>

<br><br>

<a href="session">
    Test Cookie and Session
</a>

</body>

</html>