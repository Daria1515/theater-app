//СКРИПТЫ СПИСКА ПОЛЬЗОВАТЕЛЕЙ
//переход на страницу
function GoPage(page){
    window.open("/ShowUsersController?page=" + page, "_self");
}

//удаление
function Delete(id_user){
    var isDel = confirm('Удалить пользователя из системы?');
    if(isDel) window.open("/DeleteUserController?id_user=" + id_user + "&page=" + Page, "_self");
}