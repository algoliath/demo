<!-- Latest compiled and minified JavaScript -->

<!-- toggle_function -->
function toggle_by_class(target_class, flag) {
    $('.' + target_class).toggle(flag);
}

function toggle_header(header, container) {

    let headers = $('.' + header);
    let containers = $('.' + container);

    if (headers.length == 0 || containers.length == 0) {
        console.log("invalid: input size of 0");
        return;
    }

    let header_active = false;
    let container_active = false;
    headers.each(function (index, element) {
        if ($(element).hasClass('header-show')) {
            $(element).removeClass('header-show');
            $(element).addClass('header-collapse');
            header_active = true;
        } else if ($(element).hasClass('header-collapse')) {
            $(element).removeClass('header-collapse');
            $(element).addClass('header-show');
            header_active = true;
        }
    });

    containers.each(function (index, element) {
        if ($(element).hasClass('pop-up')) {
            $(element).removeClass('pop-up');
            $(element).addClass('pop-hide');
            container_active = true;
        } else if ($(element).hasClass('pop-hide')) {
            $(element).removeClass('pop-hide');
            $(element).addClass('pop-up');
            container_active = true;
        }
    });

    if (!header_active) {
        $(headers[0]).addClass('header-show');
    }
    if (!container_active) {
        $(containers[0]).addClass('pop-up');
    }
}

function toggle_popup(anchorClass, targetClass) {

    let containers = $('.' + anchorClass);
    if (containers.length == 0) {
        console.log("invalid input: input size of 0");
        return;
    }

    let displayCount = 0;
    containers.each(function (index, element) {
        console.log($(element));
        if ($(element).hasClass(targetClass) && $(element).css('display') == 'none') {
            $(element).toggle('slow');
            $(element).removeClass('pop-hide');
            $(element).addClass('pop-up');
            displayCount++;
        }
        if (!$(element).hasClass(targetClass) && $(element).css('display') != 'none') {
            $(element).toggle('slow');
            $(element).removeClass('pop-up');
            $(element).addClass('pop-hide');
        }
    });

    console.log(displayCount);

    if(displayCount == 0){
        const empty_container = containers.filter(container => $(container).hasClass('empty'));
        $(empty_container).addClass('pop-up');
        $(empty_container).removeClass('pop-hide');
        $(empty_container).toggle('slow');
    }
}

function confirm_alert(message = "확인: ") {
    confirm(message);
}

<!-- insert -->
function insert_by_id(input_id, val) {
    $('#' + input_id).val(val);
}

<!-- focus -->
function shift_focus(input_id, type, another_input_id) {
    insert_by_id(input_id, type);
    $('#' + another_input_id).focus();
}

<!-- drag-and-drop -->
function dragstart_handler(event) {
    // Add the target element's id to the data transfer object
    console.log('dragstart_handler', event);
    event.dataTransfer.setData("text/plain", event.target.getAttribute('template-name'));
    const data = event.dataTransfer.getData("text/plain");
    console.log('data:' + data);
    event.dataTransfer.effectAllowed = "move";
}

function dragover_handler(event) {
    event.preventDefault();
    console.log('dragover_handler', event);
    console.log('target:' + event.target.id);
    event.dataTransfer.dropEffect = "move";
}

function dragend_handler(event) {
    event.preventDefault();
    console.log('dragend_handler', event);
    const data = event.dataTransfer.getData("text/plain");
    console.log('target:' + event.target.id);
    console.log('drag_end - data:' + data, 'target:' + event.target.id);
}

function drop_handler(event) {
    console.log('drop_handler', event);
    const data = event.dataTransfer.getData("text/plain");
    const form_id = event.currentTarget.getAttribute('form-id');
    const input_id = event.currentTarget.getAttribute('input-id');
    console.log('data', data);
    console.log('target', event.target);
    console.log('target', event.currentTarget);
    console.log('form_id', form_id);
    submit_value_form(input_id, data, form_id);
}