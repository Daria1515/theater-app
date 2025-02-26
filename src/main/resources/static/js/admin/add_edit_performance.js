//СКРИПТЫ ПО ДОБАВЛЕНИЮ ИЛИ ИЗМЕНЕНИЮ ДАННЫХ СПЕКТАКЛЯ
var ListPositions = [];//список должностей по спектаклю
var ListRoles = [];//список ролей по спектаклю
var IdPosition = 0, IdRole = 0, Position = 0;
var SelectPosition = document.getElementById("select_position");
var ImagesEditPositions = [], ImagesDeletePositions = [], ImagesEditRoles = [], ImagesDeleteRoles = [];

//событие при окончании загрузки страницы
$(window).on('load', function() {
    document.getElementById('on_change_image1').value='0';
    document.getElementById('on_change_image2').value='0';
    document.getElementById('on_change_image3').value='0';
    document.getElementById('on_change_image4').value='0';
    document.getElementById('on_change_image5').value='0';
});

//событие перед отправкой данных на сервер из формы
$('#form_data').submit(function() {
    if(ListPositions.length == 0){
        alert('Укажите должности в спектакле.');
        return false;
    }
    if(ListRoles.length == 0){
        alert('Укажите роли в спектакле.');
        return false;
    }
    var i, data, list_positions = [];
    for(i=0;i<ListPositions.length;i++)
        list_positions.push(ListPositions[i].id);
    document.getElementById('list_text_positions').value=JSON.stringify(list_positions);
    document.getElementById('list_text_roles').value=JSON.stringify(ListRoles);
    return true;
});

//открытие модального окна добавления или редактирования должности
function OpenModelPosition(position){
    Position = position;
    if(position == -1){
        document.getElementById('text_oper_position').innerHTML = 'Добавить должность';
        document.getElementById('button_add_edit_position').innerHTML = 'Добавить';
    }
    else{
        document.getElementById('text_oper_position').innerHTML = 'Изменить должность';
        for (var i = 0; i < SelectPosition.length; i++)
            if(SelectPosition.options[i].value == String(IdPosition)){
                SelectPosition.selectedIndex = i;
                break;
            }
        document.getElementById('button_add_edit_position').innerHTML = 'Изменить';
    }
    OpenModal("modal_add_edit_position");
}

//открытие модального окна добавления или редактирования роли
function OpenModelRole(position){
    Position = position;
    if(position == -1){
            document.getElementById('text_oper_role').innerHTML = 'Добавить роль';
            document.getElementById('button_add_edit_role').innerHTML = 'Добавить';
            document.getElementById('role').value = "";
            document.getElementById('description_role').value = "";
        }
        else{
            document.getElementById('text_oper_role').innerHTML = 'Изменить роль';
            document.getElementById('role').value = ListRoles[position].name;
            document.getElementById('description_role').value = ListRoles[position].description;
            document.getElementById('button_add_edit_role').innerHTML = 'Изменить';
        }
        OpenModal("modal_add_edit_role");
}

//добавление или изменение должности
function AddEditPosition(){
    var i, data;
    data = {'id': Number(SelectPosition.value), 'name': SelectPosition.options[SelectPosition.selectedIndex].text};
    if(Position == -1) ListPositions.push(data);
    else ListPositions[Position] = data;
    RepaintTablePositions();
    CloseModal();
}

//добавление или изменение роли
function AddEditRole(){
    var i, data;
    var name = document.getElementById('role').value;
    var description = document.getElementById('description_role').value;
    if(name.length == 0){
        alert('Не введена роль.');
        return;
    }
    if(description.length == 0){
        alert('Не введено описание.');
        return;
    }
    if(Position == -1){
        data = {'id': ListRoles.length + 1, 'name': name, 'description': description};
        ListRoles.push(data);
    }
    else{
        data = {'id': Position + 1, 'name': name, 'description': description};
        ListRoles[Position] = data;
    }
    RepaintTableRoles();
    CloseModal();
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
        //текст
        cell = row.insertCell();
        cell.setAttribute('width', '100%');
        label=document.createElement('label');
        label.className='text5';
        label.innerHTML=ListPositions[i].name;
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
                IdPosition = ListPositions[this.pos].id;
                Position = this.pos;
                OpenModelPosition(Position);
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
        //текст
        cell = row.insertCell();
        cell.setAttribute('width', '100%');
        label=document.createElement('label');
        label.className='text5';
        label.innerHTML=ListRoles[i].name;
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
                IdRole = ListRoles[this.pos].id;
                Position = this.pos;
                OpenModelRole(Position);
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

//событие при выборе изображения 1
document.getElementById('image1').onchange = function (evt) {
    var tgt = evt.target || window.event.srcElement,
    files = tgt.files;
    //если поддерживает загрузку файлов
    if (FileReader && files && files.length) {
        var fr = new FileReader();
        fr.onload = function () {
            document.getElementById('image_str1').src = fr.result;
        }
        fr.readAsDataURL(files[0]);
        var change=document.getElementById('on_change_image1');
        change.value='1';
    }
    //если не поддерживает загрузку файлов
    else {
        alert('Загрузка изображения не поддерживается.');
    }
}

//удаление изображения 1
function DeleteImage1(){
    var image_input=document.getElementById('image1');
    var image=document.getElementById('image_str1');
    var change=document.getElementById('on_change_image1');
    change.value='2';
    image.src='';
    image_input.value='';
}

//событие при выборе изображения 2
document.getElementById('image2').onchange = function (evt) {
    var tgt = evt.target || window.event.srcElement,
    files = tgt.files;
    //если поддерживает загрузку файлов
    if (FileReader && files && files.length) {
        var fr = new FileReader();
        fr.onload = function () {
            document.getElementById('image_str2').src = fr.result;
        }
        fr.readAsDataURL(files[0]);
        var change=document.getElementById('on_change_image2');
        change.value='1';
    }
    //если не поддерживает загрузку файлов
    else {
        alert('Загрузка изображения не поддерживается.');
    }
}

//удаление изображения 2
function DeleteImage2(){
    var image_input=document.getElementById('image2');
    var image=document.getElementById('image_str2');
    var change=document.getElementById('on_change_image2');
    change.value='2';
    image.src='';
    image_input.value='';
}

//событие при выборе изображения 3
document.getElementById('image3').onchange = function (evt) {
    var tgt = evt.target || window.event.srcElement,
    files = tgt.files;
    //если поддерживает загрузку файлов
    if (FileReader && files && files.length) {
        var fr = new FileReader();
        fr.onload = function () {
            document.getElementById('image_str3').src = fr.result;
        }
        fr.readAsDataURL(files[0]);
        var change=document.getElementById('on_change_image3');
        change.value='1';
    }
    //если не поддерживает загрузку файлов
    else {
        alert('Загрузка изображения не поддерживается.');
    }
}

//удаление изображения 3
function DeleteImage3(){
    var image_input=document.getElementById('image3');
    var image=document.getElementById('image_str3');
    var change=document.getElementById('on_change_image3');
    change.value='2';
    image.src='';
    image_input.value='';
}

//событие при выборе изображения 4
document.getElementById('image4').onchange = function (evt) {
    var tgt = evt.target || window.event.srcElement,
    files = tgt.files;
    //если поддерживает загрузку файлов
    if (FileReader && files && files.length) {
        var fr = new FileReader();
        fr.onload = function () {
            document.getElementById('image_str4').src = fr.result;
        }
        fr.readAsDataURL(files[0]);
        var change=document.getElementById('on_change_image4');
        change.value='1';
    }
    //если не поддерживает загрузку файлов
    else {
        alert('Загрузка изображения не поддерживается.');
    }
}

//удаление изображения 4
function DeleteImage4(){
    var image_input=document.getElementById('image4');
    var image=document.getElementById('image_str4');
    var change=document.getElementById('on_change_image4');
    change.value='2';
    image.src='';
    image_input.value='';
}

//событие при выборе изображения 5
document.getElementById('image5').onchange = function (evt) {
    var tgt = evt.target || window.event.srcElement,
    files = tgt.files;
    //если поддерживает загрузку файлов
    if (FileReader && files && files.length) {
        var fr = new FileReader();
        fr.onload = function () {
            document.getElementById('image_str5').src = fr.result;
        }
        fr.readAsDataURL(files[0]);
        var change=document.getElementById('on_change_image5');
        change.value='1';
    }
    //если не поддерживает загрузку файлов
    else {
        alert('Загрузка изображения не поддерживается.');
    }
}

//удаление изображения 5
function DeleteImage5(){
    var image_input=document.getElementById('image5');
    var image=document.getElementById('image_str5');
    var change=document.getElementById('on_change_image5');
    change.value='2';
    image.src='';
    image_input.value='';
}