document.addEventListener("DOMContentLoaded", function () {
    // DOM Elements
    const emailSubject = document.getElementById('emailSubject');
    const emailFrom = document.getElementById('emailFrom');
    const emailDate = document.getElementById('emailDate');
    const emailBody = document.getElementById('emailBody');
    const closeEmailView = document.getElementById('closeEmailView');

    // Get email data from query parameters
    const urlParams = new URLSearchParams(window.location.search);
    const subject = urlParams.get('subject') || 'No Subject';
    const from = urlParams.get('from') || 'Unknown Sender';
    const date = urlParams.get('date') || 'Unknown Date';
    const body = urlParams.get('body') || 'No content available.';

    // Populate email content
    emailSubject.textContent = decodeURIComponent(subject);
    emailFrom.textContent = decodeURIComponent(from);
    emailDate.textContent = decodeURIComponent(date);
    emailBody.innerHTML = decodeURIComponent(body);

    // Close button handler
    closeEmailView.addEventListener('click', function () {
        window.location.href = 'inbox';
    });

    // Keyboard shortcut to close
    document.addEventListener('keydown', function (e) {
        if (e.key === "Escape") {
            window.location.href = 'inbox';
        }
    });
});