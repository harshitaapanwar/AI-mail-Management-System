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
        item.addEventListener('click', () => {
            const subject = item.dataset.subject;
            const from = item.dataset.from;
            const date = item.dataset.date;
            const body = item.dataset.body;

            // Clear previous selection
            document.querySelectorAll('.email-item').forEach(el => el.classList.remove('active'));
            item.classList.add('active');

            // Redirect to emailView.jsp with query parameters
            const url = `emailView.jsp?subject=${encodeURIComponent(subject)}&from=${encodeURIComponent(from)}&date=${encodeURIComponent(date)}&body=${encodeURIComponent(body)}`;
            window.location.href = url;
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