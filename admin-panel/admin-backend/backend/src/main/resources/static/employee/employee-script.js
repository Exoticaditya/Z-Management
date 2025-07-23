// Employee Dashboard JavaScript
document.addEventListener('DOMContentLoaded', function() {
    loadUserInfo();
});

function loadUserInfo() {
    const userData = localStorage.getItem('zplusUser');
    if (userData) {
        try {
            const user = JSON.parse(userData);
            document.getElementById('employee-id').textContent = user.selfId || 'N/A';
            document.getElementById('employee-name').textContent = user.name || 'N/A';
            
            // Update status based on user data
            const statusElement = document.getElementById('employee-status');
            if (user.isActive === false) {
                statusElement.textContent = 'Inactive';
                statusElement.className = 'status-badge status-pending';
            }
        } catch (e) {
            console.error('Failed to parse user data:', e);
            showNotification('Failed to load user information', 'error');
        }
    } else {
        // Redirect to login if no user data
        window.location.href = '/index/index.html';
    }
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('zplusUser');
    showNotification('Logged out successfully', 'success');
    setTimeout(() => {
        window.location.href = '/index/index.html';
    }, 1000);
}

function showNotification(message, type = 'info') {
    const container = document.getElementById('notification-container');
    if (!container) return;
    
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-triangle' : 'info-circle'}"></i>
        ${message}
    `;
    
    container.appendChild(notification);
    
    // Slide in
    setTimeout(() => {
        notification.classList.add('show');
    }, 10);
    
    // Slide out and remove
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            if (container.contains(notification)) {
                container.removeChild(notification);
            }
        }, 300);
    }, 4000);
}

// Auto-refresh user info every 30 seconds
setInterval(loadUserInfo, 30000);
