# Day 7: Java Web Architecture — Filters, JSPs, Sessions, Cookies & Listeners

---

## 1. Quick Start: Running & Testing Today's Code

Start the application with Maven:

```bash
mvn jetty:run
```

### URLs to Test in Your Browser:
1. **Home Navigation Hub:** [http://localhost:8080/](http://localhost:8080/)
2. **Student List (Servlet + JSP):** [http://localhost:8080/students](http://localhost:8080/students)
3. **Add Student Form:** [http://localhost:8080/student.html](http://localhost:8080/student.html)
4. **Session & Cookie Demo:** [http://localhost:8080/session](http://localhost:8080/session)

### What to Watch in Your Terminal:
Every time you load any page, you will see output like:
```text
Request received
Response completed
```
And when visiting `/session` for the first time:
```text
Request received
New session created
Response completed
```

---

## 2. What is a Servlet Filter? (For Absolute Beginners)

### Real-World Analogy: The Airport Security Checkpoint 🛂
Imagine you are catching a flight:
1. **Passenger (HTTP Request):** You arrive at the airport intending to board your flight (**Servlet**).
2. **Security Gate (Filter):** Before you can enter the gate, security scans your baggage, checks your ID, and verifies your ticket.
   - If everything is valid &rarr; They wave you through (**`chain.doFilter()`**).
   - If something is invalid &rarr; They stop you right there and deny entry.
3. **Flight / Cabin (Servlet):** You sit down and reach your destination.
4. **Baggage Claim / Exit (Filter Post-Processing):** On your way out, you pass through customs or exit inspection before leaving the airport (**HTTP Response**).

```
[Browser / Client]
       │  ▲
       │  │ (1) Request In  /  (4) Response Out
       ▼  │
┌────────────────────────────────────────────────────────┐
│                     Servlet Filter                     │
│  (1) Pre-processing: System.out.println("Request")    │
│  (2) Pass along:     chain.doFilter(request, response) │
│  (4) Post-processing: System.out.println("Response")   │
└────────────────────────────────────────────────────────┘
       │  ▲
       │  │ (2) Pass down   /  (3) Return output
       ▼  │
┌────────────────────────────────────────────────────────┐
│                   Servlet / JSP                        │
│          (StudentServlet, SessionServlet, etc.)        │
└────────────────────────────────────────────────────────┘
```

### Core Purpose of Filters
A **Filter** is a reusable Java component that **intercepts requests and responses** before they reach Servlets or static files (HTML, CSS, JSP).
- It allows you to inspect, modify, or block requests.
- It prevents code duplication by centralizing cross-cutting logic like **logging**, **authentication**, **encryption**, and **character encoding**.

---

## 3. How Filters Work: The `FilterChain`

In Jakarta EE, a filter implements the interface:
```java
jakarta.servlet.Filter
```

It contains one mandatory method:
```java
void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
```

### What is the `FilterChain`?
A web application can have multiple filters lined up one after another (e.g., `AuthenticationFilter` &rarr; `LoggingFilter` &rarr; `EncodingFilter`).
- The `FilterChain` represents this sequence of filters.
- **`chain.doFilter(request, response)`** means:
  > *"I am done with my pre-checks. Pass this request to the next filter in line, or directly to the target Servlet if no more filters exist."*

> ⚠️ **CRITICAL RULE:**  
> If you forget to call `chain.doFilter(request, response);`, the request **freezes and stops right there**. The browser will hang or show a blank white page because the request never reaches the Servlet!

---

## 4. Deep Dive: Code Explanation of `LoggingFilter.java`

File: `src/main/java/com/campus/filter/LoggingFilter.java`

```java
package com.campus.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

// 1. URL MAPPING ANNOTATION:
// "/*" tells the server to run this filter for EVERY single request!
@WebFilter("/*")
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        // 2. PRE-PROCESSING (Executes BEFORE the Servlet is reached)
        System.out.println("Request received");

        // 3. PASS TO NEXT COMPONENT (Servlet or next Filter in chain)
        chain.doFilter(request, response);

        // 4. POST-PROCESSING (Executes AFTER the Servlet finishes its response)
        System.out.println("Response completed");
    }
}
```

### Key Highlights:
1. **`@WebFilter("/*")`**:
   - The wildcard `"/*"` intercepts **all incoming traffic** (Servlets, HTML files, JSPs, CSS, images).
2. **Pre-processing Phase (Line 22)**:
   - Code before `chain.doFilter()` runs while the request travels from the client to the server.
3. **Execution Phase (Line 27)**:
   - `chain.doFilter()` yields control to the target Servlet (`StudentServlet`, `SessionServlet`, etc.).
4. **Post-processing Phase (Line 32)**:
   - Once the Servlet finishes generating HTML, control returns to this filter, executing the post-processing code right before the response is sent back to the browser.

---

## 5. Common Real-World Use Cases for Filters

| Use Case | How Filter Solves It |
| :--- | :--- |
| **Audit & Logging** | Record client IP addresses, requested URLs, and request timestamps (like our `LoggingFilter`). |
| **Authentication & Security** | Check if user session exists (`request.getSession(false) != null`). If not logged in, redirect to `/login.html`. |
| **Character Encoding** | Automatically enforce `request.setCharacterEncoding("UTF-8")` globally on all POST requests. |
| **Performance / Execution Timer** | Measure how many milliseconds a request took (`System.currentTimeMillis()` before and after `chain.doFilter()`). |
| **GZIP Compression** | Compress large HTML/JSON payloads on the fly before sending over the network to save bandwidth. |

---

## 6. Today's Full Implementation Breakdown

Today's project evolved from pure Servlets into a complete **Enterprise Java Web Application** with four key pillars:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Campus Web Application                          │
├───────────────────┬───────────────────┬────────────────────────────────┤
│ 1. Interception   │ 2. View Rendering │ 3. State Management & Events   │
│ (Filters)         │ (MVC with JSP)    │ (Sessions, Cookies, Listeners) │
├───────────────────┼───────────────────┼────────────────────────────────┤
│ LoggingFilter     │ StudentServlet    │ SessionServlet                 │
│                   │   + students.jsp  │ CampusSessionListener          │
└───────────────────┴───────────────────┴────────────────────────────────┘
```

---

### Pillar 1: MVC Architecture with Servlets & JSP

In Day 6, HTML was manually written inside Java code (`out.println("<html>...")`).  
That was messy and hard to maintain. Today, we adopted the **MVC (Model-View-Controller)** pattern:

- **Model:** `StudentService` holds student data.
- **Controller:** `StudentServlet` receives request, calls business logic, attaches data to request, and forwards.
- **View:** `students.jsp` receives the data and focuses strictly on displaying HTML.

#### Controller: `StudentServlet.java` (Snippet)
```java
var students = studentService.getAllStudents();

// Attach data to request scope
request.setAttribute("students", students);

// Forward to View (students.jsp)
RequestDispatcher dispatcher = request.getRequestDispatcher("/students.jsp");
dispatcher.forward(request, response);
```

#### View: `students.jsp` (Snippet)
```jsp
<h2>Student List</h2>

<%
    java.util.List<String> students =
            (java.util.List<String>) request.getAttribute("students");

    for (String student : students) {
%>
    <p><%= student %></p>
<%
    }
%>
```

> **Forward vs Redirect:**
> - **Forward (`dispatcher.forward`):** Server-side handover. The browser's URL does not change. Request attributes are preserved.
> - **Redirect (`response.sendRedirect`):** Tells the browser to make a brand-new HTTP GET request. The URL in the address bar changes.

---

### Pillar 2: State Management (Session & Cookies)

HTTP is **stateless** — each request is completely independent. The server has no memory of who you were 1 second ago.  
We use **Sessions** and **Cookies** to maintain user state.

File: `src/main/java/com/campus/controller/SessionServlet.java`

```java
@WebServlet("/session")
public class SessionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {

        // 1. HTTP SESSION (Server-Side Storage)
        HttpSession session = request.getSession();
        session.setAttribute("username", "Arun");

        // 2. HTTP COOKIE (Client-Side Storage)
        Cookie usernameCookie = new Cookie("username", "Arun");
        usernameCookie.setMaxAge(60 * 60); // 1 hour expiration
        response.addCookie(usernameCookie);

        // 3. User Response
        response.setContentType("text/html");
        response.getWriter().println("<h1>Session and Cookie Demo</h1>");
        response.getWriter().println("<p>Session username: Arun</p>");
        response.getWriter().println("<p>Cookie username: Arun</p>");
    }
}
```

#### Session vs Cookie Comparison:

| Feature | Cookie | HttpSession |
| :--- | :--- | :--- |
| **Stored Where?** | In the **Browser** (Client machine) | In **Server Memory / Heap** |
| **Security** | Low (accessible to browser tools, users can edit) | High (users cannot tamper with server memory) |
| **Capacity** | ~4 KB limit per cookie | Limited only by server RAM |
| **How they connect** | Stores the cookie `JSESSIONID` | Uses `JSESSIONID` cookie to look up user's session |
| **Best used for** | Non-sensitive preferences (Theme, Remember Me) | Sensitive data (Login token, Cart, User ID) |

---

### Pillar 3: Application Event Listeners

A **Listener** is an observer that automatically wakes up when important lifecycle events occur inside the container.

File: `src/main/java/com/campus/listener/CampusSessionListener.java`

```java
package com.campus.listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class CampusSessionListener implements HttpSessionListener {

    // Triggered automatically when ANY user starts a new session
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println("New session created");
    }

    // Triggered automatically when a session expires or is invalidated
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        System.out.println("Session destroyed");
    }
}
```

### Why use Listeners?
- **User Activity Tracking:** Count how many active users are currently online.
- **Resource Management:** Clean up database connections, temp files, or caches when a user session ends.
- **Security:** Log when sessions expire due to timeout.

---

## 7. Complete Request-Response Flow Walkthrough

Here is the exact lifecycle that occurs when you visit `http://localhost:8080/session`:

```
1. Browser requests GET /session
      │
      ▼
2. LoggingFilter intercepts request
   ──> Terminal prints: "Request received"
   ──> Calls chain.doFilter(request, response)
      │
      ▼
3. CampusSessionListener fires (if first visit)
   ──> Session does not exist yet -> Container creates session
   ──> Terminal prints: "New session created"
      │
      ▼
4. SessionServlet executes
   ──> Stores "username" in session
   ──> Adds Cookie to HTTP response header: Set-Cookie: username=Arun
   ──> Writes HTML to output stream
      │
      ▼
5. Control returns to LoggingFilter
   ──> Terminal prints: "Response completed"
      │
      ▼
6. Browser receives HTML and stores Cookie!
```

---

## 8. Summary Cheatsheet

| Component | Annotation | Interface / Base Class | Primary Purpose |
| :--- | :--- | :--- | :--- |
| **Filter** | `@WebFilter` | `jakarta.servlet.Filter` | Intercept requests/responses globally before Servlets |
| **Servlet** | `@WebServlet` | `HttpServlet` | Business controller; handles GET/POST HTTP logic |
| **JSP** | *None (file)* | Compiled to Servlet by container | Presentation View; generates dynamic HTML cleanly |
| **Session** | *N/A* | `HttpSession` | Secure server-side state persistence |
| **Cookie** | *N/A* | `Cookie` | Client-side small state persistence across requests |
| **Listener** | `@WebListener` | `HttpSessionListener` / `ServletContextListener` | Background event listener for container lifecycle |
