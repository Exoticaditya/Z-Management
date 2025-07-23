// Employee Dashboard JavaScript Functions

// API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Global variables
let currentEmployee = null;
let currentSection = 'dashboard';
let timeTrackingInterval = null;
let currentTask = null;

// Initialize dashboard
document.addEventListener('DOMContentLoaded', function() {
    checkAuthentication();
    loadEmployeeData();
    showSection('dashboard');
    initializeEventListeners();
    updateLastUpdated();
    setInterval(updateLastUpdated, 60000);
});

// Check authentication
function checkAuthentication() {
    const employeeToken = localStorage.getItem('employeeToken');
    if (!employeeToken) {
        window.location.href = '../login.html';
        return;
    }
    
    // Validate token with backend
    fetch(`${API_BASE_URL}/employees/validate-token`, {
        headers: {
            'Authorization': `Bearer ${employeeToken}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Invalid token');
        }
        return response.json();
    })
    .then(data => {
        currentEmployee = data;
        updateUserInfo();
    })
    .catch(error => {
        console.error('Authentication failed:', error);
        localStorage.removeItem('employeeToken');
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
        case 'my-tasks':
            loadMyTasks();
            break;
        case 'assigned-projects':
            loadAssignedProjects();
            break;
        case 'time-tracking':
            loadTimeTrackingData();
            break;
        case 'reports':
            loadReports();
            break;
        case 'documents':
            loadDocuments();
            break;
        case 'team-collaboration':
            loadTeamCollaboration();
            break;
        case 'leave-management':
            loadLeaveManagement();
            break;
    }
}

// Logout function
function logout() {
    if (confirm('Are you sure you want to logout?')) {
        // Clear any stored authentication data
        localStorage.removeItem('employeeToken');
        localStorage.removeItem('employeeData');
        
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
    const employeeData = {
        phone: document.getElementById('employeePhone').value,
        department: document.getElementById('employeeDepartment').value,
        position: document.getElementById('employeePosition').value,
        emergencyContact: document.getElementById('emergencyContact').value,
        skills: document.getElementById('employeeSkills').value
    };

    // Validate required fields
    if (!employeeData.phone.trim()) {
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
        console.log('Updating profile:', employeeData);
        
        // Show success message
        alert('Profile updated successfully!');
        
        // Reset button state
        updateBtn.textContent = originalText;
        updateBtn.disabled = false;
        
        // Store updated data locally for demonstration
        let storedData = JSON.parse(localStorage.getItem('employeeData') || '{}');
        Object.assign(storedData, employeeData);
        localStorage.setItem('employeeData', JSON.stringify(storedData));
        
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

// Task management functions
function updateTaskStatus(taskId, status) {
    const taskElement = document.querySelector(`[data-task-id="${taskId}"]`);
    if (taskElement) {
        const statusBadge = taskElement.querySelector('.status-badge');
        const progressBar = taskElement.querySelector('.progress');
        
        // Update status badge
        statusBadge.className = `status-badge status-${status.toLowerCase()}`;
        statusBadge.textContent = status;
        
        // Update progress based on status
        let progress = 0;
        switch(status.toLowerCase()) {
            case 'pending': progress = 0; break;
            case 'in progress': progress = 50; break;
            case 'review': progress = 80; break;
            case 'completed': progress = 100; break;
        }
        
        if (progressBar) {
            progressBar.style.width = `${progress}%`;
        }
        
        // Show notification
        showNotification(`Task status updated to ${status}`, 'success');
    }
}

function addTask() {
    const title = prompt('Enter task title:');
    const description = prompt('Enter task description:');
    
    if (title && title.trim()) {
        const taskList = document.querySelector('.task-list');
        const taskId = 'task-' + Date.now();
        
        const taskHTML = `
            <div class="task-item" data-task-id="${taskId}">
                <div class="task-header">
                    <h4>${title}</h4>
                    <span class="status-badge status-pending">Pending</span>
                </div>
                <p>${description || 'No description provided'}</p>
                <div class="task-meta">
                    <span class="priority-high">High Priority</span>
                    <span class="due-date">Due: ${new Date(Date.now() + 7*24*60*60*1000).toLocaleDateString()}</span>
                </div>
                <div class="task-progress">
                    <div class="progress-bar">
                        <div class="progress" style="width: 0%"></div>
                    </div>
                </div>
                <div class="task-actions">
                    <button class="btn-small btn-primary" onclick="updateTaskStatus('${taskId}', 'In Progress')">Start</button>
                    <button class="btn-small btn-secondary" onclick="updateTaskStatus('${taskId}', 'Completed')">Complete</button>
                </div>
            </div>
        `;
        
        taskList.insertAdjacentHTML('afterbegin', taskHTML);
        showNotification('Task added successfully!', 'success');
    }
}

// File management functions
function uploadFile() {
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.multiple = true;
    
    fileInput.onchange = function(e) {
        const files = Array.from(e.target.files);
        
        files.forEach(file => {
            // Simulate file upload
            const fileItem = createFileItem(file.name, file.size);
            document.querySelector('.file-list').appendChild(fileItem);
        });
        
        showNotification(`${files.length} file(s) uploaded successfully!`, 'success');
    };
    
    fileInput.click();
}

function createFileItem(fileName, fileSize) {
    const fileItem = document.createElement('div');
    fileItem.className = 'file-item';
    
    const fileSizeKB = Math.round(fileSize / 1024);
    const uploadDate = new Date().toLocaleDateString();
    
    fileItem.innerHTML = `
        <div class="file-icon">📄</div>
        <div class="file-info">
            <h4>${fileName}</h4>
            <p>Size: ${fileSizeKB} KB | Uploaded: ${uploadDate}</p>
        </div>
        <div class="file-actions">
            <button class="btn-icon" title="Download" onclick="downloadFile('${fileName}')">⬇️</button>
            <button class="btn-icon" title="Share" onclick="shareFile('${fileName}')">📤</button>
            <button class="btn-icon" title="Delete" onclick="deleteFile(this, '${fileName}')">🗑️</button>
        </div>
    `;
    
    return fileItem;
}

function downloadFile(fileName) {
    console.log(`Downloading: ${fileName}`);
    showNotification(`Downloading ${fileName}...`, 'info');
}

function shareFile(fileName) {
    const recipient = prompt('Enter email address to share with:');
    if (recipient && recipient.includes('@')) {
        showNotification(`File ${fileName} shared with ${recipient}`, 'success');
    } else if (recipient) {
        alert('Please enter a valid email address');
    }
}

function deleteFile(buttonElement, fileName) {
    if (confirm(`Are you sure you want to delete ${fileName}?`)) {
        const fileItem = buttonElement.closest('.file-item');
        fileItem.remove();
        showNotification(`File ${fileName} deleted`, 'warning');
    }
}

// Report generation
function generateReport(reportType) {
    const reportData = {
        type: reportType,
        dateRange: '2024-01-01 to 2024-12-31',
        generated: new Date().toLocaleString()
    };
    
    showNotification(`Generating ${reportType} report...`, 'info');
    
    // Simulate report generation
    setTimeout(() => {
        // Add report to recent reports list
        const reportsList = document.querySelector('.reports-list');
        const reportItem = document.createElement('div');
        reportItem.className = 'report-item';
        
        reportItem.innerHTML = `
            <div class="report-icon">📊</div>
            <div class="report-info">
                <h4>${reportType} Report</h4>
                <p>Generated: ${reportData.generated}</p>
                <p>Period: ${reportData.dateRange}</p>
            </div>
            <div class="report-actions">
                <button class="btn-small btn-primary" onclick="downloadReport('${reportType}')">Download</button>
                <button class="btn-small btn-secondary" onclick="viewReport('${reportType}')">View</button>
            </div>
        `;
        
        reportsList.insertAdjacentElement('afterbegin', reportItem);
        showNotification(`${reportType} report generated successfully!`, 'success');
    }, 2000);
}

function downloadReport(reportType) {
    showNotification(`Downloading ${reportType} report...`, 'info');
}

function viewReport(reportType) {
    showNotification(`Opening ${reportType} report viewer...`, 'info');
}

// Notification system
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    
    // Add to notification container
    let container = document.querySelector('.notification-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'notification-container';
        document.body.appendChild(container);
    }
    
    container.appendChild(notification);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        notification.remove();
    }, 3000);
    
    // Update notification count
    updateNotificationCount();
}

function updateNotificationCount() {
    const badge = document.getElementById('notificationCount');
    if (badge) {
        const currentCount = parseInt(badge.textContent) || 0;
        badge.textContent = currentCount + 1;
        badge.style.display = 'inline';
    }
}

// Load employee data
function loadEmployeeData() {
    // Load stored employee data or use defaults
    const employeeData = JSON.parse(localStorage.getItem('employeeData') || '{}');
    
    // Set default values if no stored data
    const defaults = {
        name: 'Sarah Johnson',
        employeeId: 'EMP-2024-089',
        email: 'sarah.johnson@company.com',
        phone: '+1 (555) 987-6543',
        department: 'Technology',
        position: 'Senior Developer',
        emergencyContact: '+1 (555) 123-0000',
        skills: 'JavaScript, Python, React, Node.js'
    };
    
    // Merge defaults with stored data
    const data = { ...defaults, ...employeeData };
    
    // Update header
    document.getElementById('userName').textContent = data.name;
    
    // Update profile form if elements exist
    const nameField = document.getElementById('employeeName');
    if (nameField) nameField.value = data.name;
    
    const idField = document.getElementById('employeeId');
    if (idField) idField.value = data.employeeId;
    
    const emailField = document.getElementById('employeeEmail');
    if (emailField) emailField.value = data.email;
    
    const phoneField = document.getElementById('employeePhone');
    if (phoneField) phoneField.value = data.phone;
    
    const deptField = document.getElementById('employeeDepartment');
    if (deptField) deptField.value = data.department;
    
    const posField = document.getElementById('employeePosition');
    if (posField) posField.value = data.position;
    
    const emergencyField = document.getElementById('emergencyContact');
    if (emergencyField) emergencyField.value = data.emergencyContact;
    
    const skillsField = document.getElementById('employeeSkills');
    if (skillsField) skillsField.value = data.skills;
}

// Dropdown menu functionality
function initDropdowns() {
    const dropdowns = document.querySelectorAll('.nav-item.dropdown');
    
    dropdowns.forEach(dropdown => {
        const link = dropdown.querySelector('.nav-link');
        
        if (link) {
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
        }
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

// Department info management
function updateDepartmentInfo() {
    const updates = {
        teamSize: document.getElementById('teamSize').value,
        currentProjects: document.getElementById('currentProjects').value,
        budget: document.getElementById('departmentBudget').value
    };
    
    showNotification('Department information updated successfully!', 'success');
    console.log('Department updates:', updates);
}

// Time tracking
function startTimeTracking(taskName) {
    const startTime = new Date();
    localStorage.setItem('timeTrackingTask', taskName);
    localStorage.setItem('timeTrackingStart', startTime.toISOString());
    
    showNotification(`Time tracking started for: ${taskName}`, 'info');
    
    // Update UI to show tracking is active
    const trackingElement = document.getElementById('activeTimeTracking');
    if (trackingElement) {
        trackingElement.textContent = `Tracking: ${taskName}`;
        trackingElement.style.display = 'block';
    }
}

function stopTimeTracking() {
    const taskName = localStorage.getItem('timeTrackingTask');
    const startTime = new Date(localStorage.getItem('timeTrackingStart'));
    const endTime = new Date();
    
    if (taskName && startTime) {
        const duration = Math.round((endTime - startTime) / 1000 / 60); // minutes
        
        showNotification(`Time tracking stopped. Duration: ${duration} minutes`, 'success');
        
        // Clear tracking data
        localStorage.removeItem('timeTrackingTask');
        localStorage.removeItem('timeTrackingStart');
        
        // Update UI
        const trackingElement = document.getElementById('activeTimeTracking');
        if (trackingElement) {
            trackingElement.style.display = 'none';
        }
    }
}

// Initialize dashboard when page loads
document.addEventListener('DOMContentLoaded', function() {
    // Load employee data
    loadEmployeeData();
    
    // Initialize dropdown menus
    initDropdowns();
    
    // Add styles for notifications if not already present
    if (!document.querySelector('#notification-styles')) {
        const styles = document.createElement('style');
        styles.id = 'notification-styles';
        styles.textContent = `
            .notification-container {
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 10000;
                max-width: 300px;
            }
            
            .notification {
                background: #f8f9fa;
                border: 1px solid #dee2e6;
                border-radius: 4px;
                padding: 12px 16px;
                margin-bottom: 8px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                animation: slideIn 0.3s ease-out;
            }
            
            .notification-success {
                background: #d4edda;
                border-color: #c3e6cb;
                color: #155724;
            }
            
            .notification-warning {
                background: #fff3cd;
                border-color: #ffeaa7;
                color: #856404;
            }
            
            .notification-info {
                background: #d1ecf1;
                border-color: #bee5eb;
                color: #0c5460;
            }
            
            @keyframes slideIn {
                from { transform: translateX(100%); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
        `;
        document.head.appendChild(styles);
    }
    
    // Check for active time tracking on page load
    const activeTask = localStorage.getItem('timeTrackingTask');
    if (activeTask) {
        const trackingElement = document.getElementById('activeTimeTracking');
        if (trackingElement) {
            trackingElement.textContent = `Tracking: ${activeTask}`;
            trackingElement.style.display = 'block';
        }
    }
    
    // Add click handlers for various interactive elements
    document.querySelectorAll('.action-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const action = this.textContent.trim();
            
            if (action === 'Add Task') {
                addTask();
            } else if (action === 'Upload File') {
                uploadFile();
            } else if (action === 'Generate Report') {
                // Show report type selection
                const reportType = prompt('Select report type:\n1. Performance\n2. Activity\n3. Department\n4. Custom\n\nEnter number (1-4):');
                const types = ['', 'Performance', 'Activity', 'Department', 'Custom'];
                if (reportType && types[reportType]) {
                    generateReport(types[reportType]);
                }
            }
        });
    });
    
    // Simulate real-time updates
    setInterval(() => {
        // Randomly update notification count
        if (Math.random() < 0.1) { // 10% chance every interval
            updateNotificationCount();
        }
    }, 30000); // Check every 30 seconds
    
    console.log('Employee Dashboard initialized successfully');
});

// ===== ENHANCED FUNCTIONALITY =====

// Update user info in header
function updateUserInfo() {
    if (!currentEmployee) return;
    
    const nameElement = document.getElementById('employeeName');
    const idElement = document.getElementById('employeeId');
    const deptElement = document.getElementById('employeeDepartment');
    
    if (nameElement) nameElement.textContent = `${currentEmployee.firstName} ${currentEmployee.lastName}`;
    if (idElement) idElement.textContent = currentEmployee.employeeId || 'EMP-001';
    if (deptElement) deptElement.textContent = currentEmployee.department || 'Technology';
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
    const employeeId = localStorage.getItem('employeeId');
    if (!employeeId) return;
    
    // Load dashboard statistics
    fetch(`${API_BASE_URL}/employees/${employeeId}/dashboard-stats`)
        .then(response => response.json())
        .then(data => {
            updateDashboardStats(data);
        })
        .catch(error => {
            console.error('Error loading dashboard stats:', error);
            updateDashboardStats(getMockEmployeeStats());
        });
    
    // Load recent tasks
    loadRecentTasks();
    
    // Load recent activity
    loadRecentActivity();
}

// Update dashboard statistics
function updateDashboardStats(stats) {
    const elements = {
        'totalTasks': document.getElementById('totalTasks'),
        'completedTasks': document.getElementById('completedTasks'),
        'activeProjects': document.getElementById('activeProjects'),
        'hoursWorked': document.getElementById('hoursWorked')
    };
    
    if (elements.totalTasks) elements.totalTasks.textContent = stats.totalTasks || 0;
    if (elements.completedTasks) elements.completedTasks.textContent = stats.completedTasks || 0;
    if (elements.activeProjects) elements.activeProjects.textContent = stats.activeProjects || 0;
    if (elements.hoursWorked) elements.hoursWorked.textContent = `${stats.hoursWorked || 0}h`;
}

// Load my tasks
function loadMyTasks() {
    const employeeId = localStorage.getItem('employeeId');
    if (!employeeId) return;
    
    fetch(`${API_BASE_URL}/employees/${employeeId}/tasks`)
        .then(response => response.json())
        .then(data => {
            displayTasks(data);
        })
        .catch(error => {
            console.error('Error loading tasks:', error);
            displayTasks(getMockTasks());
        });
}

// Display tasks
function displayTasks(tasks) {
    const container = document.querySelector('.task-list');
    if (!container) return;
    
    if (tasks.length === 0) {
        container.innerHTML = '<p>No tasks found.</p>';
        return;
    }
    
    container.innerHTML = tasks.map(task => `
        <div class="task-item" data-task-id="${task.id}">
            <div class="task-header">
                <h4>${task.title}</h4>
                <span class="status-badge status-${task.status.toLowerCase()}">${task.status}</span>
            </div>
            <p>${task.description}</p>
            <div class="task-meta">
                <span class="priority-${task.priority.toLowerCase()}">${task.priority} Priority</span>
                <span class="due-date">Due: ${new Date(task.dueDate).toLocaleDateString()}</span>
            </div>
            <div class="task-progress">
                <div class="progress-bar">
                    <div class="progress" style="width: ${task.progress}%"></div>
                </div>
            </div>
            <div class="task-actions">
                <button class="btn-small btn-primary" onclick="updateTaskStatus('${task.id}', 'In Progress')">Start</button>
                <button class="btn-small btn-secondary" onclick="updateTaskStatus('${task.id}', 'Completed')">Complete</button>
                <button class="btn-small btn-success" onclick="startTimeTracking('${task.title}')">Track Time</button>
            </div>
        </div>
    `).join('');
}

// Load assigned projects
function loadAssignedProjects() {
    const employeeId = localStorage.getItem('employeeId');
    if (!employeeId) return;
    
    fetch(`${API_BASE_URL}/employees/${employeeId}/projects`)
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
        <div class="project-item">
            <div class="project-header">
                <h4>${project.name}</h4>
                <span class="status-badge status-${project.status.toLowerCase()}">${project.status}</span>
            </div>
            <p>${project.description}</p>
            <div class="project-meta">
                <span>Client: ${project.client}</span>
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
                <button class="btn-small btn-secondary" onclick="viewProjectTasks(${project.id})">Tasks</button>
            </div>
        </div>
    `).join('');
}

// Enhanced time tracking
function startTimeTracking(taskName) {
    if (timeTrackingInterval) {
        stopTimeTracking();
    }
    
    currentTask = taskName;
    const startTime = new Date();
    
    // Update UI
    const trackingBtn = document.querySelector(`[onclick="startTimeTracking('${taskName}')"]`);
    if (trackingBtn) {
        trackingBtn.textContent = 'Stop Tracking';
        trackingBtn.onclick = () => stopTimeTracking();
        trackingBtn.classList.remove('btn-success');
        trackingBtn.classList.add('btn-danger');
    }
    
    // Start timer
    timeTrackingInterval = setInterval(() => {
        const elapsed = Math.floor((new Date() - startTime) / 1000);
        const hours = Math.floor(elapsed / 3600);
        const minutes = Math.floor((elapsed % 3600) / 60);
        const seconds = elapsed % 60;
        
        // Update timer display
        const timerDisplay = document.getElementById('timerDisplay');
        if (timerDisplay) {
            timerDisplay.textContent = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        }
    }, 1000);
    
    showNotification(`Started tracking time for: ${taskName}`, 'success');
}

function stopTimeTracking() {
    if (timeTrackingInterval) {
        clearInterval(timeTrackingInterval);
        timeTrackingInterval = null;
        
        // Reset UI
        const trackingBtn = document.querySelector('.btn-danger');
        if (trackingBtn) {
            trackingBtn.textContent = 'Track Time';
            trackingBtn.onclick = () => startTimeTracking(currentTask);
            trackingBtn.classList.remove('btn-danger');
            trackingBtn.classList.add('btn-success');
        }
        
        // Save time entry
        if (currentTask) {
            saveTimeEntry(currentTask);
            currentTask = null;
        }
        
        showNotification('Time tracking stopped', 'info');
    }
}

function saveTimeEntry(taskName) {
    const employeeId = localStorage.getItem('employeeId');
    if (!employeeId) return;
    
    const timeEntry = {
        taskName: taskName,
        startTime: new Date().toISOString(),
        endTime: new Date().toISOString(),
        duration: 0 // Calculate duration
    };
    
    fetch(`${API_BASE_URL}/employees/${employeeId}/time-entries`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(timeEntry)
    })
    .then(response => response.json())
    .then(data => {
        showNotification('Time entry saved successfully', 'success');
    })
    .catch(error => {
        console.error('Error saving time entry:', error);
        showNotification('Failed to save time entry', 'error');
    });
}

// Load time tracking data
function loadTimeTrackingData() {
    const employeeId = localStorage.getItem('employeeId');
    if (!employeeId) return;
    
    fetch(`${API_BASE_URL}/employees/${employeeId}/time-entries`)
        .then(response => response.json())
        .then(data => {
            displayTimeEntries(data);
        })
        .catch(error => {
            console.error('Error loading time entries:', error);
            displayTimeEntries(getMockTimeEntries());
        });
}

// Display time entries
function displayTimeEntries(entries) {
    const container = document.querySelector('.time-entries-list');
    if (!container) return;
    
    if (entries.length === 0) {
        container.innerHTML = '<p>No time entries found.</p>';
        return;
    }
    
    container.innerHTML = entries.map(entry => `
        <div class="time-entry-item">
            <div class="entry-header">
                <h4>${entry.taskName}</h4>
                <span class="entry-duration">${entry.duration}h</span>
            </div>
            <div class="entry-meta">
                <span>Date: ${new Date(entry.date).toLocaleDateString()}</span>
                <span>Project: ${entry.project}</span>
            </div>
        </div>
    `).join('');
}

// Load reports
function loadReports() {
    const employeeId = localStorage.getItem('employeeId');
    if (!employeeId) return;
    
    fetch(`${API_BASE_URL}/employees/${employeeId}/reports`)
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
                <span>Period: ${report.period}</span>
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
    const employeeId = localStorage.getItem('employeeId');
    if (!employeeId) return;
    
    fetch(`${API_BASE_URL}/employees/${employeeId}/documents`)
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
        <div class="document-item">
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

// Load team collaboration
function loadTeamCollaboration() {
    const employeeId = localStorage.getItem('employeeId');
    if (!employeeId) return;
    
    fetch(`${API_BASE_URL}/employees/${employeeId}/team`)
        .then(response => response.json())
        .then(data => {
            displayTeamMembers(data);
        })
        .catch(error => {
            console.error('Error loading team data:', error);
            displayTeamMembers(getMockTeamMembers());
        });
}

// Display team members
function displayTeamMembers(members) {
    const container = document.querySelector('.team-list');
    if (!container) return;
    
    if (members.length === 0) {
        container.innerHTML = '<p>No team members found.</p>';
        return;
    }
    
    container.innerHTML = members.map(member => `
        <div class="team-member-item">
            <div class="member-avatar">
                <img src="${member.avatar || 'https://via.placeholder.com/50x50/3498db/ffffff?text=' + member.name.charAt(0)}" alt="${member.name}">
            </div>
            <div class="member-info">
                <h4>${member.name}</h4>
                <p>${member.position}</p>
                <span class="member-status ${member.status}">${member.status}</span>
            </div>
            <div class="member-actions">
                <button class="btn-small btn-primary" onclick="messageTeamMember('${member.id}')">Message</button>
                <button class="btn-small btn-secondary" onclick="viewMemberProfile('${member.id}')">Profile</button>
            </div>
        </div>
    `).join('');
}

// Load leave management
function loadLeaveManagement() {
    const employeeId = localStorage.getItem('employeeId');
    if (!employeeId) return;
    
    fetch(`${API_BASE_URL}/employees/${employeeId}/leave-requests`)
        .then(response => response.json())
        .then(data => {
            displayLeaveRequests(data);
        })
        .catch(error => {
            console.error('Error loading leave requests:', error);
            displayLeaveRequests(getMockLeaveRequests());
        });
}

// Display leave requests
function displayLeaveRequests(requests) {
    const container = document.querySelector('.leave-requests-list');
    if (!container) return;
    
    if (requests.length === 0) {
        container.innerHTML = '<p>No leave requests found.</p>';
        return;
    }
    
    container.innerHTML = requests.map(request => `
        <div class="leave-request-item">
            <div class="request-header">
                <h4>${request.type} Leave</h4>
                <span class="status-badge status-${request.status.toLowerCase()}">${request.status}</span>
            </div>
            <div class="request-details">
                <p><strong>From:</strong> ${new Date(request.startDate).toLocaleDateString()}</p>
                <p><strong>To:</strong> ${new Date(request.endDate).toLocaleDateString()}</p>
                <p><strong>Reason:</strong> ${request.reason}</p>
            </div>
            <div class="request-actions">
                ${request.status === 'pending' ? `
                <button class="btn-small btn-danger" onclick="cancelLeaveRequest('${request.id}')">Cancel</button>
                ` : ''}
                <button class="btn-small btn-secondary" onclick="viewLeaveRequest('${request.id}')">Details</button>
            </div>
        </div>
    `).join('');
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
function getMockEmployeeStats() {
    return {
        totalTasks: 15,
        completedTasks: 8,
        activeProjects: 3,
        hoursWorked: 42
    };
}

function getMockTasks() {
    return [
        {
            id: 1,
            title: 'Website Redesign - Homepage',
            description: 'Update the homepage design according to new requirements',
            status: 'In Progress',
            priority: 'High',
            dueDate: '2024-04-15',
            progress: 60
        },
        {
            id: 2,
            title: 'Database Optimization',
            description: 'Optimize database queries for better performance',
            status: 'Pending',
            priority: 'Medium',
            dueDate: '2024-04-20',
            progress: 0
        }
    ];
}

function getMockProjects() {
    return [
        {
            id: 1,
            name: 'Website Redesign',
            description: 'Complete redesign of company website',
            status: 'Active',
            client: 'ABC Company',
            progress: 75,
            dueDate: '2024-06-30'
        },
        {
            id: 2,
            name: 'Mobile App Development',
            description: 'Development of iOS and Android mobile applications',
            status: 'Planning',
            client: 'XYZ Corp',
            progress: 25,
            dueDate: '2024-12-31'
        }
    ];
}

function getMockTimeEntries() {
    return [
        {
            taskName: 'Website Redesign - Homepage',
            duration: 4.5,
            date: '2024-03-20',
            project: 'Website Redesign'
        },
        {
            taskName: 'Database Optimization',
            duration: 3.0,
            date: '2024-03-19',
            project: 'System Maintenance'
        }
    ];
}

function getMockReports() {
    return [
        {
            id: 1,
            name: 'Weekly Progress Report',
            type: 'Progress',
            description: 'Weekly progress summary for assigned projects',
            generatedDate: '2024-03-20',
            period: 'Week 12'
        },
        {
            id: 2,
            name: 'Time Tracking Summary',
            type: 'Time',
            description: 'Monthly time tracking and productivity report',
            generatedDate: '2024-03-01',
            period: 'March 2024'
        }
    ];
}

function getMockDocuments() {
    return [
        {
            id: 1,
            name: 'Project Requirements.pdf',
            description: 'Detailed project requirements document',
            type: 'pdf',
            size: 2048576,
            uploadDate: '2024-03-15'
        },
        {
            id: 2,
            name: 'Design Mockups.pptx',
            description: 'Presentation with design mockups',
            type: 'ppt',
            size: 5120000,
            uploadDate: '2024-03-10'
        }
    ];
}

function getMockTeamMembers() {
    return [
        {
            id: 1,
            name: 'John Smith',
            position: 'Senior Developer',
            status: 'online',
            avatar: null
        },
        {
            id: 2,
            name: 'Sarah Johnson',
            position: 'UI/UX Designer',
            status: 'away',
            avatar: null
        }
    ];
}

function getMockLeaveRequests() {
    return [
        {
            id: 1,
            type: 'Annual',
            status: 'Approved',
            startDate: '2024-04-15',
            endDate: '2024-04-19',
            reason: 'Family vacation'
        },
        {
            id: 2,
            type: 'Sick',
            status: 'Pending',
            startDate: '2024-03-25',
            endDate: '2024-03-26',
            reason: 'Medical appointment'
        }
    ];
}

// Action functions (to be implemented)
function viewProject(projectId) {
    showNotification(`Viewing project ${projectId}`, 'info');
}

function viewProjectTasks(projectId) {
    showNotification(`Viewing tasks for project ${projectId}`, 'info');
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

function downloadDocument(docId) {
    showNotification(`Downloading document ${docId}`, 'success');
}

function messageTeamMember(memberId) {
    showNotification(`Messaging team member ${memberId}`, 'info');
}

function viewMemberProfile(memberId) {
    showNotification(`Viewing profile for member ${memberId}`, 'info');
}

function cancelLeaveRequest(requestId) {
    if (confirm('Are you sure you want to cancel this leave request?')) {
        showNotification(`Cancelled leave request ${requestId}`, 'success');
    }
}

function viewLeaveRequest(requestId) {
    showNotification(`Viewing leave request ${requestId}`, 'info');
}

function loadRecentTasks() {
    // Implementation for loading recent tasks
}

function loadRecentActivity() {
    // Implementation for loading recent activity
}
