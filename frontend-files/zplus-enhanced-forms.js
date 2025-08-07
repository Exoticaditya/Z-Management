/**
 * Z+ Management System - Enhanced Form Handlers
 * Place this file in your GoDaddy hosting and include it in your HTML files
 * File: zplus-enhanced-forms.js
 */

class ZPlusFormsManager {
    constructor() {
        // Replace with your actual Railway backend URL
        this.API_BASE_URL = 'https://z-management-production.up.railway.app/api';
        
        // Initialize when DOM is ready
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.init());
        } else {
            this.init();
        }
    }

    init() {
        console.log('🚀 Z+ Forms Manager initialized');
        this.initializeRegistrationForm();
        this.initializeContactForm();
        this.addFormStyles();
    }

    // =================== REGISTRATION FORM ===================
    initializeRegistrationForm() {
        const forms = document.querySelectorAll('#registrationForm, .registration-form, form[data-type="registration"]');
        forms.forEach(form => {
            form.addEventListener('submit', (e) => this.handleRegistrationSubmit(e));
        });
    }

    async handleRegistrationSubmit(event) {
        event.preventDefault();
        console.log('📝 Processing registration submission...');
        
        const form = event.target;
        const formData = new FormData(form);
        
        // Create registration object
        const registrationData = {
            firstName: formData.get('firstName') || formData.get('first_name') || '',
            lastName: formData.get('lastName') || formData.get('last_name') || '',
            email: formData.get('email') || '',
            phone: formData.get('phone') || formData.get('mobile') || '',
            department: formData.get('department') || 'General',
            userType: formData.get('userType') || formData.get('user_type') || 'CLIENT',
            password: formData.get('password') || this.generateTempPassword(),
            selfId: formData.get('selfId') || this.generateSelfId(),
            username: formData.get('username') || formData.get('email') || this.generateUsername(formData.get('firstName'), formData.get('lastName'))
        };

        // Validate required fields
        if (!this.validateRegistrationData(registrationData)) {
            return;
        }

        console.log('Submitting registration:', {
            ...registrationData,
            password: '[HIDDEN]'
        });

        try {
            this.showLoading(form, true, 'Submitting Registration...');
            
            const response = await fetch(`${this.API_BASE_URL}/registrations`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    'Access-Control-Allow-Origin': '*'
                },
                mode: 'cors',
                body: JSON.stringify(registrationData)
            });

            const result = await response.json();
            console.log('Registration response:', result);

            if (response.ok && result.success) {
                this.showSuccess(form, 
                    `🎉 Registration submitted successfully! 
                    <br><strong>Application ID:</strong> ${result.registrationId}
                    <br><strong>Status:</strong> Under Review
                    <br>You will be notified via email once reviewed.`
                );
                form.reset();
                
                // Optional: Store registration ID for user reference
                localStorage.setItem('lastRegistrationId', result.registrationId);
                
            } else {
                this.showError(form, result.message || 'Registration failed. Please try again.');
            }

        } catch (error) {
            console.error('Registration error:', error);
            this.showError(form, 'Network error. Please check your connection and try again.');
        } finally {
            this.showLoading(form, false);
        }
    }

    // =================== CONTACT FORM ===================
    initializeContactForm() {
        const forms = document.querySelectorAll('#contactForm, .contact-form, form[data-type="contact"]');
        forms.forEach(form => {
            form.addEventListener('submit', (e) => this.handleContactSubmit(e));
        });
    }

    async handleContactSubmit(event) {
        event.preventDefault();
        console.log('📧 Processing contact submission...');
        
        const form = event.target;
        const formData = new FormData(form);
        
        // Create contact object
        const contactData = {
            fullName: formData.get('fullName') || formData.get('name') || '',
            organization: formData.get('organization') || formData.get('company') || '',
            email: formData.get('email') || '',
            phone: formData.get('phone') || formData.get('mobile') || '',
            country: formData.get('country') || 'India',
            state: formData.get('state') || '',
            city: formData.get('city') || '',
            subject: formData.get('subject') || 'General Inquiry',
            message: formData.get('message') || '',
            serviceType: formData.get('serviceType') || 'GENERAL',
            priority: formData.get('priority') || 'MEDIUM'
        };

        // Validate required fields
        if (!this.validateContactData(contactData)) {
            return;
        }

        console.log('Submitting contact inquiry:', contactData);

        try {
            this.showLoading(form, true, 'Sending Message...');
            
            const response = await fetch(`${this.API_BASE_URL}/contact`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    'Access-Control-Allow-Origin': '*'
                },
                mode: 'cors',
                body: JSON.stringify(contactData)
            });

            const result = await response.json();
            console.log('Contact response:', result);

            if (response.ok && result.success) {
                this.showSuccess(form, 
                    `✅ Thank you for your inquiry! 
                    <br><strong>Inquiry ID:</strong> ${result.inquiryId}
                    <br>We'll get back to you within 24 hours.`
                );
                form.reset();
            } else {
                this.showError(form, result.message || 'Failed to submit inquiry. Please try again.');
            }

        } catch (error) {
            console.error('Contact form error:', error);
            this.showError(form, 'Network error. Please check your connection and try again.');
        } finally {
            this.showLoading(form, false);
        }
    }

    // =================== VALIDATION METHODS ===================
    validateRegistrationData(data) {
        if (!data.firstName.trim()) {
            this.showError(null, 'First name is required');
            return false;
        }
        if (!data.lastName.trim()) {
            this.showError(null, 'Last name is required');
            return false;
        }
        if (!data.email.trim() || !this.isValidEmail(data.email)) {
            this.showError(null, 'Valid email is required');
            return false;
        }
        if (!data.phone.trim()) {
            this.showError(null, 'Phone number is required');
            return false;
        }
        return true;
    }

    validateContactData(data) {
        if (!data.fullName.trim()) {
            this.showError(null, 'Full name is required');
            return false;
        }
        if (!data.email.trim() || !this.isValidEmail(data.email)) {
            this.showError(null, 'Valid email is required');
            return false;
        }
        if (!data.message.trim()) {
            this.showError(null, 'Message is required');
            return false;
        }
        return true;
    }

    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    // =================== UTILITY METHODS ===================
    generateSelfId() {
        const timestamp = Date.now();
        const random = Math.floor(Math.random() * 1000);
        return `USR${timestamp}${random}`;
    }

    generateUsername(firstName, lastName) {
        if (!firstName || !lastName) return '';
        return `${firstName.toLowerCase()}.${lastName.toLowerCase()}`;
    }

    generateTempPassword() {
        return Math.random().toString(36).slice(-8);
    }

    // =================== UI FEEDBACK METHODS ===================
    showLoading(form, show, message = 'Processing...') {
        const submitBtn = form ? form.querySelector('button[type="submit"]') : null;
        if (submitBtn) {
            if (show) {
                submitBtn.disabled = true;
                submitBtn.dataset.originalText = submitBtn.innerHTML;
                submitBtn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> ${message}`;
            } else {
                submitBtn.disabled = false;
                submitBtn.innerHTML = submitBtn.dataset.originalText || 'Submit';
            }
        }
    }

    showSuccess(form, message) {
        this.showMessage(form, message, 'success');
    }

    showError(form, message) {
        this.showMessage(form, message, 'error');
    }

    showMessage(form, message, type) {
        // Remove existing messages
        const existingMessages = document.querySelectorAll('.zplus-form-message');
        existingMessages.forEach(msg => msg.remove());

        // Create new message element
        const messageDiv = document.createElement('div');
        messageDiv.className = `zplus-form-message zplus-form-message--${type}`;
        messageDiv.innerHTML = `
            <div class="zplus-form-message__content">
                <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
                <div class="zplus-form-message__text">${message}</div>
            </div>
            <button class="zplus-form-message__close" onclick="this.parentElement.remove()">
                <i class="fas fa-times"></i>
            </button>
        `;

        // Insert message
        if (form) {
            form.parentNode.insertBefore(messageDiv, form);
        } else {
            document.body.appendChild(messageDiv);
        }
        
        // Auto-hide success messages after 8 seconds
        if (type === 'success') {
            setTimeout(() => {
                if (messageDiv.parentNode) {
                    messageDiv.remove();
                }
            }, 8000);
        }

        // Scroll to message
        messageDiv.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }

    addFormStyles() {
        if (document.getElementById('zplus-form-styles')) return;

        const styles = `
        .zplus-form-message {
            margin: 15px 0;
            padding: 15px;
            border-radius: 8px;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            position: relative;
            animation: zplus-slideIn 0.3s ease-out;
        }

        .zplus-form-message--success {
            background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
            border: 1px solid #c3e6cb;
            color: #155724;
        }

        .zplus-form-message--error {
            background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
            border: 1px solid #f5c6cb;
            color: #721c24;
        }

        .zplus-form-message__content {
            display: flex;
            align-items: flex-start;
            gap: 12px;
        }

        .zplus-form-message__content i {
            font-size: 20px;
            margin-top: 2px;
            flex-shrink: 0;
        }

        .zplus-form-message__text {
            flex: 1;
            line-height: 1.5;
        }

        .zplus-form-message__close {
            position: absolute;
            top: 10px;
            right: 10px;
            background: none;
            border: none;
            font-size: 16px;
            cursor: pointer;
            opacity: 0.7;
            transition: opacity 0.2s;
        }

        .zplus-form-message__close:hover {
            opacity: 1;
        }

        @keyframes zplus-slideIn {
            from { opacity: 0; transform: translateY(-10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .zplus-form-loading {
            pointer-events: none;
            opacity: 0.7;
        }
        `;

        const styleSheet = document.createElement('style');
        styleSheet.id = 'zplus-form-styles';
        styleSheet.textContent = styles;
        document.head.appendChild(styleSheet);
    }
}

// Initialize the forms manager
new ZPlusFormsManager();

console.log('✅ Z+ Management Forms System Loaded Successfully');
