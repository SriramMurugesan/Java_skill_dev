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
        fileSizeThreshold = 1024 * 1024,      // 1 MB
        maxFileSize = 1024 * 1024 * 5,        // 5 MB max file
        maxRequestSize = 1024 * 1024 * 10     // 10 MB max request
)
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/upload.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Get the file part from request
        Part filePart = request.getPart("file");

        if (filePart != null && filePart.getSize() > 0) {
            String fileName = filePart.getSubmittedFileName();
            long fileSize = filePart.getSize();
            String contentType = filePart.getContentType();

            // 2. Read file bytes and convert to Base64 for instant preview in browser
            try (InputStream inputStream = filePart.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();
                String base64Content = Base64.getEncoder().encodeToString(bytes);

                request.setAttribute("fileName", fileName);
                request.setAttribute("fileSize", fileSize);
                request.setAttribute("contentType", contentType);
                request.setAttribute("base64Content", base64Content);
                request.setAttribute("isImage", contentType != null && contentType.startsWith("image/"));
            }
        }

        request.getRequestDispatcher("/upload.jsp")
                .forward(request, response);
    }
}
