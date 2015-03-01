/* Initialize */
(function($) {
    $(function() {

        var width = $(window).width();
        if (width > 767) {
            /* Header Animation on Scroll */
            $(window).scroll(function() {
                if ($(this).scrollTop() > 108) {
                    $("#navbar").addClass('navbar-fixed-top');
                    $("#company").removeClass('hidden');
                } else {
                    $("#navbar").removeClass('navbar-fixed-top');
                    $("#company").addClass('hidden');
                }
            });
            /* Navbar Dropdown Animation */
            $('.dropdown').hover(function() {
                $(this).find('.dropdown-menu').first().stop(true, true).slideDown();
            }, function() {
                $(this).find('.dropdown-menu').first().stop(true, true).slideUp()
            });
        } else if (width <= 767) {
            $(window).scroll(function() {
                if ($(this).scrollTop() > 110) {} else {}
            });

        };
        /* Searchbar Expander */
        $('.nav .btn').on('mouseover', function(e) {
            e.preventDefault();
            var $this = $(this);
            var $collapse = $this.closest('.collapse-group').find('.collapse');
            $collapse.collapse('show');
        });

    }); // end of document ready
})(jQuery); // end of jQuery name space
