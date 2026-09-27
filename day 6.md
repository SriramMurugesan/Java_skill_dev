# Day 6: Java Web Development with Servlets & Maven

---

## 1. Quick Start: How to Run the Application

To run this entire web application, you only need **one single command**:

```bash
mvn jetty:run
```

### Breakdown of the Run Command:
- **`mvn`** &rarr; Runs the Apache Maven tool.
- **`jetty`** &rarr; Calls the embedded Jetty Web Server plugin configured in `pom.xml`.
- **`:run`** &rarr; Tells Jetty to compile your Java code, load web files, and start the server immediately.

### Once the command runs:
Open your browser and visit:
- **Add Student Form:** [http://localhost:8080/student.html](http://localhost:8080/student.html)
- **Student List (Servlet):** [http://localhost:8080/students](http://localhost:8080/students)

> **To stop the server:** Press `Ctrl + C` in the terminal.

---

## 2. What is a Servlet? (For Absolute Beginners)

### Real-World Analogy: The Restaurant
Think of a website like a restaurant:
1. **You (Browser/Client):** Sits at the table and places an order.
2. **The Waiter (Servlet):** 
   - Takes your order (**Request**).
   - Goes to the kitchen to fetch or prepare data (**Service/Database**).
   - Brings back the prepared dish on a plate (**HTML Response**).
3. **The Kitchen (Service layer):** Prepares the food / manages the data.

```
+----------+      1. HTTP Request (GET/POST)       +----------------+      2. Process Data      +------------------+
| Browser  | ------------------------------------> | StudentServlet | ------------------------> |  StudentService  |
| (Client) | <------------------------------------ |   (Waiter)     | <------------------------ | (Data / Storage) |
+----------+          3. HTML Response             +----------------+      Data Returned        +------------------+
```

A **Servlet** is simply a Java class that listens for web requests over the internet (HTTP), runs Java logic, and sends back an HTML page or data to the user's browser.

---

## 3. GET vs POST: The Two Main HTTP Methods

| Feature | HTTP GET | HTTP POST |
| :--- | :--- | :--- |
| **Purpose** | **Fetch / Read data** from server | **Send / Submit data** to server |
| **Real-Time Example** | Asking the waiter: *"Show me the menu"* | Handing the waiter: *"Here is my order form"* |
| **Data Visibility** | Parameters are visible in the URL bar | Data is hidden inside the request body |
| **In our App** | Visiting `/students` to view the student list | Submitting `student.html` form to save a student |
| **Servlet Method** | `protected void doGet(...)` | `protected void doPost(...)` |

---

## 4. Deep Dive: Code Explanation of the Main Servlet

File: `src/main/java/com/campus/controller/StudentServlet.java`

Here is the exact code with clear, beginner-friendly explanation of every section:

```java
package com.campus.controller;

import com.campus.service.StudentService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

// 1. URL MAPPING ANNOTATION
@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    // 2. BACKEND SERVICE INSTANCE
    private final StudentService studentService = new StudentService();

    // 3. HANDLES GET REQUESTS (View student list)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Tell the browser what kind of content is coming back (HTML, UTF-8)
        response.setContentType("text/html;charset=UTF-8");

        // Get the output writer to send HTML text to the browser
        PrintWriter out = response.getWriter();

        // Write HTML page directly to the browser
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Student Management</title></head>");
        out.println("<body>");
        out.println("<h1>Campus Student Management System</h1>");
        out.println("<h2>Student List</h2>");
        out.println("<ul>");

        // Loop through all students from the service and display each in a bullet point
        for (String student : studentService.getAllStudents()) {
            out.println("<li>" + student + "</li>");
        }

        out.println("</ul>");
        out.println("<br>");
        out.println("<a href='student.html'>Add Student</a>");
        out.println("</body>");
        out.println("</html>");
    }

    // 4. HANDLES POST REQUESTS (Save new student from form)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Ensure form characters are handled properly
        request.setCharacterEncoding("UTF-8");

        // Extract input values submitted from student.html form (<input name="...">)
        String name = request.getParameter("name");
        String department = request.getParameter("department");

        // Save the new student using our service
        studentService.addStudent(name, department);

        // Redirect the browser back to the student list page (doGet)
        response.sendRedirect("students");
    }
}
```

### Key Components Explained:
1. **`@WebServlet("/students")`**
   - Maps this Servlet to the URL path `/students`.
   - When anyone types `http://localhost:8080/students`, this class is executed.
2. **`extends HttpServlet`**
   - Inherits Java's built-in web capabilities from Jakarta Servlet API.
3. **`doGet(HttpServletRequest request, HttpServletResponse response)`**
   - Runs whenever someone visits the page or clicks a link pointing to `/students`.
   - `request`: Contains information sent by the user (cookies, headers, query parameters).
   - `response`: What we send back to the user (HTML content).
4. **`doPost(HttpServletRequest request, HttpServletResponse response)`**
   - Runs when the `<form method="post" action="students">` in `student.html` is submitted.
   - `request.getParameter("name")`: Reads whatever text the user typed into the input field `<input name="name">`.
   - `response.sendRedirect("students")`: After adding the student, tells the browser to reload `/students` so the updated list is shown immediately.

---

## 5. What is Maven? (Simple Explanation)

### Real-World Analogy: The Smart Construction Manager
Imagine building a house:
- **Without Maven (Manual Java):** You must personally go to every factory, buy bricks, nails, cement, carry them by hand, figure out matching versions, and compile everything using long terminal commands.
- **With Maven:** You write a list in a single file called `pom.xml`: *"I need Jakarta Servlet 6.0 and a web server"*. Maven automatically goes to the internet repository (Maven Central), downloads the exact right `.jar` files, sets up folders, compiles your code, and runs your project.

### Core Responsibilities of Maven:
1. **Dependency Management:** Automatically downloads external Java libraries (`jakarta.servlet-api`, etc.).
2. **Standard Folder Structure:** Organizes your code so every developer knows where files live.
3. **Build Automation:** One command (`mvn clean package`) compiles code, runs tests, and packages it into a `.war` file.

### Standard Maven Web Project Structure:
```
Java_skill_dev/
├── pom.xml                     <-- Maven configuration & dependencies recipe
├── src/
│   └── main/
│       ├── java/               <-- All your Java source code (.java files)
│       │   └── com/campus/
│       │       ├── controller/ <-- Web Servlets (StudentServlet)
│       │       └── service/    <-- Business logic (StudentService)
│       └── webapp/             <-- Static web files (HTML, CSS, JS)
│           └── student.html
└── target/                     <-- Built output (compiled .class and .war)
```

---

## 6. How to Initiate a Maven Project

There are two common ways to start a Maven project:

### Method 1: Using the Maven Command Line (Archetype)
```bash
mvn archetype:generate -DgroupId=com.campus -DartifactId=campus-student-management -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false
```
This automatically creates the standard folders and a starter `pom.xml`.

### Method 2: Creating `pom.xml` Manually
Simply create a file named `pom.xml` in your project root with the standard Maven structure and directories:
- `src/main/java`
- `src/main/webapp`

---

## 7. Why No Need of Manual Tomcat Installation?

### The Old / Complicated Way (Manual Tomcat):
1. Go to Apache website & download `apache-tomcat-10.1.x.tar.gz`.
2. Extract it using terminal: `tar -xvf ...` (often fails due to wrong paths or missing filenames).
3. Set environment variables like `CATALINA_HOME`, `JAVA_HOME`, `PATH`.
4. Compile your app with Maven to get `campus-student-management.war`.
5. Manually copy the `.war` file into `apache-tomcat/webapps/`.
6. Start Tomcat using `./bin/startup.sh`.
7. Hard to debug if ports conflict or files don't deploy.

### The Modern Way (Embedded Server Plugin):
Instead of installing an external server on your operating system, we put the server **inside Maven as a plugin**!
- Maven downloads and runs the lightweight server directly in memory.
- No software installation.
- No `tar.gz` extraction errors.
- One single command: `mvn jetty:run`.

---

## 8. Exact Changes Made to `pom.xml`

### 1. Fixed the File Name
- The file was accidentally named `pox.xml` (which caused `[ERROR] no POM in this directory`).
- It was renamed to `pom.xml`.

### 2. Added the Embedded Server Plugin (`jetty-ee10-maven-plugin`)
We added this block inside `<plugins>` in `pom.xml`:

```xml
<plugin>
    <groupId>org.eclipse.jetty.ee10</groupId>
    <artifactId>jetty-ee10-maven-plugin</artifactId>
    <version>12.0.14</version>
    <configuration>
        <!-- Run on port 8080 -->
        <httpConnector>
            <port>8080</port>
        </httpConnector>
        <!-- Root context path so URLs are clean: http://localhost:8080/ -->
        <webApp>
            <contextPath>/</contextPath>
        </webApp>
    </configuration>
</plugin>
```

### Why Jetty EE10?
- Our code uses **`jakarta.servlet-api` version 6.0.0** (Jakarta EE 10).
- Older plugins (like the old Tomcat 7 plugin) only support the legacy `javax.servlet` (Java EE).
- `jetty-ee10-maven-plugin:12.0.14` fully supports modern `jakarta.servlet` (Servlet 6.0) out of the box with zero external setup.

---

## 9. Summary Cheatsheet

| Task | Command |
| :--- | :--- |
| **Run Web Application** | `mvn jetty:run` |
| **Compile & Build WAR** | `mvn clean package` |
| **Stop Server** | `Ctrl + C` |
| **View Form in Browser** | `http://localhost:8080/student.html` |
| **View Student List in Browser** | `http://localhost:8080/students` |
