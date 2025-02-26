//СКРИПТЫ ГОСТЕВОЙ КНИГИ
//переход на страницу добавления обращения
function Add(){
    var only_me = false;
    if(UserStatus == 'User') only_me = document.getElementById('only_me').value;
    window.open("/ShowAddGuestbookController?page=" + Page + "&only_me=" + only_me, "_self");
}

//удаление
function Delete(id_guestbook){
    var isDel = confirm('Удалить обращение?');
    if(isDel){
        var only_me = false;
        if(UserStatus == 'User') only_me = document.getElementById('only_me').value;
        window.open("/DeleteGuestbookController?id_guestbook=" + id_guestbook + "&page=" + Page + "&only_me=" + only_me, "_self");
    }
}

//добавление или изменение ответа
function AddEditGuestbook(oper, id_guestbook){
    window.open("/ShowAddEditGuestbookController?oper=" + oper + "&id_guestbook=" + id_guestbook + "&page=" + Page, "_self");
}

//переход на выбранную страницу
function GoPage(page){
    var only_me = false;
    if(UserStatus == 'User') only_me = document.getElementById('only_me').value;
    window.open("/ShowGuestbookController?page=" + page + "&only_me=" + only_me, "_self");
}