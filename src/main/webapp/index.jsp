<html>
<body>
    <h2>Login</h2>
    <a href="login">Login with Google</a>
</body>
</html>


<%@ page import="com.example.utils.GmailUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inthread - AI Email Management</title>
    <link rel="stylesheet" href="email.css">
    <link rel="stylesheet" href="css/index.css">
</head>
<body>

    <!-- Navigation Bar -->
    <header>
        <div class="logo">Inthread</div>
        <nav>
            <ul class="nav-links">
                <li><a href="#home">Home</a></li>
                <li><a href="#features">Features</a></li>
                <li><a href="#contact">Contact</a></li>
                <li><a href="login">Login with Google</a></li>
            </ul>
        </nav>
    </header>

    <!-- Hero Section -->
    <section class="hero" id="home">
        <div class="hero-content">
            <h1>Revolutionize Your Email Experience</h1>
            <p>AI-powered email management that simplifies your workflow.</p>
            <div class="buttons">
                <button class="btn">Get Started</button>
                <button class="btn btn-outline">Learn More</button>
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section class="features" id="features">
        <h2>Why Choose Inthread?</h2>
        <div class="features-container">
            <div class="feature-box">
                <h3>📩 AI Email Summarization</h3>
                <p>Instantly summarize long emails.</p>
            </div>
            <div class="feature-box">
                <h3>🌍 Multilingual Translation</h3>
                <p>Translate emails in real-time.</p>
            </div>
            <div class="feature-box">
                <h3>✍️ Post-Send Editing</h3>
                <p>Fix errors after sending.</p>
            </div>
            <div class="feature-box">
                <h3>🔔 Smart Notifications</h3>
                <p>Stay ahead with AI alerts.</p>
            </div>
        </div>
    </section>

    <!-- Contact Section -->
    <section class="contact" id="contact">
        <h2>Contact Us</h2>
        <form id="contactForm">
            <input type="text" id="name" placeholder="Your Name" required>
            <input type="email" id="email" placeholder="Your Email" required>
            <textarea id="message" placeholder="Your Message" required></textarea>
            <button type="submit" class="btn">Send Message</button>
        </form>
    </section>

    <!-- Footer -->
    <footer>
        <p>&copy; 2025 Inthread | All rights reserved.</p>
    </footer>

</body>
</html>