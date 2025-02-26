//СКРИПТЫ ДОБАВЛЕНИЯ И ИЗМЕННЕИЯ ПОСТАНОВКИ В АФИШУ
var ListPositions = [], ListRoles = [];
var SelectIdDirectoryPerformance = document.getElementById('id_directory_performance');
var ImagesEditPositions = [], ImagesDeletePositions = [];
var ImagesEditRoles = [], ImagesDeleteRoles = [];
var Position, IdPersonPosition, IdPersonRole;
var View;//куда добавлять 1 - должность 2 - роль
var SelectWorkerEl = document.getElementById('worker');
var NamePersonEl = document.getElementById('name_person');
var SocialLinkEl = document.getElementById('social_link');

//событие при окончании загрузки страницы
$(window).on('load', function() {
    if(IdPoster == 0)GetPositionsActors();
    else{
        ListPositions = JSON.parse(ListTextPositions);
        ListRoles = JSON.parse(ListTextRoles);
        RepaintTablePositions();
        RepaintTableRoles();
    }
});

//событие при изменении в списке постановок
SelectIdDirectoryPerformance.addEventListener('change',function(){
    GetPositionsActors();
});

//событие перед отправкой данных на сервер из формы
$('#form_data').submit(function() {
    var i, data, list_positions = [], list_roles = [];
    if(ListPositions.length == 0){
        alert('Укажите должности в спектакле.');
        return false;
    }
    if(ListRoles.length == 0){
        alert('Укажите роли в спектакле.');
        return false;
    }
    //проверка все ли должности заполнены
    for(i = 0; i < ListPositions.length; i++){
        data = ListPositions[i];
        if(data.name_person.length == 0){
            alert('Укажите все должности.');
            return false;
        }
    }
    //проверка все ли должности заполнены
    for(i = 0; i < ListRoles.length; i++){
        data = ListRoles[i];
        if(data.name_person.length == 0){
            alert('Укажите все роли.');
            return false;
        }
    }
    for(i = 0; i < ListPositions.length; i++){
        if(Theater == 1) data = {'id_position': ListPositions[i].id_position, 'id_person': ListPositions[i].id_person};
        else data = {'id_position': ListPositions[i].id_position, 'name_person': ListPositions[i].name_person, 'social_link': ListPositions[i].social_link};
        list_positions.push(data);
    }
    for(i = 0; i < ListRoles.length; i++){
        if(Theater == 1) data = {'id_position': ListRoles[i].id_position, 'id_person': ListRoles[i].id_person};
        else data = {'id_position': ListRoles[i].id_position, 'name_person': ListRoles[i].name_person, 'social_link': ListRoles[i].social_link};
        list_roles.push(data);
    }
    document.getElementById('list_text_positions').value=JSON.stringify(list_positions);
    document.getElementById('list_text_roles').value=JSON.stringify(list_roles);
    return true;
});

//открытие модального окна назначения должности или роли
function OpenModalEdit(){
    switch(Theater){
        case 1://свой театр
            if(View == 1) document.getElementById('text_oper_my').innerHTML = 'Назначить должность: ' + ListPositions[Position].name_position;
            else document.getElementById('text_oper_my').innerHTML = 'Назначить роль: ' + ListRoles[Position].name_position;
            if(View == 1){
                if(ListPositions[Position].id_person > 0){
                    for (var i = 0; i < SelectWorkerEl.length; i++)
                        if(SelectWorkerEl.options[i].value == String(IdPersonPosition)){
                            SelectWorkerEl.selectedIndex = i;
                            break;
                        }
                }else SelectWorkerEl.selectedIndex = 0;
            }else{
                if(ListRoles[Position].id_person > 0){
                    for (var i = 0; i < SelectWorkerEl.length; i++)
                        if(SelectWorkerEl.options[i].value == String(IdPersonRole)){
                            SelectWorkerEl.selectedIndex = i;
                            break;
                        }
                }else SelectWorkerEl.selectedIndex = 0;
            }
            OpenModal('modal_edit_my');
            break;
        case 0://приезжий театр
            if(View == 1) document.getElementById('text_oper_not_my').innerHTML = 'Назначить должность: ' + ListPositions[Position].name_position;
            else document.getElementById('text_oper_not_my').innerHTML = 'Назначить роль: ' + ListRoles[Position].name_position;
            if(View == 1){
                NamePersonEl.value = ListPositions[Position].name_person;
                SocialLinkEl.value = ListPositions[Position].social_link;
            }else{
                NamePersonEl.value = ListRoles[Position].name_person;
                SocialLinkEl.value = ListRoles[Position].social_link;
            }
            OpenModal('modal_edit_not_my');
            break;
    }
}

//установка данных в таблицу должностей или ролей
function SetData(){
    var data, name_person;
    switch(Theater){
        case 1://постановка своего театра
            if(SelectWorkerEl.length > 0){
                var id_person;
                id_person = Number(SelectWorkerEl.value);
                name_person = SelectWorkerEl.options[SelectWorkerEl.selectedIndex].text;
                if(View == 1){
                    data = ListPositions[Position];
                    data.id_person = id_person;
                    data.name_person = name_person;
                    ListPositions[Position] = data;
                    RepaintTablePositions();
                }else{
                    data = ListRoles[Position];
                    data.id_person = id_person;
                    data.name_person = name_person;
                    ListRoles[Position] = data;
                    RepaintTableRoles();
                }
            }
            break;
        case 0://постановка приезжего театра
            var social_link;
            name_person = NamePersonEl.value;
            social_link = SocialLinkEl.value;
            if(name_person.length == 0){
                alert('Укажите ФИО.');
                return;
            }
            if(View == 1){
                data = ListPositions[Position];
                data.name_person = name_person;
                data.social_link = social_link;
                ListPositions[Position] = data;
                RepaintTablePositions();
            }else{
                data = ListRoles[Position];
                data.name_person = name_person;
                data.social_link = social_link;
                ListRoles[Position] = data;
                RepaintTableRoles();
            }
            break;
    }
    CloseModal();
}

//получение данных по должностям и актера ajax
function GetPositionsActors(){
    if(SelectIdDirectoryPerformance.length > 0){
        var id_directory_performance = SelectIdDirectoryPerformance.value;
        $.ajax({
            type: "POST",
            data:{
                id_directory_performance: id_directory_performance,
                theater: Theater
            },
            url: "/GetPositionsActorsPosterController",
            success: function( data ){
                var data_json = JSON.parse(data);
                ListPositions = data_json.list_positions;
                ListRoles = data_json.list_roles;
                RepaintTablePositions();
                RepaintTableRoles();
            },
            error: function(jqXHR, textStatus, errorThrown){
                alert('Ошибка: ' + textStatus);
            }
        })
    }
}

//перерисовка таблицы с должностями
function RepaintTablePositions(){
    var div = document.getElementById("table_positions");
    var i, row, cell, label, img;
    div.innerHTML="";
    var table = document.createElement('table');
    table.setAttribute('width', '100%');
    table.setAttribute('cellpadding', '0');
    table.setAttribute('cellspacing', '0');
    ImagesDeletePositions = [];ImagesEditPositions = [];
    for(i = 0; i< ListPositions.length; i++){
        row = table.insertRow();
        //текст должности
        cell = row.insertCell();
        cell.setAttribute('width', '100%');
        label=document.createElement('label');
        label.className='text5';
        label.innerHTML=ListPositions[i].name_position + ":";
        cell.append(label);
        //текст ФИО человека на должности
        cell = row.insertCell();
        cell.setAttribute('width', '100%');
        label=document.createElement('label');
        label.className='text5';
        label.innerHTML=ListPositions[i].name_person;
        cell.append(label);
        //иконка редактирования
        cell = row.insertCell();
        cell.style.textAlign='right';
        img=document.createElement('img');
        img.title='Изменить';
        img.src='/images/edit_icon.gif';
        img.style.height = '30px';
        img.className='effect1';
        img.style.marginLeft='5px';
        ImagesEditPositions.push(img);
        ImagesEditPositions[i].pos=i;
        ImagesEditPositions[i].addEventListener("click",
            function(){
                IdPersonPosition = ListPositions[this.pos].id_person;
                View = 1;
                Position = this.pos;
                OpenModalEdit();
            }, false);
        cell.append(img);
        //иконка удаления
        cell = row.insertCell();
        cell.style.textAlign='right';
        img=document.createElement('img');
        img.title='Удалить';
        img.src='/images/close_icon.gif';
        img.style.height = '30px';
        img.className='effect1';
        img.style.marginLeft='5px';
        ImagesDeletePositions.push(img);
        ImagesDeletePositions[i].pos=i;
        ImagesDeletePositions[i].addEventListener("click",
            function(){
                ListPositions.splice(this.pos, 1);
                RepaintTablePositions();
            }, false);
        cell.append(img);
        //разделяющая линия в виде новой строки
        row = table.insertRow();
        cell = row.insertCell();
        hr=document.createElement('hr');
        cell.setAttribute('colspan', '3');
        cell.append(hr);
    }
    div.appendChild(table);
}

//перерисовка таблицы с ролями
function RepaintTableRoles(){
    var div = document.getElementById("table_roles");
    var i, row, cell, label, img;
    div.innerHTML="";
    ImagesEditRoles = [];
    ImagesDeleteRoles = [];
    var table = document.createElement('table');
    table.setAttribute('width', '100%');
    table.setAttribute('cellpadding', '0');
    table.setAttribute('cellspacing', '0');
    for(i = 0; i< ListRoles.length; i++){
        row = table.insertRow();
        //текст роли
        cell = row.insertCell();
        cell.setAttribute('width', '100%');
        label=document.createElement('label');
        label.className='text5';
        label.innerHTML=ListRoles[i].name_position;
        cell.append(label);
        //текст ФИО человека, который исполняет роль
        cell = row.insertCell();
        cell.setAttribute('width', '100%');
        label=document.createElement('label');
        label.className='text5';
        label.innerHTML=ListRoles[i].name_person;
        cell.append(label);
        //иконка редактирования
        cell = row.insertCell();
        cell.style.textAlign='right';
        img=document.createElement('img');
        img.title='Изменить';
        img.src='/images/edit_icon.gif';
        img.style.height = '30px';
        img.className='effect1';
        img.style.marginLeft='5px';
        ImagesEditRoles.push(img);
        ImagesEditRoles[i].pos=i;
        ImagesEditRoles[i].addEventListener("click",
            function(){
                IdPersonRole = ListRoles[this.pos].id_person;
                View = 2;
                Position = this.pos;
                OpenModalEdit();
            }, false);
        cell.append(img);
        //иконка удаления
        cell = row.insertCell();
        cell.style.textAlign='right';
        img=document.createElement('img');
        img.title='Удалить';
        img.src='/images/close_icon.gif';
        img.style.height = '30px';
        img.className='effect1';
        img.style.marginLeft='5px';
        ImagesDeleteRoles.push(img);
        ImagesDeleteRoles[i].pos=i;
        ImagesDeleteRoles[i].addEventListener("click",
            function(){
                ListRoles.splice(this.pos, 1);
                RepaintTableRoles();
            }, false);
        cell.append(img);
        //разделяющая линия в виде новой строки
        row = table.insertRow();
        cell = row.insertCell();
        hr=document.createElement('hr');
        cell.setAttribute('colspan', '3');
        cell.append(hr);
    }
    div.appendChild(table);
}