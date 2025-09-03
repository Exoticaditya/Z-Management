document.addEventListener('DOMContentLoaded', function() {
    // Use dynamic API base URL if available, fallback to Railway production
    const API_BASE_URL = (window.API_BASE_URL || window.apiConfig?.API_BASE_URL || 'https://z-management-production.up.railway.app/api');
    const adminDashboard = document.querySelector('.admin-dashboard');
    const contentTitle = document.getElementById('contentTitle');
    const contentBody = document.getElementById('contentBody');
    const backButton = document.getElementById('backButton');
    const loginFormContainer = document.getElementById('login-form-container');
    let activeLink = null;
    let navigationHistory = [];
    let currentSectionCleanup = null; // Used to clean up event listeners

    // =================================================================
    // 1. AUTHENTICATION FLOW
    // =================================================================
    function checkAuth() {
        const token = localStorage.getItem('token');
        const user = localStorage.getItem('zplusUser');

        console.log('[ADMIN DEBUG] Auth check - token:', !!token);
        console.log('[ADMIN DEBUG] Auth check - user:', user);

        if (token && user) {
            try {
                const userData = JSON.parse(user);
                console.log('[ADMIN DEBUG] Parsed user data:', userData);

                // Check if user is admin - be more flexible with the check
                if (userData.userType === 'ADMIN' || userData.userType === 'admin') {
                    // Update user info in header
                    const userNameEl = document.querySelector('.user-name');
                    const userRoleEl = document.querySelector('.user-role');

                    const fullName = userData.name || userData.fullName || 'System Administrator';
                    console.log('[ADMIN DEBUG] Setting user name to:', fullName);

                    if (userNameEl) userNameEl.textContent = fullName;
                    if (userRoleEl) userRoleEl.textContent = 'System Administrator';

                    // Ensure we show the admin dashboard
                    if (adminDashboard) adminDashboard.style.display = 'grid';
                    if (loginFormContainer) loginFormContainer.style.display = 'none';
                    
                    // Load the dashboard
                    loadDashboard();
                    return; // Exit function, user is authenticated
                } else {
                    console.log('[ADMIN DEBUG] User is not ADMIN, userType is:', userData.userType);
                    // Clear authentication for non-admin users
                    localStorage.removeItem('zplusUser');
                    localStorage.removeItem('token');
                    
                    showLoginForm();
                    showErrorMessage('Access denied. Admin privileges required.');
                    return;
                }
            } catch (e) {
                console.error('[ADMIN DEBUG] Failed to parse user data:', e);
                localStorage.removeItem('zplusUser');
                localStorage.removeItem('token');
            }
        }
        
        // If we reach here, authentication has failed or is missing.
        console.log('[ADMIN DEBUG] Showing login form due to invalid/missing auth');
        showLoginForm();
    }

    function showLoginForm() {
        if(adminDashboard) adminDashboard.style.display = 'none';
        if(loginFormContainer) {
            loginFormContainer.style.display = 'block';
            loginFormContainer.innerHTML = `
                <div class="login-container">
                    <div class="login-box">
                        <h3>Z+ Admin Panel</h3><p>Please log in to continue</p>
                        <form id="loginForm">
                            <div class="form-group"><label for="username">Username</label><input type="text" id="username" required></div>
                            <div class="form-group"><label for="password">Password</label><input type="password" id="password" required></div>
                            <button type="submit" class="btn btn-primary">Login</button>
                        </form>
                    </div>
                </div>
            `;
            document.getElementById('loginForm').addEventListener('submit', handleLogin);
        }
    }

    function showErrorMessage(message) {
        const existingError = document.querySelector('.error-message');
        if (existingError) {
            existingError.remove();
        }

        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.style.cssText = 'background: #ff4444; color: white; padding: 10px; margin: 10px 0; border-radius: 4px; text-align: center;';
        errorDiv.textContent = message;

        const loginBox = document.querySelector('.login-box');
        if (loginBox) {
            loginBox.insertBefore(errorDiv, loginBox.querySelector('form'));
        }
    }

    async function handleLogin(e) {
        e.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    selfId: username, // Changed from username to selfId to match backend
                    password: password
                })
            });

            if (!response.ok) {
                throw new Error('Login failed. Please check your credentials.');
            }

            const loginData = await response.json();
            console.log('[ADMIN DEBUG] Login response:', loginData);

            if (loginData.success && loginData.token) {
                // Store authentication data temporarily
                localStorage.setItem('token', loginData.token);
                localStorage.setItem('zplusUser', JSON.stringify({
                    selfId: loginData.selfId,
                    name: loginData.name,
                    userType: loginData.userType
                }));

                console.log('[ADMIN DEBUG] Auth data stored, checking user type:', loginData.userType);

                // Check if user is admin
                if (loginData.userType === 'ADMIN' || loginData.userType === 'admin') {
                    loginFormContainer.innerHTML = '';
                    loginFormContainer.style.display = 'none';
                    adminDashboard.style.display = 'grid';
                    loadDashboard(); // Reload dashboard UI and event listeners
                } else {
                    // If not an admin, clear the stored data and throw error
                    localStorage.removeItem('token');
                    localStorage.removeItem('zplusUser');
                    throw new Error('Access denied. Admin privileges required.');
                }
            } else {
                throw new Error(loginData.message || 'Login failed');
            }

        } catch (error) {
            console.error('[ADMIN DEBUG] Login error:', error);
            showErrorMessage(error.message);
        }
    }

    // Auto-refresh functionality for new registrations and contacts
    let refreshInterval = null;
    let lastRegistrationCount = 0;
    let lastContactCount = 0;

    function startAutoRefresh() {
        if (refreshInterval) clearInterval(refreshInterval); // Ensure no multiple intervals running
        
        // Initialize counts before starting the timer
        initializeCounts();

        // Check for new data every 30 seconds
        refreshInterval = setInterval(async () => {
            try {
                // Check for new pending registrations
                const registrations = await apiFetch('/registrations/pending');
                const currentRegCount = registrations ? registrations.length : 0;
                
                if (currentRegCount > lastRegistrationCount) {
                    const newCount = currentRegCount - lastRegistrationCount;
                    showNotification(`${newCount} new registration(s) pending approval!`, 'info');
                }
                lastRegistrationCount = currentRegCount;

                // Check for new pending contacts
                const contacts = await apiFetch('/contact/status/PENDING');
                const currentContactCount = contacts ? contacts.length : 0;
                
                if (currentContactCount > lastContactCount) {
                    const newCount = currentContactCount - lastContactCount;
                    showNotification(`${newCount} new contact inquiry(s) received!`, 'info');
                }
                lastContactCount = currentContactCount;

            } catch (error) {
                console.error('Auto-refresh error:', error);
            }
        }, 30000); // 30 seconds
    }

    async function initializeCounts() {
        try {
            const registrations = await apiFetch('/registrations/pending');
            lastRegistrationCount = registrations ? registrations.length : 0;
            
            const contacts = await apiFetch('/contact/status/PENDING');
            lastContactCount = contacts ? contacts.length : 0;
        } catch (error)
        {
            console.error('Error initializing counts:', error);
        }
    }
    
    function stopAutoRefresh() {
        if (refreshInterval) {
            clearInterval(refreshInterval);
            refreshInterval = null;
        }
    }

    window.logout = () => {
        stopAutoRefresh(); // Stop the auto-refresh timer
        localStorage.removeItem('token');
        localStorage.removeItem('zplusUser');
        window.location.href = '/index/index.html'; // Redirect to main login
    };

    // =================================================================
    // 2. API & CORE LOGIC
    // =================================================================
    async function apiFetch(endpoint, options = {}) {
        const token = localStorage.getItem('token');
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const url = `${API_BASE_URL}${endpoint}`;
        const fetchOptions = {
            ...options,
            headers
        };
        
        const response = await fetch(url, fetchOptions);

        if (response.status === 401 || response.status === 403) {
            console.log('[ADMIN DEBUG] Authentication failed, logging out');
            logout();
            throw new Error('Session expired. Please log in again.');
        }

        if (!response.ok) {
            const errorText = await response.text();
            console.error('[ADMIN DEBUG] API Error Response:', errorText);
            throw new Error(errorText || `HTTP error! Status: ${response.status}`);
        }

        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            return await response.json();
        } else {
            return await response.text();
        }
    }

    // =================================================================
    // 3. DASHBOARD SETUP AND EVENT LISTENERS  
    // =================================================================
    function loadDashboard() {
        // Show dashboard main content by default
        loadContent('dashboard');
        
        // Set up global event listeners only once
        setupGlobalEventListeners();

        // Start auto-refresh for notifications
        startAutoRefresh();
    }
    
    // Listeners that should only be set up once
    function setupGlobalEventListeners() {
        // Handle dropdown functionality
        document.addEventListener('click', function(e) {
            const isDropdownButton = e.target.matches('[data-target]');
            const dropdowns = document.querySelectorAll('.dropdown-menu');
            
            dropdowns.forEach(menu => {
                const button = document.querySelector(`[data-target="${menu.id}"]`);
                // If the click is on the button for this menu, toggle it
                if (button && isDropdownButton && button.contains(e.target)) {
                     menu.classList.toggle('show');
                     button.classList.toggle('active', menu.classList.contains('show'));
                } else if (!menu.contains(e.target) && button && !button.contains(e.target)) {
                    // Otherwise, if the click is outside, close it
                    menu.classList.remove('show');
                    button.classList.remove('active');
                }
            });
        });

        backButton.addEventListener('click', () => {
            if (navigationHistory.length > 1) {
                navigationHistory.pop();
                const lastPage = navigationHistory[navigationHistory.length - 1];
                loadContent(lastPage, true);
            }
        });
    }

    // =================================================================
    // 4. SECTION LOADERS
    // =================================================================
    const sectionLoaders = {
        'dashboard': loadDashboardOverview,
        'all-registrations': createRegistrationLoader('all'),
        'pending-registrations': createRegistrationLoader('pending'),
        'approved-registrations': createRegistrationLoader('approved'),
        'rejected-registrations': createRegistrationLoader('rejected'),
        'registration-approval': createRegistrationLoader('all'),
        'all-contacts': createContactLoader('all'),
        'pending-contacts': createContactLoader('pending'),
        'resolved-contacts': createContactLoader('resolved'),
        'contact-statistics': loadContactStatistics,
        'games-overview': loadGamesOverview,
        'tic-tac-toe': loadTicTacToe,
        'memory-game': loadMemoryGame,
        'snake-game': loadSnakeGame
    };

    // Main content loader function
    function loadContent(section, skipHistory = false) {
        // Run cleanup for the previous section
        if (typeof currentSectionCleanup === 'function') {
            currentSectionCleanup();
            currentSectionCleanup = null;
        }

        if (!skipHistory) {
            navigationHistory.push(section);
        }

        // Update active navigation state
        updateActiveNavigation(section);

        // Update title and show back button if needed
        contentTitle.textContent = getSectionDisplayName(section);
        backButton.style.display = navigationHistory.length > 1 ? 'block' : 'none';

        // Load the section content
        const loader = sectionLoaders[section] || loadGenericContent;
        if (typeof loader === 'function') {
            try {
                // The loader function is responsible for rendering into contentBody
                loader(contentBody, section);
            } catch (error) {
                console.error(`[ADMIN DEBUG] Error loading section ${section}:`, error);
                contentBody.innerHTML = `
                    <div class="error-container">
                        <h3>Error Loading Content</h3>
                        <p>Failed to load ${getSectionDisplayName(section)}</p>
                        <button onclick="loadContent('dashboard')" class="btn btn-primary">Back to Dashboard</button>
                    </div>
                `;
            }
        }
    }

    function updateActiveNavigation(section) {
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });

        const activeNav = document.querySelector(`[data-section="${section}"]`);
        if (activeNav) {
            activeNav.classList.add('active');
        }
    }

    async function loadDashboardOverview(container) {
        console.log('[ADMIN DEBUG] Loading dashboard overview...');
        container.innerHTML = `
            <div class="dashboard-overview">
                <div class="welcome-section">
                    <h2><i class="fas fa-tachometer-alt"></i> Welcome to Z+ Admin Panel</h2>
                    <p class="welcome-text">Manage your system users, registrations, and administrative tasks efficiently.</p>
                </div>
                
                <div class="stats-grid">
                    <div class="stat-card stat-primary">
                        <div class="stat-icon"><i class="fas fa-users"></i></div>
                        <div class="stat-content">
                            <h3>User Management</h3>
                            <p>Manage all user registrations and approvals</p>
                            <button onclick="loadContent('pending-registrations')" class="btn btn-outline">View Pending <i class="fas fa-arrow-right"></i></button>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-success">
                        <div class="stat-icon"><i class="fas fa-check-circle"></i></div>
                        <div class="stat-content">
                            <h3>Approved Users</h3>
                            <p>View and manage approved registrations</p>
                            <button onclick="loadContent('approved-registrations')" class="btn btn-outline">View All <i class="fas fa-arrow-right"></i></button>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-warning">
                        <div class="stat-icon"><i class="fas fa-exclamation-triangle"></i></div>
                        <div class="stat-content">
                            <h3>Rejected Users</h3>
                            <p>Review rejected registration requests</p>
                            <button onclick="loadContent('rejected-registrations')" class="btn btn-outline">View Rejected <i class="fas fa-arrow-right"></i></button>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-info">
                        <div class="stat-icon"><i class="fas fa-list"></i></div>
                        <div class="stat-content">
                            <h3>All Registrations</h3>
                            <p>Complete overview of all user data</p>
                            <button onclick="loadContent('all-registrations')" class="btn btn-outline">View All <i class="fas fa-arrow-right"></i></button>
                        </div>
                    </div>
                </div>
                
                <div class="quick-actions">
                    <h3><i class="fas fa-bolt"></i> Quick Actions</h3>
                    <div class="action-buttons">
                        <button onclick="loadContent('pending-registrations')" class="btn btn-primary"><i class="fas fa-clock"></i> Pending Approvals</button>
                        <button onclick="refreshData()" class="btn btn-secondary"><i class="fas fa-sync-alt"></i> Refresh Data</button>
                        <button onclick="loadContent('all-registrations')" class="btn btn-info"><i class="fas fa-table"></i> All Users</button>
                        <button onclick="exportData()" class="btn btn-success"><i class="fas fa-download"></i> Export Data</button>
                    </div>
                </div>
                
                <div class="system-info">
                    <h3><i class="fas fa-info-circle"></i> System Information</h3>
                    <div class="info-grid">
                        <div class="info-item"><span class="label">Status:</span><span class="value text-success"><i class="fas fa-check"></i> Online</span></div>
                        <div class="info-item"><span class="label">Last Updated:</span><span class="value">${new Date().toLocaleString()}</span></div>
                        <div class="info-item"><span class="label">Version:</span><span class="value">Z+ Admin v1.0</span></div>
                        <div class="info-item"><span class="label">Environment:</span><span class="value">Production</span></div>
                    </div>
                </div>
            </div>
        `;
    }

    function createRegistrationLoader(type) {
        return async (container) => {
            let endpoint = '/registrations';
            if (type !== 'all') {
                endpoint = `/registrations/${type}`;
            }

            try {
                container.innerHTML = `<div class="loading-container"><div class="loading-spinner"><i class="fas fa-spinner fa-spin"></i></div><p>Loading ${type} registrations...</p></div>`;
                
                console.log(`[ADMIN DEBUG] Fetching ${type} registrations from ${endpoint}`);
                const data = await apiFetch(endpoint);
                console.log(`[ADMIN DEBUG] Received ${type} registrations:`, data);

                if (!Array.isArray(data)) {
                    throw new Error('Invalid data format received from server');
                }

                container.innerHTML = `
                    <div class="table-container">
                        <div class="table-header">
                            <h3><i class="fas fa-users"></i> ${getSectionDisplayName(type + '-registrations')}</h3>
                            <div class="table-actions">
                                <button onclick="refreshData()" class="btn btn-secondary btn-sm"><i class="fas fa-sync-alt"></i> Refresh</button>
                                <button onclick="exportData()" class="btn btn-success btn-sm"><i class="fas fa-download"></i> Export</button>
                            </div>
                        </div>
                        ${renderRegistrationsTable(data, type === 'pending')}
                    </div>
                `;
            } catch (error) {
                console.error(`[ADMIN DEBUG] Failed to load ${type} registrations:`, error);
                container.innerHTML = `
                    <div class="error-container">
                        <div class="error-icon"><i class="fas fa-exclamation-triangle"></i></div>
                        <h3>Unable to Load ${getSectionDisplayName(type + '-registrations')}</h3>
                        <p class="error-message">${error.message}</p>
                        <div class="error-actions">
                            <button onclick="refreshData()" class="btn btn-primary"><i class="fas fa-redo"></i> Try Again</button>
                            <button onclick="loadContent('dashboard')" class="btn btn-secondary"><i class="fas fa-home"></i> Back to Dashboard</button>
                        </div>
                        <details class="error-details">
                            <summary>Technical Details</summary>
                            <p><strong>Endpoint:</strong> ${endpoint}</p>
                            <p><strong>Error:</strong> ${error.message}</p>
                            <p><strong>Time:</strong> ${new Date().toLocaleString()}</p>
                        </details>
                    </div>
                `;
            }
        };
    }

    function createContactLoader(type) {
        return async (container) => {
            let endpoint = '/contact/inquiries'; // Default for 'all'
            if (type !== 'all') {
                endpoint = `/contact/status/${type.toUpperCase()}`;
            }
            
            try {
                container.innerHTML = `<div class="loading-container"><div class="loading-spinner"><i class="fas fa-spinner fa-spin"></i></div><p>Loading ${type} contact inquiries...</p></div>`;

                console.log(`[ADMIN DEBUG] Fetching ${type} contact inquiries from ${endpoint}`);
                const response = await apiFetch(endpoint);
                console.log(`[ADMIN DEBUG] Received ${type} contact inquiries response:`, response);

                let data;
                if (response && response.content && Array.isArray(response.content)) {
                    data = response.content; // Spring Data Page response
                } else if (Array.isArray(response)) {
                    data = response; // Direct array response
                } else {
                    throw new Error('Invalid data format received from server');
                }

                container.innerHTML = `
                    <div class="table-container">
                        <div class="table-header">
                            <h3><i class="fas fa-envelope"></i> ${getSectionDisplayName(type + '-contacts')}</h3>
                            <div class="table-actions">
                                <button onclick="refreshData()" class="btn btn-secondary btn-sm"><i class="fas fa-sync-alt"></i> Refresh</button>
                                <button onclick="exportData()" class="btn btn-success btn-sm"><i class="fas fa-download"></i> Export</button>
                            </div>
                        </div>
                        ${renderContactInquiriesTable(data, type === 'pending' || type === 'all')}
                    </div>
                `;
            } catch (error) {
                console.error(`[ADMIN DEBUG] Failed to load ${type} contact inquiries:`, error);
                container.innerHTML = `
                    <div class="error-container">
                        <div class="error-icon"><i class="fas fa-exclamation-triangle"></i></div>
                        <h3>Unable to Load ${getSectionDisplayName(type + '-contacts')}</h3>
                        <p class="error-message">${error.message}</p>
                        <div class="error-actions">
                            <button onclick="refreshData()" class="btn btn-primary"><i class="fas fa-redo"></i> Try Again</button>
                            <button onclick="loadContent('dashboard')" class="btn btn-secondary"><i class="fas fa-home"></i> Back to Dashboard</button>
                        </div>
                    </div>
                `;
            }
        };
    }

    function loadContactStatistics(container) {
        container.innerHTML = `
            <div class="dashboard-overview">
                <div class="welcome-section">
                    <h2><i class="fas fa-chart-bar"></i> Contact Inquiry Statistics</h2>
                    <p class="welcome-text">Overview of contact form submissions and inquiry trends.</p>
                </div>
                
                <div class="stats-grid">
                    <div class="stat-card stat-info">
                        <div class="stat-icon"><i class="fas fa-envelope"></i></div>
                        <div class="stat-content">
                            <h3>Total Inquiries</h3>
                            <p>All time contact submissions</p>
                            <button onclick="loadContent('all-contacts')" class="btn btn-outline">View All <i class="fas fa-arrow-right"></i></button>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-warning">
                        <div class="stat-icon"><i class="fas fa-clock"></i></div>
                        <div class="stat-content">
                            <h3>Pending Inquiries</h3>
                            <p>Awaiting response or action</p>
                            <button onclick="loadContent('pending-contacts')" class="btn btn-outline">View Pending <i class="fas fa-arrow-right"></i></button>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-success">
                        <div class="stat-icon"><i class="fas fa-check-circle"></i></div>
                        <div class="stat-content">
                            <h3>Resolved Inquiries</h3>
                            <p>Successfully handled contacts</p>
                            <button onclick="loadContent('resolved-contacts')" class="btn btn-outline">View Resolved <i class="fas fa-arrow-right"></i></button>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-primary">
                        <div class="stat-icon"><i class="fas fa-calendar-day"></i></div>
                        <div class="stat-content">
                            <h3>Recent Inquiries</h3>
                            <p>Latest contact submissions</p>
                            <button onclick="loadRecentContacts()" class="btn btn-outline">View Recent <i class="fas fa-arrow-right"></i></button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    function loadGamesOverview(container) {
        container.innerHTML = `
            <div class="dashboard-overview">
                <div class="welcome-section">
                    <h2><i class="fas fa-gamepad"></i> Mini Games Collection</h2>
                    <p class="welcome-text">Interactive games for users and entertainment features.</p>
                </div>
                
                <div class="stats-grid">
                    <div class="stat-card stat-primary">
                        <div class="stat-icon"><i class="fas fa-th-large"></i></div>
                        <div class="stat-content">
                            <h3>Tic Tac Toe</h3>
                            <p>Classic strategy game for two players</p>
                            <button onclick="loadContent('tic-tac-toe')" class="btn btn-outline">Play Game <i class="fas fa-play"></i></button>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-success">
                        <div class="stat-icon"><i class="fas fa-brain"></i></div>
                        <div class="stat-content">
                            <h3>Memory Game</h3>
                            <p>Test your memory with card matching</p>
                            <button onclick="loadContent('memory-game')" class="btn btn-outline">Play Game <i class="fas fa-play"></i></button>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-warning">
                        <div class="stat-icon"><i class="fas fa-gamepad"></i></div> <div class="stat-content">
                            <h3>Snake Game</h3>
                            <p>Classic arcade-style snake game</p>
                            <button onclick="loadContent('snake-game')" class="btn btn-outline">Play Game <i class="fas fa-play"></i></button>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-info">
                        <div class="stat-icon"><i class="fas fa-trophy"></i></div>
                        <div class="stat-content">
                            <h3>Game Statistics</h3>
                            <p>View game usage and scores</p>
                            <button onclick="showNotification('Game statistics coming soon!', 'info')" class="btn btn-outline">View Stats <i class="fas fa-chart-line"></i></button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    function loadGenericContent(container, section) {
        container.innerHTML = `<div class="placeholder-content"><h3>Under Construction</h3><p>The '${getSectionDisplayName(section)}' section is not yet implemented.</p></div>`;
    }

    // =================================================================
    // 5. HTML RENDERING
    // =================================================================
    function renderRegistrationsTable(registrations, showActions = false) {
        if (!registrations || registrations.length === 0) {
            return `
                <div class="empty-state">
                    <div class="empty-state-icon"><i class="fas fa-users-slash"></i></div>
                    <h3>No Registrations Found</h3>
                    <p>There are no registrations matching the current filter.</p>
                    <button onclick="refreshData()" class="btn btn-secondary"><i class="fas fa-sync-alt"></i> Refresh</button>
                </div>
            `;
        }
        const rows = registrations.map(reg => {
            const fullName = `${reg.firstName || ''} ${reg.lastName || ''}`.trim();
            const status = reg.status || 'PENDING';
            const statusDisplay = typeof status === 'object' ? (status.displayName || status.name || status) : status;
            return `
                <tr>
                    <td>${reg.id}</td>
                    <td>${fullName}</td>
                    <td>${reg.email || '-'}</td>
                    <td>${reg.department || '-'}</td>
                    <td>${reg.projectId || '-'}</td>
                    <td><span class="badge ${getStatusClass(statusDisplay)}">${statusDisplay.replace('_', ' ')}</span></td>
                    <td>
                        ${showActions ? `
                            <button class="btn btn-sm btn-primary" onclick="approveRegistration(${reg.id})" title="Approve"><i class="fas fa-check"></i></button>
                            <button class="btn btn-sm btn-danger" onclick="rejectRegistration(${reg.id})" title="Reject"><i class="fas fa-times"></i></button>
                            <button class="btn btn-sm btn-info" onclick="shareRegistration(${reg.id})" title="Share"><i class="fas fa-share"></i></button>
                        ` : (reg.rejectionReason ? `Reason: ${reg.rejectionReason}` : (reg.sharedWith ? `Shared with: ${reg.sharedWith}` : '-'))}
                    </td>
                </tr>
            `;
        }).join('');
        return `<table class="data-table"><thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Department</th><th>Project ID</th><th>Status</th><th>Actions/Details</th></tr></thead><tbody>${rows}</tbody></table>`;
    }

    function renderContactInquiriesTable(inquiries, showActions = false) {
        if (!inquiries || inquiries.length === 0) {
            return `
                <div class="empty-state">
                    <div class="empty-state-icon"><i class="fas fa-envelope-open"></i></div>
                    <h3>No Contact Inquiries</h3>
                    <p>There are currently no contact inquiries in this category.</p>
                </div>
            `;
        }
        const rows = inquiries.map(inquiry => {
            const fullName = inquiry.fullName || `${inquiry.firstName || ''} ${inquiry.lastName || ''}`.trim();
            const status = inquiry.status || 'PENDING';
            const createdDate = inquiry.createdAt ? new Date(inquiry.createdAt).toLocaleDateString() : '-';
            const assignedTo = inquiry.assignedTo || 'Unassigned';
            return `
                <tr>
                    <td>${inquiry.id}</td>
                    <td>${fullName}</td>
                    <td>${inquiry.email || '-'}</td>
                    <td>${inquiry.subject || '-'}</td>
                    <td title="${inquiry.message || ''}">${inquiry.message ? inquiry.message.substring(0, 50) + '...' : '-'}</td>
                    <td><span class="badge ${getContactStatusClass(status)}">${status}</span></td>
                    <td>${createdDate}</td>
                    <td>${assignedTo}</td>
                    <td>
                        ${showActions ? `
                            <button class="btn btn-sm btn-primary" onclick="viewContactInquiry(${inquiry.id})" title="View Details"><i class="fas fa-eye"></i></button>
                            <button class="btn btn-sm btn-secondary" onclick="assignContactInquiry(${inquiry.id})" title="Assign to Employee"><i class="fas fa-user-plus"></i></button>
                            <button class="btn btn-sm btn-success" onclick="resolveContactInquiry(${inquiry.id})" title="Mark Resolved"><i class="fas fa-check"></i></button>
                            <button class="btn btn-sm btn-info" onclick="replyToInquiry(${inquiry.id})" title="Reply"><i class="fas fa-reply"></i></button>
                        ` : (inquiry.response ? `Response sent` : '-')}
                    </td>
                </tr>
            `;
        }).join('');
        return `<table class="data-table"><thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Subject</th><th>Message Preview</th><th>Status</th><th>Date</th><th>Assigned To</th><th>Actions</th></tr></thead><tbody>${rows}</tbody></table>`;
    }

    // =================================================================
    // GAME RENDERERS
    // =================================================================
    function loadTicTacToe(container) {
        container.innerHTML = `
            <div class="game-container">
                <div class="game-header"><h2><i class="fas fa-th-large"></i> Tic Tac Toe</h2></div>
                <div class="tic-tac-toe-game">
                    <div class="game-info">
                        <span id="current-player">Player X's turn</span>
                        <button onclick="resetTicTacToe()" class="btn btn-secondary">Reset Game</button>
                    </div>
                    <div class="game-board" id="tic-tac-toe-board">
                        ${Array(9).fill(0).map((_, i) => `<div class="cell" onclick="makeMove(${i})"></div>`).join('')}
                    </div>
                    <div class="game-stats">
                        <div class="stat">Player X Wins: <span id="x-wins">0</span></div>
                        <div class="stat">Player O Wins: <span id="o-wins">0</span></div>
                        <div class="stat">Draws: <span id="draws">0</span></div>
                    </div>
                </div>
            </div>
        `;
        initializeTicTacToe();
    }

    function loadMemoryGame(container) {
        container.innerHTML = `
            <div class="game-container">
                <div class="game-header"><h2><i class="fas fa-brain"></i> Memory Game</h2></div>
                <div class="memory-game">
                    <div class="game-info">
                        <span>Moves: <span id="move-count">0</span></span>
                        <span>Matches: <span id="match-count">0</span>/8</span>
                        <button onclick="resetMemoryGame()" class="btn btn-secondary">New Game</button>
                    </div>
                    <div class="memory-board" id="memory-board"></div>
                </div>
            </div>
        `;
        initializeMemoryGame();
    }

    function loadSnakeGame(container) {
        container.innerHTML = `
            <div class="game-container">
                <div class="game-header"><h2><i class="fas fa-gamepad"></i> Snake Game</h2></div>
                <div class="snake-game">
                    <div class="game-info">
                        <span>Score: <span id="snake-score">0</span></span>
                        <span>High Score: <span id="snake-high-score">0</span></span>
                        <button onclick="startSnakeGame()" class="btn btn-primary" id="snake-start-btn">Start Game</button>
                    </div>
                    <canvas id="snake-canvas" width="400" height="400"></canvas>
                    <div class="game-controls">
                        <p>Use arrow keys to control the snake. Eat the red food to grow.</p>
                    </div>
                </div>
            </div>
        `;
        initializeSnakeGame();
    }
    
    // =================================================================
    // 6. USER ACTIONS
    // =================================================================
    window.approveRegistration = async (id) => {
        try {
            await apiFetch(`/registrations/${id}/approve`, { method: 'POST' });
            showNotification(`Registration #${id} has been approved.`, 'success');
            refreshData();
        } catch (error) {
            showNotification(`Error: ${error.message}`, 'error');
        }
    };

    window.rejectRegistration = async (id) => {
        const reason = prompt("Please provide a reason for rejection:");
        if (reason && reason.trim()) {
            try {
                await apiFetch(`/registrations/${id}/reject?reason=${encodeURIComponent(reason.trim())}`, { method: 'POST' });
                showNotification(`Registration #${id} has been rejected.`, 'info');
                refreshData();
            } catch (error) {
                showNotification(`Error: ${error.message}`, 'error');
            }
        }
    };

    window.shareRegistration = async (id) => {
        const sharedWith = prompt("Enter the email or user ID to share with:");
        if (sharedWith && sharedWith.trim()) {
            try {
                await apiFetch(`/registrations/${id}/share?sharedWith=${encodeURIComponent(sharedWith.trim())}`, { method: 'POST' });
                showNotification(`Registration #${id} has been shared.`, 'success');
                refreshData();
            } catch (error) {
                showNotification(`Error: ${error.message}`, 'error');
            }
        }
    };

    window.viewContactInquiry = async (id) => {
        try {
            const inquiry = await apiFetch(`/contact/${id}`);
            showDetailedNotification({
                type: 'info',
                title: `Inquiry #${id} Details`,
                message: `From: ${inquiry.fullName} (${inquiry.email})`,
                details: {
                    'Subject': inquiry.subject,
                    'Message': inquiry.message,
                    'Status': inquiry.status,
                    'Received': new Date(inquiry.createdAt).toLocaleString()
                }
            });
        } catch (error) {
            showNotification(`Error fetching details for inquiry #${id}`, 'error');
        }
    };

    window.resolveContactInquiry = async (id) => {
        try {
            await apiFetch(`/contact/${id}/resolve`, { method: 'POST' });
            showNotification(`Contact inquiry #${id} marked as resolved`, 'success');
            refreshData();
        } catch (error) {
            showNotification(`Error: ${error.message}`, 'error');
        }
    };

    window.replyToInquiry = (id) => {
        const response = prompt("Enter your response:");
        if (response && response.trim()) {
            // TODO: Implement actual API call for replying
            showNotification(`Response sent for inquiry #${id}`, 'success');
        }
    };

    window.assignContactInquiry = async (id) => {
        const assignedTo = prompt("Enter employee Self ID (e.g., ZP002) to assign this inquiry to:");
        if (assignedTo && assignedTo.trim()) {
            try {
                const response = await apiFetch(`/contact/${id}/assign?assignedTo=${encodeURIComponent(assignedTo.trim())}`, { method: 'PUT' });
                showNotification(`Inquiry #${id} assigned to ${assignedTo}.`, 'success');
                refreshData();
            } catch (error) {
                showNotification(`Error assigning inquiry: ${error.message}`, 'error');
            }
        }
    };

    // =================================================================
    // 7. HELPER FUNCTIONS
    // =================================================================
    const displayNames = {
        'dashboard': 'Dashboard Overview',
        'all-registrations': 'All Registrations',
        'pending-registrations': 'Pending Registrations',
        'approved-registrations': 'Approved Registrations',
        'rejected-registrations': 'Rejected Registrations',
        'registration-approval': 'Registration Approvals',
        'all-contacts': 'All Contact Inquiries',
        'pending-contacts': 'Pending Contact Inquiries',
        'resolved-contacts': 'Resolved Contact Inquiries',
        'contact-statistics': 'Contact Statistics',
        'games-overview': 'Games Overview',
        'tic-tac-toe': 'Tic Tac Toe',
        'memory-game': 'Memory Game',
        'snake-game': 'Snake Game'
    };

    function getSectionDisplayName(section) {
        return displayNames[section] || section.replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
    }

    window.refreshData = () => {
        const currentSection = navigationHistory[navigationHistory.length - 1] || 'dashboard';
        loadContent(currentSection, true);
        showNotification('Data refreshed', 'success');
    };

    window.refreshAllData = window.refreshData; // Alias for dashboard button

    // Make loadContent available globally for HTML onclick handlers
    window.loadContent = loadContent;

    window.exportData = async () => {
        const currentSection = navigationHistory[navigationHistory.length - 1] || 'dashboard';
        try {
            let data, filename;
            if (currentSection.includes('registrations')) {
                const type = currentSection.replace('-registrations', '');
                data = await apiFetch(type === 'all' ? '/registrations' : `/registrations/${type}`);
                filename = `registrations_${type}_${new Date().toISOString().split('T')[0]}.csv`;
            } else if (currentSection.includes('contacts')) {
                const type = currentSection.replace('-contacts', '');
                const response = await apiFetch(type === 'all' ? '/contact/inquiries' : `/contact/status/${type.toUpperCase()}`);
                data = Array.isArray(response) ? response : (response.content || []);
                filename = `contacts_${type}_${new Date().toISOString().split('T')[0]}.csv`;
            } else {
                showNotification('Export not available for this section.', 'warning');
                return;
            }

            if (!data || data.length === 0) {
                showNotification('No data available to export.', 'warning');
                return;
            }

            const csvContent = convertToCSV(data, currentSection.includes('registrations'));
            downloadCSV(csvContent, filename);
            showNotification(`Successfully exported ${data.length} records.`, 'success');
        } catch (error) {
            console.error('[ADMIN DEBUG] Export failed:', error);
            showNotification(`Export failed: ${error.message}`, 'error');
        }
    };

    function convertToCSV(data, isRegistrationData) {
        let headers, rows;
        if (isRegistrationData) {
            headers = ['ID', 'First Name', 'Last Name', 'Email', 'Department', 'Project ID', 'Status', 'Phone'];
            rows = data.map(item => [item.id, item.firstName, item.lastName, item.email, item.department, item.projectId, item.status, item.phone]);
        } else { // Contact Data
            headers = ['ID', 'Full Name', 'Email', 'Subject', 'Message', 'Status', 'Created At'];
            rows = data.map(item => [item.id, item.fullName, item.email, item.subject, `"${(item.message || '').replace(/"/g, '""')}"`, item.status, item.createdAt]);
        }
        const csvRows = [headers.join(','), ...rows.map(row => row.join(','))];
        return csvRows.join('\n');
    }

    function downloadCSV(csvContent, filename) {
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', filename);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
    
    function showNotification(message, type = 'info') {
        const container = document.getElementById('notification-container') || createNotificationContainer();
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.innerHTML = `
            <div class="notification-content"><i class="fas ${getNotificationIcon(type)}"></i><span>${message}</span></div>
            <button class="notification-close" onclick="this.parentElement.remove()"><i class="fas fa-times"></i></button>
        `;
        container.appendChild(notification);
        setTimeout(() => notification.classList.add('show'), 10);
        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => notification.remove(), 500);
        }, 5000);
    }

    function showDetailedNotification(options) {
        const { type, title, message, details } = options;
        const container = document.getElementById('notification-container') || createNotificationContainer();
        const detailsHtml = details ? Object.entries(details).map(([key, value]) => `<div class="detail-item"><strong>${key}:</strong> ${value}</div>`).join('') : '';
        
        const notification = document.createElement('div');
        notification.className = `notification detailed-notification ${type}`;
        notification.innerHTML = `
            <div class="notification-header">
                <div class="notification-title"><i class="fas ${getNotificationIcon(type)}"></i><span>${title}</span></div>
                <button class="notification-close" onclick="this.parentElement.parentElement.remove()"><i class="fas fa-times"></i></button>
            </div>
            <div class="notification-body">
                <p class="notification-message">${message}</p>
                ${detailsHtml ? `<div class="notification-details">${detailsHtml}</div>` : ''}
            </div>
        `;
        container.appendChild(notification);
        setTimeout(() => notification.classList.add('show'), 10);
        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => notification.remove(), 500);
        }, 8000); // Longer timeout for detailed notifications
    }

    function getNotificationIcon(type) {
        const icons = { success: 'fa-check-circle', error: 'fa-exclamation-circle', warning: 'fa-exclamation-triangle', info: 'fa-info-circle' };
        return icons[type] || 'fa-info-circle';
    }

    function createNotificationContainer() {
        const container = document.createElement('div');
        container.id = 'notification-container';
        document.body.appendChild(container);
        return container;
    }

    function getStatusClass(status) {
        const s = (status || '').toUpperCase();
        if (s === 'APPROVED') return 'badge-success';
        if (s === 'PENDING') return 'badge-warning';
        if (s === 'REJECTED') return 'badge-danger';
        return 'badge-secondary';
    }

    function getContactStatusClass(status) {
        const s = (status || '').toUpperCase();
        if (s === 'RESOLVED') return 'badge-success';
        if (s === 'PENDING') return 'badge-warning';
        if (s === 'IN_PROGRESS') return 'badge-info';
        return 'badge-secondary';
    }

    window.loadRecentContacts = () => loadContent('pending-contacts');

    // =================================================================
    // GAME LOGIC
    // =================================================================
    let ticTacToeState = {};

    function initializeTicTacToe() {
        resetTicTacToe();
    }
    
    window.resetTicTacToe = () => {
        ticTacToeState = {
            board: Array(9).fill(''),
            currentPlayer: 'X',
            gameOver: false,
            wins: ticTacToeState.wins || { X: 0, O: 0, draws: 0 } // Persist scores
        };
        updateTicTacToeDisplay();
    };

    window.makeMove = (index) => {
        if (ticTacToeState.board[index] || ticTacToeState.gameOver) return;
        ticTacToeState.board[index] = ticTacToeState.currentPlayer;
        if (checkWinner()) {
            ticTacToeState.wins[ticTacToeState.currentPlayer]++;
            ticTacToeState.gameOver = true;
            showNotification(`Player ${ticTacToeState.currentPlayer} wins!`, 'success');
        } else if (ticTacToeState.board.every(cell => cell)) {
            ticTacToeState.wins.draws++;
            ticTacToeState.gameOver = true;
            showNotification("It's a draw!", 'info');
        } else {
            ticTacToeState.currentPlayer = ticTacToeState.currentPlayer === 'X' ? 'O' : 'X';
        }
        updateTicTacToeDisplay();
    };

    function checkWinner() {
        const winPatterns = [[0, 1, 2], [3, 4, 5], [6, 7, 8], [0, 3, 6], [1, 4, 7], [2, 5, 8], [0, 4, 8], [2, 4, 6]];
        return winPatterns.some(p => p.every(i => ticTacToeState.board[i] === ticTacToeState.currentPlayer));
    }

    function updateTicTacToeDisplay() {
        document.querySelectorAll('#tic-tac-toe-board .cell').forEach((cell, i) => {
            cell.textContent = ticTacToeState.board[i];
            cell.className = `cell ${ticTacToeState.board[i] ? 'filled' : ''}`;
        });
        document.getElementById('current-player').textContent = ticTacToeState.gameOver ? 'Game Over' : `Player ${ticTacToeState.currentPlayer}'s turn`;
        document.getElementById('x-wins').textContent = ticTacToeState.wins.X;
        document.getElementById('o-wins').textContent = ticTacToeState.wins.O;
        document.getElementById('draws').textContent = ticTacToeState.wins.draws;
    }

    let memoryGameState = {};

    function initializeMemoryGame() {
        resetMemoryGame();
    }

    window.resetMemoryGame = () => {
        const symbols = ['🎮', '🎯', '🎲', '🧩', '🎨', '🎸', '🎹', '🎷'];
        const cards = [...symbols, ...symbols].sort(() => Math.random() - 0.5);
        const board = document.getElementById('memory-board');
        board.innerHTML = cards.map((symbol, index) =>
            `<div class="memory-card" data-symbol="${symbol}" onclick="flipCard(this)">
                <div class="card-inner">
                    <div class="card-front">?</div>
                    <div class="card-back">${symbol}</div>
                </div>
            </div>`
        ).join('');
        memoryGameState = { flippedCards: [], matchedPairs: 0, moves: 0, canFlip: true };
        document.getElementById('move-count').textContent = '0';
        document.getElementById('match-count').textContent = `0/${symbols.length}`;
    };

    window.flipCard = (card) => {
        if (!memoryGameState.canFlip || card.classList.contains('flipped') || card.classList.contains('matched')) return;
        card.classList.add('flipped');
        memoryGameState.flippedCards.push(card);
        if (memoryGameState.flippedCards.length === 2) {
            memoryGameState.canFlip = false;
            memoryGameState.moves++;
            document.getElementById('move-count').textContent = memoryGameState.moves;
            const [card1, card2] = memoryGameState.flippedCards;
            if (card1.dataset.symbol === card2.dataset.symbol) {
                card1.classList.add('matched');
                card2.classList.add('matched');
                memoryGameState.matchedPairs++;
                document.getElementById('match-count').textContent = `${memoryGameState.matchedPairs}/8`;
                if (memoryGameState.matchedPairs === 8) {
                    showNotification(`Congratulations! You won in ${memoryGameState.moves} moves!`, 'success');
                }
                memoryGameState.flippedCards = [];
                memoryGameState.canFlip = true;
            } else {
                setTimeout(() => {
                    card1.classList.remove('flipped');
                    card2.classList.remove('flipped');
                    memoryGameState.flippedCards = [];
                    memoryGameState.canFlip = true;
                }, 1000);
            }
        }
    };
    
    let snakeGame = { interval: null };

    function initializeSnakeGame() {
        const highScore = localStorage.getItem('snakeHighScore') || '0';
        document.getElementById('snake-high-score').textContent = highScore;
    }
    
    // Cleanup function for Snake Game
    function cleanupSnakeGame() {
        document.removeEventListener('keydown', snakeGame.changeDirectionHandler);
        if (snakeGame.interval) {
            clearInterval(snakeGame.interval);
        }
        console.log('[ADMIN DEBUG] Snake game cleaned up.');
    }

    window.startSnakeGame = () => {
        // Run cleanup before starting a new game
        cleanupSnakeGame(); 

        const canvas = document.getElementById('snake-canvas');
        const ctx = canvas.getContext('2d');
        const gridSize = 20;
        const tileCount = canvas.width / gridSize;
        
        snakeGame = {
            ...snakeGame,
            snake: [{ x: 10, y: 10 }],
            food: { x: 15, y: 15 },
            dx: 0,
            dy: 0,
            score: 0,
            running: true,
            changeDirectionHandler: (e) => changeDirection(e) // Store handler to remove it later
        };
        
        currentSectionCleanup = cleanupSnakeGame; // Register the cleanup function
        
        document.getElementById('snake-start-btn').textContent = 'Restart Game';
        document.addEventListener('keydown', snakeGame.changeDirectionHandler);
        
        generateFood();
        snakeGame.interval = setInterval(gameLoop, 100);

        function gameLoop() {
            if (!snakeGame.running) {
                clearInterval(snakeGame.interval);
                return;
            }
            ctx.fillStyle = '#1a1a1a';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            moveSnake();
            drawFood();
            drawSnake();
        }

        function moveSnake() {
            const head = { x: snakeGame.snake[0].x + snakeGame.dx, y: snakeGame.snake[0].y + snakeGame.dy };
            snakeGame.snake.unshift(head);
            if (head.x === snakeGame.food.x && head.y === snakeGame.food.y) {
                snakeGame.score += 10;
                document.getElementById('snake-score').textContent = snakeGame.score;
                generateFood();
            } else {
                snakeGame.snake.pop();
            }
            checkCollision(head);
        }

        function checkCollision(head) {
            if (head.x < 0 || head.x >= tileCount || head.y < 0 || head.y >= tileCount || snakeGame.snake.slice(1).some(s => s.x === head.x && s.y === head.y)) {
                snakeGame.running = false;
                showNotification(`Game Over! Final Score: ${snakeGame.score}`, 'info');
                const highScore = parseInt(localStorage.getItem('snakeHighScore') || '0');
                if (snakeGame.score > highScore) {
                    localStorage.setItem('snakeHighScore', snakeGame.score);
                    document.getElementById('snake-high-score').textContent = snakeGame.score;
                    showNotification('New High Score!', 'success');
                }
            }
        }

        function drawSnake() {
            ctx.fillStyle = 'lime';
            snakeGame.snake.forEach(s => ctx.fillRect(s.x * gridSize, s.y * gridSize, gridSize - 2, gridSize - 2));
        }

        function drawFood() {
            ctx.fillStyle = 'red';
            ctx.fillRect(snakeGame.food.x * gridSize, snakeGame.food.y * gridSize, gridSize - 2, gridSize - 2);
        }

        function generateFood() {
            snakeGame.food = {
                x: Math.floor(Math.random() * tileCount),
                y: Math.floor(Math.random() * tileCount)
            };
        }

        function changeDirection(event) {
            if (!snakeGame.running) return;
            const key = event.keyCode;
            if (key === 37 && snakeGame.dx !== 1) { snakeGame.dx = -1; snakeGame.dy = 0; }
            if (key === 38 && snakeGame.dy !== 1) { snakeGame.dx = 0; snakeGame.dy = -1; }
            if (key === 39 && snakeGame.dx !== -1) { snakeGame.dx = 1; snakeGame.dy = 0; }
            if (key === 40 && snakeGame.dy !== -1) { snakeGame.dx = 0; snakeGame.dy = 1; }
        }
    };


    // =================================================================
    // 8. INITIAL START
    // =================================================================
    checkAuth();
});