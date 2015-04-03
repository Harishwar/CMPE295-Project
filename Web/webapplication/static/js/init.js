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
            slideout.open();
            $('#navbar-toggle').remove();
            $("#content").css({
                "padding-right": '192px'
            });

        } else if (width <= 960) {
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

    }); // end of document ready
})(jQuery); // end of jQuery name space