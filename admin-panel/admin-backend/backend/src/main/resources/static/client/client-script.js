// Z+ Client Dashboard Script
// Handles section switching, data fetching, and UI updates for the client dashboard

const API_BASE_URL = 'http://localhost:8080/api';
let currentSection = 'dashboard';
let currentUser = null;

document.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('zplusUser'));
    if (!token || !user) {
        window.location.href = '../index/index.html';
        return;
    }
    currentUser = user;
    document.getElementById('clientName').textContent = user.fullName || 'Client';
    loadContent('dashboard');
    initializeEventListeners();
    updateLastUpdated();
    setInterval(updateLastUpdated, 60000);
});

function initializeEventListeners() {
    document.querySelector('.navbar-container').addEventListener('click', (e) => {
        const link = e.target.closest('button.nav-link');
        if (!link) return;
        const section = link.textContent.trim().toLowerCase().replace(/ /g, '-');
        loadContent(section);
    });
}

function loadContent(section) {
    currentSection = section;
    document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
    const activeBtn = Array.from(document.querySelectorAll('.nav-link')).find(btn => btn.textContent.trim().toLowerCase().replace(/ /g, '-') === section);
    if (activeBtn) activeBtn.classList.add('active');
    updateBreadcrumb(section);
    updateContentTitle(section);
    loadSectionContent(section);
}

function updateBreadcrumb(section) {
    document.getElementById('breadcrumb').innerHTML = `<span>Dashboard</span> &nbsp;/&nbsp; <span>${getSectionDisplayName(section)}</span>`;
}

function updateContentTitle(section) {
    document.getElementById('contentTitle').textContent = getSectionDisplayName(section);
}

function loadSectionContent(section) {
    const contentBody = document.getElementById('contentBody');
    switch(section) {
        case 'dashboard':
            loadDashboardOverview(contentBody);
            break;
        case 'my-projects':
            loadMyProjects(contentBody);
            break;
        case 'project-reports':
            loadProjectReports(contentBody);
            break;
        case 'documents':
            loadDocuments(contentBody);
            break;
        case 'communications':
            loadCommunications(contentBody);
            break;
        case 'billing':
            loadBilling(contentBody);
            break;
        case 'support':
            loadSupport(contentBody);
            break;
        case 'profile-settings':
            loadProfileSettings(contentBody);
            break;
        default:
            contentBody.innerHTML = `<div class='section-placeholder'><h3>${getSectionDisplayName(section)}</h3><p>This section is under construction.</p></div>`;
    }
}

function getSectionDisplayName(section) {
    const names = {
        'dashboard': 'Client Dashboard',
        'my-projects': 'My Projects',
        'project-reports': 'Project Reports',
        'documents': 'Documents',
        'communications': 'Communications',
        'billing': 'Billing',
        'support': 'Support',
        'profile-settings': 'Profile Settings',
    };
    return names[section] || section.replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
}

function loadDashboardOverview(container) {
    container.innerHTML = `<div class='dashboard-summary'>
        <div class='summary-card'><h4>Active Projects</h4><p id='activeProjects'>-</p></div>
        <div class='summary-card'><h4>Pending Tasks</h4><p id='pendingTasks'>-</p></div>
        <div class='summary-card'><h4>Recent Reports</h4><p id='recentReports'>-</p></div>
    </div>
    <div class='recent-activity'><h4>Recent Activity</h4><div id='recentActivityList'><div class='loading-spinner'></div></div></div>`;
    fetchDashboardData();
}

function fetchDashboardData() {
    authFetch(`${API_BASE_URL}/client/dashboard/stats`).then(res => res.json()).then(stats => {
        document.getElementById('activeProjects').textContent = stats.activeProjects ?? 0;
        document.getElementById('pendingTasks').textContent = stats.pendingTasks ?? 0;
        document.getElementById('recentReports').textContent = stats.recentReports ?? 0;
    });
    authFetch(`${API_BASE_URL}/client/dashboard/recent-activity`).then(res => res.json()).then(activities => {
        renderRecentActivity(activities);
    });
}

function renderRecentActivity(activities = []) {
    const list = document.getElementById('recentActivityList');
    if (!activities.length) {
        list.innerHTML = '<p>No recent activity.</p>';
        return;
    }
    list.innerHTML = activities.map(act => `<div class='activity-item'><div class='activity-icon'><i class='fas fa-info-circle'></i></div><div class='activity-content'><p><strong>${act.title}</strong> - ${act.description}</p><span class='activity-time'>${formatTimeAgo(act.timestamp)}</span></div></div>`).join('');
}

function loadMyProjects(container) {
    container.innerHTML = `<div class='table-container'><table class='data-table'><thead><tr><th>Project ID</th><th>Name</th><th>Status</th><th>Start</th><th>End</th></tr></thead><tbody id='projectsTableBody'><tr><td colspan='5'><div class='loading-spinner'></div></td></tr></tbody></table></div>`;
    authFetch(`${API_BASE_URL}/client/projects`).then(res => res.json()).then(projects => {
        renderProjectsTable(projects);
    });
}

function renderProjectsTable(projects = []) {
    const tableBody = document.getElementById('projectsTableBody');
    if (!projects.length) {
        tableBody.innerHTML = '<tr><td colspan="5">No projects found.</td></tr>';
        return;
    }
    tableBody.innerHTML = projects.map(p => `<tr><td>${p.projectId}</td><td>${p.name}</td><td>${p.status}</td><td>${p.startDate}</td><td>${p.endDate || '-'}</td></tr>`).join('');
}

function loadProjectReports(container) {
    container.innerHTML = `<div class='table-container'><table class='data-table'><thead><tr><th>Report ID</th><th>Title</th><th>Date</th><th>Status</th></tr></thead><tbody id='reportsTableBody'><tr><td colspan='4'><div class='loading-spinner'></div></td></tr></tbody></table></div>`;
    authFetch(`${API_BASE_URL}/client/reports`).then(res => res.json()).then(reports => {
        renderReportsTable(reports);
    });
}

function renderReportsTable(reports = []) {
    const tableBody = document.getElementById('reportsTableBody');
    if (!reports.length) {
        tableBody.innerHTML = '<tr><td colspan="4">No reports found.</td></tr>';
        return;
    }
    tableBody.innerHTML = reports.map(r => `<tr><td>${r.reportId}</td><td>${r.title}</td><td>${r.date}</td><td>${r.status}</td></tr>`).join('');
}

function loadDocuments(container) {
    container.innerHTML = `<div class='table-container'><table class='data-table'><thead><tr><th>Document ID</th><th>Name</th><th>Type</th><th>Uploaded</th></tr></thead><tbody id='documentsTableBody'><tr><td colspan='4'><div class='loading-spinner'></div></td></tr></tbody></table></div>`;
    authFetch(`${API_BASE_URL}/client/documents`).then(res => res.json()).then(docs => {
        renderDocumentsTable(docs);
    });
}

function renderDocumentsTable(docs = []) {
    const tableBody = document.getElementById('documentsTableBody');
    if (!docs.length) {
        tableBody.innerHTML = '<tr><td colspan="4">No documents found.</td></tr>';
        return;
    }
    tableBody.innerHTML = docs.map(d => `<tr><td>${d.documentId}</td><td>${d.name}</td><td>${d.type}</td><td>${d.uploadedAt}</td></tr>`).join('');
}

function loadCommunications(container) {
    container.innerHTML = `<div class='communications-list' id='communicationsList'><div class='loading-spinner'></div></div>`;
    authFetch(`${API_BASE_URL}/client/communications`).then(res => res.json()).then(msgs => {
        renderCommunicationsList(msgs);
    });
}

function renderCommunicationsList(msgs = []) {
    const list = document.getElementById('communicationsList');
    if (!msgs.length) {
        list.innerHTML = '<p>No communications found.</p>';
        return;
    }
    list.innerHTML = msgs.map(m => `<div class='comm-item'><div class='comm-title'><strong>${m.subject}</strong></div><div class='comm-body'>${m.body}</div><div class='comm-date'>${m.date}</div></div>`).join('');
}

function loadBilling(container) {
    container.innerHTML = `<div class='table-container'><table class='data-table'><thead><tr><th>Invoice ID</th><th>Amount</th><th>Status</th><th>Date</th></tr></thead><tbody id='billingTableBody'><tr><td colspan='4'><div class='loading-spinner'></div></td></tr></tbody></table></div>`;
    authFetch(`${API_BASE_URL}/client/billing`).then(res => res.json()).then(bills => {
        renderBillingTable(bills);
    });
}

function renderBillingTable(bills = []) {
    const tableBody = document.getElementById('billingTableBody');
    if (!bills.length) {
        tableBody.innerHTML = '<tr><td colspan="4">No billing records found.</td></tr>';
        return;
    }
    tableBody.innerHTML = bills.map(b => `<tr><td>${b.invoiceId}</td><td>${b.amount}</td><td>${b.status}</td><td>${b.date}</td></tr>`).join('');
}

function loadSupport(container) {
    container.innerHTML = `<div class='support-section'><h4>Support</h4><p>For assistance, contact support@zplus.com or call 1800-000-000.</p></div>`;
}

function loadProfileSettings(container) {
    container.innerHTML = `<div class='profile-settings'><h4>Profile Settings</h4><p>Profile management coming soon.</p></div>`;
}

function updateLastUpdated() {
    const el = document.getElementById('lastUpdated');
    if (el) el.textContent = new Date().toLocaleTimeString();
}

async function authFetch(url, options = {}) {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '../index/index.html';
        throw new Error('No authentication token');
    }
    const headers = {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        ...options.headers
    };
    const response = await fetch(url, { ...options, headers });
    if (response.status === 401 || response.status === 403) {
        window.location.href = '../index/index.html';
        throw new Error('Session expired');
    }
    if (!response.ok) {
        throw new Error('Request failed');
    }
    return response;
}

function formatTimeAgo(timestamp) {
    if (!timestamp) return 'N/A';
    const now = new Date();
    const time = new Date(timestamp);
    const diffSeconds = Math.round((now - time) / 1000);
    if (diffSeconds < 60) return 'Just now';
    const diffMinutes = Math.round(diffSeconds / 60);
    if (diffMinutes < 60) return `${diffMinutes} min ago`;
    const diffHours = Math.round(diffMinutes / 60);
    if (diffHours < 24) return `${diffHours} hr ago`;
    const diffDays = Math.round(diffHours / 24);
    if (diffDays <= 7) return `${diffDays} days ago`;
    return time.toLocaleDateString();
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('zplusUser');
    window.location.href = '../index/index.html';
}

function refreshData() {
    loadContent(currentSection);
}

function exportData() {
    alert('Export functionality coming soon.');
}

function toggleNotifications() {
    const panel = document.getElementById('notificationPanel');
    panel.classList.toggle('show');
}

function clearAllNotifications() {
    document.getElementById('notificationList').innerHTML = '';
    document.getElementById('notificationCount').textContent = '0';
}
