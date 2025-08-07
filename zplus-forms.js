// Registration form handler for Z+ Management System
// Add this to your main website HTML files

class RegistrationFormHandler {
    constructor() {
        this.API_BASE_URL = 'https://z-management-production.up.railway.app/api'; // Replace with your Railway URL
        this.initializeForm();
    }

    initializeForm() {
        const registrationForm = document.getElementById('registrationForm');
        if (registrationForm) {
            registrationForm.addEventListener('submit', (e) => this.handleRegistrationSubmit(e));
        }
    }

    async handleRegistrationSubmit(event) {
        event.preventDefault();
        
        const form = event.target;
        const formData = new FormData(form);
        
        // Create registration request object
        const registrationData = {
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            email: formData.get('email'),
            phone: formData.get('phone'),
            department: formData.get('department'),
            userType: formData.get('userType') || 'CLIENT',
            password: formData.get('password'),
            selfId: formData.get('selfId') || this.generateSelfId(),
            username: formData.get('username') || formData.get('email')
        };

        console.log('Submitting registration:', registrationData);

        try {
            // Show loading state
            this.showLoading(true);
            
            const response = await fetch(`${this.API_BASE_URL}/registrations`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(registrationData)
            });

            const result = await response.json();

            if (response.ok && result.success) {
                this.showSuccess('Registration submitted successfully! Your application is under review.');
                form.reset();
                
                // Optional: Redirect to a thank you page
                setTimeout(() => {
                    window.location.href = '/thank-you.html';
                }, 2000);
                
            } else {
                this.showError(result.message || 'Registration failed. Please try again.');
            }

        } catch (error) {
            console.error('Registration error:', error);
            this.showError('Network error. Please check your connection and try again.');
        } finally {
            this.showLoading(false);
        }
    }

    generateSelfId() {
        // Generate a unique self ID
        const timestamp = Date.now();
        const random = Math.floor(Math.random() * 1000);
        return `USR${timestamp}${random}`;
    }

    showLoading(show) {
        const submitBtn = document.querySelector('button[type="submit"]');
        if (submitBtn) {
            if (show) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Submitting...';
            } else {
                submitBtn.disabled = false;
                submitBtn.innerHTML = 'Submit Registration';
            }
        }
    }

    showSuccess(message) {
        this.showMessage(message, 'success');
    }

    showError(message) {
        this.showMessage(message, 'error');
    }

    showMessage(message, type) {
        // Remove existing messages
        const existingMessages = document.querySelectorAll('.form-message');
        existingMessages.forEach(msg => msg.remove());

        // Create new message element
        const messageDiv = document.createElement('div');
        messageDiv.className = `form-message ${type}`;
        messageDiv.innerHTML = `
            <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
            ${message}
        `;

        // Insert message before the form
        const form = document.getElementById('registrationForm');
        if (form) {
            form.parentNode.insertBefore(messageDiv, form);
            
            // Auto-hide after 5 seconds
            setTimeout(() => {
                messageDiv.remove();
            }, 5000);
        }
    }
}

// Contact form handler
class ContactFormHandler {
    constructor() {
        this.API_BASE_URL = 'https://z-management-production.up.railway.app/api'; // Replace with your Railway URL
        this.initializeForm();
    }

    initializeForm() {
        const contactForm = document.getElementById('contactForm');
        if (contactForm) {
            contactForm.addEventListener('submit', (e) => this.handleContactSubmit(e));
        }
    }

    async handleContactSubmit(event) {
        event.preventDefault();
        
        const form = event.target;
        const formData = new FormData(form);
        
        // Create contact request object
        const contactData = {
            fullName: formData.get('fullName'),
            organization: formData.get('organization') || '',
            email: formData.get('email'),
            phone: formData.get('phone'),
            country: formData.get('country'),
            state: formData.get('state'),
            city: formData.get('city'),
            subject: formData.get('subject'),
            message: formData.get('message'),
            serviceType: formData.get('serviceType') || 'GENERAL',
            priority: formData.get('priority') || 'MEDIUM'
        };

        console.log('Submitting contact inquiry:', contactData);

        try {
            this.showLoading(true);
            
            const response = await fetch(`${this.API_BASE_URL}/contact`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(contactData)
            });

            const result = await response.json();

            if (response.ok && result.success) {
                this.showSuccess('Thank you for your inquiry! We\'ll get back to you soon.');
                form.reset();
            } else {
                this.showError(result.message || 'Failed to submit inquiry. Please try again.');
            }

        } catch (error) {
            console.error('Contact form error:', error);
            this.showError('Network error. Please check your connection and try again.');
        } finally {
            this.showLoading(false);
        }
    }

    showLoading(show) {
        const submitBtn = document.querySelector('#contactForm button[type="submit"]');
        if (submitBtn) {
            if (show) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Sending...';
            } else {
                submitBtn.disabled = false;
                submitBtn.innerHTML = 'Send Message';
            }
        }
    }

    showSuccess(message) {
        this.showMessage(message, 'success');
    }

    showError(message) {
        this.showMessage(message, 'error');
    }

    showMessage(message, type) {
        const existingMessages = document.querySelectorAll('.form-message');
        existingMessages.forEach(msg => msg.remove());

        const messageDiv = document.createElement('div');
        messageDiv.className = `form-message ${type}`;
        messageDiv.innerHTML = `
            <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
            ${message}
        `;

        const form = document.getElementById('contactForm');
        if (form) {
            form.parentNode.insertBefore(messageDiv, form);
            setTimeout(() => messageDiv.remove(), 5000);
        }
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    new RegistrationFormHandler();
    new ContactFormHandler();
});

// CSS for form messages (add to your CSS file)
const messageStyles = `
.form-message {
    padding: 15px;
    margin: 15px 0;
    border-radius: 5px;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 10px;
}

.form-message.success {
    background-color: #d4edda;
    border: 1px solid #c3e6cb;
    color: #155724;
}

.form-message.error {
    background-color: #f8d7da;
    border: 1px solid #f5c6cb;
    color: #721c24;
}

.form-message i {
    font-size: 18px;
}
`;

// Add styles to head
const styleSheet = document.createElement('style');
styleSheet.textContent = messageStyles;
document.head.appendChild(styleSheet);
