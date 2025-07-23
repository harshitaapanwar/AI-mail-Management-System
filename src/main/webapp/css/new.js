document.addEventListener("DOMContentLoaded", function () {
    // DOM Elements
    const composeBtn = document.getElementById('composeBtn');
    const composeModal = document.getElementById('composeModal');
    const closeCompose = document.getElementById('closeCompose');
    const discardCompose = document.getElementById('discardCompose');
    const composeForm = document.getElementById('composeForm');
    const composeEditor = document.getElementById('composeEditor');
    const emailBodyInput = document.getElementById('emailBody');
    const csrfTokenInput = document.querySelector('input[name="csrfToken"]');

document.getElementById('translateBtn')?.addEventListener('click', async () => {
    // Get the email body as plain text (strips HTML)
    const htmlContent = document.getElementById("panelEmailBody").innerHTML;
    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = htmlContent;
    const originalText = tempDiv.textContent || tempDiv.innerText || "";

    const targetLang = prompt("Enter target language code (e.g. en, es, fr, hi):");

    if (!targetLang) return;

    try {
        const response = await fetch('translate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                text: originalText,
                targetLang: targetLang
            })
        });

        const text = await response.text();
        console.log("Raw response:", text);

        let result;
        try {
            result = JSON.parse(text);
        } catch (err) {
            alert("Invalid response from server. Raw response: " + text);
            return;
        }

        if (result.translatedText) {
            document.getElementById("panelEmailBody").innerHTML =
                `<p><em>(Translated to ${targetLang})</em></p><p>${result.translatedText.replace(/\n/g, "<br>")}</p>`;
        } else {
            alert("Translation failed: " + (result.error || "Unknown error"));
        }

    } catch (error) {
        alert("Error: " + error.message);
    }
});



  document.getElementById('voiceBtn')?.addEventListener('click', () => {
      const subject = document.getElementById("panelEmailSubject")?.textContent;
      const from = document.getElementById("panelEmailFrom")?.textContent;
      const bodyHtml = document.getElementById("panelEmailBody")?.innerHTML;

      // Convert HTML body to plain text
      const bodyText = bodyHtml?.replace(/<[^>]*>/g, ' ').replace(/\s+/g, ' ').trim();

      const textToRead = `From: ${from}. Subject: ${subject}. Body: ${bodyText}`;

      if ('speechSynthesis' in window) {
          const utterance = new SpeechSynthesisUtterance(textToRead);
          utterance.lang = 'en-US'; // You can change language here
          speechSynthesis.speak(utterance);
      } else {
          alert("Sorry, your browser doesn't support text-to-speech.");
      }
  });


 document.getElementById('summaryBtn')?.addEventListener('click', () => {
        const emailBodyElement = document.getElementById("panelEmailBody");
        const summaryBox = document.getElementById("floatingSummaryBox");
        const summaryText = document.getElementById("summaryTextContent");

        if (!emailBodyElement || !summaryBox || !summaryText) return;

        const fullText = emailBodyElement.innerText || emailBodyElement.textContent;
        const summary = generateSummary(fullText);

        summaryText.textContent = summary;
        summaryBox.style.display = "block";
    });

    // 👉 Hide summary box when switching emails
    document.querySelectorAll('.email-item').forEach(item => {
        item.addEventListener('click', (event) => {
            event.preventDefault();
            event.stopPropagation();

            const subject = item.dataset.subject;
            const from = item.dataset.from;
            const date = item.dataset.date;
            const body = item.dataset.body;

            document.querySelectorAll('.email-item').forEach(el => el.classList.remove('active'));
            item.classList.add('active');

            document.getElementById("panelEmailSubject").textContent = subject;
            document.getElementById("panelEmailFrom").textContent = from;
            document.getElementById("panelEmailDate").textContent = date;
            document.getElementById("panelEmailBody").innerHTML = body;

            document.getElementById("emailViewPanel").classList.add("active");

            // 👇 Hide summary box when opening a new email
            const floatingSummary = document.getElementById("floatingSummaryBox");
            if (floatingSummary) {
                floatingSummary.style.display = "none";
            }

            const panelCloseBtn = document.getElementById('panelCloseBtn');
            const emailViewPanel = document.getElementById('emailViewPanel');
            if (panelCloseBtn && emailViewPanel) {
                panelCloseBtn.addEventListener('click', () => {
                    emailViewPanel.classList.remove('active');
                });
            }
        });
    });

    // 👉 Summary Generator Function
    function generateSummary(text) {
        if (!text) return "No content available to summarize.";

        const sentences = text.match(/[^.!?]+[.!?]+/g) || [text];
        if (sentences.length <= 2) return text.trim();

        return sentences.slice(0, 2).join(" ").trim();
    }


    document.getElementById('trashBtn')?.addEventListener('click', () => {
        if (confirm("Are you sure you want to move this email to trash?")) {
            alert("Email moved to trash.");
            document.getElementById("emailViewPanel").classList.remove("active");
        }
    });


    // Show alert helper
    function showAlert(message, type = 'info') {
        const alertBox = document.createElement('div');
        alertBox.className = `alert ${type}`;
        alertBox.innerHTML = `
            <span>${message}</span>
            <button class="alert-close">×</button>
        `;
        document.body.appendChild(alertBox);
        setTimeout(() => alertBox.classList.add('fade-out'), 4800);
        setTimeout(() => alertBox.remove(), 5200);
        alertBox.querySelector('.alert-close').addEventListener('click', () => alertBox.remove());
    }

    // Open Compose Modal
    function openComposeModal() {
        composeModal.style.display = 'flex';
        composeModal.classList.add('active');
        composeModal.setAttribute('aria-hidden', 'false');
        composeEditor.focus();
        // Verify CSRF token input
        if (csrfTokenInput && !csrfTokenInput.value) {
            console.warn("CSRF token input is empty on modal open");
            showAlert("Session may have expired. Please refresh the page.", 'error');
        }
    }

    // Close Compose Modal
    function closeComposeModal() {
        composeModal.style.display = 'none';
        composeModal.classList.remove('active');
        composeModal.setAttribute('aria-hidden', 'true');
        composeForm.reset();
        composeEditor.innerHTML = '';
        if (emailBodyInput) emailBodyInput.value = '';
    }

    // Event Listeners
    if (composeBtn) composeBtn.addEventListener('click', openComposeModal);
    if (closeCompose) closeCompose.addEventListener('click', closeComposeModal);

    if (discardCompose) {
        discardCompose.addEventListener('click', () => {
            if (composeEditor.innerHTML.trim() !== '') {
                if (confirm("Discard this draft?")) {
                    closeComposeModal();
                }
            } else {
                closeComposeModal();
            }
        });
    }

    // Add click listeners to email items to redirect to emailView.jsp
   document.querySelectorAll('.email-item').forEach(item => {
       item.addEventListener('click', (event) => {
           event.preventDefault();  // Prevent unwanted navigation
           event.stopPropagation(); // Just to be safe

           const subject = item.dataset.subject;
           const from = item.dataset.from;
           const date = item.dataset.date;
           const body = item.dataset.body;

           document.querySelectorAll('.email-item').forEach(el => el.classList.remove('active'));
           item.classList.add('active');

           document.getElementById("panelEmailSubject").textContent = subject;
           document.getElementById("panelEmailFrom").textContent = from;
           document.getElementById("panelEmailDate").textContent = date;
           document.getElementById("panelEmailBody").innerHTML = body;

           document.getElementById("emailViewPanel").classList.add("active");
           // Handle closing of email view panel
           const panelCloseBtn = document.getElementById('panelCloseBtn');
           const emailViewPanel = document.getElementById('emailViewPanel');

           if (panelCloseBtn && emailViewPanel) {
               panelCloseBtn.addEventListener('click', () => {
                   emailViewPanel.classList.remove('active'); // or .add('hidden') based on your CSS logic
               });
           }


       });

    });

    // Form submission handler
    if (composeForm) {
        composeForm.addEventListener('submit', async function (e) {
            e.preventDefault();

            const to = composeForm.querySelector('input[name="to"]').value.trim();
            const subject = composeForm.querySelector('input[name="subject"]').value.trim();
            const body = composeEditor.innerHTML.trim();
            const csrf = csrfTokenInput ? csrfTokenInput.value.trim() : "";

            // Log form data and CSRF token for debugging
            console.debug("Form submission: to=", to, "subject=", subject, "body=", body, "csrf=", csrf);
            if (!csrf) {
                console.warn("CSRF token is empty or missing in form");
                showAlert("CSRF token is missing. Please refresh the page or re-authenticate.", 'error');
                return;
            }

            if (!to || !subject || !body) {
                console.warn("Form fields incomplete: to=", to, "subject=", subject, "body=", body);
                showAlert("Please fill in all fields.", 'error');
                return;
            }

            // Populate the hidden emailBody input
            if (emailBodyInput) {
                emailBodyInput.value = body;
            } else {
                console.warn("emailBody input not found");
                showAlert("Form configuration error. Please refresh the page.", 'error');
                return;
            }

            // Ensure CSRF token is set
            if (csrfTokenInput) {
                csrfTokenInput.value = csrf;
            }

            // Create FormData from form
            const formData = new FormData(composeForm);

            // Log FormData entries for debugging
            for (let [key, value] of formData.entries()) {
                console.debug(`FormData: ${key}=${value}`);
            }

            const submitBtn = composeForm.querySelector('button[type="submit"]');
            const originalText = submitBtn.textContent;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Sending...';

            try {
                const response = await fetch('send', {
                    method: 'POST',
                    credentials: 'include',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: new URLSearchParams(formData).toString()
                });

                const result = await response.json();

                if (response.ok) {
                    showAlert(result.success || "Email sent successfully!", 'success');
                    closeComposeModal();
                    if (window.location.pathname.includes("inbox")) {
                        window.location.reload();
                    }
                } else {
                    console.error("Server error:", result.error);
                    showAlert(result.error || "Failed to send email", 'error');
                }
            } catch (err) {
                console.error("Network error:", err);
                showAlert("Network error: " + err.message, 'error');
            } finally {
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            }
        });
    }

    // Keyboard Shortcuts
    document.addEventListener('keydown', function (e) {
        if (composeModal.style.display === 'flex') {
            if (e.key === "Escape") closeComposeModal();
            if (e.ctrlKey && e.key === "Enter") composeForm.requestSubmit();
        }
    });

    // Rich Text Shortcuts
    composeEditor.addEventListener('keydown', function (e) {
        if (e.ctrlKey) {
            switch (e.key.toLowerCase()) {
                case 'b':
                    document.execCommand('bold'); e.preventDefault(); break;
                case 'i':
                    document.execCommand('italic'); e.preventDefault(); break;
                case 'u':
                    document.execCommand('underline'); e.preventDefault(); break;
            }
        }
    });
});