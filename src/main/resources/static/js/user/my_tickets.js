//СКРИПТЫ БИЛЕТОВ ПОЛЬЗОВАТЕЛЯ
var SelectStatusEl = document.getElementById('status_time');

//событие при изменении в списке статуса
SelectStatusEl.addEventListener('change',function(){
    Search();
});

//поиск
function Search(){
    var status = SelectStatusEl.value;
    window.open("/ShowMyTicketsController?status=" + status, "_self");
}

//переход на страницу
function GoPage(page){
    var status = SelectStatusEl.value;
    window.open("/ShowMyTicketsController?status=" + status + "&page=" + page, "_self");
}

//переход на страницу о спектакле
function AboutPerformance(id_directory_performance){
    window.open("/AboutPerformanceController?id_performance=" + id_directory_performance, "_blank");
}

//переход на страницу о постановке
function AboutPoster(id_poster){
    window.open("/AboutPosterController?id_poster=" + id_poster, "_blank");
}

//переход на страницу добавления или изменения оценки по постановки и актерам
function SetRating(oper, id_poster){
    var status = SelectStatusEl.value;
    window.open("/ShowSetRatingController?oper=" + oper + "&id_poster=" + id_poster + "&page=" + Page + "&status=" + status, "_self");
}

//распечатать билет
function PrintTicket(id_ticket){
    var list_id_tickets = [];
    list_id_tickets.push(Number(id_ticket));
    window.open("/PrintTicketController?list_id_tickets=" + encodeURI(JSON.stringify(list_id_tickets)), "_blank");
}