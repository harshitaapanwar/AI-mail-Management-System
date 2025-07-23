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
          <%-- Static email list --%>
          <%
            class StaticEmail {
              String from, subject, date, body;
              StaticEmail(String f, String s, String d, String b) {
                from = f; subject = s; date = d; body = b;
              }
            }
            List<StaticEmail> staticEmails = List.of(
                new StaticEmail("InthreadOfficial@google.com", "Welcome to Inthread!", "2025-05-18",
                    "Dear Harshita,\n\nWelcome to Inthread – your new intelligent email companion. We're excited to have you join our platform!\n\n" +
                    "With Inthread, you can:\n- Summarize long emails instantly\n- Translate content in real time\n- Edit emails even after sending\n\n" +
                    "To get started, log in to your dashboard and explore your inbox. If you have any questions or feedback, feel free to reach out to our support team anytime.\n\n" +
                    "Best regards,\nThe Inthread Team"),

                new StaticEmail("admin@inthread.com", "System Update Notice", "2025-05-17",
                    "Dear User,\n\nThis is to inform you about a scheduled maintenance window on **May 19th, from 1:00 AM to 4:00 AM IST**. " +
                    "During this time, the Inthread platform may experience intermittent downtime.\n\n" +
                    "We're upgrading our infrastructure to improve performance and reliability. Your data will remain safe and secure.\n\n" +
                    "Thank you for your patience and continued support.\n\nSincerely,\nInthread Admin Team"),

                new StaticEmail("boss@company.com", "Project Deadline Reminder", "2025-05-16",
                    "Hi Harshita,\n\nJust a quick reminder that the final report for the Q2 marketing analysis is due today by **5:00 PM sharp**.\n\n" +
                    "Please ensure that:\n- All graphs and charts are updated\n- Feedback from the last meeting is incorporated\n- The report is uploaded to the shared drive\n\n" +
                    "Let me know if you're facing any blockers. Great work so far – we're almost there.\n\nRegards,\nMr. Sharma"),

                new StaticEmail("news@techdaily.com", "Tech Weekly Digest", "2025-05-15",
                    "Hello Tech Enthusiast,\n\nHere’s your curated digest of the top stories from this week:\n\n" +
                    "🧠 OpenAI unveils GPT-5 with improved contextual memory\n📱 Samsung confirms Galaxy Fold 6 launch for August\n🌐 India's 6G roadmap gets greenlight from TRAI\n\n" +
                    "Bonus Read: How quantum computing could reshape email encryption.\n\nStay curious!\n\n– The TechDaily Team"),

                new StaticEmail("alerts@bank.com", "Transaction Alert", "2025-05-14",
                    "Dear Customer,\n\nA transaction of ₹18,750.00 was made on your debit card ending with 1021 at Amazon India on 14-May-2025 at 10:32 AM.\n\n" +
                    "If this transaction is unauthorized, please call our 24/7 helpline at 1800-987-654 immediately.\n\nThank you for banking with us.\n\nSincerely,\nAxis Bank"),

                new StaticEmail("jane@friends.com", "Weekend Plans", "2025-05-13",
                    "Hey Harshita,\n\nLet’s finally catch up this Saturday! I’m thinking dinner at Olive Bistro at 7 PM. They’ve got great live music and amazing mocktails.\n\n" +
                    "Also, I wanted to show you my new sketchbook – been drawing again lately 😊.\n\nLet me know if that works!\n\nCheers,\nJane"),

                new StaticEmail("sales@onlinestore.com", "Your Order #12345", "2025-05-12",
                    "Hi Harshita,\n\nGood news! Your order #12345 has been shipped and is expected to be delivered by May 14th.\n\n" +
                    "**Items Shipped:**\n- Samsung Galaxy Buds2\n- Protective Carry Case\n\nTrack your shipment here: [Track My Order]\n\nThanks for shopping with us!\n\nTeam ShopNow"),

                new StaticEmail("support@inthread.com", "Support Ticket Closed", "2025-05-11",
                    "Hello Harshita,\n\nWe’re happy to inform you that your support ticket regarding Gmail login failure has been successfully resolved.\n\n" +
                    "**Resolution:** The issue was related to an outdated token. We’ve refreshed your OAuth credentials and verified your login.\n\n" +
                    "Please try accessing your inbox again. Let us know if you encounter any further problems.\n\nWarm regards,\nInthread Support"),

                new StaticEmail("invite@events.com", "You're Invited!", "2025-05-10",
                    "Dear Harshita,\n\nYou’re cordially invited to the **Annual Gala Night 2025** – a celebration of innovation, community, and excellence.\n\n" +
                    "**Date:** May 30, 2025\n**Time:** 6:00 PM onwards\n**Venue:** Taj Palace, Delhi\n\nThe evening will feature:\n- Award ceremonies\n- Live performances\n- Networking dinner\n\nDress Code: Formal\n\nRSVP by May 20th to confirm your presence.\n\nSee you there!\n– Events Team"),

                new StaticEmail("hr@company.com", "Policy Update", "2025-05-09",
                    "Hello Team,\n\nPlease take a moment to review the updated HR policies effective from June 1, 2025. Key updates include:\n\n" +
                    "1. Remote Work Flexibility – Up to 3 days/week\n2. New Reimbursement Policy for Internet & Equipment\n3. Revised Code of Conduct for Hybrid Meetings\n\nYou can view the full policy document on the intranet (HR > Policies).\n\nAcknowledgment is required by May 25th.\n\nRegards,\nHuman Resources")
            );

            int index = 0;
            for (StaticEmail mail : staticEmails) {
          %>
              <div class="email-item" style="border-bottom: 1px solid #ddd; padding: 10px; cursor: pointer;"
                   data-subject="<%= mail.subject.replace("\"", "") %>"
                   data-from="<%= mail.from.replace("\"", "") %>"
                   data-date="<%= mail.date %>"
                   data-body="<%= mail.body.replace("\"", "").replace("\n", "<br>") %>">
                <div class="email-from"><strong>From:</strong> <%= mail.from %></div>
                <div class="email-subject"><strong>Subject:</strong> <%= mail.subject %></div>
                <div class="email-date"><strong>Date:</strong> <%= mail.date %></div>
              </div>
          <%
              index++;
            }
          %>
        </div>
      <!-- Email View Panel -->
      <div class="email-view-panel" id="emailViewPanel">
        <div class="email-view-header">
          <h3 id="panelEmailSubject"></h3>
          <div class="email-view-actions">
            <button id="panelCloseBtn" title="Close"><i class="fas fa-times"></i></button>
          </div>
        </div>

        <div class="email-view-details">
          <p><strong>From:</strong> <span id="panelEmailFrom"></span></p>
          <p><strong>Date:</strong> <span id="panelEmailDate"></span></p>
        </div>

        <div class="email-view-body" id="panelEmailBody">
          <!-- Email content loads here -->
        </div>

       <div class="email-view-actions-bar">


         <button class="action-btn" id="translateBtn"><i class="fas fa-language"></i> Translate</button>
         <button class="action-btn" id="voiceBtn"><i class="fas fa-headphones"></i> AI Voice</button>

         <button class="action-btn" id="summaryBtn"><i class="fas fa-file-alt"></i> Summary</button>
         <button class="action-btn" id="trashBtn"><i class="fas fa-trash"></i> Trash</button>
       </div>
      <!-- Floating Summary Box -->
     <div id="floatingSummaryBox" style="
       position: absolute;
       right: 30px;
       top: 100px;
       width: 280px;
       background: #fff;
       border: 1px solid #ccc;
       box-shadow: 0 4px 8px rgba(0,0,0,0.1);
       padding: 15px;
       font-size: 14px;
       line-height: 1.4;
       font-style: italic;
       border-radius: 8px;
       z-index: 999;
       display: none;
     ">
       <button onclick="document.getElementById('floatingSummaryBox').style.display='none'"
         style="position: absolute; top: 5px; right: 8px; background: transparent; border: none; font-size: 16px; cursor: pointer;">
         &times;
       </button>
       <strong>📄 Summary</strong>
       <div id="summaryTextContent" style="margin-top: 10px;"></div>
     </div>



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