//СКРИПТ ПО ДОЛЖНОСТЯМ
//переход на страницу добавления
function Add(){
    window.open("/ShowAddEditPositionController", "_self");
}

//переход на страницу изменения
function Edit(id_position){
    window.open("/ShowAddEditPositionController?id_position=" + id_position, "_self");
}

//удаление
function Delete(id_position){
    var isDel = confirm("Удалить должность? При удалении удалятся служащие с этой должностью.");
    if(isDel) window.open("/DeletePositionController?id_position=" + id_position, "_self");
}