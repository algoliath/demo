<!-- sync form -->
function submit_value_form(input_id, value, form_id) {
    console.log('input_id',input_id);
    console.log('form_id',form_id);
    insert_by_id(input_id, value);
    submit_form(form_id);
}

function submit_form(form_id) {
    console.log(form_id);
    $('#' + form_id).submit();
}

<!-- async form-->
function ajax_form(form_id, action, method = "post", table_id = "", content_type = 'application/x-www-form-urlencoded;charset=UTF-8', message = "success") {
    // var data = $('#' + form_id).serializeArray().reduce(function(x){form_data[x.name] = x.value;});
    console.log('form_id=' + form_id);
    console.log('form_url=' + action);
    console.log('method=' + method);
    console.log('table_id=' + table_id);
    console.log('content_type=' + content_type);

    $.ajax({
        type: method,
        url: action,
        data: $('#' + form_id).serialize(),
        contentType: content_type,
        success: function () {
            console.log(message);
        },
        error: function() {
            console.log('error message', message);
        }

    }).done(function (fragment) {
        console.log(fragment);
        if (!table_id) {
            table_id = form_id;
        }
        $('#' + table_id).replaceWith(fragment);
    });
    return false;
}