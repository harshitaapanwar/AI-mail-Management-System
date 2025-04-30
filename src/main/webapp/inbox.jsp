<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Email Clone - Inbox</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="header">
  <div class="logo">Inthread</div>
  <div class="search-bar">
    <input type="text" placeholder="Search emails...">
  </div>
  <div class="nav-links">
    <a href="#">Home</a>
    <a href="#">About Us</a>
    <a href="#">Settings</a>
  </div>
</div>

<div class="container">
  <div class="sidebar">
    <!-- Displaying the username dynamically -->
    <div class="profile-name">
        <%= session.getAttribute("profileName") != null ? session.getAttribute("profileName") : "Guest" %>
    </div>

    <ul>
      <li><a href="#" onclick="loadEmails('primary')">Inbox</a></li>
      <li><a href="#" onclick="loadEmails('social')">Social</a></li>
      <li><a href="#" onclick="loadEmails('important')">Important</a></li>
      <li><a href="#" onclick="loadEmails('promotions')">Promotions</a></li>
    </ul>
  </div>

  <div class="main-content">
    <div class="cards">
      <div class="card" onclick="loadEmails('primary')">
        <h3>Primary</h3>
        <p>Your main personal emails will show up here.</p>
      </div>
      <div class="card" onclick="loadEmails('social')">
        <h3>Social</h3>
        <p>Emails from social media like Facebook, Twitter, etc.</p>
      </div>
      <div class="card" onclick="loadEmails('important')">
        <h3>Important</h3>
        <p>Flagged or priority messages that need your attention.</p>
      </div>
      <div class="card" onclick="loadEmails('promotions')">
        <h3>Promotions</h3>
        <p>Deals, offers, and newsletters from brands.</p>
      </div>
    </div>

    <div class="email-list" id="emailList">
      <p class="placeholder">Select a category to view emails</p>
    </div>

    <!-- Compose Email Button -->
    <button class="compose-btn">Compose</button>

    <!-- Compose Modal -->
    <div class="compose-modal" id="composeModal">
        <div class="compose-content">
            <h3>New Message</h3>
            <form method="post" action="send">

                <input type="email" name="to" placeholder="To" required><br>
                <input type="text" name="subject" placeholder="Subject" required><br>
                <textarea name="message" placeholder="Message" required></textarea><br>
                <div class="form-buttons">
                    <button type="submit">Send</button>
                    <button type="button" id="closeCompose">Close</button>
                </div>
            </form>
        </div>
    </div>

  </div>
</div>
<script>
document.addEventListener("DOMContentLoaded", function () {
    // Compose modal functionality
    const composeBtn = document.querySelector('.compose-btn');
    const composeModal = document.getElementById('composeModal');
    const closeCompose = document.getElementById('closeCompose');

    composeBtn.addEventListener('click', () => {
        composeModal.style.display = 'block';
    });

    closeCompose.addEventListener('click', () => {
        composeModal.style.display = 'none';
    });

    // Load emails function
    window.loadEmails = function(category) {
        if (!category) {
            console.error("No category provided to loadEmails");
            return;
        }

        console.log("Fetching category:", category); // Debug log

        fetch(`/inbox?category=${category}`)

            .then(response => {
                if (!response.ok) throw new Error("Network error");
                return response.json();
            })
            .then(data => {
                const emailListContainer = document.getElementById('emailList');
                emailListContainer.innerHTML = "";

                if (data.length === 0) {
                    emailListContainer.innerHTML = "<p>No emails found in this category.</p>";
                } else {
                    data.forEach(email => {
                        const emailElement = document.createElement('div');
                        emailElement.classList.add('email-item');
                        emailElement.innerHTML = `
                            <div class="email-from">${email.From}</div>
                            <div class="email-subject">${email.Subject}</div>
                            <div class="email-date">${email.Date}</div>
                        `;
                        emailListContainer.appendChild(emailElement);
                    });
                }
            })
            .catch(error => {
                console.error("Error fetching emails:", error);
            });
    };
});
</script>


</body>
</html>
