# Day 6: Java Web Development with Servlets & Maven

---

## 1. Quick Start: How to Run the Application

To run this entire web application, you only need **one single command**:

```bash
mvn jetty:run
```

### Breakdown of the Run Command:
- **`mvn`** &rarr; Runs the Apache Maven build tool.
- **`jetty`** &rarr; Calls the embedded Jetty Web Server plugin configured in `pom.xml`.
- **`:run`** &rarr; Tells Jetty to compile your Java code, load web files, and start the server immediately on port `8080`.

### Once the command runs:
Open your browser and visit:
- **Add Student Form:** [http://localhost:8080/student.html](http://localhost:8080/student.html)
- **Student List (Servlet):** [http://localhost:8080/students](http://localhost:8080/students)

> **To stop the server:** Press `Ctrl + C` in the terminal.

---

## 2. Why Do We Need Servlets?

Before learning how Servlets work, let's understand **why** they exist:

### 1. Why Not Just Plain HTML & CSS?
- HTML & CSS are **static**. They only display fixed text and designs.
- HTML **cannot**:
  - Connect to a database.
  - Dynamically store student records in memory or files.
  - Perform calculations (like calculating GPA or pass/fail).
  - Authenticate logins or process business rules.

### 2. Why Not Regular Java (`public static void main`)?
- A standard Java console program only runs in a terminal window, prints output using `System.out.println()`, and terminates.
- It **cannot** listen for network requests from web browsers (Chrome, Edge, Firefox).

### 3. The Solution: Servlets
A **Servlet** bridges the gap between Java and the Web:
- It runs inside a Web Server.
- It listens on a URL (like `/students`).
- When a user clicks a button in their browser, the Servlet receives the data, executes Java business logic, and sends back dynamic HTML!

> 💡 **Did you know?**  
> Even modern frameworks like **Spring Boot**, **Spring MVC**, and **REST APIs** are built on top of Servlets (`DispatcherServlet`) under the hood!

---

## 3. What is a Servlet? (For Absolute Beginners)

### Real-World Analogy: The Restaurant
Think of a web application like a restaurant:
1. **You (Browser/Client):** Sits at the table and places an order.
2. **The Waiter (Servlet):** 
   - Takes your order (**HTTP Request**).
   - Walks to the kitchen to fetch or prepare data (**Service/Database**).
   - Brings back the prepared dish on a plate (**HTML Response**).
3. **The Kitchen (Service layer):** Prepares the food / manages the data.

```
+----------+      1. HTTP Request (GET/POST)       +----------------+      2. Process Data      +------------------+
| Browser  | ------------------------------------> | StudentServlet | ------------------------> |  StudentService  |
| (Client) | <------------------------------------ |   (Waiter)     | <------------------------ | (Data / Storage) |
+----------+          3. HTML Response             +----------------+      Data Returned        +------------------+
```

A **Servlet** is a Java class that receives HTTP requests from a client (browser), runs backend logic, and returns a response.

---

## 4. What is Jakarta? (`javax` vs `jakarta`)

When writing Servlets, you will see imports like:
```java
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
```

### The Story Behind Jakarta:
1. **The Origin (Java EE):**  
   Sun Microsystems created **J2EE** (later renamed **Java EE** - *Java Enterprise Edition*). All packages started with `javax.*` (e.g., `javax.servlet.*`).
2. **The Transfer to Open Source (2017):**  
   Oracle owned Java EE and decided to donate the entire enterprise platform to the open-source **Eclipse Foundation**.
3. **The Trademark Issue:**  
   Oracle kept the legal rights to the trademark name **"Java"**. Therefore, the Eclipse Foundation was not legally allowed to use `javax` for new specifications.
4. **The Rebranding to "Jakarta EE":**  
   The platform was renamed to **Jakarta EE**. Starting from Jakarta EE 9 and 10, all package names changed from:
   - `javax.servlet.*` &rarr; **`jakarta.servlet.*`**

### Quick Comparison:

| Package | Platform | Era | Used In |
| :--- | :--- | :--- | :--- |
| `javax.servlet.*` | Java EE 8 & older | Legacy | Tomcat 9, Spring Boot 2 |
| `jakarta.servlet.*` | Jakarta EE 9 / 10 | **Modern Standard** | **Tomcat 10+, Spring Boot 3+, Jetty 12** |

> In this project, we use `jakarta.servlet-api:6.0.0` (Jakarta EE 10), which is the current industry standard.

---

## 5. GET vs POST: The Two Main HTTP Methods

| Feature | HTTP GET | HTTP POST |
| :--- | :--- | :--- |
| **Purpose** | **Fetch / Read data** from server | **Send / Submit data** to server |
| **Real-Time Example** | Asking the waiter: *"Show me the menu"* | Handing the waiter: *"Here is my order form"* |
| **Data Visibility** | Parameters are visible in the URL bar | Data is securely sent inside the request body |
| **In our App** | Visiting `/students` to view the student list | Submitting `student.html` form to save a student |
| **Servlet Method** | `protected void doGet(...)` | `protected void doPost(...)` |

---

## 6. Deep Dive: Code Explanation of the Main Servlet

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
   - When anyone visits `http://localhost:8080/students`, this class handles the request.
2. **`extends HttpServlet`**
   - Gives our class full HTTP web server capabilities.
3. **`doGet(HttpServletRequest request, HttpServletResponse response)`**
   - Runs whenever someone visits the page or clicks a link pointing to `/students`.
   - `request`: Contains information sent by the user.
   - `response`: Contains what we send back to the user (HTML content).
4. **`doPost(HttpServletRequest request, HttpServletResponse response)`**
   - Runs when `<form method="post" action="students">` in `student.html` is submitted.
   - `request.getParameter("name")`: Reads whatever text the user typed into the input field `<input name="name">`.
   - `response.sendRedirect("students")`: After adding the student, tells the browser to reload `/students` so the updated list is shown immediately.

---

## 7. What is Maven? (Simple Explanation)

### Real-World Analogy: The Construction Manager
- **Without Maven (Manual Java):** You must manually find, download, and copy external `.jar` files, configure classpaths, and run complex terminal commands.
- **With Maven:** You declare what you need in one file called `pom.xml`: *"I need Jakarta Servlet 6.0 and an embedded web server"*. Maven automatically downloads the exact right libraries from Maven Central, organizes folders, compiles your code, and runs your app.

### Core Responsibilities of Maven:
1. **Dependency Management:** Automatically downloads external Java libraries (`jakarta.servlet-api`, etc.).
2. **Standard Folder Structure:** Organizes your code predictably.
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

## 8. Why No Need of Manual Tomcat Installation?

### The Old / Complicated Way (Manual Tomcat):
1. Download `apache-tomcat-10.1.x.tar.gz`.
2. Extract it using terminal: `tar -xvf ...` (often fails due to wrong paths or missing filenames).
3. Set environment variables like `CATALINA_HOME`, `JAVA_HOME`, `PATH`.
4. Compile your app with Maven to get `campus-student-management.war`.
5. Manually copy the `.war` file into `apache-tomcat/webapps/`.
6. Start Tomcat using `./bin/startup.sh`.

### The Modern Way (Embedded Server Plugin):
Instead of installing an external server on your operating system, we put the server **inside Maven as a plugin**!
- Maven downloads and runs the lightweight server directly in memory.
- No separate software to install.
- No `tar.gz` extraction errors.
- One single command: `mvn jetty:run`.

---

## 9. Embedded Server Configuration in `pom.xml`

We configured the **`jetty-ee10-maven-plugin`** inside `<plugins>` in `pom.xml`:

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
- Older plugins (like the old Tomcat 7 plugin) only support legacy `javax.servlet`.
- `jetty-ee10-maven-plugin:12.0.14` fully supports modern `jakarta.servlet` (Servlet 6.0) out of the box with zero external setup.

---

## 10. Summary Cheatsheet

| Task | Command |
| :--- | :--- |
| **Run Web Application** | `mvn jetty:run` |
| **Compile & Build WAR** | `mvn clean package` |
| **Stop Server** | `Ctrl + C` |
| **Free Port 8080 (if stuck)** | `fuser -k 8080/tcp` |
| **View Form in Browser** | `http://localhost:8080/student.html` |
| **View Student List in Browser** | `http://localhost:8080/students` |
