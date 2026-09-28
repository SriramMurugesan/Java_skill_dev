package com.campus.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/session")
public class SessionServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");

        // -------------------------
        // SESSION
        // -------------------------

        HttpSession session =
                request.getSession();

        session.setAttribute(
                "username",
                "Arun"
        );

        // -------------------------
        // COOKIE
        // -------------------------

        Cookie usernameCookie =
                new Cookie(
                        "username",
                        "Arun"
                );

        usernameCookie.setMaxAge(
                60 * 60
        );

        response.addCookie(
                usernameCookie
        );

        // -------------------------
        // RESPONSE
        // -------------------------

        response.getWriter().println(
                "<h1>Session and Cookie Demo</h1>"
        );

        response.getWriter().println(
                "<p>Session username: Arun</p>"
        );

        response.getWriter().println(
                "<p>Cookie username: Arun</p>"
        );

        response.getWriter().println(
                "<p>Session created successfully.</p>"
        );

        response.getWriter().println(
                "<br><a href='students'>Back to Students</a>"
        );
    }
}