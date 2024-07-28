document.addEventListener('DOMContentLoaded', function() {
    var submenuToggles = document.querySelectorAll('.collapsed[data-bs-toggle="collapse"]');

    submenuToggles.forEach(function(toggle) {
        var targetId = toggle.getAttribute('href');
        var target = document.querySelector(targetId);
        var icon = toggle.querySelector('.submenu-icon');

        // Set initial icon state based on collapsed state
        if (target.classList.contains('show')) {
            icon.classList.remove('fa-caret-right');
            icon.classList.add('fa-caret-down');
        } else {
            icon.classList.remove('fa-caret-down');
            icon.classList.add('fa-caret-right');
        }

        toggle.addEventListener('click', function() {
            var isExpanded = toggle.getAttribute('aria-expanded') === 'true';

            if (isExpanded) {
                icon.classList.remove('fa-caret-down');
                icon.classList.add('fa-caret-right');
            } else {
                icon.classList.remove('fa-caret-right');
                icon.classList.add('fa-caret-down');
            }
        });
    });
});
