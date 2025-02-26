//СКРИПТЫ ЛЮДЕЙ ТЕАТРА
var SelectPositionEl = document.getElementById("id_position");
var EditNameEl = document.getElementById("name");
//переход на страницу добавления
function Add(){
    var id_position = SelectPositionEl.value;
    var name = EditNameEl.value;
    window.open("/ShowAddEditWorkerController?page=" + Page + "&id_position=" + id_position + "&name=" + name, "_self");
}

//переход на старицу изменения данных
function Edit(id_worker){
    var id_position = SelectPositionEl.value;
    var name = EditNameEl.value;
    window.open("/ShowAddEditWorkerController?page=" + Page + "&id_worker=" + id_worker + "&id_position=" + id_position + "&name=" + name, "_self");
}

//удаление
function Delete(id_worker){
    var isDel = confirm('Удалить сотрудника? При удалении удалятся');
    if(isDel){
        var id_position = SelectPositionEl.value;
        var name = EditNameEl.value;
        window.open("DeleteWorkerController?page=" + Page + "&id_worker=" + id_worker + "&id_position=" + id_position + "&name=" + name, "_self");
    }
}

//нажата кнопка поиска
function Search(){
    var id_position = SelectPositionEl.value;
    var name = EditNameEl.value;
    window.open("/ShowWorkersController?id_position=" + id_position + "&name=" + name, "_self");
}

//переход на страницу
function GoPage(page){
    var id_position = SelectPositionEl.value;
    var name = EditNameEl.value;
    window.open("/ShowWorkersController?page=" + page + "&id_position=" + id_position + "&name=" + name, "_self");
}

//переход по ссылке на социальное медиа
function GoSocialMediaLink(link){
    window.open(link, "_blank");
}

//переход на страницу с подробной информацией о сотруднике
function About(id_worker){
    window.open("/AboutWorkerController?id_worker=" + id_worker, "_blank");
}