document.addEventListener('DOMContentLoaded', function() {
    // Add initial page transition
    document.body.classList.add('page-transition');
    
    // Check if we should animate the entry
    if (sessionStorage.getItem('animateEntry') === 'true') {
        // Clear the flag
        sessionStorage.removeItem('animateEntry');
        
        // Add animations to dashboard elements
        const header = document.querySelector('.dashboard-header');
        if (header) header.classList.add('animate-fade-in');
        
        // Add animations to cards with delay
        const cards = document.querySelectorAll('.dashboard-card');
        cards.forEach((card, index) => {
            card.style.animationDelay = `${0.1 * (index + 1)}s`;
            card.classList.add('animate-bounce');
        });
        
        // Add hover effects to interactive elements
        const interactiveElements = document.querySelectorAll('.interactive');
        interactiveElements.forEach(el => el.classList.add('hover-lift'));
    }
    
    // Initialize smooth transitions
    const smoothElements = document.querySelectorAll('.smooth-transition');
    smoothElements.forEach(el => {
        el.addEventListener('mouseenter', () => el.style.transform = 'scale(1.02)');
        el.addEventListener('mouseleave', () => el.style.transform = 'scale(1)');
    });
    
    // Show the page when everything is ready
    document.body.classList.remove('loading');
    document.body.classList.add('loaded');
});
