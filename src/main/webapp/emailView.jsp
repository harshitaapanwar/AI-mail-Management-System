<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email View - Inthread</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="css/emailView.css">
</head>
<body>
    <div class="email-view-container" id="emailViewContainer">
        <div class="email-view-header">
            <h3 id="emailSubject"></h3>
            <button class="email-view-close" id="closeEmailView" aria-label="Close email view">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <div class="email-view-details">
            <p><strong>From:</strong> <span id="emailFrom"></span></p>
            <p><strong>Date:</strong> <span id="emailDate"></span></p>
        </div>
        <div class="email-view-body" id="emailBody"></div>
    </div>
    <script src="css/emailView.js"></script>
</body>
</html>