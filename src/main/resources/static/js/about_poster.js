//СКРИПТЫ СТРАНИЦЫ ИНФОРМАЦИИ О ПОСТАНОВКЕ В НАЗНАЧЕННОЕ ВРЕМЯ
//переход на страницу о сотруднике театра
function ShowAboutWorkerTheater(id_worker){
    window.open("/AboutWorkerController?id_worker=" + id_worker, "_blank");
}

//переход на страницу о человеке по медиа ссылке
function ShowAboutSocialLink(link){
    window.open(link, "_blank");
}