$(document).ready(function() {
    $('td[data-href]').on("click", function () {
        document.location = $(this).data('href');
    });
    // $('select').material_select();
    $('.modal').modal();
});

