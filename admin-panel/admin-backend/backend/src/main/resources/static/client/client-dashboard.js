// Client Dashboard JavaScript Functions

// API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Global variables
let currentClient = null;
let currentSection = 'dashboard';

// Initialize dashboard
document.addEventListener('DOMContentLoaded', function() {
    checkAuthentication();
    loadClientData();
    showSection('dashboard');
    initializeEventListeners();
    updateLastUpdated();
    setInterval(updateLastUpdated, 60000);
});

// Check authentication
function checkAuthentication() {
    const clientToken = localStorage.getItem('clientToken');
    if (!clientToken) {
        window.location.href = '../login.html';
        return;
    }
    
    // Validate token with backend
    fetch(`${API_BASE_URL}/clients/validate-token`, {
        headers: {
            'Authorization': `Bearer ${clientToken}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Invalid token');
        }
        return response.json();
    })
    .then(data => {
        currentClient = data;
        updateUserInfo();
    })
    .catch(error => {
        console.error('Authentication failed:', error);
        localStorage.removeItem('clientToken');
        window.location.href = '../login.html';
    });
}

// Show specific section and update navigation
function showSection(sectionId) {
    currentSection = sectionId;
    
    // Hide all sections
    const sections = document.querySelectorAll('.content-section');
    sections.forEach(section => {
        section.classList.remove('active');
    });

    // Show the requested section
    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        targetSection.classList.add('active');
    }

    // Update navigation active state
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.classList.remove('active');
    });

    // Find and activate the corresponding nav link
    const activeLink = document.querySelector(`[onclick="showSection('${sectionId}')"]`);
    if (activeLink) {
        activeLink.classList.add('active');
    }
    
    // Load section-specific data
    loadSectionData(sectionId);
}

// Load section-specific data
function loadSectionData(sectionId) {
    switch(sectionId) {
        case 'dashboard':
            loadDashboardData();
            break;
        case 'my-projects':
            loadMyProjects();
            break;
        case 'project-reports':
            loadProjectReports();
            break;
        case 'documents':
            loadDocuments();
            break;
        case 'communications':
            loadCommunications();
            break;
        case 'billing':
            loadBilling();
            break;
        case 'support':
            loadSupport();
            break;
        case 'profile-settings':
            loadProfileSettings();
            break;
    }
}

// Logout function
function logout() {
    if (confirm('Are you sure you want to logout?')) {
        // Clear any stored authentication data
        localStorage.removeItem('clientToken');
        localStorage.removeItem('clientData');
        
        // Redirect to login page
        window.location.href = 'login.html';
    }
}

// Profile photo preview
function previewPhoto() {
    const fileInput = document.getElementById('profilePhoto');
    const preview = document.getElementById('profilePreview');
    const headerPhoto = document.getElementById('userPhoto');
    
    if (fileInput.files && fileInput.files[0]) {
        const reader = new FileReader();
        
        reader.onload = function(e) {
            preview.src = e.target.result;
            headerPhoto.src = e.target.result;
        };
        
        reader.readAsDataURL(fileInput.files[0]);
    }
}

// Update profile information
function updateProfile() {
    const clientData = {
        phone: document.getElementById('clientPhone').value,
        company: document.getElementById('clientCompany').value,
        industry: document.getElementById('clientIndustry').value,
        address: document.getElementById('clientAddress').value
    };

    // Validate required fields
    if (!clientData.phone.trim()) {
        alert('Phone number is required');
        return;
    }

    // Show loading state
    const updateBtn = document.querySelector('[onclick="updateProfile()"]');
    const originalText = updateBtn.textContent;
    updateBtn.textContent = 'Updating...';
    updateBtn.disabled = true;

    // Simulate API call
    setTimeout(() => {
        // Here you would make an actual API call to update the profile
        console.log('Updating profile:', clientData);
        
        // Show success message
        alert('Profile updated successfully!');
        
        // Reset button state
        updateBtn.textContent = originalText;
        updateBtn.disabled = false;
        
        // Store updated data locally for demonstration
        let storedData = JSON.parse(localStorage.getItem('clientData') || '{}');
        Object.assign(storedData, clientData);
        localStorage.setItem('clientData', JSON.stringify(storedData));
        
    }, 1500);
}

// Change password
function changePassword() {
    const newPassword = prompt('Enter new password:');
    if (newPassword && newPassword.length >= 6) {
        // Simulate password change
        setTimeout(() => {
            alert('Password changed successfully!');
        }, 500);
    } else if (newPassword) {
        alert('Password must be at least 6 characters long');
    }
}

// Filter documents by type
function filterDocuments(type) {
    const documents = document.querySelectorAll('.document-item');
    const filterBtns = document.querySelectorAll('.filter-btn');
    
    // Update filter button states
    filterBtns.forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    
    // Show/hide documents based on type
    documents.forEach(doc => {
        if (type === 'all' || doc.dataset.type === type) {
            doc.style.display = 'flex';
        } else {
            doc.style.display = 'none';
        }
    });
}

// Load client data on page load
function loadClientData() {
    // Load stored client data or use defaults
    const clientData = JSON.parse(localStorage.getItem('clientData') || '{}');
    
    // Set default values if no stored data
    const defaults = {
        name: 'John Anderson',
        clientId: 'CLI-2024-001',
        email: 'john.anderson@email.com',
        phone: '+1 (555) 123-4567',
        company: 'Anderson Consulting Inc.',
        industry: 'Technology Consulting',
        address: '123 Business Avenue\nSuite 456\nNew York, NY 10001'
    };
    
    // Merge defaults with stored data
    const data = { ...defaults, ...clientData };
    
    // Update header
    document.getElementById('userName').textContent = data.name;
    
    // Update profile form
    document.getElementById('clientName').value = data.name;
    document.getElementById('clientId').value = data.clientId;
    document.getElementById('clientEmail').value = data.email;
    document.getElementById('clientPhone').value = data.phone;
    document.getElementById('clientCompany').value = data.company;
    document.getElementById('clientIndustry').value = data.industry;
    document.getElementById('clientAddress').value = data.address;
}

// Dropdown menu functionality
function initDropdowns() {
    const dropdowns = document.querySelectorAll('.nav-item.dropdown');
    
    dropdowns.forEach(dropdown => {
        const link = dropdown.querySelector('.nav-link');
        const menu = dropdown.querySelector('.dropdown-menu');
        
        link.addEventListener('click', (e) => {
            e.preventDefault();
            
            // Close other dropdowns
            dropdowns.forEach(other => {
                if (other !== dropdown) {
                    other.classList.remove('active');
                }
            });
            
            // Toggle current dropdown
            dropdown.classList.toggle('active');
        });
    });
    
    // Close dropdowns when clicking outside
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.nav-item.dropdown')) {
            dropdowns.forEach(dropdown => {
                dropdown.classList.remove('active');
            });
        }
    });
}

// FAQ accordion functionality
function initFAQ() {
    const faqItems = document.querySelectorAll('.faq-item');
    
    faqItems.forEach(item => {
        const question = item.querySelector('.faq-question');
        const answer = item.querySelector('.faq-answer');
        
        question.addEventListener('click', () => {
            const isActive = item.classList.contains('active');
            
            // Close all FAQ items
            faqItems.forEach(faq => {
                faq.classList.remove('active');
            });
            
            // Open clicked item if it wasn't active
            if (!isActive) {
                item.classList.add('active');
            }
        });
    });
}

// Notification management
function updateNotificationCount(count) {
    const badge = document.getElementById('notificationCount');
    if (badge) {
        badge.textContent = count;
        badge.style.display = count > 0 ? 'inline' : 'none';
    }
}

// Service request functionality
function createServiceRequest() {
    const serviceType = prompt('What type of service do you need?');
    if (serviceType && serviceType.trim()) {
        // Simulate creating a service request
        setTimeout(() => {
            alert(`Service request for "${serviceType}" has been submitted. You will receive a confirmation email shortly.`);
            
            // Update pending requests count
            const pendingElement = document.getElementById('pendingRequests');
            if (pendingElement) {
                const currentCount = parseInt(pendingElement.textContent);
                pendingElement.textContent = currentCount + 1;
            }
        }, 500);
    }
}

// Document download simulation
function downloadDocument(documentName) {
    // Simulate document download
    console.log(`Downloading: ${documentName}`);
    alert(`Downloading ${documentName}...`);
}

// Initialize dashboard when page loads
document.addEventListener('DOMContentLoaded', function() {
    // Load client data
    loadClientData();
    
    // Initialize dropdown menus
    initDropdowns();
    
    // Initialize FAQ accordion
    initFAQ();
    
    // Add click handlers for document actions
    document.querySelectorAll('.document-item .btn-icon').forEach(btn => {
        btn.addEventListener('click', function() {
            const documentItem = this.closest('.document-item');
            const documentName = documentItem.querySelector('h4').textContent;
            
            if (this.title === 'Download') {
                downloadDocument(documentName);
            } else if (this.title === 'View') {
                // Simulate opening document viewer
                alert(`Opening ${documentName} in viewer...`);
            }
        });
    });
    
    // Add click handlers for service actions
    document.querySelectorAll('.service-item .btn-primary, .service-item .btn-secondary').forEach(btn => {
        btn.addEventListener('click', function() {
            const serviceItem = this.closest('.service-item');
            const serviceName = serviceItem.querySelector('h4').textContent;
            
            if (this.textContent.includes('Contact') || this.textContent.includes('View Details')) {
                alert(`Opening ${serviceName} details...`);
            } else if (this.textContent.includes('Upload')) {
                // Simulate file upload
                alert(`Opening file upload for ${serviceName}...`);
            }
        });
    });
    
    // Add click handlers for quick actions
    document.querySelectorAll('.action-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const action = this.textContent.trim();
            
            if (action === 'New Request') {
                createServiceRequest();
            } else if (action === 'Contact Support') {
                showSection('support');
            }
        });
    });
    
    // Add click handlers for support options
    document.querySelectorAll('.support-card .btn-primary').forEach(btn => {
        btn.addEventListener('click', function() {
            const supportType = this.textContent.trim();
            
            switch(supportType) {
                case 'Start Chat':
                    alert('Starting live chat... Please wait while we connect you to a support agent.');
                    break;
                case 'Send Email':
                    alert('Opening email form... You will be redirected to compose a support email.');
                    break;
                case 'Call Now':
                    alert('Support Phone: +1 (800) 555-HELP\nBusiness Hours: Mon-Fri 9AM-6PM EST');
                    break;
            }
        });
    });
    
    // Simulate real-time updates
    setInterval(() => {
        // Randomly update notification count
        if (Math.random() < 0.1) { // 10% chance every interval
            const currentCount = parseInt(document.getElementById('notificationCount').textContent);
            updateNotificationCount(currentCount + 1);
        }
    }, 30000); // Check every 30 seconds
    
    console.log('Client Dashboard initialized successfully');
});

// ===== ENHANCED FUNCTIONALITY =====

// Update user info in header
function updateUserInfo() {
    if (!currentClient) return;
    
    const nameElement = document.getElementById('clientName');
    const idElement = document.getElementById('clientId');
    const companyElement = document.getElementById('clientCompany');
    
    if (nameElement) nameElement.textContent = `${currentClient.firstName} ${currentClient.lastName}`;
    if (idElement) idElement.textContent = currentClient.clientId || 'CLI-001';
    if (companyElement) companyElement.textContent = currentClient.company || 'Client Company';
}

// Initialize event listeners
function initializeEventListeners() {
    // Close modals when clicking outside
    document.addEventListener('click', function(event) {
        if (event.target.classList.contains('modal-overlay')) {
            closeModal();
        }
    });
    
    // Close notification panel when clicking outside
    document.addEventListener('click', function(event) {
        if (!event.target.closest('.notification-container')) {
            const panel = document.getElementById('notificationPanel');
            if (panel) panel.classList.remove('show');
        }
    });
}

// Load dashboard data
function loadDashboardData() {
    const clientId = localStorage.getItem('clientId');
    if (!clientId) return;
    
    // Load dashboard statistics
    fetch(`${API_BASE_URL}/clients/${clientId}/dashboard-stats`)
        .then(response => response.json())
        .then(data => {
            updateDashboardStats(data);
        })
        .catch(error => {
            console.error('Error loading dashboard stats:', error);
            updateDashboardStats(getMockClientStats());
        });
    
    // Load recent projects
    loadRecentProjects();
    
    // Load recent activity
    loadRecentActivity();
}

// Update dashboard statistics
function updateDashboardStats(stats) {
    const elements = {
        'totalProjects': document.getElementById('totalProjects'),
        'activeProjects': document.getElementById('activeProjects'),
        'completedProjects': document.getElementById('completedProjects'),
        'totalSpent': document.getElementById('totalSpent')
    };
    
    if (elements.totalProjects) elements.totalProjects.textContent = stats.totalProjects || 0;
    if (elements.activeProjects) elements.activeProjects.textContent = stats.activeProjects || 0;
    if (elements.completedProjects) elements.completedProjects.textContent = stats.completedProjects || 0;
    if (elements.totalSpent) elements.totalSpent.textContent = `$${stats.totalSpent || 0}`;
}

// Load my projects
function loadMyProjects() {
    const clientId = localStorage.getItem('clientId');
    if (!clientId) return;
    
    fetch(`${API_BASE_URL}/clients/${clientId}/projects`)
        .then(response => response.json())
        .then(data => {
            displayProjects(data);
        })
        .catch(error => {
            console.error('Error loading projects:', error);
            displayProjects(getMockProjects());
        });
}

// Display projects
function displayProjects(projects) {
    const container = document.querySelector('.project-list');
    if (!container) return;
    
    if (projects.length === 0) {
        container.innerHTML = '<p>No projects found.</p>';
        return;
    }
    
    container.innerHTML = projects.map(project => `
        <div class="project-item" data-project-id="${project.id}">
            <div class="project-header">
                <h4>${project.name}</h4>
                <span class="status-badge status-${project.status.toLowerCase()}">${project.status}</span>
            </div>
            <p>${project.description}</p>
            <div class="project-meta">
                <span>Budget: $${project.budget}</span>
                <span>Progress: ${project.progress}%</span>
                <span>Due: ${new Date(project.dueDate).toLocaleDateString()}</span>
            </div>
            <div class="project-progress">
                <div class="progress-bar">
                    <div class="progress" style="width: ${project.progress}%"></div>
                </div>
            </div>
            <div class="project-actions">
                <button class="btn-small btn-primary" onclick="viewProject(${project.id})">View</button>
                <button class="btn-small btn-secondary" onclick="viewProjectReports(${project.id})">Reports</button>
                <button class="btn-small btn-success" onclick="contactProjectManager(${project.id})">Contact</button>
            </div>
        </div>
    `).join('');
}

// Load project reports
function loadProjectReports() {
    const clientId = localStorage.getItem('clientId');
    if (!clientId) return;
    
    fetch(`${API_BASE_URL}/clients/${clientId}/reports`)
        .then(response => response.json())
        .then(data => {
            displayReports(data);
        })
        .catch(error => {
            console.error('Error loading reports:', error);
            displayReports(getMockReports());
        });
}

// Display reports
function displayReports(reports) {
    const container = document.querySelector('.reports-list');
    if (!container) return;
    
    if (reports.length === 0) {
        container.innerHTML = '<p>No reports found.</p>';
        return;
    }
    
    container.innerHTML = reports.map(report => `
        <div class="report-item">
            <div class="report-header">
                <h4>${report.name}</h4>
                <span class="report-type">${report.type}</span>
            </div>
            <p>${report.description}</p>
            <div class="report-meta">
                <span>Generated: ${new Date(report.generatedDate).toLocaleDateString()}</span>
                <span>Project: ${report.project}</span>
            </div>
            <div class="report-actions">
                <button class="btn-small btn-primary" onclick="viewReport('${report.id}')">View</button>
                <button class="btn-small btn-secondary" onclick="downloadReport('${report.id}')">Download</button>
            </div>
        </div>
    `).join('');
}

// Load documents
function loadDocuments() {
    const clientId = localStorage.getItem('clientId');
    if (!clientId) return;
    
    fetch(`${API_BASE_URL}/clients/${clientId}/documents`)
        .then(response => response.json())
        .then(data => {
            displayDocuments(data);
        })
        .catch(error => {
            console.error('Error loading documents:', error);
            displayDocuments(getMockDocuments());
        });
}

// Display documents
function displayDocuments(documents) {
    const container = document.querySelector('.documents-list');
    if (!container) return;
    
    if (documents.length === 0) {
        container.innerHTML = '<p>No documents found.</p>';
        return;
    }
    
    container.innerHTML = documents.map(doc => `
        <div class="document-item" data-type="${doc.type}">
            <div class="document-icon">
                <i class="fas ${getDocumentIcon(doc.type)}"></i>
            </div>
            <div class="document-info">
                <h4>${doc.name}</h4>
                <p>${doc.description}</p>
                <div class="document-meta">
                    <span>Size: ${formatFileSize(doc.size)}</span>
                    <span>Uploaded: ${new Date(doc.uploadDate).toLocaleDateString()}</span>
                </div>
            </div>
            <div class="document-actions">
                <button class="btn-small btn-primary" onclick="viewDocument('${doc.id}')">View</button>
                <button class="btn-small btn-secondary" onclick="downloadDocument('${doc.id}')">Download</button>
            </div>
        </div>
    `).join('');
}

// Load communications
function loadCommunications() {
    const clientId = localStorage.getItem('clientId');
    if (!clientId) return;
    
    fetch(`${API_BASE_URL}/clients/${clientId}/communications`)
        .then(response => response.json())
        .then(data => {
            displayCommunications(data);
        })
        .catch(error => {
            console.error('Error loading communications:', error);
            displayCommunications(getMockCommunications());
        });
}

// Display communications
function displayCommunications(communications) {
    const container = document.querySelector('.communications-list');
    if (!container) return;
    
    if (communications.length === 0) {
        container.innerHTML = '<p>No communications found.</p>';
        return;
    }
    
    container.innerHTML = communications.map(comm => `
        <div class="communication-item">
            <div class="comm-header">
                <h4>${comm.subject}</h4>
                <span class="comm-type">${comm.type}</span>
            </div>
            <p>${comm.message}</p>
            <div class="comm-meta">
                <span>From: ${comm.from}</span>
                <span>Date: ${new Date(comm.date).toLocaleDateString()}</span>
                <span class="comm-status ${comm.status}">${comm.status}</span>
            </div>
            <div class="comm-actions">
                <button class="btn-small btn-primary" onclick="replyToMessage('${comm.id}')">Reply</button>
                <button class="btn-small btn-secondary" onclick="viewMessage('${comm.id}')">View</button>
            </div>
        </div>
    `).join('');
}

// Load billing
function loadBilling() {
    const clientId = localStorage.getItem('clientId');
    if (!clientId) return;
    
    fetch(`${API_BASE_URL}/clients/${clientId}/billing`)
        .then(response => response.json())
        .then(data => {
            displayBilling(data);
        })
        .catch(error => {
            console.error('Error loading billing:', error);
            displayBilling(getMockBilling());
        });
}

// Display billing
function displayBilling(billing) {
    const container = document.querySelector('.billing-list');
    if (!container) return;
    
    if (billing.length === 0) {
        container.innerHTML = '<p>No billing records found.</p>';
        return;
    }
    
    container.innerHTML = billing.map(bill => `
        <div class="billing-item">
            <div class="bill-header">
                <h4>Invoice #${bill.invoiceNumber}</h4>
                <span class="bill-status ${bill.status}">${bill.status}</span>
            </div>
            <div class="bill-details">
                <p><strong>Project:</strong> ${bill.project}</p>
                <p><strong>Amount:</strong> $${bill.amount}</p>
                <p><strong>Due Date:</strong> ${new Date(bill.dueDate).toLocaleDateString()}</p>
            </div>
            <div class="bill-actions">
                <button class="btn-small btn-primary" onclick="viewInvoice('${bill.id}')">View</button>
                <button class="btn-small btn-secondary" onclick="downloadInvoice('${bill.id}')">Download</button>
                ${bill.status === 'pending' ? `
                <button class="btn-small btn-success" onclick="payInvoice('${bill.id}')">Pay</button>
                ` : ''}
            </div>
        </div>
    `).join('');
}

// Load support
function loadSupport() {
    const clientId = localStorage.getItem('clientId');
    if (!clientId) return;
    
    fetch(`${API_BASE_URL}/clients/${clientId}/support-tickets`)
        .then(response => response.json())
        .then(data => {
            displaySupportTickets(data);
        })
        .catch(error => {
            console.error('Error loading support tickets:', error);
            displaySupportTickets(getMockSupportTickets());
        });
}

// Display support tickets
function displaySupportTickets(tickets) {
    const container = document.querySelector('.support-tickets-list');
    if (!container) return;
    
    if (tickets.length === 0) {
        container.innerHTML = '<p>No support tickets found.</p>';
        return;
    }
    
    container.innerHTML = tickets.map(ticket => `
        <div class="support-ticket-item">
            <div class="ticket-header">
                <h4>${ticket.subject}</h4>
                <span class="ticket-status ${ticket.status}">${ticket.status}</span>
            </div>
            <p>${ticket.description}</p>
            <div class="ticket-meta">
                <span>Priority: ${ticket.priority}</span>
                <span>Created: ${new Date(ticket.createdDate).toLocaleDateString()}</span>
                <span>Last Updated: ${new Date(ticket.lastUpdated).toLocaleDateString()}</span>
            </div>
            <div class="ticket-actions">
                <button class="btn-small btn-primary" onclick="viewTicket('${ticket.id}')">View</button>
                <button class="btn-small btn-secondary" onclick="updateTicket('${ticket.id}')">Update</button>
            </div>
        </div>
    `).join('');
}

// Load profile settings
function loadProfileSettings() {
    const clientId = localStorage.getItem('clientId');
    if (!clientId) return;
    
    fetch(`${API_BASE_URL}/clients/${clientId}/profile`)
        .then(response => response.json())
        .then(data => {
            updateProfileForm(data);
        })
        .catch(error => {
            console.error('Error loading profile:', error);
            updateProfileForm(getMockClientProfile());
        });
}

// Update profile form
function updateProfileForm(profile) {
    const elements = {
        'clientName': document.getElementById('clientName'),
        'clientEmail': document.getElementById('clientEmail'),
        'clientPhone': document.getElementById('clientPhone'),
        'clientCompany': document.getElementById('clientCompany'),
        'clientIndustry': document.getElementById('clientIndustry'),
        'clientAddress': document.getElementById('clientAddress')
    };
    
    if (elements.clientName) elements.clientName.value = profile.name || '';
    if (elements.clientEmail) elements.clientEmail.value = profile.email || '';
    if (elements.clientPhone) elements.clientPhone.value = profile.phone || '';
    if (elements.clientCompany) elements.clientCompany.value = profile.company || '';
    if (elements.clientIndustry) elements.clientIndustry.value = profile.industry || '';
    if (elements.clientAddress) elements.clientAddress.value = profile.address || '';
}

// Utility functions
function updateLastUpdated() {
    const lastUpdated = document.getElementById('lastUpdated');
    if (lastUpdated) {
        lastUpdated.textContent = new Date().toLocaleTimeString();
    }
}

function getDocumentIcon(type) {
    const icons = {
        'pdf': 'fa-file-pdf',
        'doc': 'fa-file-word',
        'xls': 'fa-file-excel',
        'ppt': 'fa-file-powerpoint',
        'image': 'fa-file-image'
    };
    return icons[type] || 'fa-file';
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function formatTimeAgo(timestamp) {
    const now = new Date();
    const time = new Date(timestamp);
    const diffInMinutes = Math.floor((now - time) / (1000 * 60));
    
    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes} minutes ago`;
    
    const diffInHours = Math.floor(diffInMinutes / 60);
    if (diffInHours < 24) return `${diffInHours} hours ago`;
    
    const diffInDays = Math.floor(diffInHours / 24);
    if (diffInDays < 7) return `${diffInDays} days ago`;
    
    return time.toLocaleDateString();
}

// Mock data functions
function getMockClientStats() {
    return {
        totalProjects: 8,
        activeProjects: 3,
        completedProjects: 5,
        totalSpent: 45000
    };
}

function getMockProjects() {
    return [
        {
            id: 1,
            name: 'Website Redesign',
            description: 'Complete redesign of company website with modern UI/UX',
            status: 'Active',
            budget: 15000,
            progress: 75,
            dueDate: '2024-06-30'
        },
        {
            id: 2,
            name: 'Mobile App Development',
            description: 'Development of iOS and Android mobile applications',
            status: 'Planning',
            budget: 25000,
            progress: 25,
            dueDate: '2024-12-31'
        }
    ];
}

function getMockReports() {
    return [
        {
            id: 1,
            name: 'Project Progress Report',
            type: 'Progress',
            description: 'Monthly progress report for active projects',
            generatedDate: '2024-03-20',
            project: 'Website Redesign'
        },
        {
            id: 2,
            name: 'Financial Summary',
            type: 'Financial',
            description: 'Quarterly financial summary and budget analysis',
            generatedDate: '2024-03-01',
            project: 'All Projects'
        }
    ];
}

function getMockDocuments() {
    return [
        {
            id: 1,
            name: 'Project Contract.pdf',
            description: 'Signed project contract and terms',
            type: 'pdf',
            size: 1024000,
            uploadDate: '2024-03-15'
        },
        {
            id: 2,
            name: 'Design Specifications.docx',
            description: 'Detailed design specifications document',
            type: 'doc',
            size: 512000,
            uploadDate: '2024-03-10'
        }
    ];
}

function getMockCommunications() {
    return [
        {
            id: 1,
            subject: 'Project Update - Website Redesign',
            type: 'Update',
            message: 'We have completed the homepage design and are ready for your review.',
            from: 'Project Manager',
            date: '2024-03-20',
            status: 'unread'
        },
        {
            id: 2,
            subject: 'Budget Approval Required',
            type: 'Request',
            message: 'Please review and approve the additional budget for mobile app features.',
            from: 'Finance Team',
            date: '2024-03-18',
            status: 'read'
        }
    ];
}

function getMockBilling() {
    return [
        {
            id: 1,
            invoiceNumber: 'INV-2024-001',
            status: 'paid',
            project: 'Website Redesign',
            amount: 7500,
            dueDate: '2024-03-15'
        },
        {
            id: 2,
            invoiceNumber: 'INV-2024-002',
            status: 'pending',
            project: 'Mobile App Development',
            amount: 12500,
            dueDate: '2024-04-15'
        }
    ];
}

function getMockSupportTickets() {
    return [
        {
            id: 1,
            subject: 'Login Issue',
            description: 'Unable to access the client portal with my credentials',
            status: 'open',
            priority: 'High',
            createdDate: '2024-03-20',
            lastUpdated: '2024-03-21'
        },
        {
            id: 2,
            subject: 'Feature Request',
            description: 'Request for additional reporting features in the dashboard',
            status: 'in-progress',
            priority: 'Medium',
            createdDate: '2024-03-15',
            lastUpdated: '2024-03-18'
        }
    ];
}

function getMockClientProfile() {
    return {
        name: 'John Anderson',
        email: 'john.anderson@email.com',
        phone: '+1 (555) 123-4567',
        company: 'Anderson Consulting Inc.',
        industry: 'Technology Consulting',
        address: '123 Business Avenue\nSuite 456\nNew York, NY 10001'
    };
}

// Action functions (to be implemented)
function viewProject(projectId) {
    showNotification(`Viewing project ${projectId}`, 'info');
}

function viewProjectReports(projectId) {
    showNotification(`Viewing reports for project ${projectId}`, 'info');
}

function contactProjectManager(projectId) {
    showNotification(`Contacting project manager for project ${projectId}`, 'info');
}

function viewReport(reportId) {
    showNotification(`Viewing report ${reportId}`, 'info');
}

function downloadReport(reportId) {
    showNotification(`Downloading report ${reportId}`, 'success');
}

function viewDocument(docId) {
    showNotification(`Viewing document ${docId}`, 'info');
}

function replyToMessage(messageId) {
    showNotification(`Replying to message ${messageId}`, 'info');
}

function viewMessage(messageId) {
    showNotification(`Viewing message ${messageId}`, 'info');
}

function viewInvoice(invoiceId) {
    showNotification(`Viewing invoice ${invoiceId}`, 'info');
}

function downloadInvoice(invoiceId) {
    showNotification(`Downloading invoice ${invoiceId}`, 'success');
}

function payInvoice(invoiceId) {
    showNotification(`Processing payment for invoice ${invoiceId}`, 'success');
}

function viewTicket(ticketId) {
    showNotification(`Viewing support ticket ${ticketId}`, 'info');
}

function updateTicket(ticketId) {
    showNotification(`Updating support ticket ${ticketId}`, 'info');
}

function loadRecentProjects() {
    // Implementation for loading recent projects
}

function loadRecentActivity() {
    // Implementation for loading recent activity
}
