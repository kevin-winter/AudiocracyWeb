jQuery(document).ready(function($) {
    $.stellar({
        hideDistantElements: true,
        horizontalScrolling: false,
        responsive: true,
        parallexElements: false
    });
    var links = $('.navlink');
    slide = $('.cSlide');
    button = $('.button');
    htmlbody = $('html,body');

    function goToByScroll(dataslide) {

        htmlbody.animate({
            scrollTop: $('.cSlide[stelldata-slide="' + dataslide + '"]').offset().top-50
        }, 1000, 'easeInOutQuint');
    }

    links.click(function(e) {
            e.preventDefault();
            dataslide = $(this).attr('stelldata-slide');
            goToByScroll(dataslide);
    });

    button.click(function(e) {
        e.preventDefault();
        dataslide = $(this).attr('stelldata-slide');
        goToByScroll(dataslide);
    });
});