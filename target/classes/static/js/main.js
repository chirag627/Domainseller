// Countdown timers
function updateCountdown(el) {
    const endTime = new Date(el.getAttribute('data-end-time')).getTime();
    const now = new Date().getTime();
    const diff = endTime - now;
    if (diff <= 0) {
        el.textContent = 'Auction Ended';
        el.classList.add('text-muted');
        return;
    }
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);
    if (days > 0) {
        el.textContent = days + 'd ' + hours + 'h ' + minutes + 'm';
    } else {
        el.textContent = hours + 'h ' + minutes + 'm ' + seconds + 's';
    }
}

document.addEventListener('DOMContentLoaded', function() {
    // Countdown timers
    const countdowns = document.querySelectorAll('.countdown-timer');
    if (countdowns.length > 0) {
        countdowns.forEach(updateCountdown);
        setInterval(() => countdowns.forEach(updateCountdown), 1000);
    }

    // Auto-dismiss flash messages
    const alerts = document.querySelectorAll('.alert-flash');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.5s';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // Confirm delete dialogs
    document.querySelectorAll('[data-confirm]').forEach(el => {
        el.addEventListener('click', function(e) {
            if (!confirm(this.getAttribute('data-confirm') || 'Are you sure?')) {
                e.preventDefault();
            }
        });
    });

    // Price range display
    const priceRange = document.getElementById('priceRange');
    const priceDisplay = document.getElementById('priceDisplay');
    if (priceRange && priceDisplay) {
        priceRange.addEventListener('input', function() {
            priceDisplay.textContent = '$' + this.value;
        });
    }
});
