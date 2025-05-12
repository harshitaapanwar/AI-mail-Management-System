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
       <li><a href="#">Inbox</a></li>
      <li><a href="#">Starred</a></li>
      <li><a href="#">Snoozed</a></li>
      <li><a href="#">Important</a></li>
      <li><a href="#">Sent</a></li>
      <li><a href="#">Drafts</a></li>
      <li><a href="#">Scheduled</a></li>
      <li><a href="#">All Mail</a></li>
      <li><a href="#">Spam</a></li>
      <li><a href="#">Trash</a></li>
      <li><a href="#">Manage Labels</a></li>
      <li><a href="#">Feedback</a></li>


    </ul>
  </div>

    <div class="main-content">
      <a href="inbox" class="block mt-4 text-blue-500 hover:underline">View Inbox</a>
    <% if (request.getAttribute("error") != null) { %>
      <p class="mt-4 text-red-500"><%= request.getAttribute("error") %></p>
    <% } %>
    <% if (request.getAttribute("success") != null) { %>
      <p class="mt-4 text-green-500"><%= request.getAttribute("success") %></p>
    <% } %>

    </div>

   <button class="compose-btn" id="composeBtn">Compose</button>

    <div class="compose-modal" id="composeModal">
        <div class="compose-content">
            <h3>New Message</h3>
            <form method="post" action="send">
                <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") != null ? session.getAttribute("csrfToken") : "" %>">
                <input type="email" name="to" placeholder="To" required>
                <input type="text" name="subject" placeholder="Subject" required>
                <textarea name="body" placeholder="Message" required></textarea>
                <div class="form-buttons">
                    <button type="submit">Send</button>
                    <button type="button" id="closeCompose">Close</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const composeBtn = document.getElementById('composeBtn');
        const composeModal = document.getElementById('composeModal');
        const closeCompose = document.getElementById('closeCompose');

        composeBtn?.addEventListener('click', () => {
            composeModal.style.display = 'flex';
        });

        closeCompose?.addEventListener('click', () => {
            composeModal.style.display = 'none';
        });
    });
</script>

</body>
</html>
