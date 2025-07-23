// Z+ Registration Page - JavaScript Functionality

// DOM Elements
const registerForm = document.getElementById('registerForm');
const photoInput = document.getElementById('profilePhoto');
const photoPreview = document.getElementById('photoPreview');
const passwordInput = document.getElementById('password');
const confirmPasswordInput = document.getElementById('confirmPassword');
const loadingOverlay = document.getElementById('loadingOverlay');
const successModal = document.getElementById('successModal');

// Initialize event listeners
document.addEventListener('DOMContentLoaded', function() {
    // Form submission
    registerForm.addEventListener('submit', handleRegistration);
    
    // Photo upload
    photoInput.addEventListener('change', handlePhotoUpload);
    
    // Password validation
    passwordInput.addEventListener('input', checkPasswordStrength);
    confirmPasswordInput.addEventListener('input', checkPasswordMatch);
    
    // Real-time form validation
    initializeFormValidation();
});

// Handle registration form submission
function handleRegistration(event) {
    event.preventDefault();
    
    // Get form data
    const formData = new FormData(registerForm);
    const registrationData = {
        firstName: formData.get('firstName'),
        lastName: formData.get('lastName'),
        email: formData.get('email'),
        phone: formData.get('phone'),
        department: formData.get('department'),
        requestedUserType: formData.get('requestedUserType'),
        requestedProjectId: formData.get('requestedProjectId'),
        password: formData.get('password'),
        confirmPassword: formData.get('confirmPassword'),
        reason: formData.get('reason'),
        supervisor: formData.get('supervisor'),
        agreeTerms: formData.get('agreeTerms'),
        agreeDataUse: formData.get('agreeDataUse'),
        agreePhoto: formData.get('agreePhoto'),
        profilePhoto: formData.get('profilePhoto')
    };
    
    // Validate form
    if (!validateRegistrationForm(registrationData)) {
        return;
    }
    
    // Show loading
    showLoading();
    
    // Simulate registration process
    setTimeout(() => {
        submitRegistration(registrationData);
    }, 3000);
}

// Validate registration form
function validateRegistrationForm(data) {
    let isValid = true;
    
    // Clear previous errors
    clearFormErrors();
    
    // Validate required fields
    const requiredFields = [
        { field: 'firstName', message: 'First name is required' },
        { field: 'lastName', message: 'Last name is required' },
        { field: 'email', message: 'Email is required' },
        { field: 'phone', message: 'Phone number is required' },
        { field: 'department', message: 'Please select a department' },
        { field: 'requestedUserType', message: 'Please select a user type' },
        { field: 'requestedProjectId', message: 'Project ID is required' },
        { field: 'password', message: 'Password is required' },
        { field: 'confirmPassword', message: 'Please confirm your password' },
        { field: 'reason', message: 'Please provide a reason for access' },
        { field: 'supervisor', message: 'Supervisor name is required' }
    ];
    
    requiredFields.forEach(({ field, message }) => {
        if (!data[field] || data[field].trim() === '') {
            showError(message);
            markFieldError(field);
            isValid = false;
        }
    });
    
    // Validate email format
    if (data.email && !isValidEmail(data.email)) {
        showError('Please enter a valid email address');
        markFieldError('email');
        isValid = false;
    }
    
    // Validate phone format
    if (data.phone && !isValidPhone(data.phone)) {
        showError('Please enter a valid phone number');
        markFieldError('phone');
        isValid = false;
    }
    
    // Validate project ID format
    if (data.requestedProjectId && !data.requestedProjectId.match(/^ZP-\d{4}-\d{3}$/)) {
        showError('Project ID must be in format: ZP-YYYY-XXX');
        markFieldError('requestedProjectId');
        isValid = false;
    }
    
    // Validate password strength
    if (data.password && !isStrongPassword(data.password)) {
        showError('Password must be at least 8 characters with uppercase, lowercase, number, and special character');
        markFieldError('password');
        isValid = false;
    }
    
    // Validate password match
    if (data.password !== data.confirmPassword) {
        showError('Passwords do not match');
        markFieldError('confirmPassword');
        isValid = false;
    }
    
    // Validate photo upload
    if (!photoInput.files || photoInput.files.length === 0) {
        showError('Please upload a profile photo');
        isValid = false;
    } else {
        const file = photoInput.files[0];
        if (!isValidImageFile(file)) {
            showError('Please upload a valid image file (JPG, PNG, max 2MB)');
            isValid = false;
        }
    }
    
    // Validate checkboxes
    if (!data.agreeTerms) {
        showError('Please agree to the Terms of Service and Privacy Policy');
        isValid = false;
    }
    
    if (!data.agreeDataUse) {
        showError('Please consent to data processing');
        isValid = false;
    }
    
    if (!data.agreePhoto) {
        showError('Please consent to photo usage');
        isValid = false;
    }
    
    return isValid;
}

// Handle photo upload
function handlePhotoUpload() {
    const file = photoInput.files[0];
    
    if (!file) {
        resetPhotoPreview();
        return;
    }
    
    // Validate file
    if (!isValidImageFile(file)) {
        showError('Please upload a valid image file (JPG, PNG, max 2MB)');
        resetPhotoPreview();
        return;
    }
    
    // Show preview
    const reader = new FileReader();
    reader.onload = function(e) {
        displayPhotoPreview(e.target.result, file.name);
    };
    reader.readAsDataURL(file);
}

// Display photo preview
function displayPhotoPreview(imageSrc, fileName) {
    photoPreview.innerHTML = `
        <img src="${imageSrc}" alt="Profile Preview">
        <span>Photo uploaded successfully</span>
        <small>${fileName}</small>
    `;
    photoPreview.classList.add('has-image');
}

// Reset photo preview
function resetPhotoPreview() {
    photoPreview.innerHTML = `
        <i class="fas fa-cloud-upload-alt"></i>
        <span>Click to upload your photo</span>
        <small>JPG, PNG, max 2MB</small>
    `;
    photoPreview.classList.remove('has-image');
}

// Check password strength
function checkPasswordStrength() {
    const password = passwordInput.value;
    const strengthIndicator = document.getElementById('passwordStrength');
    
    if (password.length === 0) {
        strengthIndicator.className = 'password-strength';
        return;
    }
    
    let strength = 0;
    
    // Check password criteria
    if (password.length >= 8) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;
    
    if (strength <= 2) {
        strengthIndicator.className = 'password-strength weak';
    } else if (strength <= 3) {
        strengthIndicator.className = 'password-strength medium';
    } else {
        strengthIndicator.className = 'password-strength strong';
    }
}

// Check password match
function checkPasswordMatch() {
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;
    const matchIndicator = document.getElementById('passwordMatch');
    
    if (confirmPassword.length === 0) {
        matchIndicator.textContent = '';
        matchIndicator.className = 'password-match';
        return;
    }
    
    if (password === confirmPassword) {
        matchIndicator.textContent = '✓ Passwords match';
        matchIndicator.className = 'password-match match';
    } else {
        matchIndicator.textContent = '✗ Passwords do not match';
        matchIndicator.className = 'password-match no-match';
    }
}

// Toggle password visibility
function togglePasswordVisibility(fieldId, iconId) {
    const passwordField = document.getElementById(fieldId);
    const toggleIcon = document.getElementById(iconId);
    
    if (passwordField.type === 'password') {
        passwordField.type = 'text';
        toggleIcon.className = 'fas fa-eye-slash';
    } else {
        passwordField.type = 'password';
        toggleIcon.className = 'fas fa-eye';
    }
}

// Submit registration
function submitRegistration(data) {
    // Generate reference ID
    const referenceId = 'REG' + Date.now().toString().slice(-6);
    
    // Store registration data (in real app, send to server)
    const registrationRequest = {
        ...data,
        referenceId: referenceId,
        submittedAt: new Date().toISOString(),
        status: 'pending'
    };
    
    // Store in localStorage for demo purposes
    const existingRegistrations = JSON.parse(localStorage.getItem('zplusRegistrations') || '[]');
    existingRegistrations.push(registrationRequest);
    localStorage.setItem('zplusRegistrations', JSON.stringify(existingRegistrations));
    
    // Hide loading
    hideLoading();
    
    // Show success modal
    showSuccessModal(referenceId);
}

// Show success modal
function showSuccessModal(referenceId) {
    const referenceIdElement = document.getElementById('referenceId');
    referenceIdElement.textContent = referenceId;
    successModal.classList.add('show');
}

// Redirect to login
function redirectToLogin() {
    window.location.href = 'index.html';
}

// Validation helper functions
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function isValidPhone(phone) {
    const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
    return phoneRegex.test(phone.replace(/[\s\-\(\)]/g, ''));
}

function isStrongPassword(password) {
    return password.length >= 8 &&
           /[a-z]/.test(password) &&
           /[A-Z]/.test(password) &&
           /[0-9]/.test(password) &&
           /[^A-Za-z0-9]/.test(password);
}

function isValidImageFile(file) {
    const validTypes = ['image/jpeg', 'image/jpg', 'image/png'];
    const maxSize = 2 * 1024 * 1024; // 2MB
    
    return validTypes.includes(file.type) && file.size <= maxSize;
}

// Initialize form validation
function initializeFormValidation() {
    const inputs = document.querySelectorAll('.form-input, .form-textarea');
    
    inputs.forEach(input => {
        input.addEventListener('blur', function() {
            validateField(this);
        });
        
        input.addEventListener('input', function() {
            if (this.classList.contains('error')) {
                validateField(this);
            }
        });
    });
}

// Validate individual field
function validateField(field) {
    const value = field.value.trim();
    const fieldName = field.name;
    
    // Remove error class
    field.classList.remove('error', 'success');
    
    if (value === '') {
        if (field.hasAttribute('required')) {
            field.classList.add('error');
        }
        return;
    }
    
    // Field-specific validation
    switch (fieldName) {
        case 'email':
            if (isValidEmail(value)) {
                field.classList.add('success');
            } else {
                field.classList.add('error');
            }
            break;
        case 'phone':
            if (isValidPhone(value)) {
                field.classList.add('success');
            } else {
                field.classList.add('error');
            }
            break;
        case 'requestedProjectId':
            if (value.match(/^ZP-\d{4}-\d{3}$/)) {
                field.classList.add('success');
            } else {
                field.classList.add('error');
            }
            break;
        case 'password':
            if (isStrongPassword(value)) {
                field.classList.add('success');
            } else {
                field.classList.add('error');
            }
            break;
        default:
            if (value.length >= 2) {
                field.classList.add('success');
            }
    }
}

// Utility functions
function showLoading() {
    loadingOverlay.classList.add('show');
}

function hideLoading() {
    loadingOverlay.classList.remove('show');
}

function showError(message) {
    removeMessages();
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message show';
    errorDiv.innerHTML = `<i class="fas fa-exclamation-triangle"></i> ${message}`;
    registerForm.insertBefore(errorDiv, registerForm.firstElementChild);
    
    setTimeout(() => {
        errorDiv.classList.remove('show');
        setTimeout(() => errorDiv.remove(), 300);
    }, 5000);
}

function showSuccess(message) {
    removeMessages();
    const successDiv = document.createElement('div');
    successDiv.className = 'success-message show';
    successDiv.innerHTML = `<i class="fas fa-check-circle"></i> ${message}`;
    registerForm.insertBefore(successDiv, registerForm.firstElementChild);
}

function removeMessages() {
    const messages = document.querySelectorAll('.error-message, .success-message');
    messages.forEach(msg => msg.remove());
}

function markFieldError(fieldId) {
    const field = document.getElementById(fieldId);
    if (field) {
        field.classList.add('error');
        
        setTimeout(() => {
            field.classList.remove('error');
        }, 3000);
    }
}

function clearFormErrors() {
    const errorFields = document.querySelectorAll('.form-input.error, .form-textarea.error');
    errorFields.forEach(field => field.classList.remove('error'));
    removeMessages();
}

// Auto-format project ID
document.getElementById('requestedProjectId').addEventListener('input', function() {
    let value = this.value.toUpperCase().replace(/[^A-Z0-9]/g, '');
    
    if (value.length <= 2) {
        value = 'ZP-' + value;
    } else if (value.length <= 6) {
        value = value.slice(0, 2) + '-' + value.slice(2);
    } else if (value.length <= 9) {
        value = value.slice(0, 2) + '-' + value.slice(2, 6) + '-' + value.slice(6);
    }
    
    this.value = value;
});

// Auto-format phone number
document.getElementById('phone').addEventListener('input', function() {
    let value = this.value.replace(/\D/g, '');
    
    if (value.length >= 10) {
        value = value.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2-$3');
    } else if (value.length >= 6) {
        value = value.replace(/(\d{3})(\d{3})/, '($1) $2-');
    } else if (value.length >= 3) {
        value = value.replace(/(\d{3})/, '($1) ');
    }
    
    this.value = value;
});

// Department suggestions based on user type
document.querySelectorAll('input[name="requestedUserType"]').forEach(radio => {
    radio.addEventListener('change', function() {
        const departmentSelect = document.getElementById('department');
        const userType = this.value;
        
        // Clear current selection
        departmentSelect.value = '';
        
        // Highlight relevant departments
        Array.from(departmentSelect.options).forEach(option => {
            option.style.fontWeight = 'normal';
            option.style.color = '';
        });
        
        if (userType === 'employee') {
            ['development', 'design', 'marketing', 'hr', 'finance'].forEach(dept => {
                const option = departmentSelect.querySelector(`option[value="${dept}"]`);
                if (option) {
                    option.style.fontWeight = 'bold';
                    option.style.color = '#e67e22';
                }
            });
        } else if (userType === 'client') {
            const option = departmentSelect.querySelector('option[value="client"]');
            if (option) {
                option.style.fontWeight = 'bold';
                option.style.color = '#e67e22';
            }
        }
    });
});

// Close modal when clicking outside
successModal.addEventListener('click', function(e) {
    if (e.target === this) {
        redirectToLogin();
    }
});

// Prevent form submission on Enter in specific fields
document.querySelectorAll('.form-input, .form-textarea').forEach(field => {
    field.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' && this.tagName !== 'TEXTAREA') {
            e.preventDefault();
            const nextField = getNextField(this);
            if (nextField) {
                nextField.focus();
            }
        }
    });
});

// Get next form field for tab navigation
function getNextField(currentField) {
    const fields = Array.from(document.querySelectorAll('.form-input, .form-textarea, input[type="radio"], input[type="checkbox"]'));
    const currentIndex = fields.indexOf(currentField);
    return fields[currentIndex + 1] || null;
}
