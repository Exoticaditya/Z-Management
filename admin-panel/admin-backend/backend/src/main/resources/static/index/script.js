// --- Element Selectors ---
// Initialize DOM elements
const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container');
const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');

// Password toggle function
function togglePasswordVisibility() {
    const passwordInput = document.getElementById('passwordInput');
    const toggleIcon = document.getElementById('passwordToggleIcon');
    
    if (!passwordInput || !toggleIcon) {
        console.error('Password input or toggle icon not found');
        return;
    }
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleIcon.classList.remove('fa-eye');
        toggleIcon.classList.add('fa-eye-slash');
    } else {
        passwordInput.type = 'password';
        toggleIcon.classList.remove('fa-eye-slash');
        toggleIcon.classList.add('fa-eye');
    }
}

// --- Event Listeners for Panel Sliding ---
console.log('[PANEL DEBUG] signUpButton:', signUpButton);
console.log('[PANEL DEBUG] signInButton:', signInButton);
console.log('[PANEL DEBUG] container:', container);

if (signUpButton && signInButton && container) {
    signUpButton.addEventListener('click', () => {
        console.log('[PANEL DEBUG] Sign Up button clicked - adding right-panel-active class');
        container.classList.add("right-panel-active");
        console.log('[PANEL DEBUG] Container classes after adding:', container.className);
    });
    signInButton.addEventListener('click', () => {
        console.log('[PANEL DEBUG] Sign In button clicked - removing right-panel-active class');
        container.classList.remove("right-panel-active");
        console.log('[PANEL DEBUG] Container classes after removing:', container.className);
    });
} else {
    console.error('[PANEL DEBUG] Missing elements:', { 
        hasSignUpButton: !!signUpButton, 
        hasSignInButton: !!signInButton, 
        hasContainer: !!container 
    });
}

// --- Form Submission Logic ---
if (loginForm) loginForm.addEventListener('submit', handleLogin);
if (registerForm) registerForm.addEventListener('submit', handleRegister);

/**
 * Handles the login form submission.
 */
async function handleLogin(event) {
    console.log('[DEBUG] Login form submission started');
    event.preventDefault();
    
    const selfIdElement = loginForm.querySelector('input[name="selfId"]');
    const passwordElement = loginForm.querySelector('input[name="password"]');
    const userTypeElement = loginForm.querySelector('input[name="userType"]:checked');
    
    const selfId = selfIdElement ? selfIdElement.value.trim() : '';
    const password = passwordElement ? passwordElement.value.trim() : '';
    const userType = userTypeElement ? userTypeElement.value : 'ADMIN';
    
    console.log('[DEBUG] Form values:', { selfId, password, userType });
    console.log('[DEBUG] selfId length:', selfId.length);
    console.log('[DEBUG] password length:', password.length);
    console.log('[DEBUG] userType:', userType);

    if (!selfId || !password) {
        console.log('[DEBUG] Validation failed - empty fields');
        showError('loginErrorMessage', 'All fields are required.');
        return false;
    }

    setLoadingState(true, 'login');
    console.log('[DEBUG] Making API request to /api/users/login');
    
    try {
        // Create login request data
        const loginData = {
            selfId: selfId,
            password: password,
            userType: userType
        };
        
        console.log('[DEBUG] Sending login request with data:', { selfId, userType });
        
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(loginData)
        });
        
        console.log('[DEBUG] Raw response:', response);
        
        if (!response.ok) {
            let errorMessage = 'Login failed. Please check your credentials.';
            
            switch (response.status) {
                case 403:
                    errorMessage = 'Authentication failed. Please try logging in again.';
                    console.log('[DEBUG] Authentication failed (403)');
                    break;
                case 401:
                    errorMessage = 'Invalid username or password.';
                    console.log('[DEBUG] Invalid credentials (401)');
                    break;
                case 400:
                    errorMessage = 'Invalid request. Please check your input.';
                    console.log('[DEBUG] Bad request (400)');
                    break;
                case 500:
                    errorMessage = 'Server error. Please try again later.';
                    console.log('[DEBUG] Server error (500)');
                    break;
            }
            
            try {
                const contentType = response.headers.get('content-type');
                if (contentType && contentType.includes('application/json')) {
                    const errorData = await response.json();
                    if (errorData.message) {
                        errorMessage = errorData.message;
                        console.log('[DEBUG] Server error message:', errorData.message);
                    }
                } else {
                    console.log('[DEBUG] Non-JSON error response');
                    const text = await response.text();
                    console.log('[DEBUG] Error response text:', text || '(empty response)');
                }
            } catch (e) {
                console.log('[DEBUG] Could not parse error response:', e);
            }
            
            throw new Error(errorMessage);
        }

        const data = await response.json();
        console.log('[DEBUG] Parsed response:', data);

        // Store the token separately (this is what admin-script.js is looking for)
        if (data.token) {
            localStorage.setItem('token', data.token);
            console.log('[DEBUG] Stored token separately:', data.token);
        }

        // Store user data with type - let's be more explicit about the structure
        const userData = {
            selfId: data.selfId || selfId,
            name: data.name || `${data.firstName || ''} ${data.lastName || ''}`.trim() || 'System Administrator',
            token: data.token,
            userType: userType || data.userType || 'ADMIN',
            success: data.success,
            message: data.message,
            // Include any other fields from the response
            ...data
        };

        console.log('[DEBUG] Storing user data with structure:', userData);
        localStorage.setItem('zplusUser', JSON.stringify(userData));
        
        // Verify data was stored correctly
        const storedToken = localStorage.getItem('token');
        const storedUser = localStorage.getItem('zplusUser');
        console.log('[DEBUG] Stored token:', storedToken);
        console.log('[DEBUG] Stored user:', storedUser);
        
        // Parse and verify user data
        try {
            const parsedUser = JSON.parse(storedUser);
            console.log('[DEBUG] Parsed user object:', parsedUser);
            console.log('[DEBUG] User type from parsed object:', parsedUser.userType);
        } catch (e) {
            console.error('[DEBUG] Failed to parse stored user data:', e);
        }
        
        // Get the effective user type (either from form or server response)
        const effectiveUserType = userData.userType;
        console.log('[DEBUG] Attempting redirect to dashboard for userType:', effectiveUserType);
        
        const dashboardUrls = {
            'ADMIN': '/admin/index.html',
            'EMPLOYEE': '/employee-dashboard/index.html',
            'CLIENT': '/client/client-dashboard.html'
        };
        
        const path = dashboardUrls[effectiveUserType];
        if (path) {
            // Verify we have both token and user data before redirecting
            const token = localStorage.getItem('token');
            const user = localStorage.getItem('zplusUser');
            
            if (!token || !user) {
                console.error('[DEBUG] Missing auth data before redirect:', { hasToken: !!token, hasUser: !!user });
                showError('loginErrorMessage', 'Authentication data not properly set. Please try logging in again.');
                return false;
            }

            // Add transition effect before redirect
            document.body.style.opacity = '0';
            document.body.style.transition = 'opacity 0.5s ease';
            
            // Add a delay for the fade-out effect and ensure storage operations complete
            setTimeout(() => {
                const redirectUrl = window.location.origin + path;
                console.log('[DEBUG] Redirecting to:', redirectUrl);
                
                // Store the animation preference for the target page
                sessionStorage.setItem('animateEntry', 'true');
                
                window.location.href = redirectUrl;
            }, 500);
        } else {
            console.error('[DEBUG] Invalid user type:', effectiveUserType);
            showError('loginErrorMessage', 'Invalid user type or access denied');
        }
        return false;
        
    } catch (error) {
        console.error('[DEBUG] Login error:', error);
        showError('loginErrorMessage', error.message);
        return false;
    } finally {
        console.log('[DEBUG] Login flow completed');
        setLoadingState(false, 'login');
    }
}

/**
 * Handles the registration form submission.
 */
async function handleRegister(event) {
    console.log('[REGISTER DEBUG] Registration form submission started');
    event.preventDefault();
    
    const firstName = registerForm.querySelector('input[name="firstName"]').value;
    const lastName = registerForm.querySelector('input[name="lastName"]').value;
    const email = registerForm.querySelector('input[name="email"]').value;
    const phone = registerForm.querySelector('input[name="phone"]').value;
    const department = registerForm.querySelector('select[name="department"]').value;
    const selfId = registerForm.querySelector('input[name="selfId"]').value;
    const username = registerForm.querySelector('input[name="username"]').value;
    const password = registerForm.querySelector('input[name="password"]').value;
    const userType = registerForm.querySelector('input[name="userType"]:checked').value;

    console.log('[REGISTER DEBUG] Form data:', { firstName, lastName, email, phone, department, selfId, username, userType });

    // Validate Self ID format (must start with ZP followed by at least 3 digits)
    const selfIdPattern = /^ZP[0-9]{3,}$/;
    if (!selfIdPattern.test(selfId)) {
        console.log('[REGISTER DEBUG] Invalid Self ID format');
        showError('registerErrorMessage', 'Self ID must start with "ZP" followed by at least 3 digits (e.g., ZP001).');
        return;
    }

    if (!firstName || !lastName || !email || !phone || !department || !selfId || !username || !password) {
        console.log('[REGISTER DEBUG] Validation failed - missing fields');
        showError('registerErrorMessage', 'All fields are required.');
        return;
    }
    
    setLoadingState(true, 'register');
    console.log('[REGISTER DEBUG] Making API request to /api/auth/register');

    try {
        const registrationData = { firstName, lastName, email, phone, department, selfId, username, password, userType };
        console.log('[REGISTER DEBUG] Sending registration data:', registrationData);
        
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(registrationData)
        });

        console.log('[REGISTER DEBUG] Response status:', response.status);
        console.log('[REGISTER DEBUG] Response headers:', response.headers);

        if (!response.ok) {
            console.log('[REGISTER DEBUG] Response not OK, trying to parse error');
            let errorMessage = 'Registration failed.';
            
            try {
                const contentType = response.headers.get('content-type');
                if (contentType && contentType.includes('application/json')) {
                    const errorData = await response.json();
                    console.log('[REGISTER DEBUG] Error data:', errorData);
                    errorMessage = errorData.message || errorMessage;
                } else {
                    const errorText = await response.text();
                    console.log('[REGISTER DEBUG] Error text:', errorText);
                    errorMessage = errorText || errorMessage;
                }
            } catch (parseError) {
                console.log('[REGISTER DEBUG] Failed to parse error response:', parseError);
            }
            
            throw new Error(errorMessage);
        }
        
        const data = await response.json();
        console.log('[REGISTER DEBUG] Registration successful:', data);
        
        showSuccess('registerSuccessMessage', 'Registration successful! Please sign in.');
        registerForm.reset();
        
        setTimeout(() => {
             container.classList.remove("right-panel-active");
        }, 2000);

    } catch (error) {
        console.error('[REGISTER DEBUG] Registration error:', error);
        showError('registerErrorMessage', error.message);
    } finally {
        console.log('[REGISTER DEBUG] Registration flow completed');
        setLoadingState(false, 'register');
    }
}

/**
 * Redirects the user to the correct dashboard after successful login.
 */
function redirectToDashboard(userType) {
    console.log('[DEBUG] redirectToDashboard called with userType:', userType);
    
    const baseUrl = window.location.origin;
    const dashboardPaths = {
        'ADMIN': '/admin/index.html',
        'EMPLOYEE': '/employee-dashboard/index.html',
        'CLIENT': '/client/client-dashboard.html'
    };
    
    const path = dashboardPaths[userType];
    console.log('[DEBUG] Resolved path for userType:', path);
    
    if (path) {
        const fullUrl = `${baseUrl}${path}`;
        console.log('[DEBUG] Redirecting to:', fullUrl);
        
        // Add a small delay to ensure localStorage operations complete
        setTimeout(() => {
            window.location.href = fullUrl;
        }, 100);
    } else {
        console.log('[DEBUG] No valid path found for userType:', userType);
        showError('loginErrorMessage', 'Invalid user type or missing dashboard assignment.');
    }
}

// --- UI Helper Functions ---
function showError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    if (!errorElement) return;
    errorElement.textContent = message;
    errorElement.style.display = 'block';
    
    const successId = elementId.replace('Error', 'Success');
    const successElement = document.getElementById(successId);
    if(successElement) successElement.style.display = 'none';
}

function showSuccess(elementId, message) {
    const successElement = document.getElementById(elementId);
    if (!successElement) return;
    successElement.textContent = message;
    successElement.style.display = 'block';

    const errorId = elementId.replace('Success', 'Error');
    const errorElement = document.getElementById(errorId);
    if(errorElement) errorElement.style.display = 'none';
}

function setLoadingState(isLoading, formType) {
    const button = document.querySelector(`#${formType}Form button[type="submit"]`);
    if (!button) return;
    
    if (isLoading) {
        button.disabled = true;
        button.innerHTML = `<div class="spinner"></div>`;
    } else {
        button.disabled = false;
        button.innerHTML = formType === 'login' ? 'Sign In' : 'Sign Up';
    }
}

/**
 * Toggles the visibility of the password field in the login form.
 */
function togglePasswordVisibility() {
    console.log('[DEBUG] Toggle password visibility called');
    const passwordInput = document.getElementById('passwordInput');
    const toggleIcon = document.getElementById('passwordToggleIcon');
    
    if (!passwordInput || !toggleIcon) {
        console.error('[DEBUG] Password input or toggle icon not found:', { passwordInput, toggleIcon });
        return;
    }
    
    // Toggle between password and text input types
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleIcon.classList.remove('fa-eye');
        toggleIcon.classList.add('fa-eye-slash');
        console.log('[DEBUG] Password visibility: shown');
    } else {
        passwordInput.type = 'password';
        toggleIcon.classList.remove('fa-eye-slash');
        toggleIcon.classList.add('fa-eye');
        console.log('[DEBUG] Password visibility: hidden');
    }
}
