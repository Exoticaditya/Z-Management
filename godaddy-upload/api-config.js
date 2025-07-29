/**
 * API Configuration for Z+ Management Platform
 * Automatically detects environment and uses appropriate backend URL
 */

// Configuration object
const API_CONFIG = {
    // Railway Production URL (replace with your actual Railway URL)
    PRODUCTION_URL: 'https://z-management-production.railway.app',
    
    // Local development URL
    DEVELOPMENT_URL: 'http://localhost:8080',
    
    // Auto-detect environment
    getBaseUrl() {
        // If running on zpluse.com (production), use Railway
        if (window.location.hostname === 'zpluse.com' || 
            window.location.hostname === 'www.zpluse.com') {
            return this.PRODUCTION_URL;
        }
        
        // If running locally, use localhost
        if (window.location.hostname === 'localhost' || 
            window.location.hostname === '127.0.0.1' ||
            window.location.protocol === 'file:') {
            return this.DEVELOPMENT_URL;
        }
        
        // Default to production for any other domain
        return this.PRODUCTION_URL;
    },
    
    // Get full API URL
    getApiUrl(endpoint = '') {
        const baseUrl = this.getBaseUrl();
        const apiPath = '/api';
        
        // Remove leading slash from endpoint if present
        const cleanEndpoint = endpoint.startsWith('/') ? endpoint.slice(1) : endpoint;
        
        if (cleanEndpoint) {
            return `${baseUrl}${apiPath}/${cleanEndpoint}`;
        }
        return `${baseUrl}${apiPath}`;
    }
};

// Global API base URL for backward compatibility
const API_BASE_URL = API_CONFIG.getApiUrl();

// Helper function for making API calls
async function makeApiCall(endpoint, options = {}) {
    const url = API_CONFIG.getApiUrl(endpoint);
    
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
    
    console.log(`[API] Making request to: ${url}`);
    
    try {
        const response = await fetch(url, config);
        return response;
    } catch (error) {
        console.error(`[API] Request failed:`, error);
        throw error;
    }
}

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { API_CONFIG, API_BASE_URL, makeApiCall };
}

console.log(`[API CONFIG] Environment detected: ${window.location.hostname}`);
console.log(`[API CONFIG] Using backend: ${API_CONFIG.getBaseUrl()}`);
