// Common JavaScript functions for RWE Platform

// Logout function - available on all pages
function logout(event) {
    if (event) event.preventDefault();
    
    console.log('Logout initiated');
    
    const logoutBtn = event?.target;
    if (logoutBtn) logoutBtn.disabled = true;
    
    fetch('/api/auth/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include'
    })
    .then(response => {
        console.log('Logout response status:', response.status);
        if (response.ok) {
            console.log('Logout successful');
        } else {
            console.error('Logout failed with status:', response.status);
        }
    })
    .catch(error => {
        console.error('Logout error:', error);
    })
    .finally(() => {
        console.log('Redirecting to login page');
        window.location.href = '/login';
    });
}

// Smooth scrolling for anchor links
function initSmoothScrolling() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const href = this.getAttribute('href');
            if (href && href !== '#') {
                const target = document.querySelector(href);
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            }
        });
    });
}

// Initialize common functionality when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initSmoothScrolling();
}); 