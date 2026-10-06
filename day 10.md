# Day 10: Object-Relational Mapping (ORM) with JPA, Hibernate 6 & Entity Relationships

---

## 1. Quick Start: Running & Testing Today's Application

### Start the Server:
In your terminal, run:

```bash
mvn clean package
mvn jetty-ee10:run
```

### URLs to Open in Your Browser:
1. **Application Home:** [http://localhost:8081/](http://localhost:8081/)
2. **Students & Departments Hub:** [http://localhost:8081/students](http://localhost:8081/students)
3. **Add Student Form:** [http://localhost:8081/student.html](http://localhost:8081/student.html)

> **Port Note:** The server is configured on port **`8081`** in `pom.xml`.

---

## 2. The Big Picture: Why JPA & Hibernate?

### Real-World Analogy: The Automated Translator 🌐
* **In Day 8 (JDBC):** You were a manual builder. Every single database query required writing raw SQL strings (`"INSERT INTO students (name, department, age) VALUES (?, ?, ?)"`), mapping every column manually from `ResultSet`, and managing SQL connections by hand.
* **In Day 10 (JPA & Hibernate):** You hire an automated translator (**Hibernate**). You just talk in Java objects (`student.setName("Alice")`), and Hibernate automatically speaks PostgreSQL behind the scenes, creating and running SQL queries for you!

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                                 DAY 10 MVC ARCHITECTURE                                 │
├───────────────────────┬───────────────────────────┬────────────────────────────────────┤
│ 1. Presentation View  │ 2. Controller & Service   │ 3. Persistence (ORM & Database)    │
│ (JSP, JSTL, HTML)     │ (Servlets & Business Logic│ (JPA, Hibernate 6 & PostgreSQL)    │
├───────────────────────┼───────────────────────────┼────────────────────────────────────┤
│ students.jsp          │ StudentServlet            │ JPAUtil (EntityManagerFactory)     │
│ edit-student.jsp      │ StudentService            │ StudentJPADAO (JPQL & CRUD)        │
│ student.html          │                           │ DepartmentJPADAO (Department CRUD) │
│ index.html            │                           │ PostgreSQL (students, departments) │
└───────────────────────┴───────────────────────────┴────────────────────────────────────┘
```

---

## 3. End-to-End Request Flow (How Data Moves)

Here is exactly what happens when a user visits the application or submits a form:

```mermaid
sequenceDiagram
    autonumber
    actor User as User Browser
    participant Servlet as StudentServlet (Controller)
    participant Service as StudentService (Business Layer)
    participant DAO as StudentJPADAO / DepartmentJPADAO
    participant JPA as JPA EntityManager (Hibernate)
    participant DB as PostgreSQL Database

    User->>Servlet: HTTP GET /students
    Servlet->>Service: getAllStudents() & getAllDepartments()
    Service->>DAO: Query via DAOs
    DAO->>JPA: createQuery("SELECT s FROM Student s", Student.class)
    JPA->>DB: Executes SQL SELECT with JOIN
    DB-->>JPA: Returns Table Rows
    JPA-->>DAO: Converts Rows into Student & Department Objects
    DAO-->>Service: List<Student>, List<Department>
    Service-->>Servlet: Returns Lists
    Servlet->>Servlet: request.setAttribute("students", ...);
    Servlet->>User: Forwards to students.jsp (Rendered HTML)
```

---

## 4. Simple Explanation of Every Implementation

### 📁 1. The Configuration Bridge: `persistence.xml`
* **File Location:** `src/main/resources/META-INF/persistence.xml`
* **What it does:** Tells JPA which database to connect to, which credentials to use, and which Java entity classes exist.
* **Key settings:**
  * `persistence-unit name="campusPU"`: The unique name used by Java code to load these settings.
  * `<class>com.campus.model.Student</class>` and `<class>com.campus.model.Department</class>`: Registers our entity classes with Hibernate.
  * `hibernate.dialect`: Tells Hibernate to speak PostgreSQL syntax.
  * `hibernate.show_sql=true`: Prints the generated SQL queries to your terminal so you can learn what Hibernate is doing.

---

### 📁 2. The EntityManager Factory: `JPAUtil.java`
* **File Location:** `src/main/java/com/campus/util/JPAUtil.java`
* **What it does:** Creates and manages JPA database connections.
* **Why it matters:**
  * `EntityManagerFactory` is heavyweight: Created once when the app starts.
  * `EntityManager` is lightweight: Created per request/transaction and closed immediately after work is done.
```java
// How it is used in code:
EntityManager em = JPAUtil.getEntityManager();
try {
    // perform database operations
} finally {
    em.close(); // always release resources
}
```

---

### 📁 3. Domain Entities & Relationships: `Department.java` & `Student.java`

Instead of storing department as a simple `String`, we model real-world relationships where a Department is its own entity containing multiple students.

#### Department Entity (`Department.java`):
* **One Department has Many Students (`@OneToMany`)**
```java
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "department")
    private List<Student> students = new ArrayList<>();
}
```

#### Student Entity (`Student.java`):
* **Many Students belong to One Department (`@ManyToOne`)**
```java
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int age;

    @ManyToOne
    @JoinColumn(name = "department_id") // Foreign key column in PostgreSQL
    private Department department;
}
```

#### Relationship Concept:
| Annotation | Where It Belongs | What It Means |
|------------|------------------|---------------|
| `@ManyToOne` | `Student.java` | Many students can be enrolled in the same department. |
| `@JoinColumn(name = "department_id")` | `Student.java` | Creates the foreign key column `department_id` in the `students` table. |
| `@OneToMany(mappedBy = "department")` | `Department.java` | One department links back to its list of students. `mappedBy` says `Student.department` owns the relationship. |

---

### 📁 4. The Database Schema: `schema.sql`

```sql
CREATE TABLE departments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE students (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department_id INT,
    age INT,
    CONSTRAINT fk_student_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id)
);
```

---

### 📁 5. Data Access Layer: `StudentJPADAO.java` & `DepartmentJPADAO.java`

These classes handle database transactions without writing low-level JDBC code:

#### The 4 Core JPA Operations:
1. **Create (`persist`)**: Saves a new Java object into PostgreSQL.
   ```java
   em.getTransaction().begin();
   em.persist(student);
   em.getTransaction().commit();
   ```
2. **Read (`find` & `createQuery`)**:
   * Find by ID: `em.find(Student.class, id);`
   * Find all using **JPQL** (Java Persistence Query Language):
     ```java
     em.createQuery("SELECT s FROM Student s ORDER BY s.id", Student.class).getResultList();
     ```
3. **Update (`merge`)**: Saves changes to an existing student.
   ```java
   em.getTransaction().begin();
   em.merge(student);
   em.getTransaction().commit();
   ```
4. **Delete (`remove`)**: Deletes a student from the database.
   ```java
   em.getTransaction().begin();
   Student student = em.find(Student.class, id);
   if (student != null) { em.remove(student); }
   em.getTransaction().commit();
   ```

#### JPQL Queries Implemented in `StudentJPADAO.java`:
* **Filter by Department:**
  `SELECT s FROM Student s WHERE s.department.name = :dept`
* **Search by Name:**
  `SELECT s FROM Student s WHERE LOWER(s.name) LIKE LOWER(:name)`
* **Count Total Students:**
  `SELECT COUNT(s) FROM Student s`

---

### 📁 6. Business Service Layer: `StudentService.java`
* **File Location:** `src/main/java/com/campus/service/StudentService.java`
* **What it does:** Acts as the clean single point of contact for the web layer. It coordinates between `StudentJPADAO` and `DepartmentJPADAO`.
* The servlet never talks directly to raw JPA methods; it calls clean service methods like:
  * `service.getAllStudents()`
  * `service.getAllDepartments()`
  * `service.addStudent(student)`
  * `service.deleteStudent(id)`
  * `service.findStudentsByDepartment("Computer Science")`

---

### 📁 7. Controller: `StudentServlet.java`
* **File Location:** `src/main/java/com/campus/controller/StudentServlet.java`
* **Mapped to:** `@WebServlet("/students")`
* **What it does:** Inspects incoming HTTP requests, calls the service, and decides which JSP page to show.

#### Actions Handled by `doGet`:
| Action URL Parameter | What It Does | Target View |
|----------------------|--------------|-------------|
| `action=null` or `action=list` | Fetches all students and departments | Forwards to `students.jsp` |
| `action=edit&id=3` | Loads student #3 and all departments | Forwards to `edit-student.jsp` |
| `action=delete&id=3` | Deletes student #3 from database | Redirects to `/students` |
| `action=department&department=IT` | Filters students enrolled in "IT" | Forwards to `students.jsp` |

#### Actions Handled by `doPost`:
* **Add Student:** Reads `name`, `age`, and `departmentId`. Fetches the `Department` entity by ID, constructs `new Student(name, age, department)`, and calls `service.addStudent(student)`.
* **Update Student (`action=update`):** Reads updated fields, sets the ID, calls `service.updateStudent(student)`, and redirects back to `/students`.

---

### 📁 8. Presentation Layer (JSPs & JSTL)

#### `students.jsp`:
* Uses JSTL `<c:forEach>` to loop through the department list and populate the `<select>` dropdown.
* Displays the department name using Expression Language:
  ```jsp
  <td>${student.department.name}</td>
  ```
  *(Notice: JPA automatically follows the relationship `student -> department -> name`!)*
* Contains both the **Add Student Form** and the **Department Filter Form**.

#### `edit-student.jsp`:
* Pre-populates the input fields with the existing student's data:
  ```jsp
  <input type="text" name="name" value="${student.name}" required>
  ```
* Automatically marks the student's current department as `selected` in the dropdown:
  ```jsp
  <option value="${department.id}" ${department.id == student.department.id ? 'selected' : ''}>
      ${department.name}
  </option>
  ```

---

## 5. Summary Cheat Sheet: Day 8 vs Day 10

| Feature | Day 8 (JDBC) | Day 10 (JPA & Hibernate) |
|---------|--------------|--------------------------|
| **SQL Queries** | Written manually as strings in Java | Generated automatically by Hibernate |
| **Data Mapping** | Manual reading: `rs.getString("name")` | Automatic mapping to Java Entity objects |
| **Relationships** | Manual SQL `JOIN` queries | Handled cleanly with `@ManyToOne` and `@OneToMany` |
| **Object Updates** | `UPDATE students SET ... WHERE id = ?` | Single line: `em.merge(student)` |
| **Object Deletion** | `DELETE FROM students WHERE id = ?` | Single line: `em.remove(student)` |
| **Transactions** | `connection.setAutoCommit(false)` | `em.getTransaction().begin()` and `.commit()` |
| **Port & Server** | Tomcat / Jetty on `8080` | Jetty EE10 on `8081` (`mvn jetty-ee10:run`) |

---

## 6. Visual Flowcharts: Class by Class & Method by Method

---

### 🗺️ Class Hierarchy & Dependency Graph

```mermaid
graph TD
    subgraph View ["Presentation (JSP)"]
        JSP1["students.jsp"]
        JSP2["edit-student.jsp"]
    end

    subgraph Controller ["Web Controller"]
        Servlet["StudentServlet.java"]
    end

    subgraph Service ["Business Layer"]
        ServiceClass["StudentService.java"]
    end

    subgraph DAO ["Data Access Layer (JPA DAOs)"]
        SDAO["StudentJPADAO.java"]
        DDAO["DepartmentJPADAO.java"]
    end

    subgraph ORM ["JPA & Hibernate Core"]
        JPAU["JPAUtil.java"]
        EMF["EntityManagerFactory ('campusPU')"]
        EM["EntityManager (Session)"]
    end

    subgraph Entities ["Entities (Domain Model)"]
        Dept["Department.java (@OneToMany)"]
        Stud["Student.java (@ManyToOne)"]
    end

    subgraph DB ["PostgreSQL"]
        Postgres[("campus_db")]
    end

    JSP1 -->|HTTP GET / POST| Servlet
    JSP2 -->|HTTP POST| Servlet
    Servlet -->|Delegates to| ServiceClass
    ServiceClass -->|Calls| SDAO
    ServiceClass -->|Calls| DDAO
    SDAO -->|Requests EM from| JPAU
    DDAO -->|Requests EM from| JPAU
    JPAU --> EMF
    EMF --> EM
    EM -->|Maps & Persists| Stud
    EM -->|Maps & Persists| Dept
    Dept -. 1 to Many .-> Stud
    EM -->|Executes SQL| Postgres
    Servlet -->|Forwards model to| JSP1
    Servlet -->|Forwards model to| JSP2
```

---

### 🌐 1. `StudentServlet.java` Method Flowcharts

#### `doGet(HttpServletRequest request, HttpServletResponse response)`
Handles page requests, data loading, filtering, and deletion:

```mermaid
flowchart TD
    Start([User sends GET /students]) --> ReadAction[Read 'action' parameter]
    ReadAction --> ActionCheck{What is action?}

    ActionCheck -->|null OR 'list'| ListFlow[List All Students & Departments]
    ListFlow --> CallGetAll["service.getAllStudents()<br/>service.getAllDepartments()"]
    CallGetAll --> SetAttr1["request.setAttribute('students', ...)<br/>request.setAttribute('departments', ...)"]
    SetAttr1 --> FwdList["Forward to /students.jsp"]

    ActionCheck -->|'edit'| EditFlow[Edit Student Form]
    EditFlow --> ParseEditId["id = Integer.parseInt(request.getParameter('id'))"]
    ParseEditId --> CallGetStudent["service.getStudentById(id)<br/>service.getAllDepartments()"]
    CallGetStudent --> SetAttr2["request.setAttribute('student', ...)<br/>request.setAttribute('departments', ...)"]
    SetAttr2 --> FwdEdit["Forward to /edit-student.jsp"]

    ActionCheck -->|'delete'| DeleteFlow[Delete Student]
    DeleteFlow --> ParseDelId["id = Integer.parseInt(request.getParameter('id'))"]
    ParseDelId --> CallDel["service.deleteStudent(id)"]
    CallDel --> RedirectList["response.sendRedirect('/students')"]

    ActionCheck -->|'department'| DeptFlow[Filter by Department]
    DeptFlow --> ReadDeptParam["dept = request.getParameter('department')"]
    ReadDeptParam --> CallFilter["service.findStudentsByDepartment(dept)<br/>service.getAllDepartments()"]
    CallFilter --> SetAttr3["request.setAttribute('students', filteredList)<br/>request.setAttribute('selectedDepartment', dept)"]
    SetAttr3 --> FwdFiltered["Forward to /students.jsp"]
```

---

#### `doPost(HttpServletRequest request, HttpServletResponse response)`
Handles form submissions (Adding new students or Updating existing ones):

```mermaid
flowchart TD
    Start([User Submits Form via POST /students]) --> ReadAction[Read 'action' parameter]
    ReadAction --> CheckAction{action == 'update'?}

    CheckAction -->|Yes: Update Student| UpdatePath[Update Existing Record]
    UpdatePath --> ReadUpParams["Read: id, name, age, departmentId"]
    ReadUpParams --> FetchDeptUp["dept = service.getDepartmentById(departmentId)"]
    FetchDeptUp --> BuildStudentUp["student = new Student(name, age, dept)<br/>student.setId(id)"]
    BuildStudentUp --> CallUpdateService["service.updateStudent(student)"]
    CallUpdateService --> RedirectAfterUp["response.sendRedirect('/students')"]

    CheckAction -->|No: Add Student| AddPath[Add New Record]
    AddPath --> ReadAddParams["Read: name, age, departmentId"]
    ReadAddParams --> FetchDeptAdd["dept = service.getDepartmentById(departmentId)"]
    FetchDeptAdd --> BuildStudentAdd["student = new Student(name, age, dept)"]
    BuildStudentAdd --> CallAddService["service.addStudent(student)"]
    CallAddService --> RedirectAfterAdd["response.sendRedirect('/students')"]
```

---

### 💼 2. `StudentService.java` Method Delegation

`StudentService` isolates business logic from servlets. Every method coordinates between the two DAOs:

```mermaid
flowchart LR
    subgraph ServletCallers ["Controller Requests"]
        C1["getAllStudents()"]
        C2["getStudentById(id)"]
        C3["addStudent(student)"]
        C4["updateStudent(student)"]
        C5["deleteStudent(id)"]
        C6["findStudentsByDepartment(dept)"]
        C7["getAllDepartments()"]
        C8["getDepartmentById(id)"]
    end

    subgraph ServiceLayer ["StudentService.java"]
        S["StudentService Coordinator"]
    end

    subgraph DAOs ["DAOs"]
        SDAO["StudentJPADAO"]
        DDAO["DepartmentJPADAO"]
    end

    C1 --> S -->|Delegates to| SDAO
    C2 --> S -->|Delegates to| SDAO
    C3 --> S -->|Delegates to| SDAO
    C4 --> S -->|Delegates to| SDAO
    C5 --> S -->|Delegates to| SDAO
    C6 --> S -->|Delegates to| SDAO

    C7 --> S -->|Delegates to| DDAO
    C8 --> S -->|Delegates to| DDAO
```

---

### 💾 3. `StudentJPADAO.java` Method Flowcharts

#### `addStudent(Student student)` — Persisting New Entity
```mermaid
flowchart TD
    Start([addStudent Called]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> TryBlock[Try Block]
    TryBlock --> BeginTx["em.getTransaction().begin()"]
    BeginTx --> Persist["em.persist(student)"]
    Persist --> CommitTx["em.getTransaction().commit()"]
    CommitTx --> FinallyBlock[Finally Block]

    TryBlock -. Error Occurs .-> CatchBlock[Catch Exception]
    CatchBlock --> CheckActive{Transaction Active?}
    CheckActive -->|Yes| Rollback["em.getTransaction().rollback()"]
    CheckActive -->|No| LogErr["e.printStackTrace()"]
    Rollback --> LogErr
    LogErr --> FinallyBlock

    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> End([Done])
```

---

#### `getAllStudents()` — JPQL Query Execution
```mermaid
flowchart TD
    Start([getAllStudents Called]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> TryBlock[Try Block]
    TryBlock --> CreateQuery["query = em.createQuery('SELECT s FROM Student s', Student.class)"]
    CreateQuery --> GetList["list = query.getResultList()"]
    GetList --> FinallyBlock[Finally Block]
    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> Return([Return List<Student>])
```

---

#### `getStudentById(int id)` — Finding by Primary Key
```mermaid
flowchart TD
    Start([getStudentById Called]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> TryBlock[Try Block]
    TryBlock --> Find["student = em.find(Student.class, id)"]
    Find --> FinallyBlock[Finally Block]
    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> Return([Return Student or null])
```

---

#### `updateStudent(Student student)` — Merging Changes
```mermaid
flowchart TD
    Start([updateStudent Called]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> TryBlock[Try Block]
    TryBlock --> BeginTx["em.getTransaction().begin()"]
    BeginTx --> Merge["em.merge(student)"]
    Merge --> CommitTx["em.getTransaction().commit()"]
    CommitTx --> FinallyBlock[Finally Block]

    TryBlock -. Error Occurs .-> CatchBlock[Catch Exception]
    CatchBlock --> CheckActive{Transaction Active?}
    CheckActive -->|Yes| Rollback["em.getTransaction().rollback()"]
    Rollback --> FinallyBlock
    CheckActive -->|No| FinallyBlock

    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> End([Done])
```

---

#### `deleteStudent(int id)` — Finding & Removing
```mermaid
flowchart TD
    Start([deleteStudent Called]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> TryBlock[Try Block]
    TryBlock --> BeginTx["em.getTransaction().begin()"]
    BeginTx --> Find["student = em.find(Student.class, id)"]
    Find --> CheckNull{student != null?}
    CheckNull -->|Yes| Remove["em.remove(student)"]
    CheckNull -->|No| CommitTx
    Remove --> CommitTx["em.getTransaction().commit()"]
    CommitTx --> FinallyBlock[Finally Block]

    TryBlock -. Error Occurs .-> CatchBlock[Catch Exception]
    CatchBlock --> Rollback["em.getTransaction().rollback()"]
    Rollback --> FinallyBlock

    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> End([Done])
```

---

#### `findStudentsByDepartment(String departmentName)` — Parameterized JPQL
```mermaid
flowchart TD
    Start([findStudentsByDepartment Called]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> CreateQuery["em.createQuery('SELECT s FROM Student s WHERE s.department.name = :dept', Student.class)"]
    CreateQuery --> BindParam["query.setParameter('dept', departmentName)"]
    BindParam --> FetchResults["results = query.getResultList()"]
    FetchResults --> CloseEM["em.close()"]
    CloseEM --> Return([Return Filtered List<Student>])
```

---

### 🏢 4. `DepartmentJPADAO.java` Method Flowcharts

#### `getAllDepartments()` & `getDepartmentById(int id)`
```mermaid
flowchart TD
    subgraph GetAll ["getAllDepartments()"]
        A1([Call]) --> A2["em = JPAUtil.getEntityManager()"]
        A2 --> A3["SELECT d FROM Department d ORDER BY d.name"]
        A3 --> A4["list = query.getResultList()"]
        A4 --> A5["em.close()"]
        A5 --> A6([Return List<Department>])
    end

    subgraph GetById ["getDepartmentById(id)"]
        B1([Call]) --> B2["em = JPAUtil.getEntityManager()"]
        B2 --> B3["dept = em.find(Department.class, id)"]
        B3 --> B4["em.close()"]
        B4 --> B5([Return Department])
    end
```

---

### 🏭 5. `JPAUtil.java` Lifecycle Flowchart

How the application initializes the factory once and vends connections safely:

```mermaid
flowchart TD
    AppStart([Application Boots / Class Loaded]) --> InitStatic["static final factory = Persistence.createEntityManagerFactory('campusPU')"]
    InitStatic --> ParseXML["Reads /META-INF/persistence.xml"]
    ParseXML --> BuildPool["Builds DB Connection Pool & Validates Entities (Student, Department)"]

    subgraph PerRequest ["Per Request / Thread Execution"]
        Req([Incoming Request]) --> CallGet["JPAUtil.getEntityManager()"]
        CallGet --> CreateEM["factory.createEntityManager()"]
        CreateEM --> Work["Perform persist / find / merge / query"]
        Work --> CloseEM["em.close()"]
    end

    AppStop([Application Shutdown]) --> CallClose["JPAUtil.close()"]
    CallClose --> DestroyFactory["factory.close()"]
```

---

### 🔗 6. Entity Relationship Diagram (Object Graph)

```mermaid
erDiagram
    DEPARTMENTS ||--o{ STUDENTS : "has many"
    DEPARTMENTS {
        int id PK "GenerationType.IDENTITY"
        string name "Department Name"
    }
    STUDENTS {
        int id PK "GenerationType.IDENTITY"
        string name "Student Name"
        int age "Student Age"
        int department_id FK "References departments(id)"
    }
```

