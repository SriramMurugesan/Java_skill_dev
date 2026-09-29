# Day 8: Stage 2 — Database Persistence (JDBC & PostgreSQL), Modern JSP (EL & JSTL) & File Uploads

---

## 1. Quick Start: Running & Testing Today's Code

Start the application with Maven:

```bash
mvn jetty:run
```

### URLs to Test in Your Browser:
1. **Application Home Hub:** [http://localhost:8080/](http://localhost:8080/)
2. **Student Database List (JSTL + EL):** [http://localhost:8080/students](http://localhost:8080/students)
3. **Add Student to PostgreSQL:** [http://localhost:8080/student.html](http://localhost:8080/student.html)
4. **File & Image Upload Demo:** [http://localhost:8080/upload](http://localhost:8080/upload)

> **To stop the server:** Press `Ctrl + C` in the terminal.

---

## 2. Welcome to Stage 2: Moving from In-Memory to Enterprise Persistence

In **Stage 1 (Days 6 & 7)**, we built:
- Servlets, Filters, Listeners, and Sessions.
- But student data was stored in an **in-memory `ArrayList`**. Whenever the server restarted, **all student records disappeared**!

In **Stage 2 (Day 8)**, we transform our project into a permanent, production-ready enterprise application:
1. **Permanent Storage:** Store data in a relational database (**PostgreSQL**) using **JDBC**.
2. **Full CRUD Operations:** Create, Read, Update, and Delete records safely.
3. **Clean Presentation:** Replace messy Java code in HTML with **JSP Expression Language (EL)** and **JSTL (Jakarta Standard Tag Library)**.
4. **Binary Data Handling:** Accept **File and Image Uploads** directly using Jakarta Servlet's `@MultipartConfig`.

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                                 STAGE 2 ARCHITECTURE                                   │
├───────────────────────┬───────────────────────────┬────────────────────────────────────┤
│ 1. Presentation View  │ 2. Controller & Service   │ 3. Persistence (Data Access Layer) │
│ (JSP, JSTL, EL, HTML) │ (Servlets, MultiPart, PO) │ (PostgreSQL + JDBC DAO)            │
├───────────────────────┼───────────────────────────┼────────────────────────────────────┤
│ students.jsp          │ StudentServlet            │ DBConnection (JDBC Driver)         │
│ student.html          │ FileUploadServlet         │ StudentDAO (PreparedStatement)     │
│ upload.jsp            │ StudentService            │ PostgreSQL (campus_db: students)   │
└───────────────────────┴───────────────────────────┴────────────────────────────────────┘
```

---

## 3. Database Connectivity (JDBC) & PostgreSQL

### Real-World Analogy: The Bank Vault & The Armored Courier 🏦
Imagine your application is an office:
1. **PostgreSQL Database:** The heavy bank vault down the street where valuable records are locked safely. Even if the office closes or has a power outage, the vault remains completely intact.
2. **JDBC Driver:** The specialized courier / translator who knows the exact security protocol and language of the bank vault.
3. **`Connection`:** The phone call or secure tunnel established between your office and the vault.

---

### Why Do We Need a Database?
| In-Memory (`ArrayList`) | Relational Database (PostgreSQL) |
| :--- | :--- |
| Stored in RAM heap memory | Stored permanently on physical disk |
| Lost on server restart / crash | Persists forever across server restarts |
| Cannot handle millions of rows efficiently | Built for complex queries, indexing, and high scale |
| Concurrency conflicts when multiple users write | ACID transactions guarantee data integrity |

---

### Core Interfaces of JDBC (`java.sql.*`)

| Component | What it Represents | Real-World Equivalent |
| :--- | :--- | :--- |
| **`DriverManager`** | The factory that loads database drivers | Phone directory to find the bank's contact |
| **`Connection`** | The active session between Java and PostgreSQL | The established phone call |
| **`PreparedStatement`** | Pre-compiled, parameterized SQL statement | Pre-printed official bank form with blank blanks to fill |
| **`ResultSet`** | The tabular result returned from a `SELECT` query | Spreadsheet or invoice of returned records |

---

### Deep Dive: `DBConnection.java`

File: `src/main/java/com/campus/util/DBConnection.java`

```java
package com.campus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Database connection URL: protocol : vendor :// host : port / databaseName
    private static final String URL = "jdbc:postgresql://localhost:5432/campus_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        // DriverManager automatically locates the registered PostgreSQL driver
        // and establishes a live connection to the database
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

> 💡 **Best Practice — Resource Management with `try-with-resources`:**  
> Always open `Connection`, `PreparedStatement`, and `ResultSet` inside a `try (...)` block. Java will automatically close them when finished, preventing **memory leaks** and database connection exhaustion.

---

## 4. JDBC Integration & Defending Against SQL Injection

### Real-World Analogy: The Signed Blank Check ✍️
Suppose you write a check:
- **Unsafe `Statement` (String Concatenation):**  
  You leave the recipient blank and sign it:  
  `"SELECT * FROM users WHERE user = '" + input + "'"`  
  If an attacker types `' OR '1'='1`, the check is altered to steal everything!
- **Safe `PreparedStatement` (Parameterized Query):**  
  You use a locked check where every field has strict boundaries and character escaping. The database treats user input **strictly as plain data**, never as executable SQL commands!

```
❌ BAD (SQL Injection Risk):
statement.executeQuery("SELECT * FROM students WHERE id = " + userInput);

✅ GOOD (PreparedStatement with ? parameter):
PreparedStatement ps = conn.prepareStatement("SELECT * FROM students WHERE id = ?");
ps.setInt(1, id); // Database treats parameter purely as literal integer value!
```

---

## 5. CRUD Operations in the Database (Complete DAO Walkthrough)

**CRUD** stands for:
- **C**reate &rarr; `INSERT`
- **R**ead &rarr; `SELECT`
- **U**pdate &rarr; `UPDATE`
- **D**elete &rarr; `DELETE`

File: `src/main/java/com/campus/dao/StudentDAO.java`

### 1. READ ALL (Querying Records)
```java
public List<Student> getAllStudents() {
    List<Student> students = new ArrayList<>();
    String sql = "SELECT * FROM students ORDER BY id";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        // resultSet.next() moves the cursor to the next row (returns false when done)
        while (resultSet.next()) {
            Student student = new Student();
            student.setId(resultSet.getInt("id"));
            student.setName(resultSet.getString("name"));
            student.setDepartment(resultSet.getString("department"));
            student.setAge(resultSet.getInt("age"));
            students.add(student);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return students;
}
```

### 2. CREATE (Inserting Records)
```java
public void addStudent(Student student) {
    String sql = "INSERT INTO students (name, department, age) VALUES (?, ?, ?)";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        // Bind parameters to question mark placeholders (1-indexed)
        statement.setString(1, student.getName());
        statement.setString(2, student.getDepartment());
        statement.setInt(3, student.getAge());

        // executeUpdate() is used for INSERT, UPDATE, and DELETE
        statement.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

### 3. UPDATE & DELETE
```java
// UPDATE: Modifies existing record by ID
public void updateStudent(Student student) {
    String sql = "UPDATE students SET name = ?, department = ?, age = ? WHERE id = ?";
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, student.getName());
        statement.setString(2, student.getDepartment());
        statement.setInt(3, student.getAge());
        statement.setInt(4, student.getId());
        statement.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// DELETE: Removes record matching specific ID
public void deleteStudent(int id) {
    String sql = "DELETE FROM students WHERE id = ?";
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, id);
        statement.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

---

## 6. JSP Advanced: Directives, Elements & JavaBeans

### 1. JSP Directives
Directives give instructions to the JSP container (Jetty/Tomcat) about how the entire page should be compiled into a Java servlet.

Syntax: `<%@ directive attribute="value" %>`

| Directive | Purpose | Example |
| :--- | :--- | :--- |
| **`page`** | Sets page encoding, content type, or Java imports | `<%@ page contentType="text/html;charset=UTF-8" import="java.util.*" %>` |
| **`include`** | Statically includes another file at compile time | `<%@ include file="navbar.jsp" %>` |
| **`taglib`** | Declares a custom tag library (such as JSTL) | `<%@ taglib prefix="c" uri="jakarta.tags.core" %>` |

---

### 2. JSP Scripting Elements (Legacy vs Modern)
Historically, JSPs allowed embedding raw Java inside HTML:
- `<%! int count = 0; %>` &rarr; **Declaration** (declares class field or method)
- `<% count++; %>` &rarr; **Scriptlet** (executes Java statements inside `_jspService`)
- `<%= count %>` &rarr; **Expression** (prints Java value directly to HTML output)

> ⚠️ **Industry Rule:**  
> Never write raw Java scriptlets (`<% ... %>`) in modern web applications! They make HTML unreadable, unmaintainable, and mix business logic with design. Modern applications use **JSTL** and **EL** instead!

---

### 3. What is a JavaBean?
A **JavaBean** is simply a standard Java class designed to hold data:
1. Must have a **public no-argument constructor** (`public Student() {}`).
2. Properties must be **private** (`private String name;`).
3. Must provide **public getters and setters** (`getName()`, `setName(...)`).
4. Implements `Serializable` (optional, for session persistence).

Our `com.campus.model.Student` is an authentic JavaBean!

---

## 7. JSP Expression Language (EL) & JSTL

### Real-World Analogy: Digital Dashboard vs Messy Chalkboard 📊
- **Old Scriptlet Way (Chalkboard):**  
  You manually write complicated Java code right in the middle of HTML:  
  `<%= ((com.campus.model.Student) request.getAttribute("student")).getName() %>`
- **Modern EL Way (Clean Digital Dashboard):**  
  You simply request the property name in clean brackets:  
  `${student.name}`  
  Under the hood, EL automatically locates the `student` object in request scope and calls its `getName()` getter!

---

### EL (Expression Language) Features
- **Property Access:** `${student.name}` &rarr; calls `student.getName()`.
- **Null Safety:** If `student` is null, EL prints an empty string instead of throwing a `NullPointerException`!
- **Implicit Scopes:** Searches in order: `pageScope` &rarr; `requestScope` &rarr; `sessionScope` &rarr; `applicationScope`.

---

### JSTL: Jakarta Standard Tag Library
JSTL provides clean, XML-like tags to perform loops, conditions, and formatting directly in JSP without writing a single line of Java code!

Declare it at the top of your JSP:
```jsp
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
```

#### Most Common JSTL Core Tags:

1. **Looping over a List (`<c:forEach>`):**
```jsp
<c:forEach var="student" items="${students}">
    <tr>
        <td>${student.id}</td>
        <td>${student.name}</td>
        <td>${student.department}</td>
        <td>${student.age}</td>
    </tr>
</c:forEach>
```

2. **Conditional Rendering (`<c:if>`):**
```jsp
<c:if test="${student.age >= 18}">
    <span>Adult Student</span>
</c:if>
```

3. **Multiple Conditions (`<c:choose>`, `<c:when>`, `<c:otherwise>`):**
```jsp
<c:choose>
    <c:when test="${empty students}">
        <p>No students found in database.</p>
    </c:when>
    <c:otherwise>
        <p>Total students loaded successfully!</p>
    </c:otherwise>
</c:choose>
```

---

## 8. Uploading Files & Images (Multipart Request Handling)

### Real-World Analogy: Sending a Letter vs Sending a Box 📦
1. **Regular Form (`application/x-www-form-urlencoded`):**  
   Like mailing a standard letter envelope. You can only write short text inside.
2. **Multipart Form (`multipart/form-data`):**  
   Like shipping a delivery parcel box. It can contain both text labels and physical items (pictures, documents, PDFs) separated into distinct compartments (**Parts**).

```
Normal Form POST:
name=Alice&department=CS  (Pure text stream)

Multipart Form POST (enctype="multipart/form-data"):
--BOUNDARY_12345
Content-Disposition: form-data; name="name"
Alice
--BOUNDARY_12345
Content-Disposition: form-data; name="file"; filename="avatar.png"
Content-Type: image/png
[Binary Raw Bytes of Image...]
--BOUNDARY_12345--
```

---

### Step 1: The HTML Upload Form
Notice the required attribute: **`enctype="multipart/form-data"`**!

```html
<form action="upload" method="post" enctype="multipart/form-data">
    <label>Choose File or Image:</label>
    <input type="file" name="file" required>
    <button type="submit">Upload</button>
</form>
```

---

### Step 2: The Servlet `@MultipartConfig`
To enable a Servlet to accept file uploads, simply add `@MultipartConfig`:

File: `src/main/java/com/campus/controller/FileUploadServlet.java`

```java
package com.campus.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@WebServlet("/upload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB: files below this stay in RAM
    maxFileSize = 1024 * 1024 * 5,        // 5 MB: maximum size for a single file
    maxRequestSize = 1024 * 1024 * 10     // 10 MB: maximum total request size
)
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to the upload form page
        request.getRequestDispatcher("/upload.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Retrieve the file Part using the name from <input type="file" name="file">
        Part filePart = request.getPart("file");

        if (filePart != null && filePart.getSize() > 0) {
            String fileName = filePart.getSubmittedFileName();
            long fileSize = filePart.getSize();
            String contentType = filePart.getContentType();

            // 2. Read bytes and encode to Base64 for instant zero-dependency browser preview
            try (InputStream inputStream = filePart.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();
                String base64Content = Base64.getEncoder().encodeToString(bytes);

                // 3. Attach metadata to request scope
                request.setAttribute("fileName", fileName);
                request.setAttribute("fileSize", fileSize);
                request.setAttribute("contentType", contentType);
                request.setAttribute("base64Content", base64Content);
                request.setAttribute("isImage", contentType != null && contentType.startsWith("image/"));
            }
        }

        // 4. Render result on upload.jsp
        request.getRequestDispatcher("/upload.jsp").forward(request, response);
    }
}
```

---

### Step 3: Displaying the Upload Result & Image Preview in `upload.jsp`

File: `src/main/webapp/upload.jsp`

```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <title>File & Image Upload Demo</title>
</head>
<body>

<h1>File & Image Upload Demo</h1>

<form action="upload" method="post" enctype="multipart/form-data">
    <label>Choose File / Image:</label>
    <input type="file" name="file" required>
    <button type="submit">Upload File</button>
</form>

<!-- Show results only when a file was submitted -->
<c:if test="${not empty fileName}">
    <hr>
    <h2>Upload Result:</h2>
    <p><strong>File Name:</strong> ${fileName}</p>
    <p><strong>File Size:</strong> ${fileSize} bytes (${fileSize / 1024} KB)</p>
    <p><strong>Content Type:</strong> ${contentType}</p>

    <!-- If it's an image, display an immediate preview using data URI! -->
    <c:if test="${isImage}">
        <h3>Image Preview:</h3>
        <img src="data:${contentType};base64,${base64Content}" 
             alt="Uploaded Preview" 
             style="max-width: 300px; border: 1px solid #ccc; padding: 5px;">
    </c:if>
</c:if>

<br><br>
<a href="index.html">Back to Home</a>

</body>
</html>
```

---

## 9. End-to-End Request Flow: Adding and Viewing a Student

Here is the entire data flow when you create and view a student in Stage 2:

```
[Browser Form: student.html]
       │
       │ HTTP POST /students (name="Sriram", department="CSE", age=22)
       ▼
[LoggingFilter] ───> Logs "Request received"
       │
       ▼
[StudentServlet: doPost()]
       │ extracts parameters & creates Student bean
       ▼
[StudentService: addStudent(student)]
       │ passes to data access layer
       ▼
[StudentDAO: addStudent(student)]
       │ opens Connection via DBConnection
       │ binds parameters into PreparedStatement
       │ runs executeUpdate()
       ▼
[PostgreSQL Database: campus_db]
       │ INSERT INTO students ... (Saved permanently to disk!)
       ▼
[StudentServlet] ───> sends response.sendRedirect("/students")
       │
       ▼
[Browser executes GET /students]
       │
       ▼
[StudentServlet: doGet()]
       │ calls studentService.getAllStudents()
       │ receives List<Student> from StudentDAO
       │ sets request.setAttribute("students", list)
       │ forwards to /students.jsp
       ▼
[students.jsp]
       │ <c:forEach items="${students}"> iterates rows
       │ generates clean HTML table
       ▼
[Browser renders complete student table with Edit & Delete links!]
```

---

## 10. Summary Cheatsheet

| Technology / Feature | Where It Is Used | What It Does | Key Code / Syntax |
| :--- | :--- | :--- | :--- |
| **PostgreSQL** | Database Server | Relational storage for permanent tables | `CREATE TABLE students (...)` |
| **`DBConnection`** | `com.campus.util` | Factory providing active JDBC connections | `DriverManager.getConnection(url, u, p)` |
| **`PreparedStatement`** | `StudentDAO` | Pre-compiled SQL with SQL injection defense | `ps.setString(1, val); ps.executeUpdate();` |
| **`ResultSet`** | `StudentDAO` | Iterates over rows returned from query | `while (rs.next()) { rs.getString("name"); }` |
| **JavaBean** | `Student.java` | Standard data carrier with getters/setters | `public String getName() { return name; }` |
| **Expression Language** | `students.jsp` | Clean, null-safe property access in JSP | `${student.name}`, `${student.age}` |
| **JSTL Core** | `students.jsp` | Tag-based loops and conditions without Java | `<c:forEach items="${students}">` |
| **`@MultipartConfig`** | `FileUploadServlet` | Tells Servlet to parse multipart binary files | `request.getPart("file")` |
| **`enctype="multipart/form-data"`** | HTML Forms | Encodes files into multipart binary stream | `<form enctype="multipart/form-data">` |
