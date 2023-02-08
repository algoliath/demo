


function pop_up() {
    $('div#dialog_query').dialog({
            modal: true,
            buttons: {
                '저장': load('accept'),
                '취소': load('decline')
            }
        }
    );
}

function toggle(){
    let setColor = function (data) {
        let $toggle_button = $(data['classname']);
        if (data['count'] = 0) {
            $toggle_button.css('background-color', 'forestgreen');
            $toggle_button.css('color', 'white');
            data['count'] = 1;
        } else {
            $toggle_button.css('background-color', 'white');
            $toggle_button.css('color', 'black');
            data['count'] = 0;
        }
    };
    let $classname = $('color__reactive');
    alert($classname.length);
    for (let i = 0; i < $classname.length; i++) {
        $classname[i].addEventListener('click', setColor({
            'classname': $classname,
            'count': 0
        }), false);
    }
}

function toggle_by_class(target_class) {
    alert(target_class);
    alert(3);
    $('.' + target_class).toggle();
}

function toggle_by_id(input_id, types, form_id){
    $('#' + input_id).val(types);
    submit_form(form_id)
}

function deselectType(){
    $('tbody.header').empty();
}

function select_type(){
    // 초기 값 '' (공백)
    if($('tbody.header tr').length > 0){
        deselectType();
        return;
    }
    let $button1 = $("<button>", {text: "엔티티", type:"submit",
        class:"form-control",
        onclick:"insertType('ENTITY')"});

    let $button2 = $("<button>", {text: "관계", type:"submit",
        class:"form-control",
        onclick:"insertType('TABLE')"});

    $button1.css('width', $('.accordion-button').width() * 10);
    $button2.css('width', $('.accordion-button').width() * 10);

    let $tr1 = $("<tr>",{});
    let $tr2 = $("<tr>",{});
    let $td1 = $("<td>",{});
    let $td2 = $("<td>",{});

    $td1.append($button1);
    $td2.append($button2);
    $tr1.append($td1);
    $tr2.append($td2);

    // unfold
    $('tbody.header').append($tr1);
    $('tbody.header').append($tr2);
}

