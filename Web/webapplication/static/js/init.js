/* Initialize */
(function($) {
    $(function() {
        /* Navbar Slideout */
        var width = $(window).width();
        var slideout = new Slideout({
            'panel': document.getElementById('content'),
            'menu': document.getElementById('menu'),
            'padding': 192,
            'tolerance': 70
        });
        if (width > 960) {
            $('#navbar-toggle').addClass('hidden');
            $('#content').addClass('slideout-panel-width');
            $('#menu').addClass('slideout-menu-visible');
        } else if (width <= 960) {
            $('#navbar-toggle').removeClass('hidden');
            document.querySelector('.js-slideout-toggle').addEventListener('click', function() {
                slideout.toggle();
                if (slideout.isOpen()) {
                    $('#navbar-toggle').html('<i class="glyphicon glyphicon-remove"></i>');
                } else {
                    $('#navbar-toggle').html('<i class="glyphicon glyphicon-menu-hamburger"></i>');
                }
            });
            document.querySelector('.menu').addEventListener('click', function(eve) {
                if (eve.target.nodeName === 'A') {
                    slideout.close();
                }
            });
            $(window).on("touchend", function() {
                if (slideout.isOpen()) {
                    $('#navbar-toggle').html('<i class="glyphicon glyphicon-remove"></i>');
                } else {
                    $('#navbar-toggle').html('<i class="glyphicon glyphicon-menu-hamburger"></i>');
                }
            });

        };
        /* Back to Top */
        var offset = 200;
        var duration = 300;
        jQuery(window).scroll(function() {
            if (jQuery(this).scrollTop() > offset) {
                jQuery('.back-to-top').fadeIn(duration);
            } else {
                jQuery('.back-to-top').fadeOut(duration);
            }
        });

        jQuery('.back-to-top').click(function(event) {
            event.preventDefault();
            jQuery('html, body').animate({
                scrollTop: 0
            }, duration);
            return false;
        })

    }); // end of document ready
})(jQuery); // end of jQuery name space
