<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <title>File & Image Upload Demo</title>
</head>
<body>

<h1>File & Image Upload Demo</h1>
<p>Upload any image or file to see how Jakarta Servlets handle multipart data.</p>

<form action="upload" method="post" enctype="multipart/form-data">
    <label>Choose File / Image:</label>
    <input type="file" name="file" required>
    <br><br>
    <button type="submit">Upload File</button>
</form>

<c:if test="${not empty fileName}">
    <hr>
    <h2>Upload Result:</h2>
    <p><strong>File Name:</strong> ${fileName}</p>
    <p><strong>File Size:</strong> ${fileSize} bytes (${fileSize / 1024} KB)</p>
    <p><strong>Content Type:</strong> ${contentType}</p>

    <c:if test="${isImage}">
        <h3>Image Preview:</h3>
        <img src="data:${contentType};base64,${base64Content}" alt="Uploaded Preview" style="max-width: 300px; border: 1px solid #ccc; padding: 5px; border-radius: 4px;">
    </c:if>
</c:if>

<br><br>
<a href="index.html">Back to Home</a>

</body>
</html>
