//СКРИПТЫ НОВОСТЕЙ
//добавить новость
function Add(){
    window.open("/ShowAddEditNewsController?page=" + Page, "_self");
}

//изменить новость
function Edit(id_news){
    window.open("/ShowAddEditNewsController?id_news=" + id_news + "&page=" + Page, "_self");
}

//подробнее о новости
function About(id_news){
    window.open("AboutNewsController?id_news=" + id_news, "_blank");
}

//удаление новости
function Delete(id_news){
    var isDel = confirm('Удалить новость?');
    if(isDel) window.open("/DeleteNewsController?id_news=" + id_news + "&page=" + Page, "_self");
}

//переход на страницу
function GoPage(page){
    window.open("/ShowNewsController?page=" + page, "_self");
}