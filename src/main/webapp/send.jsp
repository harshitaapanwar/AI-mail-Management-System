<%@ page import="com.google.api.services.gmail.model.Message" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Base64" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Inthread - Email Client</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <link rel="stylesheet" href="css/style.css">
</head>
<body>
  <div class="app-container">
    <!-- Header -->
    <header class="app-header">
      <div class="header-left">
        <button class="menu-toggle" id="menuToggle" aria-label="Toggle sidebar">
          <i class="fas fa-bars"></i>
        </button>
        <div class="logo">
          <i class="fas fa-at"></i>
          <span>Inthread</span>
        </div>
      </div>
      <div class="search-bar">
        <div class="search-container">
          <i class="fas fa-search"></i>
          <input type="text" placeholder="Search emails, contacts, or files..." aria-label="Search emails">
          <button class="search-tools" aria-label="Search options">
            <i class="fas fa-sliders"></i>
          </button>
        </div>
      </div>
      <div class="header-right">
        <button class="header-icon" title="Support" aria-label="Support">
          <i class="fas fa-question-circle"></i>
        </button>
        <button class="header-icon" id="settingsBtn" title="Settings" aria-label="Settings">
          <i class="fas fa-cog"></i>
        </button>
        <div class="user-avatar" role="button" aria-label="User profile">
          <img src="https://ui-avatars.com/api/?name=<%= session.getAttribute("profileName") != null ? session.getAttribute("profileName") : "Guest" %>&background=random" alt="User avatar">
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <div class="app-main">
      <!-- Sidebar -->
      <aside class="app-sidebar" id="appSidebar">
        <div class="sidebar-header">
          <div class="user-profile">
            <div class="user-avatar">
              <img src="https://ui-avatars.com/api/?name=<%= session.getAttribute("profileName") != null ? session.getAttribute("profileName") : "Guest" %>&background=random" alt="User avatar">
            </div>
            <div class="user-info">
              <div class="user-name"><%= session.getAttribute("profileName") != null ? session.getAttribute("profileName") : "Guest" %></div>
              <div class="user-email"><%= session.getAttribute("userEmail") != null ? session.getAttribute("userEmail") : "guest@example.com" %></div>
            </div>
            <button class="user-menu" aria-label="User menu">
              <i class="fas fa-ellipsis-v"></i>
            </button>
          </div>
        </div>
        <button class="compose-btn" id="composeBtn" aria-label="Compose new email">
          <i class="fas fa-plus"></i>
          <span>Compose</span>
        </button>
        <nav class="sidebar-nav" aria-label="Email navigation">
          <ul>
            <li class="active"><a href="inbox" aria-label="Inbox">Inbox</a></li>
            <li><a href="#" aria-label="Starred emails">Starred</a></li>
            <li><a href="#" aria-label="Snoozed emails">Snoozed</a></li>
            <li><a href="#" aria-label="Important emails">Important</a></li>
            <li><a href="#" aria-label="Sent emails">Sent</a></li>
            <li><a href="#" aria-label="Drafts">Drafts</a></li>
            <li><a href="#" aria-label="Scheduled emails">Scheduled</a></li>
            <li><a href="#" aria-label="All mail">All Mail</a></li>
            <li><a href="#" aria-label="Spam">Spam</a></li>
            <li><a href="#" aria-label="Trash">Trash</a></li>
            <li><a href="#" aria-label="Manage labels">Manage Labels</a></li>
            <li><a href="#" aria-label="Feedback">Feedback</a></li>
            <li><a href="logout" aria-label="Logout">Logout</a></li>
          </ul>
        </nav>
      </aside>

      <!-- Email Content -->
      <main class="email-content" aria-label="Email content">
        <div class="email-toolbar">
          <div class="toolbar-left">
            <div class="checkbox-container">
              <input type="checkbox" id="selectAll" aria-label="Select all emails">
              <label for="selectAll"></label>
            </div>
            <button class="toolbar-btn" aria-label="Refresh emails">
              <i class="fas fa-redo"></i>
            </button>
            <button class="toolbar-btn" aria-label="More actions">
              <i class="fas fa-ellipsis-v"></i>
            </button>
          </div>
          <div class="toolbar-right">
            <span class="pagination-info">1-50 of 1,234</span>
            <button class="toolbar-btn" aria-label="Previous page">
              <i class="fas fa-chevron-left"></i>
            </button>
            <button class="toolbar-btn" aria-label="Next page">
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
        <div class="email-list">
          <% if (request.getAttribute("error") != null) { %>
            <p class="error"><%= request.getAttribute("error") %></p>
          <% } %>
          <% if (request.getAttribute("success") != null) { %>
            <p class="success"><%= request.getAttribute("success") %></p>
          <% } %>
          <%
            List<Message> messages = (List<Message>) request.getAttribute("messages");
            if (messages != null && !messages.isEmpty()) {
              for (Message message : messages) {
                String from = "Unknown";
                String subject = "No Subject";
                String snippet = message.getSnippet() != null ? message.getSnippet() : "";
                String date = "Unknown";

                // Extract headers
                for (com.google.api.services.gmail.model.MessagePartHeader header : message.getPayload().getHeaders()) {
                  if ("From".equals(header.getName())) {
                    from = header.getValue();
                  } else if ("Subject".equals(header.getName())) {
                    subject = header.getValue();
                  } else if ("Date".equals(header.getName())) {
                    date = header.getValue();
                  }
                }
          %>
            <div class="email-item" role="button" aria-label="Email from <%= from %>">
              <div class="email-select">
                <input type="checkbox" id="email_<%= message.getId() %>" aria-label="Select email">
                <label for="email_<%= message.getId() %>"></label>
              </div>
              <div class="email-star">
                <button class="star-btn" aria-label="Star email">
                  <i class="far fa-star"></i>
                </button>
              </div>
              <div class="email-from"><%= from %></div>
              <div class="email-preview">
                <div class="email-subject"><%= subject %></div>
                <div class="email-snippet"><%= snippet %></div>
                <div class="email-time"><%= date %></div>
              </div>
            </div>
          <%
              }
            } else {
          %>
            <p>No emails found.</p>
          <% } %>
        </div>
      </main>
    </div>

    <!-- Compose Modal -->
    <div class="compose-modal" id="composeModal" aria-hidden="true">
      <div class="compose-content">
        <div class="compose-header">
          <h3>New Message</h3>
          <button class="compose-close" id="closeCompose" aria-label="Close compose window">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <form method="post" action="send" class="compose-form" id="composeForm">
          <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") != null ? session.getAttribute("csrfToken") : "" %>" />
          <input type="hidden" name="body" id="emailBody" />
          <div class="compose-field">
            <input type="email" name="to" placeholder="To" required aria-label="Recipient email">
            <button type="button" class="compose-cc-bcc" aria-label="Show Cc and Bcc fields">Cc/Bcc</button>
          </div>
          <div class="compose-field">
            <input type="text" name="subject" placeholder="Subject" required aria-label="Email subject">
          </div>
          <div class="compose-editor" contenteditable="true" aria-label="Email body" id="composeEditor"></div>
          <div class="compose-footer">
            <div class="compose-tools">
              <button type="button" class="compose-tool-btn" aria-label="Attach file">
                <i class="fas fa-paperclip"></i>
              </button>
              <button type="button" class="compose-tool-btn" aria-label="Insert link">
                <i class="fas fa-link"></i>
              </button>
            </div>
            <div class="form-buttons">
              <button type="submit" class="compose-send" aria-label="Send email">Send</button>
              <button type="button" class="compose-discard" id="discardCompose" aria-label="Discard email">Discard</button>
            </div>
          </div>
        </form>
      </div>
    </div>

    <!-- Quick Settings Panel -->
    <div class="quick-settings" id="quickSettings" aria-hidden="true">
      <div class="settings-header">
        <h4>Quick Settings</h4>
        <button class="settings-close" id="closeSettings" aria-label="Close settings">
          <i class="fas fa-times"></i>
        </button>
      </div>
      <div class="settings-content">
        <div class="settings-section">
          <h5>Theme</h5>
          <div class="theme-options">
            <button class="theme-option light active" data-theme="light" aria-label="Light theme">
              <i class="fas fa-sun"></i> Light
            </button>
            <button class="theme-option dark" data-theme="dark" aria-label="Dark theme">
              <i class="fas fa-moon"></i> Dark
            </button>
          </div>
        </div>
        <div class="settings-section">
          <h5>Inbox Type</h5>
          <select class="inbox-type-select" aria-label="Select inbox type">
            <option>Default</option>
            <option>Important First</option>
            <option>Unread First</option>
            <option>Starred First</option>
          </select>
        </div>
      </div>
    </div>
  </div>

  <script src="css/new.js"></script>
</body>
</html>