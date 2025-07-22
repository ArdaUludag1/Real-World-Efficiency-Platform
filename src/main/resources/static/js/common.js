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

// Theme switching logic
function applyThemeFromCookie() {
    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
    }
    const theme = getCookie('theme') || 'auto';
    document.documentElement.setAttribute('data-bs-theme', theme);
}

// Background style logic
function applyBackgroundStyleFromCookie() {
    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
    }
    const bg = getCookie('backgroundStyle') || 'light';
    if (bg === 'grey') {
        document.body.style.background = '#e9ecef';
    } else {
        document.body.style.background = '#fff';
    }
}

// Avatar preview
function initAvatarPreview() {
    const avatarInput = document.getElementById('avatar');
    const avatarPreview = document.getElementById('avatarPreview');
    if (avatarInput && avatarPreview) {
        avatarInput.addEventListener('change', function() {
            if (this.files && this.files[0]) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    avatarPreview.src = e.target.result;
                };
                reader.readAsDataURL(this.files[0]);
            }
        });
    }
}

// Font size logic
function applyFontSizeFromCookie() {
    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
    }
    const fontSize = getCookie('fontSize') || 'medium';
    let size = '16px';
    if (fontSize === 'small') size = '14px';
    if (fontSize === 'large') size = '18px';
    document.body.style.fontSize = size;
}

// Initialize common functionality when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    applyThemeFromCookie();
    applyBackgroundStyleFromCookie();
    applyFontSizeFromCookie();
    initAvatarPreview();
    initSmoothScrolling();
    // Export buttons
    const exportCsvBtn = document.getElementById('exportCsvBtn');
    if (exportCsvBtn) {
        exportCsvBtn.addEventListener('click', function() {
            window.location.href = '/settings/export-csv';
        });
    }
    const exportJsonBtn = document.getElementById('exportJsonBtn');
    if (exportJsonBtn) {
        exportJsonBtn.addEventListener('click', function() {
            window.location.href = '/settings/export-json';
        });
    }
}); 