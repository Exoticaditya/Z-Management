/**
 * API Configuration for Z+ Management Platform
 * Simple, reliable configuration for production deployment
 */

// Configuration object
const API_CONFIG = {
    // Railway Production URL - always use this for zpluse.com
    BASE_URL: 'https://z-management-production.up.railway.app/api',
    
    // Request timeout
    TIMEOUT: 15000,
    
    // Default headers
    HEADERS: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    },
    
    // Debug mode (set to false for production)
    DEBUG: false,
    
    // Helper methods
    getApiUrl: function() {
        return this.BASE_URL;
    },
    
    getBaseUrl: function() {
        return this.BASE_URL;
    }
};

// Global API base URL for backward compatibility
const API_BASE_URL = API_CONFIG.BASE_URL;

// Helper function for making API calls
async function makeApiCall(endpoint, options = {}) {
    const url = `${API_CONFIG.BASE_URL}/${endpoint.startsWith('/') ? endpoint.slice(1) : endpoint}`;
    
    // Add default headers
    const defaultHeaders = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    };
    
    // Merge with provided options
    const config = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...(options.headers || {})
        }
    };
    
    if (API_CONFIG.DEBUG) {
        console.log(`[API] Making request to: ${url}`);
    }
    
    try {
        const response = await fetch(url, config);
        return response;
    } catch (error) {
        console.error(`[API] Request failed:`, error);
        throw error;
    }
}

// Make API_CONFIG available globally
if (typeof window !== 'undefined') {
    window.API_CONFIG = API_CONFIG;
    window.API_BASE_URL = API_BASE_URL;
    window.makeApiCall = makeApiCall;
}

// Export for Node.js environments
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { API_CONFIG, API_BASE_URL, makeApiCall };
}
console.log(`[API CONFIG] Using backend: ${API_CONFIG.getBaseUrl()}`);